package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

import com.dolphin.core.binding.command.BindingCommand;
import com.dolphin.core.util.ToastUtil;

/***
 * 表单信息提交页
 * @author liusixiang
 */
public class FormViewModel extends ToolbarViewModel {
    public FormViewModel(@NonNull Application application) {
        super(application);
    }

    /** 顶部栏初始化 */
    public void initToolbar() {
        setRightIconVisible(View.VISIBLE);
        setTitleText("信 息 录 入");
    }

    public BindingCommand submitClickCommand = new BindingCommand(() -> {

        ToastUtil.show(getApplication(), "提交成功");
        finish();

    });

}
