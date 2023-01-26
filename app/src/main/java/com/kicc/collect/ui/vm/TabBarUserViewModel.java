package com.kicc.collect.ui.vm;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.kicc.collect.app.AppApplication;
import com.kicc.collect.constant.CacheConstant;
import com.kicc.collect.di.component.DaggerServiceComponent;
import com.kicc.collect.entity.TokenEnhancer;
import com.kicc.collect.entity.User;
import com.kicc.collect.service.LoginService;
import com.kicc.collect.ui.activity.AppAboutActivity;
import com.kicc.collect.ui.activity.PointActivity;
import com.kicc.collect.util.TimesUtil;
import com.kicc.core.base.BaseViewModel;
import com.kicc.core.binding.command.BindingAction;
import com.kicc.core.binding.command.BindingCommand;
import com.kicc.core.bus.SingleLiveEvent;
import com.kicc.core.http.api.ResultResponse;
import com.kicc.core.http.exception.ExceptionHandle;
import com.kicc.core.util.PermissionUtil;
import com.kicc.core.util.RxUtil;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by lsx on 2022.6.16
 * 个人页 viewModel 层
 */
public class TabBarUserViewModel extends BaseViewModel {

    @Inject
    LoginService loginService;

    /** 个人页界面监听 */
    public class UserUiObservable {
        /** 退出登录选项 */
        public SingleLiveEvent<Boolean> loginoutSwitchEvent = new SingleLiveEvent<>();
    }

    public UserUiObservable userUiObservable = new UserUiObservable();

    public ObservableField<String> username = new ObservableField("");

    public ObservableField<String> phoneNumber = new ObservableField("");

    User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        loadData();
    }

    public TabBarUserViewModel(@NonNull Application application) {
        super(application);
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication)Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    public void loadData() {
        LogUtils.i("控制台输出",user);
        String timeState = TimesUtil.getTimeState();
        username.set(timeState + user.getUserName());
        phoneNumber.set(user.getPhone());
    }
    /** 关于 */
    public BindingCommand aboutApp = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(AppAboutActivity.class);
        }
    });

    /** 退出登录 */
    public BindingCommand backLoginClickCommand = new BindingCommand(() -> {
        userUiObservable.loginoutSwitchEvent.setValue(ObjectUtils.isEmpty(userUiObservable.loginoutSwitchEvent.getValue()) || !userUiObservable.loginoutSwitchEvent.getValue());
    });

    public void loginOut() {
        loginService.logout()
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .doOnSubscribe(disposable -> showDialog("正在退出登录..."))
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
