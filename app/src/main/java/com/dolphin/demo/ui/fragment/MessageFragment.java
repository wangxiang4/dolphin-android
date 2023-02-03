package com.dolphin.demo.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.GsonUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentMessageBinding;
import com.dolphin.demo.entity.OssFile;
import com.dolphin.demo.ui.adapter.MessageRecyclerAdapter;
import com.dolphin.demo.ui.vm.MessageViewModel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import ezy.ui.layout.LoadingLayout;

/**
 *<p>
 * 消息
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class MessageFragment extends BaseFragment<FragmentMessageBinding, MessageViewModel> implements MessageRecyclerAdapter.EventListener {

    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RefreshLayout mRefreshLayout;
    public LoadingLayout mLoadingLayout;

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_message;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.mViewModel.mActivity = this;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView toolbarBack = getView().findViewById(R.id.iv_back);
        toolbarBack.setVisibility(View.INVISIBLE);
        mLoadingLayout = getView().findViewById(R.id.loading);
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        final MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter(mViewModel.listMessage);
        messageRecyclerAdapter.setEventListener(this);
        mAdapter = messageRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10;
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = getView().findViewById(R.id.refreshLayout);
        // 开启自动加载功能（非必须）
        mRefreshLayout.setEnableAutoLoadMore(true);
        mRefreshLayout.setOnRefreshListener(layout -> mViewModel.refreshListMessage(layout, this));
        mRefreshLayout.setOnLoadMoreListener(layout -> mViewModel.footerListMessage(layout, mAdapter));
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onItemViewClicked(OssFile ossFile) {
        ToastUtil.showCenter(GsonUtils.toJson(ossFile));
    }

}
