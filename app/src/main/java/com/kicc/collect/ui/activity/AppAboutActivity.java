package com.kicc.collect.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;

import com.kicc.collect.R;
import com.kicc.collect.databinding.KcActivityAppAboutBinding;
import com.kicc.collect.ui.vm.AppAboutViewModel;
import com.kicc.core.base.BaseActivity;

/**
 *<p>
 *  APP简介与帮助
 *</p>
 *
 * @Author: liuSiXiang
 * @since: 2022/10/24
 */
public class AppAboutActivity extends BaseActivity<KcActivityAppAboutBinding, AppAboutViewModel> {
    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.kc_activity_app_about;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
