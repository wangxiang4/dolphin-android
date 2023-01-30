package com.dolphin.demo.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;

import com.dolphin.demo.R;
import com.dolphin.demo.databinding.KcActivityAppAboutBinding;
import com.dolphin.demo.ui.vm.AppAboutViewModel;
import com.dolphin.core.base.BaseActivity;

/**
 *<p>
 *  APP简介与帮助
 *</p>
 *
 * @Author: liuSiXiang
 * @since: 2022/10/24
 */
public class AppAboutActivity extends BaseActivity<ActivityAppAboutBinding, AppAboutViewModel> {
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
