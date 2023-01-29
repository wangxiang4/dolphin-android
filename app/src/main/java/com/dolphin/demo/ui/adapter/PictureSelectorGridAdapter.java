package com.dolphin.demo.ui.adapter;

import android.net.Uri;
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
import com.dolphin.demo.listener.OnItemLongClickListener;
import com.dolphin.core.BuildConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.utils.DateUtils;

import java.util.ArrayList;


/**
 *<p>
 * 网格图片 RecyclerView 视图绑定数据适配器
 * 具体请参考: https://developer.android.com/guide/topics/ui/layout/recyclerview?hl=zh-cn
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/3
 */
public class PictureSelectorGridAdapter extends RecyclerView.Adapter<PictureSelectorGridAdapter.ViewHolder> {

    private ArrayList<LocalMedia> list = new ArrayList();

    public void delete(int position) {
        try {
            if (position != RecyclerView.NO_POSITION && list.size() > position) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PictureSelectorGridAdapter(ArrayList<LocalMedia> result) {
        this.list = result;
    }

    public ArrayList<LocalMedia> getData() {
        return list;
    }

    public void remove(int position) {
        if (position < list.size()) {
            list.remove(position);
        }
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
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.kc_item_filter_picture_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        viewHolder.mIvDel.setVisibility(View.VISIBLE);
        viewHolder.mIvDel.setOnClickListener(view -> {
            int index = viewHolder.getAbsoluteAdapterPosition();
            if (index != RecyclerView.NO_POSITION && list.size() > index) {
                list.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, list.size());
            }
        });
        LocalMedia media = list.get(position);
        int chooseModel = media.getChooseModel();
        String path = media.getAvailablePath();
        long duration = media.getDuration();
        viewHolder.tvDuration.setVisibility(PictureMimeType.isHasVideo(media.getMimeType())
                ? View.VISIBLE : View.GONE);
        if (chooseModel == SelectMimeType.ofAudio()) {
            viewHolder.tvDuration.setVisibility(View.VISIBLE);
            viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                    (R.drawable.ps_ic_audio, 0, 0, 0);
        } else {
            viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                    (R.drawable.ps_ic_video, 0, 0, 0);
        }

        viewHolder.tvDuration.setText(DateUtils.formatDurationTime(duration));
        if (chooseModel == SelectMimeType.ofAudio()) {
            viewHolder.mImg.setImageResource(R.drawable.ps_audio_placeholder);
        } else {
            Glide.with(viewHolder.itemView.getContext())
                    .load(PictureMimeType.isContent(path) && !media.isCut() && !media.isCompressed() ? Uri.parse(path)
                            : path)
                    .centerCrop()
                    .placeholder(R.color.white)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.mImg);
        }

        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(v -> {
                int adapterPosition = viewHolder.getAbsoluteAdapterPosition();
                mItemClickListener.onItemClick(v, adapterPosition);
            });
        }

        if (mItemLongClickListener != null) {
            viewHolder.itemView.setOnLongClickListener(v -> {
                int adapterPosition = viewHolder.getAbsoluteAdapterPosition();
                mItemLongClickListener.onItemLongClick(viewHolder, adapterPosition, v);
                return true;
            });
        }
    }

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public interface OnItemClickListener {

        void onItemClick(View v, int position);

    }

    private OnItemLongClickListener mItemLongClickListener;

    public void setItemLongClickListener(OnItemLongClickListener l) {
        this.mItemLongClickListener = l;
    }

}
