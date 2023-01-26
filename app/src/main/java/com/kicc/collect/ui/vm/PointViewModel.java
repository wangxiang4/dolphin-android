package com.kicc.collect.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.kicc.collect.app.AppApplication;
import com.kicc.collect.constant.CacheConstant;
import com.kicc.collect.di.component.DaggerServiceComponent;
import com.kicc.collect.entity.MapLogistic;
import com.kicc.collect.entity.User;
import com.kicc.collect.service.MapLogisticsService;
import com.kicc.core.bus.SingleLiveEvent;
import com.kicc.core.http.api.ResultResponse;
import com.kicc.core.http.exception.ExceptionHandle;
import com.kicc.core.util.RxUtil;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/**
 * 任务行程逻辑层
 * @date 2022/9/7
 * @author liusixiang
 */
public class PointViewModel extends ToolbarViewModel {

    @Inject
    MapLogisticsService mapLogisticsService;

    public SingleLiveEvent<MapLogistic> mapLogistics = new SingleLiveEvent();

    public PointViewModel(@NonNull Application application) {
        super(application);
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication)Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("行程任务");
        initData();

    }

    /** 加载数据 */
    public void initData() {
        User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);
        mapLogisticsService.getMapDataByCourierUserId(user.getId())
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                // 生命周期同步,ViewModel销毁时会清除异步观测
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showDialog())
                .subscribe(new DisposableObserver<ResultResponse<MapLogistic>>() {
                    @Override
                    public void onNext(ResultResponse<MapLogistic>  R) {
                        if (R.getCode() == R.SUCCESS){
                            mapLogistics.setValue(R.getData());
                        } else ToastUtils.showShort(R.getMsg());
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
