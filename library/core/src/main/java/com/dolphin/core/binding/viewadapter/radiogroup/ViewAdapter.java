package com.dolphin.core.binding.viewadapter.radiogroup;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;
import com.dolphin.core.binding.command.BindingCommand;

/**
 *<p>
 * RadioGroup 组件扩展绑定适配器配置
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class ViewAdapter {

    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void onCheckedChangedCommand(final RadioGroup radioGroup, final BindingCommand<String> bindingCommand) {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
            bindingCommand.execute(radioButton.getText().toString());
        });
    }

}
