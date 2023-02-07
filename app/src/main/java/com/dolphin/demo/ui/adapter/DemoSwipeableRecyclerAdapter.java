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

import com.dolphin.demo.R;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *<p>
 * demo可滑动回收列表数据适配器
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/7
 */
public class DemoSwipeableRecyclerAdapter extends RecyclerView.Adapter<DemoSwipeableRecyclerAdapter.ViewHolder> implements SwipeableItemAdapter<DemoSwipeableRecyclerAdapter.ViewHolder> {

    private List<Entity> mItemList;

    private EventListener mEventListener;

    @Data
    @Accessors
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {

        public String code;

        public String title;

        public String detail;

        public String secondDetail;

        public Integer image;

        public Integer badge;

        public Boolean hidesDisclosure;

        public Boolean hidesLeftImage;

        /** 回收视图滑动固定状态 */
        public Boolean pinned = false;
    }

    public interface EventListener {
        /** 滑动固定项后回调 */
        void onItemPinned(int position);

        /** 滑动容器点击 */
        void onItemViewClicked(View v);

        /** 滑动按钮点击1 */
        void onBtnSwipeable1(View v);

        /** 滑动按钮点击2 */
        void onBtnSwipeable2(View v);

        /** 滑动按钮点击3 */
        void onBtnSwipeable3(View v);
    }

    public static class ViewHolder extends AbstractSwipeableItemViewHolder {
        public LinearLayout behindLayout;
        public TextView btnSwipeable1;
        public TextView btnSwipeable2;
        public TextView btnSwipeable3;
        public RelativeLayout containerLayout;
        public RelativeLayout leftLayout;
        public ImageView leftImage;
        public ImageView leftBadge;
        public LinearLayout contentLayout;
        public TextView titleLabel;
        public TextView detailLabel;
        public TextView secondDetailLabel;
        public ImageView disclosureImage;

        public ViewHolder(View v) {
            super(v);
            behindLayout = v.findViewById(R.id.behind_layout);
            btnSwipeable1 = v.findViewById(R.id.btn_swipeable1);
            btnSwipeable2 = v.findViewById(R.id.btn_swipeable2);
            btnSwipeable3 = v.findViewById(R.id.btn_swipeable3);
            containerLayout = v.findViewById(R.id.container_layout);
            leftLayout = v.findViewById(R.id.left_layout);
            leftImage = v.findViewById(R.id.left_image);
            leftBadge = v.findViewById(R.id.left_badge);
            contentLayout = v.findViewById(R.id.content_layout);
            titleLabel = v.findViewById(R.id.title_label);
            detailLabel = v.findViewById(R.id.detail_label);
            secondDetailLabel = v.findViewById(R.id.second_detail_label);
            disclosureImage = v.findViewById(R.id.disclosure_image);
        }

        @Override
        public View getSwipeableContainerView() {
            return containerLayout;
        }

    }

    public DemoSwipeableRecyclerAdapter(List<Entity> mItemList) {
        this.mItemList = mItemList;
        // SwipeableItemAdapter需要稳定的ID,并且还必须适当地实现getItemId()方法,
        // 否则会导致局部刷新,找不到匹配刷新项
        setHasStableIds(true);
    }

    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }
    private void onBtnSwipeable1Click(View v) {
        if (mEventListener != null) {
            mEventListener.onBtnSwipeable1(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }
    private void onBtnSwipeable2Click(View v) {
        if (mEventListener != null) {
            mEventListener.onBtnSwipeable2(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }
    private void onBtnSwipeable3Click(View v) {
        if (mEventListener != null) {
            mEventListener.onBtnSwipeable3(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getCode().hashCode();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_swipeable, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Entity item = mItemList.get(position);
        // 设置偏移量
        holder.setMaxLeftSwipeAmount(-0.44f);
        holder.setMaxRightSwipeAmount(0);
        // 设置水平滑动的量
        holder.setSwipeItemHorizontalSlideAmount(item.getPinned() ? -0.44f : 0);

        holder.detailLabel.setVisibility(View.GONE);
        holder.secondDetailLabel.setVisibility(View.GONE);
        holder.disclosureImage.setVisibility(View.GONE);
        holder.titleLabel.setText(item.getTitle());

        holder.containerLayout.setOnClickListener(view -> onSwipeableViewContainerClick(view));
        holder.btnSwipeable1.setOnClickListener(view -> onBtnSwipeable1Click(view));
        holder.btnSwipeable2.setOnClickListener(view -> onBtnSwipeable2Click(view));
        holder.btnSwipeable3.setOnClickListener(view -> onBtnSwipeable3Click(view));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public int onGetSwipeReactionType(@NonNull ViewHolder holder, int position, int x, int y) {
        final int tx = (int) (holder.getSwipeableContainerView().getTranslationX() + 0.44f);
        final int ty = (int) (holder.getSwipeableContainerView().getTranslationY() + 0.44f);
        final int left = holder.getSwipeableContainerView().getLeft() + tx;
        final int right = holder.getSwipeableContainerView().getRight() + tx;
        final int top = holder.getSwipeableContainerView().getTop() + ty;
        final int bottom = holder.getSwipeableContainerView().getBottom() + ty;
        // 获取滑动状态,SwipeableItem内部通过此变量来进行控制
        if ((x >= left) && (x <= right) && (y >= top) && (y <= bottom)) {
            return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
        } else {
            return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H;
        }
    }

    @Override
    public void onSwipeItemStarted(@NonNull ViewHolder holder, int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onSetSwipeBackground(@NonNull ViewHolder holder, int position, int type) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
            holder.behindLayout.setVisibility(View.GONE);
        } else {
            holder.behindLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public SwipeResultAction onSwipeItem(@NonNull ViewHolder holder, int position, int result) {
        switch (result) {
            case SwipeableItemConstants.RESULT_SWIPED_LEFT:
                return new SwipeLeftResultAction(this, position);
            case SwipeableItemConstants.RESULT_SWIPED_RIGHT:
            case SwipeableItemConstants.RESULT_CANCELED:
            default:
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public DemoSwipeableRecyclerAdapter setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
        return this;
    }

    public DemoSwipeableRecyclerAdapter refresh(Collection<Entity> collection) {
        mItemList.clear();
        mItemList.addAll(collection);
        notifyDataSetChanged();
        return this;
    }

    public DemoSwipeableRecyclerAdapter loadMore(Collection<Entity> collection) {
        mItemList.addAll(collection);
        notifyDataSetChanged();
        return this;
    }

    public DemoSwipeableRecyclerAdapter insert(Collection<Entity> collection) {
        mItemList.addAll(0, collection);
        notifyItemRangeInserted(0, collection.size());
        return this;
    }

    /** 向左滑动操作 */
    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private DemoSwipeableRecyclerAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(DemoSwipeableRecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            Entity item = mAdapter.mItemList.get(mPosition);
            // 检测是否已经处于滑动完毕
            if (!item.getPinned()) {
                item.setPinned(true);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    /** 关闭向左滑动操作 */
    private static class UnpinResultAction extends SwipeResultActionDefault {
        private DemoSwipeableRecyclerAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(DemoSwipeableRecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            Entity item = mAdapter.mItemList.get(mPosition);
            // 检测是否已经处于滑动完毕
            if (item.getPinned()) {
                item.setPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

}
