package com.kicc.core.bus;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *<p>
 * 一次性的可观察对象,在订阅后只发送新的更新
 * 这避免了事件的常见问题,在配置更改（如旋转）时,如果观察者处于活动状态,则可以发出更新
 * 如果显式调用 setValue() 或 call() 则此 LiveData 仅调用 observable
 * 使用java并发编程CAS比较最终只会有一个线程可以拿到进去收取到消息
 * 不了解CAS的可以参考我写的一篇CAS帖子: https://zhuanlan.zhihu.com/p/368399588
 * </p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/22
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @Override
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {

        if (hasActiveObservers()) {
            Log.w("SingleLiveEvent", "注册了多个观察者,但只有一个会收到更改通知");
        }

        // 观察内部的可变实时数据
        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }

    @Override
    @MainThread
    public void setValue(@Nullable T t) {
        mPending.set(true);
        super.setValue(t);
    }

    @MainThread
    public void call() {
        setValue(null);
    }
    
}
