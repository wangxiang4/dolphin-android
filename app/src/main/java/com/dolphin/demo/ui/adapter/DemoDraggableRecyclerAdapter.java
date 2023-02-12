package com.dolphin.demo.ui.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.dolphin.demo.R;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *<p>
 * demo可拖动回收列表数据适配器
 * 更多拖拽方式请参考: https://github.com/h6ah4i/android-advancedrecyclerview
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/7
 */
public class DemoDraggableRecyclerAdapter extends RecyclerView.Adapter<DemoDraggableRecyclerAdapter.ViewHolder> implements DraggableItemAdapter<DemoDraggableRecyclerAdapter.ViewHolder> {

    private List<DemoDraggableRecyclerAdapter.Entity> mItemList;

    private DemoDraggableRecyclerAdapter.EventListener mEventListener;

    @Data
    @Accessors
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {

        public String code;

        public String title;

        public String detail;

        public Integer image;

        public Boolean hidesDisclosure;
    }

    public DemoDraggableRecyclerAdapter(List<DemoDraggableRecyclerAdapter.Entity> mItemList) {
        this.mItemList = mItemList;
        setHasStableIds(true);
    }

    public interface EventListener {

        void onItemViewClicked(DemoDraggableRecyclerAdapter.Entity entity);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements DraggableItemViewHolder {
        private final DraggableItemState mDragState = new DraggableItemState();

        public RelativeLayout layoutContainer;
        public ImageView dragHandle;
        public LinearLayout layoutContent;
        public RelativeLayout layoutLeft ;
        public ImageView leftImage;
        public ImageView leftBadge;
        public TextView titleLabel;
        public TextView detailLabel;
        public ImageView disclosureImage;

        public ViewHolder(@NonNull View v) {
            super(v);
            layoutContainer = v.findViewById(R.id.layout_container);
            dragHandle = v.findViewById(R.id.drag_handle);
            layoutContent = v.findViewById(R.id.layout_content);
            layoutLeft  = v.findViewById(R.id.layout_left);
            leftImage = v.findViewById(R.id.left_image);
            leftBadge = v.findViewById(R.id.left_badge);
            titleLabel = v.findViewById(R.id.title_label);
            detailLabel = v.findViewById(R.id.detail_label);
            disclosureImage = v.findViewById(R.id.disclosure_image);
        }

        @Override
        public void setDragStateFlags(int flags) {
            mDragState.setFlags(flags);
        }

        @Override
        public int getDragStateFlags() {
            return mDragState.getFlags();
        }

        @Override
        public DraggableItemState getDragState() {
            return mDragState;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_draggable, parent, false);
        return new DemoDraggableRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getCode().hashCode();
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final DemoDraggableRecyclerAdapter.Entity item = mItemList.get(position);
        viewHolder.layoutContent.setOnClickListener(view -> onItemViewClick(item));
        viewHolder.titleLabel.setText(item.title);
        viewHolder.detailLabel.setVisibility(View.GONE);
        viewHolder.leftBadge.setVisibility(View.GONE);
        if (ObjectUtils.isNotEmpty(item.image)) {
            viewHolder.leftImage.setImageResource(item.image);
        }
        if (ObjectUtils.isNotEmpty(item.hidesDisclosure)) {
            viewHolder.disclosureImage.setVisibility(item.hidesDisclosure? View.GONE: View.VISIBLE);
        }

        final DraggableItemState dragState = viewHolder.getDragState();
        if (dragState.isUpdated()) {
            int bgResId;
            if (dragState.isActive()) {
                bgResId = R.color.dragging_active_state;
                // 需要在这里清除可绘制状态以获得拖拽项的正确外观
                Drawable drawable = viewHolder.layoutContainer.getForeground();
                if (ObjectUtils.isNotEmpty(drawable)) drawable.setState( new int[] {});
            } else if (dragState.isDragging()) {
                bgResId = R.color.dragging_state;
            } else {
                bgResId = R.color.dragging_normal_state;
            }
            viewHolder.layoutContainer.setBackgroundResource(bgResId);
        }

    }

    @Override
    public boolean onCheckCanStartDrag(@NonNull ViewHolder holder, int position, int x, int y) {
        final View containerView = holder.layoutContainer;
        final View dragHandleView = holder.dragHandle;

        final int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 1f);
        final int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 1f);
        x = x - offsetX;
        y = y - offsetY;

        final int tx = (int) (dragHandleView.getTranslationX() + 1f);
        final int ty = (int) (dragHandleView.getTranslationY() + 1f);
        final int left = dragHandleView.getLeft() + tx;
        final int right = dragHandleView.getRight() + tx;
        final int top = dragHandleView.getTop() + ty;
        final int bottom = dragHandleView.getBottom() + ty;

        //return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
        return true;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        LogUtils.d("onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");
        if (fromPosition == toPosition) return;
        final Entity item = mItemList.remove(fromPosition);
        mItemList.add(toPosition, item);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(@NonNull ViewHolder holder, int position) {
        // 未指定可拖动排序范围
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    private void onItemViewClick(DemoDraggableRecyclerAdapter.Entity entity) {
        if (mEventListener != null && ObjectUtils.isNotEmpty(mItemList)) {
            mEventListener.onItemViewClicked(entity);
        }
    }

    public DemoDraggableRecyclerAdapter.EventListener getEventListener(){
        return mEventListener;
    }

    public void setEventListener(DemoDraggableRecyclerAdapter.EventListener eventListener) {
        mEventListener = eventListener;
    }

}
