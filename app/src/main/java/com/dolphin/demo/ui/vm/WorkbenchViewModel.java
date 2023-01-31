package com.dolphin.demo.ui.vm;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

/**
 *<p>
 * 工作台视图模型层
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/1/30
 */
public class WorkbenchViewModel extends ToolbarViewModel{

    public WorkbenchViewModel(@NonNull Application application) {
       super(application);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("工作台");
    }

}
