package com.dolphin.demo.ui.fragment;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.core.widget.DefaultItemDecoration;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentDemoBinding;
import com.dolphin.demo.ui.adapter.DemoDraggableRecyclerAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;
import com.google.gson.Gson;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;
import java.util.UUID;

/**
 *<p>
 * 可拖拽列表
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/12
 */
public class DemoDraggableFragment extends BaseFragment<FragmentDemoBinding, ToolbarViewModel> implements DemoDraggableRecyclerAdapter.EventListener {

    private RecyclerView mRecyclerView;
    private DemoDraggableRecyclerAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

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
        mViewModel.setTitleText("可拖拽列表");
        mRecyclerView = getView().findViewById(R.id.demo_recycler_view);
        List<DemoDraggableRecyclerAdapter.Entity> list = CollectionUtils.newArrayList(
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第1条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第2条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第3条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第4条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第5条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第6条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第7条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第8条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第9条数据哦"),
                new DemoDraggableRecyclerAdapter.Entity().setCode(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setTitle("嘿嘿悄悄告诉你我是第10条数据哦")
        );
        final DemoDraggableRecyclerAdapter demoDraggableRecyclerAdapter = new DemoDraggableRecyclerAdapter(list);
        demoDraggableRecyclerAdapter.setEventListener(this);
        mAdapter = demoDraggableRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        // 用于拖动的换行
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);
        final GeneralItemAnimator animator = new DraggableItemAnimator();
        // 配置回收视图
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(getActivity(), R.color.common_divider_color)));
        // 附加回收视图
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow_z3));
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onItemViewClicked(DemoDraggableRecyclerAdapter.Entity entity) {
        ToastUtil.showCenter(new Gson().toJson(entity));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
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
