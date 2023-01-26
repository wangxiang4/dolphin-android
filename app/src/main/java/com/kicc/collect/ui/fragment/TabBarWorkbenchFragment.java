package com.kicc.collect.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.kicc.collect.BR;
import com.kicc.collect.R;
import com.kicc.collect.databinding.KcFragmentTabBarWorkbenchBinding;
import com.kicc.collect.ui.activity.CollectTaskListActivity;
import com.kicc.collect.ui.vm.TabBarWorkbenchViewModel;
import com.kicc.core.base.BaseFragment;

/**
 * 工作台附属页
 * Created on 2018/7/18.
 * @author liusixiang
 */

public class TabBarWorkbenchFragment extends BaseFragment<KcFragmentTabBarWorkbenchBinding, TabBarWorkbenchViewModel> {

    private Integer COLLECT_GONGING_LIST = 2;
    private Integer COLLECT_HISTORY_LIST = 4;
    public static String COLLECT_LIST_TYPE = "COLLECT_LIST_TYPE";

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.kc_fragment_tab_bar_workbench;
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
    public void onStart() {
        super.onStart();
        // 历史列表
        mViewModel.workUiObservable.collectHistorySwitchEvent.observe(this, clicks -> {
            Intent intent = new Intent(getActivity(), CollectTaskListActivity.class);
            Bundle pointBundle = new Bundle();
            pointBundle.putInt(COLLECT_LIST_TYPE, COLLECT_HISTORY_LIST);
            intent.putExtras(pointBundle);
            startActivity(intent);
        });
        mViewModel.workUiObservable.collectGoingSwitchEvent.observe(this, clicks -> {
            Intent intent = new Intent(getActivity(), CollectTaskListActivity.class);
            Bundle pointBundle = new Bundle();
            pointBundle.putInt(COLLECT_LIST_TYPE, COLLECT_GONGING_LIST);
            intent.putExtras(pointBundle);
            startActivity(intent);
        });
    }
}
