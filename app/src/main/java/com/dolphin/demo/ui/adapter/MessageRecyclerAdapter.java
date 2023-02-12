package com.dolphin.demo.ui.adapter;

import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.dolphin.core.entity.OssFile;
import com.dolphin.demo.R;

import java.util.Collection;
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
        viewHolder.layoutContent.setOnClickListener(view -> onItemViewClick(item));
        viewHolder.titleLabel.setText(item.getOriginal());
        viewHolder.detailLabel.setVisibility(View.GONE);
        viewHolder.secondDetailLabel.setVisibility(View.GONE);
        viewHolder.leftImage.setImageResource(R.drawable.icon_file);
    }

    private void onItemViewClick(OssFile ossFile) {
        if (mEventListener != null && ObjectUtils.isNotEmpty(mItemList)) {
            mEventListener.onItemViewClicked(ossFile);
        }
    }

    public EventListener getEventListener(){
        return mEventListener;
    }

    public MessageRecyclerAdapter setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
        return this;
    }

    public MessageRecyclerAdapter refresh(Collection<OssFile> collection) {
        mItemList.clear();
        mItemList.addAll(collection);
        notifyDataSetChanged();
        mLastPosition = -1;
        return this;
    }

    public MessageRecyclerAdapter loadMore(Collection<OssFile> collection) {
        mItemList.addAll(collection);
        notifyDataSetChanged();
        return this;
    }

    public MessageRecyclerAdapter insert(Collection<OssFile> collection) {
        mItemList.addAll(0, collection);
        notifyItemRangeInserted(0, collection.size());
        return this;
    }

}
