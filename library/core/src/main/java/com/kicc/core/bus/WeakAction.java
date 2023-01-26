package com.kicc.core.bus;

import java.lang.ref.WeakReference;

import com.kicc.core.binding.command.BindingAction;
import com.kicc.core.binding.command.BindingConsumer;

/**
 *<p>
 * 全局组件通信触发命令调用
 * https://juejin.cn/post/7075893982953209887
 * https://github.com/Kelin-Hong/MVVMLight/blob/master/library/src/main/java/com/kelin/mvvmlight/messenger/WeakAction.java
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class WeakAction<T> {

    private BindingAction action;
    private BindingConsumer<T> consumer;
    private WeakReference reference;

    public WeakAction(Object target, BindingAction action) {
        reference = new WeakReference(target);
        this.action = action;

    }

    public WeakAction(Object target, BindingConsumer<T> consumer) {
        reference = new WeakReference(target);
        this.consumer = consumer;
    }

    public void execute() {
        if (action != null && isLive()) {
            action.call();
        }
    }

    public void execute(T parameter) {
        if (consumer != null
                && isLive()) {
            consumer.call(parameter);
        }
    }

    public void markForDeletion() {
        reference.clear();
        reference = null;
        action = null;
        consumer = null;
    }

    public BindingAction getBindingAction() {
        return action;
    }

    public BindingConsumer getBindingConsumer() {
        return consumer;
    }

    public boolean isLive() {
        if (reference == null) {
            return false;
        }
        if (reference.get() == null) {
            return false;
        }
        return true;
    }

    public Object getTarget() {
        if (reference != null) {
            return reference.get();
        }
        return null;
    }
}
