package com.dolphin.demo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.dolphin.demo.R;
import com.dolphin.core.entity.MapLogisticPoint;

import java.util.List;

/**
 *<p>
 * 行程任务左滑显示按钮回收视图适配器
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/16
 */
public class SwipeableRecyclerAdapter extends RecyclerView.Adapter<SwipeableRecyclerAdapter.MyViewHolder>
        implements SwipeableItemAdapter<SwipeableRecyclerAdapter.MyViewHolder> {

    /** 视图集合数据 */
    private List<MapLogisticPoint> mItemList;

    /** 回调事件 */
    private EventListener mEventListener;

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

    /** 视图当前持有者对象 */
    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        public RelativeLayout mContentLayout;
        public RelativeLayout mBehindLayout;
        public TextView mBtnSwipeable1;
        public TextView mBtnSwipeable2;
        public TextView mBtnSwipeable3;
        public TextView mTitleLabel;
        public TextView mDetailLabel;
        public TextView mSecondDetailLabel;

        public MyViewHolder(View v) {
            super(v);
            mContentLayout = v.findViewById(R.id.content_layout);
            mBehindLayout = v.findViewById(R.id.behind_layout);
            mBtnSwipeable1 = v.findViewById(R.id.btn_swipeable1);
            mBtnSwipeable2 = v.findViewById(R.id.btn_swipeable2);
            mBtnSwipeable3 = v.findViewById(R.id.btn_swipeable3);
            mTitleLabel = v.findViewById(R.id.title_label);
            mDetailLabel = v.findViewById(R.id.detail_label);
            mSecondDetailLabel = v.findViewById(R.id.second_detail_label);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContentLayout;
        }

    }

    public SwipeableRecyclerAdapter(List<MapLogisticPoint> mItemList) {
        this.mItemList = mItemList;
        // SwipeableItemAdapter需要稳定的ID,并且还必须适当地实现getItemId()方法,
        // 否则会导致局部刷新,找不到匹配刷新项
        setHasStableIds(true);
    }

    /** 可滑动视图容器点击监听 */
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
        return mItemList.get(position).getId().hashCode();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_swipeable, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MapLogisticPoint item = mItemList.get(position);
        // 设置偏移量
        holder.setMaxLeftSwipeAmount(-0.46f);
        holder.setMaxRightSwipeAmount(0);
        // 设置水平滑动的量
        holder.setSwipeItemHorizontalSlideAmount(item.getPinned() ? -0.46f : 0);

        holder.mDetailLabel.setVisibility(View.GONE);
        holder.mSecondDetailLabel.setVisibility(View.GONE);
        holder.mContentLayout.setOnClickListener(view -> onSwipeableViewContainerClick(view));
        holder.mBtnSwipeable1.setOnClickListener(view -> onBtnSwipeable1Click(view));
        holder.mBtnSwipeable2.setOnClickListener(view -> onBtnSwipeable2Click(view));
        holder.mBtnSwipeable3.setOnClickListener(view -> onBtnSwipeable3Click(view));
        holder.mTitleLabel.setText("这是一条审批数据");
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public int onGetSwipeReactionType(@NonNull MyViewHolder holder, int position, int x, int y) {
        final int tx = (int) (holder.getSwipeableContainerView().getTranslationX() + 0.46f);
        final int ty = (int) (holder.getSwipeableContainerView().getTranslationY() + 0.46f);
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
    public void onSwipeItemStarted(@NonNull MyViewHolder holder, int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onSetSwipeBackground(@NonNull MyViewHolder holder, int position, int type) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
            holder.mBehindLayout.setVisibility(View.GONE);
        } else {
            holder.mBehindLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public SwipeResultAction onSwipeItem(@NonNull MyViewHolder holder, int position, int result) {

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

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    /** 向左滑动操作 */
    private static class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private SwipeableRecyclerAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(SwipeableRecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            MapLogisticPoint item = mAdapter.mItemList.get(mPosition);
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
        private SwipeableRecyclerAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(SwipeableRecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            MapLogisticPoint item = mAdapter.mItemList.get(mPosition);
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
