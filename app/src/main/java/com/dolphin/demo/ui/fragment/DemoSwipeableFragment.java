package com.dolphin.demo.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentDemoBinding;
import com.dolphin.demo.ui.adapter.DemoSwipeableRecyclerAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;

/**
 *<p>
 * 可滑动列表
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class DemoSwipeableFragment extends BaseFragment<FragmentDemoBinding, ToolbarViewModel> implements DemoSwipeableRecyclerAdapter.EventListener {

    private RecyclerView mRecyclerView;
    private DemoSwipeableRecyclerAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_demo;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.setTitleText("可滑动列表");
        mRecyclerView = getView().findViewById(R.id.demo_recycler_view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mRecyclerView.getLayoutParams());
        params.setMargins(0, 0, 0, 0);
        mRecyclerView.setLayoutParams(params);
        List<DemoSwipeableRecyclerAdapter.Entity> list = CollectionUtils.newArrayList(
                new DemoSwipeableRecyclerAdapter.Entity().setCode("1").setTitle("这是第一条滑动数据"),
                new DemoSwipeableRecyclerAdapter.Entity().setCode("2").setTitle("这是第二条滑动数据"),
                new DemoSwipeableRecyclerAdapter.Entity().setCode("3").setTitle("这是第三条滑动数据")
        );

        // 初始化滑动动作保护,防止解除滑动固定发生的抖动问题
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // 初始化视图滚动管理器
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        final DemoSwipeableRecyclerAdapter swipeableAdapter = new DemoSwipeableRecyclerAdapter(list);
        swipeableAdapter.setEventListener(this);
        mAdapter = swipeableAdapter;
        // 创建滑动回收适配器
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(swipeableAdapter);
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        // 禁用默认回收视图更改动画,以使滑动项的折回动画正常工作
        animator.setSupportsChangeAnimations(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
        // 配置回收视图
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.icon_list_divider_h), true));

        // 配置滑动(优先级:触摸动作防护装置 > 滑动 > 拖放)
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
    }

    @Override
    public void onItemPinned(int position) {
        ToastUtil.show("滑动固定项回调处理");
    }

    @Override
    public void onItemViewClicked(View v) {
        ToastUtil.showCenter("滑动容器点击回调处理");
        int position = mRecyclerView.getChildAdapterPosition(v);
        DemoSwipeableRecyclerAdapter.Entity item = mAdapter.getData().get(position);
        if (position != RecyclerView.NO_POSITION) {
            if (item.getPinned()) {
                // 取消滑动固定
                item.setPinned(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onBtnSwipeable1(View v) {
        ToastUtil.showCenter("滑动按钮点击1回调处理");
        int position = mRecyclerView.getChildAdapterPosition(v);
        DemoSwipeableRecyclerAdapter.Entity item = mAdapter.getData().get(position);
        if (position != RecyclerView.NO_POSITION) {
            if (item.getPinned()) {
                // 取消滑动固定
                item.setPinned(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onBtnSwipeable2(View v) {
        ToastUtil.showCenter("滑动按钮点击2回调处理");
        int position = mRecyclerView.getChildAdapterPosition(v);
        DemoSwipeableRecyclerAdapter.Entity item = mAdapter.getData().get(position);
        if (position != RecyclerView.NO_POSITION) {
            if (item.getPinned()) {
                // 取消滑动固定
                item.setPinned(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onBtnSwipeable3(View v) {
        ToastUtil.showCenter("滑动按钮点击3回调处理");
        int position = mRecyclerView.getChildAdapterPosition(v);
        DemoSwipeableRecyclerAdapter.Entity item = mAdapter.getData().get(position);
        if (position != RecyclerView.NO_POSITION) {
            if (item.getPinned()) {
                // 取消滑动固定
                item.setPinned(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
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

