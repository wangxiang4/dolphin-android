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
 * 我的回收列表数据适配器
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/1/31
 */
public class UserRecyclerAdapter extends DefaultRecyclerAdapter {

    private List<UserEntity> mItemList;

    private EventListener mEventListener;

    @Data
    @Accessors
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserEntity {

        public String code;

        public String title;

        public String detail;

        public String secondDetail;

        public Integer image;

        public Integer badge;

        public Boolean hidesDisclosure;

        public Boolean hidesLeftImage;
    }

    public UserRecyclerAdapter(List<UserEntity> mItemList){
        this.mItemList = mItemList;
    }

    public UserRecyclerAdapter(List<UserEntity> mItemList, Boolean hidesDisclosure) {
        super(null, null, hidesDisclosure, null);
        this.mItemList = mItemList;
    }

    public UserRecyclerAdapter(List<UserEntity> mItemList, Boolean hidesDisclosure, Boolean hidesLeftImage) {
        super(null, null, hidesDisclosure, hidesLeftImage);
        this.mItemList = mItemList;
    }

    public UserRecyclerAdapter(List<UserEntity> mItemList,
                               Integer defaultImage,
                               Integer defaultBadge,
                               Boolean hidesDisclosure,
                               Boolean hidesLeftImage) {
        super(defaultImage, defaultBadge, hidesDisclosure, hidesLeftImage);
        this.mItemList = mItemList;
    }

    public interface EventListener {

        void onItemViewClicked(UserEntity userEntity);

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        final UserEntity item = mItemList.get(position);
        viewHolder.layoutContent.setOnClickListener(view -> onItemViewClick(item));
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
            viewHolder.disclosureImage.setVisibility(item.hidesDisclosure? View.GONE: View.VISIBLE);
        }
        if (ObjectUtils.isNotEmpty(item.hidesLeftImage)) {
            viewHolder.layoutLeft.setVisibility(item.hidesLeftImage? View.GONE: View.VISIBLE);
        }
    }

    private void onItemViewClick(UserEntity userEntity) {
        if (mEventListener != null && ObjectUtils.isNotEmpty(mItemList)) {
            mEventListener.onItemViewClicked(userEntity);
        }
    }

    public EventListener getEventListener(){
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }
}
