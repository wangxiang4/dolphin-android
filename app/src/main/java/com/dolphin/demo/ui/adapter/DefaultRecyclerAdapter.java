package com.dolphin.demo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ObjectUtils;
import com.dolphin.demo.R;

/**
 *<p>
 * 默认回收列表数据适配器
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/1/30
 */
public class DefaultRecyclerAdapter extends RecyclerView.Adapter<DefaultRecyclerAdapter.ViewHolder> {

    protected Integer defaultImage;

    protected Integer defaultBadge;

    protected Boolean hidesDisclosure = false;

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout contentLayout;
        public ImageView leftImage;
        public ImageView leftBadge;
        public TextView titleLabel;
        public TextView detailLabel;
        public TextView secondDetailLabel;
        public ImageView disclosureImage;

        public ViewHolder(@NonNull View v) {
            super(v);
            contentLayout = v.findViewById(R.id.content_layout);
            leftImage = v.findViewById(R.id.left_image);
            leftBadge = v.findViewById(R.id.left_badge);
            titleLabel = v.findViewById(R.id.title_label);
            detailLabel = v.findViewById(R.id.detail_label);
            secondDetailLabel = v.findViewById(R.id.second_detail_label);
            disclosureImage = v.findViewById(R.id.disclosure_image);
        }
    }

    protected DefaultRecyclerAdapter(){
    }

    protected DefaultRecyclerAdapter(Integer defaultImage, Integer defaultBadge, Boolean hidesDisclosure){
        this.defaultImage = defaultImage;
        this.defaultBadge = defaultBadge;
        this.hidesDisclosure = hidesDisclosure;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_default, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        if (ObjectUtils.isNotEmpty(defaultImage)) {
            viewHolder.leftImage.setImageResource(defaultImage);
        }

        if (ObjectUtils.isNotEmpty(defaultBadge)) {
            viewHolder.leftBadge.setImageResource(defaultBadge);
        }

        viewHolder.disclosureImage.setVisibility(hidesDisclosure? View.INVISIBLE: View.VISIBLE);
    }

}
