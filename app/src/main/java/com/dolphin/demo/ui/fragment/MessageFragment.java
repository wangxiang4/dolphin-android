package com.dolphin.demo.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.entity.OssFile;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentMessageBinding;
import com.dolphin.demo.ui.adapter.MessageRecyclerAdapter;
import com.dolphin.demo.ui.vm.MessageViewModel;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
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
    public MessageRecyclerAdapter mAdapter;
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.mViewModel.mActivity = this;
        ImageView toolbarBack = getView().findViewById(R.id.iv_back);
        toolbarBack.setVisibility(View.INVISIBLE);

        mRefreshLayout = getView().findViewById(R.id.refreshLayout);
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        mLoadingLayout = getView().findViewById(R.id.loading);

        final MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter(CollectionUtils.newArrayList());
        messageRecyclerAdapter.setEventListener(this);
        mAdapter = messageRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.icon_list_divider_h), true));
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout.setOnRefreshListener(mViewModel::refresh);
        mRefreshLayout.setOnLoadMoreListener(mViewModel::loadMore);
        mRefreshLayout.setEnableLoadMore(false);
        mLoadingLayout.showContent();
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void onItemViewClicked(OssFile ossFile) {
        ToastUtil.showCenter(GsonUtils.toJson(ossFile));
    }

}
