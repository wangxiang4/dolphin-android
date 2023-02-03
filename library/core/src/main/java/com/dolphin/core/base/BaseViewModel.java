package com.dolphin.core.base;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;

import com.dolphin.core.constant.AppConstant;
import com.dolphin.core.listener.UiObservable;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 *<p>
 * 视图模型
 * https://developer.android.com/reference/android/arch/lifecycle/AndroidViewModel
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/16
 */
public class BaseViewModel<A> extends AndroidViewModel implements IBaseViewModel, Consumer<Disposable> {

    /** 当前页码 */
    public Integer pageCurrent = 1;

    /** 页面大小 */
    public Integer pageSize = 10;

    /** 视图活动 */
    public A mActivity;

    /** ui处理订阅发布监听处理  */
    private UiObservable mUiObservable;

    /** 管理RxJava,针对RxJava异步操作造成的内存泄漏,用于一些请求跟vm周期同步 */
    private CompositeDisposable mViewModelLifeCycleSync = new CompositeDisposable();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public UiObservable getUiObservable() {
        if (mUiObservable == null) mUiObservable = new UiObservable();
        return mUiObservable;
    }

    protected void showDialog() {
        showDialog("加载中...");
    }

    protected void showDialog(String title) {
        mUiObservable.getShowDialogEvent().postValue(title);
    }

    protected void closeDialog() {
        mUiObservable.getCloseDialogEvent().call();
    }

    /**
     * 跳转页面
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Map<String, Object> params = new HashMap();
        params.put(AppConstant.ACTIVITY_CLASS, clz);
        if (bundle != null) {
            params.put(AppConstant.BUNDLE, bundle);
        }
        mUiObservable.getStartActivityEvent().postValue(params);
    }

    /**
     * 跳转容器活动页面
     * @param containerFragmentName 所跳转的目的Fragment名称
     */
    public void startFragmentContainerActivity(String containerFragmentName) {
        startFragmentContainerActivity(containerFragmentName, null);
    }

    /**
     * 跳转容器页面
     * @param containerFragmentName 所跳转的目的Fragment名称
     * @param bundle 跳转所携带的信息
     */
    public void startFragmentContainerActivity(String containerFragmentName, Bundle bundle) {
        Map<String, Object> params = new HashMap();
        params.put(AppConstant.CONTAINER_FRAGMENT_NAME, containerFragmentName);
        if (bundle != null) {
            params.put(AppConstant.BUNDLE, bundle);
        }
        mUiObservable.getStartFragmentContainerActivityEvent().postValue(params);
    }

    /** 关闭界面 */
    public void finish() {
        mUiObservable.getFinishEvent().call();
    }

    /** 返回上一层 */
    public void onBackPressed() {
        mUiObservable.getOnBackPressedEvent().call();
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        IBaseViewModel.super.onCreate(owner);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        IBaseViewModel.super.onStart(owner);
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        IBaseViewModel.super.onResume(owner);
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        IBaseViewModel.super.onPause(owner);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        IBaseViewModel.super.onStop(owner);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        IBaseViewModel.super.onDestroy(owner);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // ViewModel销毁时会执行,同时取消所有异步任务
        if (mViewModelLifeCycleSync != null) {
            mViewModelLifeCycleSync.clear();
        }
    }

    @Override
    public void accept(Disposable disposable) {
        mViewModelLifeCycleSync.add(disposable);
    }


}
