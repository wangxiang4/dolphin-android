package com.kicc.collect.ui.vm;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.kicc.core.base.BaseViewModel;

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
