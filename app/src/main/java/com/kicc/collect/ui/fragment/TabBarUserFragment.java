package com.kicc.collect.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.kicc.collect.BR;
import com.kicc.collect.R;
import com.kicc.collect.databinding.KcFragmentTabBarUserBinding;
import com.kicc.collect.ui.vm.TabBarUserViewModel;
import com.kicc.core.base.BaseFragment;


public class TabBarUserFragment extends BaseFragment<KcFragmentTabBarUserBinding, TabBarUserViewModel>{

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.kc_fragment_tab_bar_user;
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
        mViewModel.userUiObservable.loginoutSwitchEvent.observe(this, clicks -> {
            /** 点击退出登录按钮 弹出提示确认框 */
            new MaterialDialog.Builder(getActivity())
                    .title("提 示")
                    .content(R.string.login_out)
                    .positiveText("确 认")
                    .negativeText("取 消")
                    .widgetColor(Color.RED)
                    .onPositive((dialog, which) -> {
                        /** 确认后发送退出请求 */
                        mViewModel.loginOut();
                    }).show();
        });
    }

    /**
     * 周期结束
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
