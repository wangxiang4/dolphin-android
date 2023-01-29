package com.dolphin.core.binding.viewadapter.view;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.jakewharton.rxbinding4.view.RxView;
import com.dolphin.core.binding.command.BindingCommand;

import java.util.concurrent.TimeUnit;

/**
 *<p>
 * 通用扩展绑定适配器配置
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class ViewAdapter {

    /** 防重复点击间隔(秒) */
    public static final int CLICK_INTERVAL = 1;

    /**
     * View的onClick事件绑定
     * onClickCommand 绑定的命令
     * isThrottleFirst 是否开启防止过快点击
     */
    @BindingAdapter(value = {"onClickCommand", "isThrottleFirst"}, requireAll = false)
    public static void onClickCommand(View view, final BindingCommand clickCommand, final boolean isThrottleFirst) {
        if (isThrottleFirst) {
            RxView.clicks(view).subscribe(object -> {
                if (clickCommand != null) {
                    clickCommand.execute();
                }
            });
        } else {
            RxView.clicks(view)
                // 1秒钟内只允许点击1次
                .throttleFirst(CLICK_INTERVAL, TimeUnit.SECONDS)
                .subscribe(object -> {
                    if (clickCommand != null) {
                        clickCommand.execute();
                    }
                });
        }
    }

    @BindingAdapter(value = {"onLongClickCommand"}, requireAll = false)
    public static void onLongClickCommand(View view, final BindingCommand clickCommand) {
        RxView.longClicks(view).subscribe(object -> {
            if (clickCommand != null) {
                clickCommand.execute();
            }
        });
    }

    /** 回调控件本身 */
    @BindingAdapter(value = {"currentView"}, requireAll = false)
    public static void replyCurrentView(View currentView, BindingCommand bindingCommand) {
        if (bindingCommand != null) {
            bindingCommand.execute(currentView);
        }
    }

    @BindingAdapter({"requestFocus"})
    public static void requestFocusCommand(View view, final Boolean needRequestFocus) {
        if (needRequestFocus) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        } else {
            view.clearFocus();
        }
    }

    @BindingAdapter({"onFocusChangeCommand"})
    public static void onFocusChangeCommand(View view, final BindingCommand<Boolean> onFocusChangeCommand) {
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (onFocusChangeCommand != null) {
                onFocusChangeCommand.execute(hasFocus);
            }
        });
    }

    @BindingAdapter(value = {"isVisible"}, requireAll = false)
    public static void isVisible(View view, final Boolean visibility) {
        if (visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

}
