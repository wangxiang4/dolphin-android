package com.kicc.collect.ui.vm;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

/**
 *<p>
 *  APP关于与帮助
 *</p>
 *
 * @Author: liuSiXiang
 * @since: 2022/10/24
 */
public class AppAboutViewModel extends ToolbarViewModel {

    public AppAboutViewModel(@NonNull Application application) {
        super(application);
        super.setTitleText("关于APP");
    }
}
