package com.dolphin.demo.ui.adapter;

import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.dolphin.demo.entity.OssFile;

import java.util.List;

/**
 *<p>
 * 消息回收列表数据适配器
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/2
 */
public class MessageRecyclerAdapter extends DefaultRecyclerAdapter {

    private List<OssFile> mItemList;

    private EventListener mEventListener;

    public MessageRecyclerAdapter(List<OssFile> mItemList){
        this.mItemList = mItemList;
    }

    public interface EventListener {

        void onItemViewClicked(OssFile ossFile);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        final OssFile item = mItemList.get(position);
        viewHolder.contentLayout.setOnClickListener(view -> onItemViewClick(item));
        viewHolder.titleLabel.setText(item.getOriginal());
        viewHolder.detailLabel.setVisibility(View.GONE);
        viewHolder.secondDetailLabel.setVisibility(View.GONE);
    }

    private void onItemViewClick(OssFile ossFile) {
        if (mEventListener != null && ObjectUtils.isNotEmpty(mItemList)) {
            mEventListener.onItemViewClicked(ossFile);
        }
    }

    public EventListener getEventListener(){
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }
}
