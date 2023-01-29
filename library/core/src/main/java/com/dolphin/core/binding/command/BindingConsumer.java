package com.dolphin.core.binding.command;

/**
 *<p>
 * 视图触发带泛型参数的命令调用
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/17
 */
public interface BindingConsumer<T> {
    void call(T t);
}
