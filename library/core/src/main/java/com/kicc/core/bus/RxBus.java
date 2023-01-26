package com.kicc.core.bus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 *<p>
 * 全局组件通信
 * 基于rxjava PublishSubject发布主题类实现监听发布订阅
 * https://www.jianshu.com/p/d8bcbaffcd3f
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class RxBus {

    private static volatile RxBus mInstance;

    private final Subject<Object> mBus;

    /** 保存发布事件的数据对象,让发布后的订阅者也能收接受到数据 */
    private final Map<Class<?>, Object> mStickyEventMap;

    public RxBus() {
        // 创建发布主题
        mBus = PublishSubject.create().toSerialized();
        // 创建粘性事件
        mStickyEventMap = new ConcurrentHashMap();
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /** 发布事件 */
    public void post(Object event) {
        mBus.onNext(event);
    }

    /**
     * 根据传递的发送事件类型返回特定类型的rx观察者对象
     * @param eventType 发送事件类型
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mBus.ofType(eventType);
    }

    /** 判断是否有订阅者 */
    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    /** 重置rx总线实例 */
    public void reset() {
        mInstance = null;
    }

    /** 发送一个新Sticky事件 */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    /**
     * 根据传递的发送事件类型返回特定类型的rx观察者粘性对象
     * @param eventType 发送事件类型
     */
    public <T> Observable<T> toObservableSticky(final Class<T> eventType) {
        synchronized (mStickyEventMap) {
            Observable<T> observable = mBus.ofType(eventType);
            final Object event = mStickyEventMap.get(eventType);

            if (event != null) {
                return Observable.merge(observable, Observable.create(emitter -> emitter.onNext(eventType.cast(event))));
            } else {
                return observable;
            }
        }
    }

    /** 根据发送事件类型获取粘性事件 */
    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /** 移除指定发送事件类型的粘性事件 */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /** 移除所有的粘性事件 */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }
}