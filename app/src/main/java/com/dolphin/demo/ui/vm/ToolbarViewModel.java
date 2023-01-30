package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.dolphin.core.base.BaseViewModel;
import com.dolphin.core.binding.command.BindingCommand;

/**
 *<p>
 * 导航工具栏视图模型层
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/3
 */
public class ToolbarViewModel extends BaseViewModel {

    /**
     * 标题文字
     */
    public ObservableField<String> titleText = new ObservableField<>("");

    /**
     * 右边文字
     */
    public ObservableField<String> rightText = new ObservableField<>("更多");

    /**
     * 右边文字
     */
    public ObservableInt rightTextVisibleObservable = new ObservableInt(View.GONE);

    /**
     * 右边图标
     */
    public ObservableInt rightIconVisibleObservable = new ObservableInt(View.GONE);

    /**
     * 工具栏视图模型绑定
     */
    public ToolbarViewModel toolbarViewModel;

    public ToolbarViewModel(@NonNull Application application) {
        super(application);
        toolbarViewModel = this;
    }

    /** 设置标题 */
    public void setTitleText(String text) {
        titleText.set(text);
    }

    /** 设置右边文字 */
    public void setRightText(String text) {
        rightText.set(text);
    }

    /** 设置右边文字的可见 */
    public void setRightTextVisible(int visibility) {
        rightTextVisibleObservable.set(visibility);
    }

    /** 设置右边图标的可见 */
    public void setRightIconVisible(int visibility) {
        rightIconVisibleObservable.set(visibility);
    }

    /** 返回按钮的点击事件 */
    public final BindingCommand backOnClick = new BindingCommand(() -> finish());

    /** 右边文字点击事件 */
    public BindingCommand rightTextOnClick = new BindingCommand(() -> rightTextOnClick());

    /** 右边图标点击事件 */
    public BindingCommand rightIconOnClick = new BindingCommand(() -> rightIconOnClick());

    /** 右边文字的点击事件 */
    protected void rightTextOnClick() {
        System.out.println(" @Override Method ");
    }

    /** 右边图标的点击事件 */
    protected void rightIconOnClick() {
        System.out.println(" @Override Method ");
    }

}
