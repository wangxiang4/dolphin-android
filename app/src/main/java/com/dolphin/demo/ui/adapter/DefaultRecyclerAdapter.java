package com.dolphin.demo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ObjectUtils;
import com.dolphin.demo.R;

/**
 *<p>
 * 基础默认回收列表数据适配器
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/1/30
 */
public class DefaultRecyclerAdapter extends RecyclerView.Adapter<DefaultRecyclerAdapter.ViewHolder> {

    protected Integer defaultImage;

    protected Integer defaultBadge;

    protected Boolean hideDisclosure = false;

    protected Boolean hideLeftImage= false;

    protected int mLastPosition = -1;

    protected boolean mOpenAnimationEnable = true;

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout contentLayout;
        public RelativeLayout leftLayout;
        public ImageView leftImage;
        public ImageView leftBadge;
        public TextView titleLabel;
        public TextView detailLabel;
        public TextView secondDetailLabel;
        public ImageView disclosureImage;

        public ViewHolder(@NonNull View v) {
            super(v);
            contentLayout = v.findViewById(R.id.content_layout);
            leftLayout = v.findViewById(R.id.left_layout);
            leftImage = v.findViewById(R.id.left_image);
            leftBadge = v.findViewById(R.id.left_badge);
            titleLabel = v.findViewById(R.id.title_label);
            detailLabel = v.findViewById(R.id.detail_label);
            secondDetailLabel = v.findViewById(R.id.second_detail_label);
            disclosureImage = v.findViewById(R.id.disclosure_image);
        }
    }

    protected DefaultRecyclerAdapter(){}

    protected DefaultRecyclerAdapter(Integer defaultImage,
                                     Integer defaultBadge,
                                     Boolean hidesDisclosure,
                                     Boolean hideLeftImage){
        this.defaultImage = defaultImage;
        this.defaultBadge = defaultBadge;
        if (ObjectUtils.isNotEmpty(hidesDisclosure)) this.hideDisclosure = hidesDisclosure;
        if (ObjectUtils.isNotEmpty(hideLeftImage)) this.hideLeftImage = hideLeftImage;
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

        viewHolder.disclosureImage.setVisibility(hideDisclosure? View.GONE: View.VISIBLE);
        viewHolder.leftLayout.setVisibility(hideLeftImage ? View.GONE: View.VISIBLE);
        addAnimate(viewHolder, position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        addAnimate(holder, holder.getLayoutPosition());
    }

    private void addAnimate(ViewHolder holder, int position) {
        if (mOpenAnimationEnable && mLastPosition < position) {
            holder.itemView.setAlpha(0);
            holder.itemView.animate().alpha(1).start();
            mLastPosition = position;
        }
    }
}
