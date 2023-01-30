package com.dolphin.demo.ui.adapter;

import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;

import java.util.List;

/**
 *<p>
 * 首页回收列表数据适配器
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/1/30
 */
public class HomeRecyclerAdapter extends DefaultRecyclerAdapter {

    private List<HomeEntity> mItemList;

    private EventListener mEventListener;

    public static class HomeEntity {

        public String code;

        public String title;

        public String detail;

        public String secondDetail;

        public int image;

        public int badge;

        public Boolean hidesDisclosure;

        public HomeEntity(String code, String title, String detail, String secondDetail,
                          int image, int badge, Boolean hidesDisclosure) {
            this.code = code;
            this.title = title;
            this.detail = detail;
            this.secondDetail = secondDetail;
            this.image = image;
            this.badge = badge;
            this.hidesDisclosure = hidesDisclosure;
        }
    }

    public HomeRecyclerAdapter(List<HomeEntity> mItemList){
        this.mItemList = mItemList;
    }

    public HomeRecyclerAdapter(List<HomeEntity> mItemList, int defaultImage, int defaultBadge, boolean hidesDisclosure){
        super(defaultImage, defaultBadge, hidesDisclosure);
        this.mItemList = mItemList;
    }

    public interface EventListener {

        void onItemViewClicked(HomeEntity homeEntity);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        final HomeEntity item = mItemList.get(position);
        viewHolder.contentLayout.setOnClickListener(view -> onItemViewClick(item));
        viewHolder.titleLabel.setText(item.title);
        viewHolder.detailLabel.setText(item.detail);
        viewHolder.secondDetailLabel.setText(item.secondDetail);
        if (ObjectUtils.isNotEmpty(item.image)) {
            viewHolder.leftImage.setImageResource(item.image);
        }
        if (ObjectUtils.isNotEmpty(item.badge)) {
            viewHolder.leftBadge.setImageResource(item.badge);
        }
        if (ObjectUtils.isNotEmpty(item.hidesDisclosure)) {
            viewHolder.disclosureImage.setVisibility(hidesDisclosure? View.GONE: View.VISIBLE);
        }
    }

    private void onItemViewClick(HomeEntity homeEntity) {
        if (mEventListener != null && ObjectUtils.isNotEmpty(mItemList)) {
            mEventListener.onItemViewClicked(homeEntity);
        }
    }

    public EventListener getEventListener(){
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }
}
