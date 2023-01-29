package com.dolphin.demo.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CacheConstant;
import com.dolphin.demo.databinding.KcActivityCollectTaskListBinding;
import com.dolphin.demo.entity.MapTask;
import com.dolphin.demo.entity.User;
import com.dolphin.demo.ui.adapter.CollectTaskAdapter;
import com.dolphin.demo.ui.fragment.TabBarWorkbenchFragment;
import com.dolphin.demo.ui.vm.CollectTaskListViewModel;
import com.dolphin.core.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     收样任务列表
 * </p>
 * @Author: liuSiXiang
 * @since: 2022/11/15
 */
public class CollectTaskListActivity extends BaseActivity<KcActivityCollectTaskListBinding, CollectTaskListViewModel> {

    private Integer LIST_TYPE;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private List<MapTask> mDataList;

    User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);
    public String userIds = user.getId();


    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.kc_activity_collect_task_list;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = findViewById(R.id.task_recycler_view);
        LIST_TYPE =(Integer) getIntent().getIntExtra(TabBarWorkbenchFragment.COLLECT_LIST_TYPE,1);
        if (LIST_TYPE == 1) finish();
        initView();
        mViewModel.initToolbar(LIST_TYPE);
    }

    /** 组件初始化 */
    private void initView() {
        // 初始化滑动动作保护,防止解除滑动固定发生的抖动问题
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);
        // 初始化视图滚动管理器
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.mapTaskData.observe(this, mapLogistic -> {
            mDataList = mapLogistic;
            List<MapTask> needMapLogistic = new ArrayList<>();
            mDataList.forEach(item -> {
                // todo:筛选数据 0-今日数据 | 9-历史数据 | 当前收样员
                if (item.getCourierUserId().equals(userIds)) needMapLogistic.add(item);
            });
            final CollectTaskAdapter myItemAdapter = new CollectTaskAdapter(needMapLogistic);
            myItemAdapter.setEventListener(new CollectTaskAdapter.EventListener() {
                @Override
                public void onItemPinned(int position) {
                    // todo: 列表固定回调处理
                }

                @Override
                public void onItemViewClicked(View v) {
                    handleOnItemViewClicked(v);
                }

                @Override
                public void onCheckClick(View v) {
                    handleOnBtnCheckClicked(v);
                }
            });
            mAdapter = myItemAdapter;
            // 创建滑动回收适配器
            mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(myItemAdapter);
            final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
            // 禁用默认回收视图更改动画,以使滑动项的折回动画正常工作
            animator.setSupportsChangeAnimations(false);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            // 配置回收视图
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mWrappedAdapter);
            mRecyclerView.setItemAnimator(animator);
            // 配置滑动(优先级:触摸动作防护装置 > 滑动 > 拖放)
            mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
            mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        });
    }

    /** 处理点击项滑动视图取消固定 */
    private void handleOnItemViewClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        if (position != RecyclerView.NO_POSITION) {
            MapTask item = mDataList.get(position);
            if (item.getPinned()) { // 取消固定
                item.setPinned(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    /** 点击查看 */
    private void handleOnBtnCheckClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        MapTask item = mDataList.get(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager == null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }
        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
    }
}
