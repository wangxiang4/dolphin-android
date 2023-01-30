package com.dolphin.demo.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;

import com.dolphin.core.base.BaseActivity;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.ActivityAboutBinding;
import com.dolphin.demo.ui.vm.AboutViewModel;

/**
 *<p>
 *  关于
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2022/10/24
 */
public class AboutActivity extends BaseActivity<ActivityAboutBinding, AboutViewModel> {
    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.activity_about;
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
