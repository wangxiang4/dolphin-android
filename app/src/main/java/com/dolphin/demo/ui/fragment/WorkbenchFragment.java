package com.dolphin.demo.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.bus.RxBus;
import com.dolphin.core.bus.RxSubscriptions;
import com.dolphin.core.util.RxUtil;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentWorkbenchBinding;
import com.dolphin.demo.ui.vm.WorkbenchViewModel;
import com.dolphin.umeng.entity.CustomMsgDemo;
import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;

/**
 *<p>
 * 工作台
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class WorkbenchFragment extends BaseFragment<FragmentWorkbenchBinding, WorkbenchViewModel> {

    private LinearLayout btnOa1;
    private LinearLayout btnOa2;
    private LinearLayout btnOa3;
    private LinearLayout btnOa4;
    private Disposable mSubscription;

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_workbench;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscription = RxBus.getInstance().toObservableSticky(CustomMsgDemo.class)
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                .subscribe(msg -> {
                    ToastUtil.show(new Gson().toJson(msg));
                    LogUtils.i(msg);
                });
        RxSubscriptions.add(mSubscription);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView toolbarBack = getView().findViewById(R.id.iv_back);
        toolbarBack.setVisibility(View.INVISIBLE);
        btnOa1 = getView().findViewById(R.id.btn_oa1);
        btnOa2 = getView().findViewById(R.id.btn_oa2);
        btnOa3 = getView().findViewById(R.id.btn_oa3);
        btnOa4 = getView().findViewById(R.id.btn_oa4);
        btnOa1.setOnClickListener(v -> {
            ToastUtil.showCenter("你刚刚点击了OA办公");
        });
        btnOa2.setOnClickListener(v -> {
            ToastUtil.showTop("你刚刚点击了加班申请");
        });
        btnOa3.setOnClickListener(v -> {
            ToastUtil.showBottom("你刚刚点击了辞职申请");
        });
        btnOa4.setOnClickListener(v -> {
            ToastUtil.show("你刚刚点击了死亡证明");
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(mSubscription);
    }

}
