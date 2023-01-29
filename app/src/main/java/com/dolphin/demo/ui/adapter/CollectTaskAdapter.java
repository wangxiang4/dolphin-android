package com.dolphin.demo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.dolphin.demo.R;
import com.dolphin.demo.entity.MapTask;

import java.util.List;

/**
 * <p> 回收视图左右滑动帮助类 </p>
 * @Author: liuSiXiang
 * @since: 2022/11/15
 */
public class CollectTaskAdapter extends RecyclerView.Adapter<CollectTaskAdapter.MyViewHolder>
        implements SwipeableItemAdapter<CollectTaskAdapter.MyViewHolder> {

    /** 视图数据集合 */
    private List<MapTask> dataList;

    /** 回调事件 */
    private EventListener mEventListener;

    public interface EventListener {
        /** 滑动固定项后回调 */
        void onItemPinned(int position);

        /** 查看当前行信息 */
        void onCheckClick(View v);

        /** 滑动容器点击 */
        void onItemViewClicked(View v);
    }


    public static class MyViewHolder extends AbstractSwipeableItemViewHolder{

        public RelativeLayout mContain;
        public RelativeLayout mBehindView;
        public TextView btn_check;
        public TextView mHospital;
        public TextView mTaskName;
        public TextView mTaskType;
        public TextView mCreateName;
        public TextView mCreateTime;

        public MyViewHolder(@NonNull View v) {
            super(v);
            mContain = v.findViewById(R.id.show_relative);   // 可滑动的单列
            mBehindView = v.findViewById(R.id.behind_view); // 隐藏的view
            // 查看按钮点击事件
            btn_check = v.findViewById(R.id.btn_check);
            mHospital = v.findViewById(R.id.tv_task_hospital);   // 医院名
            mTaskName = v.findViewById(R.id.tv_task_name);  // 任务名
            mTaskType = v.findViewById(R.id.tv_task_type);  // 任务类型
            mCreateName = v.findViewById(R.id.tv_create_name);  // 创建人
            mCreateTime = v.findViewById(R.id.tv_create_time);  // 创建时间
        }

        @NonNull
        @Override
        public View getSwipeableContainerView() {
            return mContain;
        }

    }
    public CollectTaskAdapter(List<MapTask> mDataList){
        this.dataList = mDataList;
        setHasStableIds(true);
    }

    /** 滑动后点击单列复原 */
    private void onSwipeableViewContainerClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    /** 查看按钮点击事件 */
    private void onSwipeableViewBtnCheckClick(View v) {
        if (mEventListener != null) {
            mEventListener.onCheckClick(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getId().hashCode();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public CollectTaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.kc_item_collect_task, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectTaskAdapter.MyViewHolder holder, int position) {
        final MapTask item = dataList.get(position);
        // todo:赋值

        holder.mHospital.setText(item.getHospitalName());
        holder.mTaskName.setText(item.getName());
        holder.mTaskType.setText(item.getTaskType().equals("2") ? "交接" : "普通" );
        holder.mCreateTime.setText(item.getCreateTime());
        holder.mCreateName.setText(item.getCreateByName());
        // 设置点击事件
        holder.mContain.setOnClickListener(view -> onSwipeableViewContainerClick(view));
        holder.btn_check.setOnClickListener(view -> onSwipeableViewBtnCheckClick(view));

        // 设置偏移量
        holder.setMaxLeftSwipeAmount(-0.23f);
        holder.setMaxRightSwipeAmount(0);

        // 设置水平滑动的量
        holder.setSwipeItemHorizontalSlideAmount(item.getPinned() ? -0.23f : 0);
    }

    @Override
    public int onGetSwipeReactionType(@NonNull CollectTaskAdapter.MyViewHolder holder, int position, int x, int y) {
        final int tx = (int) (holder.getSwipeableContainerView().getTranslationX() + 0.23f);
        final int ty = (int) (holder.getSwipeableContainerView().getTranslationY() + 0.23f);
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
    public void onSwipeItemStarted(@NonNull CollectTaskAdapter.MyViewHolder holder, int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onSetSwipeBackground(@NonNull CollectTaskAdapter.MyViewHolder holder, int position, int type) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
            holder.mBehindView.setVisibility(View.GONE);
        } else {
            holder.mBehindView.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public SwipeResultAction onSwipeItem(@NonNull CollectTaskAdapter.MyViewHolder holder, int position, int result) {
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

    /** 向左滑动 */
    private class SwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private CollectTaskAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftResultAction(CollectTaskAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {  // 修改数据集
            super.onPerformAction();
            MapTask item = mAdapter.dataList.get(mPosition);
            // 检测是否已经处于滑动完毕
            if (!item.getPinned()) {
                item.setPinned(true);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {  // 调用完成后销毁
            super.onSlideAnimationEnd();
            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // 清除引用
            mAdapter = null;
        }
    }

    /** 关闭左划 */
    private class UnpinResultAction extends SwipeResultActionDefault {

        private CollectTaskAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(CollectTaskAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            MapTask item = mAdapter.dataList.get(mPosition);
            if (item.getPinned()) { // 检测是否已经处于滑动完毕
                item.setPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            mAdapter = null;
        }
    }
}

