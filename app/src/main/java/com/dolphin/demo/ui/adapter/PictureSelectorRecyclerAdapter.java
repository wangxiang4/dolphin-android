package com.dolphin.demo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dolphin.demo.R;
import com.dolphin.demo.entity.OssFile;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collection;


/**
 *<p>
 * 网格图片 RecyclerView 视图绑定数据适配器
 * 具体请参考: https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=zh-cn
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/3
 */
public class PictureSelectorRecyclerAdapter extends RecyclerView.Adapter<PictureSelectorRecyclerAdapter.ViewHolder> {

    private ArrayList<OssFile> mItemList;

    private EventListener mEventListener;

    public PictureSelectorRecyclerAdapter(ArrayList<OssFile> result) {
        this.mItemList = result;
    }

    public interface EventListener {

        void onItemViewClicked(View v, int position);

        void onItemViewLongClicked(ViewHolder viewHolder, int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        ImageView mIvDel;
        TextView tvDuration;

        public ViewHolder(View view) {
            super(view);
            mImg = view.findViewById(R.id.fiv);
            mIvDel = view.findViewById(R.id.iv_del);
            tvDuration = view.findViewById(R.id.tv_duration);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_picture_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.mIvDel.setVisibility(View.VISIBLE);
        viewHolder.mIvDel.setOnClickListener(view -> {
            int index = viewHolder.getAbsoluteAdapterPosition();
            delete(index);
        });
        OssFile item = mItemList.get(position);
        String path = item.getAvailablePath();
        long duration = item.getDuration();
        viewHolder.tvDuration.setVisibility(PictureMimeType.isHasVideo(item.getMimeType()) ? View.VISIBLE : View.GONE);
        if (PictureMimeType.isHasVideo(item.getMimeType())) {
            viewHolder.tvDuration.setVisibility(View.VISIBLE);
            viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                    (R.drawable.ps_ic_audio, 0, 0, 0);
        } else {
            viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                    (R.drawable.ps_ic_video, 0, 0, 0);
        }

        viewHolder.tvDuration.setText(DateUtils.formatDurationTime(duration));
        if (PictureMimeType.isHasVideo(item.getMimeType())) {
            viewHolder.mImg.setImageResource(R.drawable.ps_audio_placeholder);
        } else {
            Glide.with(viewHolder.itemView.getContext())
                    .load(path)
                    .centerCrop()
                    .placeholder(R.color.white)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mImg);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            int adapterPosition = viewHolder.getAbsoluteAdapterPosition();
            onItemViewClick(v, adapterPosition);
        });

        viewHolder.itemView.setOnLongClickListener(v -> {
            int adapterPosition = viewHolder.getAbsoluteAdapterPosition();
            onItemViewLongClick(viewHolder, adapterPosition, v);
            return true;
        });
    }

    private void onItemViewClick(View v, int position) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v, position);
        }
    }

    private void onItemViewLongClick(ViewHolder viewHolder, int position, View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewLongClicked(viewHolder, position, v);
        }
    }

    public PictureSelectorRecyclerAdapter.EventListener getEventListener(){
        return mEventListener;
    }

    public PictureSelectorRecyclerAdapter setEventListener(PictureSelectorRecyclerAdapter.EventListener eventListener) {
        mEventListener = eventListener;
        return this;
    }

    public ArrayList<OssFile> getData() {
        return mItemList;
    }

    public PictureSelectorRecyclerAdapter refresh(Collection<OssFile> collection) {
        mItemList.clear();
        mItemList.addAll(collection);
        notifyDataSetChanged();
        return this;
    }

    public PictureSelectorRecyclerAdapter loadMore(Collection<OssFile> collection) {
        mItemList.addAll(collection);
        notifyDataSetChanged();
        return this;
    }

    public PictureSelectorRecyclerAdapter insert(Collection<OssFile> collection) {
        mItemList.addAll(0, collection);
        notifyItemRangeInserted(0, collection.size());
        return this;
    }

    public void delete(int position) {
        if (position != RecyclerView.NO_POSITION && mItemList.size() > position) {
            mItemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mItemList.size());
        }
    }

}
