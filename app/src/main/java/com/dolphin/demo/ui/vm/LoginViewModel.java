package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.base.BaseViewModel;
import com.dolphin.core.binding.command.BindingCommand;
import com.dolphin.core.bus.SingleLiveEvent;
import com.dolphin.core.constant.AppConstant;
import com.dolphin.core.entity.DolphinUser;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.util.RxUtil;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.constant.CacheConstant;
import com.dolphin.demo.di.component.DaggerServiceComponent;
import com.dolphin.demo.entity.TokenEnhancer;
import com.dolphin.demo.entity.User;
import com.dolphin.demo.service.LoginService;
import com.dolphin.demo.ui.activity.TabBarActivity;
import com.tencent.mmkv.MMKV;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/**
 *<p>
 * 登录视图模型层
 * 提供模板规范代码参考,请尽量保证编写代码风格跟模板规范代码一致
 * 采用 rxJava 可观测编写
 * Copyright © 2020-2022 <a href="http://www.entfrm.com/">entfrm</a> All rights reserved.
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
public class LoginViewModel extends BaseViewModel {

    @Inject
    LoginService loginService;

    /** 用户名称 */
    public ObservableField<String> username = new ObservableField("admin");

    /** 用户密码 */
    public ObservableField<String> password = new ObservableField("123456");

    /** 用户名操作图标可见 */
    public ObservableInt usernameVisible = new ObservableInt();

    /** 密码操作图标可见 */
    public ObservableInt passwordVisible = new ObservableInt();

    /** 密码可见切换 */
    public SingleLiveEvent<Boolean> passwordSwitchEvent = new SingleLiveEvent();

    /** 用户名值更改事件 */
    public BindingCommand usernameValueChangeCommand = new BindingCommand<String>(value -> {
        if(!StringUtils.isTrimEmpty(value)) {
            usernameVisible.set(View.VISIBLE);
        } else usernameVisible.set(View.INVISIBLE);
    });

    /** 用户名清除焦点修改事件 */
    public BindingCommand usernameFocusChangeCommand = new BindingCommand<Boolean>(hasFocus -> {
        if(hasFocus && !StringUtils.isTrimEmpty(username.get())) {
            usernameVisible.set(View.VISIBLE);
        } else usernameVisible.set(View.INVISIBLE);
    });

    /** 用户名清除点击事件 */
    public BindingCommand usernameCleanClickCommand = new BindingCommand(() -> {
        username.set("");
        if (!StringUtils.isEmpty(password.get())) password.set("");
    });

    /** 密码值更改事件 */
    public BindingCommand passwordValueChangeCommand = new BindingCommand<String>(value -> {
        if(!StringUtils.isTrimEmpty(value)) {
            passwordVisible.set(View.VISIBLE);
        } else passwordVisible.set(View.INVISIBLE);
    });

    /**  密码可见点击事件 */
    public BindingCommand passwordVisibleClickCommand = new BindingCommand(() ->
            passwordSwitchEvent.setValue(ObjectUtils.isEmpty(passwordSwitchEvent.getValue()) || !passwordSwitchEvent.getValue()));

    public LoginViewModel(@NonNull Application application) {
        super(application);
        // 注入服务组件
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication)Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    /** 登录按钮点击事件 */
    public BindingCommand loginClickCommand = new BindingCommand(() -> {
        if (StringUtils.isTrimEmpty(username.get())) {
            ToastUtil.show("请输入用户名称!");
            return;
        }
        if (StringUtils.isTrimEmpty(password.get())) {
            ToastUtil.show("请输入用户密码!");
            return;
        }
        // 请求采用 RxJava2CallAdapterFactory 订阅监听处理
        loginService.login(new DolphinUser().setUsername(username.get()).setPassword(password.get()))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .doOnSubscribe(disposable -> showDialog())
            .subscribe(new DisposableObserver<TokenEnhancer>() {
                @Override
                public void onNext(TokenEnhancer tokenEnhancer) {
                    MMKV.defaultMMKV().putString(AppConstant.ACCESS_TOKEN_NAME, tokenEnhancer.getAccess_token());
                    MMKV.defaultMMKV().putString(AppConstant.REFRESH_TOKEN_NAME, tokenEnhancer.getRefresh_token());
                    // 请求最好不要使用同步的会阻塞ui主线程,否则超过5秒卡顿会报ANR错误,如果非要创建同步请求,尽力控制在5秒
                    // 实现方法重写com.dolphin.core.http.HttpRequest的retrofit去掉addCallAdapterFactory,极力不推荐
                    // https://developer.android.com/topic/performance/vitals/anr?hl=zh-cn
                    loginService.getUserInfo()
                        .compose(RxUtil.schedulersTransformer())
                        .compose(RxUtil.exceptionTransformer())
                        .doOnSubscribe(LoginViewModel.this)
                        .subscribe(new DisposableObserver<ResultResponse<User>>() {
                            @Override
                            public void onNext(ResultResponse<User> R) {
                                if (R.getCode() == R.SUCCESS) {
                                    // 采用磁盘缓存存储数据,内部会根据Lru淘汰策略进行淘汰如果缓存满了会丢掉最后一个缓存对象
                                    User user = R.getData();
                                    if (StringUtils.isTrimEmpty(user.getAvatar())) user.setAvatar("/default.png");
                                    CacheDiskUtils.getInstance().put(CacheConstant.USER_INFO, user);
                                    MMKV.defaultMMKV().putString(CacheConstant.LOGIN_USERNAME, username.get());
                                    MMKV.defaultMMKV().putString(CacheConstant.LOGIN_PASSWORD, password.get());
                                } else ToastUtil.show(R.getMsg());
                            }
                            @Override
                            public void onError(Throwable e) {
                                loginService.logout().compose(RxUtil.schedulersTransformer()).doOnSubscribe(LoginViewModel.this);
                                ExceptionHandle.baseExceptionMsg(e);
                            }
                            @Override
                            public void onComplete() {
                                startActivity(TabBarActivity.class);
                                finish();
                            }
                        });
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
    });

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        // 记住用户名与密码
        if(!StringUtils.isTrimEmpty(MMKV.defaultMMKV().getString(AppConstant.ACCESS_TOKEN_NAME,null))){
            username.set(MMKV.defaultMMKV().getString(CacheConstant.LOGIN_USERNAME,null));
            password.set(MMKV.defaultMMKV().getString(CacheConstant.LOGIN_PASSWORD,null));
        }
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        super.onStart(owner);
        // 设置图标初始化可见默认值
        usernameVisible.set(View.INVISIBLE);
        passwordVisible.set(View.INVISIBLE);
        if(!StringUtils.isTrimEmpty(password.get())) passwordVisible.set(View.VISIBLE);
    }
}
