package com.kicc.collect.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.kicc.collect.R;
import com.kicc.core.entity.MapLogisticPoint;

import java.util.List;

/**
 *<p>
 * 行程任务左滑显示按钮回收视图适配器
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/16
 */
public class SwipeableWithButtonPointAdapter extends RecyclerView.Adapter<SwipeableWithButtonPointAdapter.MyViewHolder>
        implements SwipeableItemAdapter<SwipeableWithButtonPointAdapter.MyViewHolder> {

    /** 视图集合数据 */
    private List<MapLogisticPoint> mItemList;

    /** 回调事件 */
    private EventListener mEventListener;

    public interface EventListener {
        /** 滑动固定项后回调 */
        void onItemPinned(int position);

        /** 滑动容器点击 */
        void onItemViewClicked(View v);

        /** 开始导航点击 */
        void onStartNavigation(View v);

        /** 已到达目的地点击 */
        void onSetPresetPoint(View v);

        /** 设置交接点点击 */
        void onReachDestination(View v);
    }

    /** 视图当前持有者对象 */
    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        public LinearLayout mContainer;
        public RelativeLayout mBehindViews;
        public TextView mHospital;
        public TextView mIndex;
        public TextView mType;
        public TextView mNavi;
        public TextView mSetPreset;
        public TextView mDestin;

        public MyViewHolder(View v) {
            super(v);
            // item_hospital_name | item_index | item_type
            mContainer = v.findViewById(R.id.container);
            mBehindViews = v.findViewById(R.id.behind_views);
            mNavi = v.findViewById(R.id.start_navigation);  // 导航
            mSetPreset = v.findViewById(R.id.set_preset_point);    // 已到达
            mDestin = v.findViewById(R.id.reach_destination);   // 交接点设置
            mHospital = v.findViewById(R.id.item_hospital_name);
            mIndex = v.findViewById(R.id.item_index);
            mType = v.findViewById(R.id.item_type);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

    }

    public SwipeableWithButtonPointAdapter(List<MapLogisticPoint> mItemList) {
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
    private void onSwipeableViewNaviClick(View v) {
        if (mEventListener != null) {
            mEventListener.onStartNavigation(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }
    private void onSwipeableViewPresetClick(View v) {
        if (mEventListener != null) {
            mEventListener.onSetPresetPoint(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }
    private void onSwipeableViewDestinClick(View v) {
        if (mEventListener != null) {
            mEventListener.onReachDestination(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getId().hashCode();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.kc_item_point, parent, false);
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

        // 赋值 | 控制文字显示类型
        holder.mHospital.setText(item.getHospitalName());
        String taskType = item.getTaskType().equals("2") ? "交接" : "普通";
        holder.mIndex.setText(taskType);
        String types = item.getType().equals("1") ? "医检" : "医院";

        if (TextUtils.isEmpty(item.getHospitalId())) {
            holder.mType.setVisibility(View.GONE);
            holder.setSwipeItemHorizontalSlideAmount(0);    // 非交接预设点 拖动自动返回
        }else holder.mType.setText(types);

        // 设置点击事件 | 按钮的显示以及隐藏
        holder.mContainer.setOnClickListener(view -> onSwipeableViewContainerClick(view));
        holder.mNavi.setOnClickListener(view -> onSwipeableViewNaviClick(view));
        holder.mSetPreset.setOnClickListener(view -> onSwipeableViewPresetClick(view)); // 已到达
        holder.mDestin.setOnClickListener(view -> onSwipeableViewDestinClick(view)); //预设点
        if (!item.getTaskType().equals("2")) holder.mDestin.setVisibility(View.GONE);
        if (TextUtils.isEmpty(item.getHospitalId())) {
            holder.mNavi.setVisibility(View.GONE);
            holder.mSetPreset.setVisibility(View.GONE);
        }
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
            holder.mBehindViews.setVisibility(View.GONE);
        } else {
            holder.mBehindViews.setVisibility(View.VISIBLE);
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
        private SwipeableWithButtonPointAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(SwipeableWithButtonPointAdapter adapter, int position) {
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
        private SwipeableWithButtonPointAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(SwipeableWithButtonPointAdapter adapter, int position) {
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
