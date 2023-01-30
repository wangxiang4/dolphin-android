package com.dolphin.demo.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.di.component.DaggerServiceComponent;
import com.dolphin.core.bus.SingleLiveEvent;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/**
 *<p>
 *  消息vm
 *</p>
 *
 * @Author: liuSiXiang
 * @since: 2022/10/25
 */
public class TabBarMessageViewModel extends ToolbarViewModel{

    @Inject
    MessageService messageService;

    public SingleLiveEvent<List<Message>> itemDataList = new SingleLiveEvent();

    public TabBarMessageViewModel(@NonNull Application application) {
        super(application);
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    public void loadListData() {
        String status = "0";    // 所有消息
        messageService.getMessageList(status)
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showDialog("消息数据加载中..."))
                .subscribe(new DisposableObserver<ResultResponse<List<Message>>>() {
                    @Override
                    public void onNext(ResultResponse<List<Message>> R) {
                        if (R.getCode() == R.SUCCESS) {
                            itemDataList.setValue(ObjectUtils.isNotEmpty(R.getData()) ? R.getData() : new ArrayList());
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

    /** 修改状态 */
    public void sendStatus(Message item) {
        item.setStatus("1");
        messageService.confirmPresetPoint(item)
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showDialog("请求加载中..."))
                .subscribe(new DisposableObserver<ResultResponse>() {
                    @Override
                    public void onNext(ResultResponse R) {
                        if (R.getCode() == R.SUCCESS) {
                            LogUtils.i(R.getMsg());
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
