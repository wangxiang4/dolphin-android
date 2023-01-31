package com.dolphin.demo.ui.adapter;

import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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

    @Data
    @Accessors
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeEntity {

        public String code;

        public String title;

        public String detail;

        public String secondDetail;

        public Integer image;

        public Integer badge;

        public Boolean hidesDisclosure;
    }

    public HomeRecyclerAdapter(List<HomeEntity> mItemList){
        this.mItemList = mItemList;
    }

    public HomeRecyclerAdapter(List<HomeEntity> mItemList, Integer defaultImage, Integer defaultBadge, Boolean hidesDisclosure){
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
            viewHolder.disclosureImage.setVisibility(item.hidesDisclosure? View.INVISIBLE: View.VISIBLE);
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
