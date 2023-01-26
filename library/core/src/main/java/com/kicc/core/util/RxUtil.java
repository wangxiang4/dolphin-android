package com.kicc.core.util;

import com.kicc.core.http.exception.ExceptionHandle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.experimental.UtilityClass;

/**
 *<p>
 * Reactive Extensions 响应式工具类
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
@UtilityClass
public class RxUtil {

    /** 主线程调度转换器 */
    public ObservableTransformer schedulersTransformer() {
        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /** 异常转换器 */
    public ObservableTransformer exceptionTransformer() {
        return observable -> observable.onErrorResumeNext((Function<Throwable, ObservableSource>) throwable ->
                Observable.error(ExceptionHandle.handleException(throwable)));
    }

}
