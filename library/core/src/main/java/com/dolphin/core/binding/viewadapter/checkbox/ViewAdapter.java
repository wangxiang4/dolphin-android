package com.dolphin.core.binding.viewadapter.checkbox;

import android.widget.CheckBox;

import androidx.databinding.BindingAdapter;

import com.dolphin.core.binding.command.BindingCommand;

/**
 *<p>
 * CheckBox组件扩展绑定适配器配置
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class ViewAdapter {

    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void setCheckedChanged(final CheckBox checkBox, final BindingCommand<Boolean> bindingCommand) {
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> bindingCommand.execute(b));
    }

}
