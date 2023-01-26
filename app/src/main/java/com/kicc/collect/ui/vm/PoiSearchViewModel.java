package com.kicc.collect.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.kicc.collect.app.AppApplication;
import com.kicc.collect.di.component.DaggerServiceComponent;
import com.kicc.collect.service.MapLogisticsService;
import com.kicc.core.entity.MapLogisticPoint;
import com.kicc.core.http.api.ResultResponse;
import com.kicc.core.http.exception.ExceptionHandle;
import com.kicc.core.util.RxUtil;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/**
 * 路径搜索
 * @author liusixiang
 * @date 2022.9.21
 */
public class PoiSearchViewModel extends ToolbarViewModel {

    @Inject
    MapLogisticsService mapLogisticsService;

    public PoiSearchViewModel(@NonNull Application application) {
        super(application);
        // 注入依赖
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("定位预设点");
    }

    /**
     * 发送预设点请求
     * @param mapLogisticPoint  任务地点
     */
    public void setPresetRequest(MapLogisticPoint mapLogisticPoint) {
        LogUtils.d(mapLogisticPoint);
        mapLogisticsService.setPresetPoint(mapLogisticPoint)
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                // 生命周期同步,ViewModel销毁时会清除异步观测
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showDialog())
                .subscribe(new DisposableObserver<ResultResponse>(){

                    @Override
                    public void onNext(ResultResponse R) {
                        if (R.getCode() == R.SUCCESS) {
                            LogUtils.i(R.getMsg());
                            finish();
                        }else ToastUtils.showShort(R.getMsg());
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
