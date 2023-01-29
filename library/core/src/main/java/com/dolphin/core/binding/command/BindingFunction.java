package com.dolphin.core.binding.command;

/**
 *<p>
 * 视图触发函数的命令调用
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/17
 */
public interface BindingFunction<T> {
    T call();
}
