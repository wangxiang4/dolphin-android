package com.dolphin.demo.ui.vm;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.util.PermissionUtil;
import com.dolphin.core.util.RxUtil;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.constant.CacheConstant;
import com.dolphin.demo.di.component.DaggerServiceComponent;
import com.dolphin.demo.entity.User;
import com.dolphin.demo.service.LoginService;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/**
 *<p>
 * 我的视图模型层
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/1/30
 */
public class UserViewModel extends ToolbarViewModel {

    @Inject
    LoginService loginService;

    public User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR, new User());

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("我的");
    }

    public UserViewModel(@NonNull Application application) {
        super(application);
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication)Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    public void loginOut() {
        loginService.logout()
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .doOnSubscribe(disposable -> showDialog("退出中..."))
            .subscribe(new DisposableObserver<ResultResponse>() {
                @Override
                public void onNext(ResultResponse R) {
                    PermissionUtil.logout();
                }
                @Override
                public void onError(Throwable e) {
                    closeDialog();
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {
                    closeDialog();
                }
            });
    }

}
