package com.dolphin.demo.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

/**
 *<p>
 *  关于
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2022/10/24
 */
public class AboutViewModel extends ToolbarViewModel {

    public AboutViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("关于");
    }

}
