package com.kicc.core.binding.viewadapter.mswitch;

import android.widget.Switch;

import androidx.databinding.BindingAdapter;

import com.kicc.core.binding.command.BindingCommand;

/**
 *<p>
 * Switch 组件扩展绑定适配器配置
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class ViewAdapter {

    /**
     * 设置开关状态
     * @param mSwitch Switch控件
     */
    @BindingAdapter("switchState")
    public static void setSwitchState(Switch mSwitch, boolean isChecked) {
        mSwitch.setChecked(isChecked);
    }

    /**
     * Switch的状态改变监听
     * @param mSwitch Switch控件
     * @param changeListener 事件绑定命令
     */
    @BindingAdapter("onCheckedChangeCommand")
    public static void onCheckedChangeCommand(final Switch mSwitch, final BindingCommand<Boolean> changeListener) {
        if (changeListener != null) {
            mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> changeListener.execute(isChecked));
        }
    }

}
