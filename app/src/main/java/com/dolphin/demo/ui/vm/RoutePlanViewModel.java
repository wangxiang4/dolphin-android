package com.dolphin.demo.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

/**
 *<p>
 * 路线规划视图模型层
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/9
 */
public class RoutePlanViewModel extends ToolbarViewModel {


    public RoutePlanViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("路线规划");
    }

}
