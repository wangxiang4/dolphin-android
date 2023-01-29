package com.dolphin.core.bus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/** 
 *<p>
 * 管理复合订阅观察者,防止内存泄露
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */ 
public class RxSubscriptions {

    private static CompositeDisposable mSubscriptions = new CompositeDisposable ();

    /** 是否解除订阅 */
    public static boolean isDisposed() {
        return mSubscriptions.isDisposed();
    }

    /** 添加指定观察者 */
    public static void add(Disposable s) {
        if (s != null) {
            mSubscriptions.add(s);
        }
    }

    /** 移除指定观察者 */
    public static void remove(Disposable s) {
        if (s != null) {
            mSubscriptions.remove(s);
        }
    }

    /** 清除并解除所有订阅 */
    public static void clear() {
        mSubscriptions.clear();
    }

}
