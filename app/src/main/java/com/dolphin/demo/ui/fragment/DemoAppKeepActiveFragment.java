package com.dolphin.demo.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.constant.AppConstant;
import com.dolphin.core.service.AppKeepActive;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.core.widget.DefaultItemDecoration;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentDemoBinding;
import com.dolphin.demo.ui.activity.TabBarActivity;
import com.dolphin.demo.ui.adapter.DemoRecyclerAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;

import java.util.List;

/**
 *<p>
 * 应用后台持续活跃
 * 支持熄屏，app退入后台，保证后台任务不被杀死
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/7
 */
public class DemoAppKeepActiveFragment extends BaseFragment<FragmentDemoBinding, ToolbarViewModel> implements DemoRecyclerAdapter.EventListener {

    private RecyclerView mRecyclerView;
    private DemoRecyclerAdapter mAdapter;
    private AppKeepActive appKeepActive;
    /** 持续保持活跃任务广播 */
    private BroadcastReceiver backgroundKeepActiveTask;

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
        appKeepActive = new AppKeepActive(TabBarActivity.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.setTitleText("应用后台持续活跃");
        mRecyclerView = getView().findViewById(R.id.demo_recycler_view);
        List<DemoRecyclerAdapter.Entity> list = CollectionUtils.newArrayList(
                new DemoRecyclerAdapter.Entity().setCode("1").setTitle("启动后台免杀持续活跃"),
                new DemoRecyclerAdapter.Entity().setCode("2").setTitle("停止后台免杀持续活跃")
        );
        final DemoRecyclerAdapter demoRecyclerAdapter = new DemoRecyclerAdapter(list);
        demoRecyclerAdapter.setEventListener(this);
        mAdapter = demoRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(requireContext(), R.color.common_divider_color)));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemViewClicked(DemoRecyclerAdapter.Entity entity) {
        switch (entity.code) {
            case "1":
                registerReceiver();
                appKeepActive.registerService();
                ToastUtil.showCenter("启动成功");
                break;
            case "2":
                unregisterReceiver();
                appKeepActive.unregisterService();
                ToastUtil.showCenter("停止成功");
                break;
        }
    }

    public void registerReceiver () {
        if (null == backgroundKeepActiveTask) {
            backgroundKeepActiveTask = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(AppConstant.BACKGROUND_KEEP_ACTIVE_TASK_SCHEDULING)) {
                        ToastUtil.show("APP进入后台执行任务逻辑!");
                        LogUtils.i("APP进入后台执行任务逻辑!");
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.BACKGROUND_KEEP_ACTIVE_TASK_SCHEDULING);
        getActivity().registerReceiver(backgroundKeepActiveTask, filter);
    }

    public void unregisterReceiver() {
        if (null != backgroundKeepActiveTask) getActivity().unregisterReceiver(backgroundKeepActiveTask);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
