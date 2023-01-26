package com.kicc.collect.ui.vm;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.kicc.collect.app.AppApplication;
import com.kicc.collect.constant.CacheConstant;
import com.kicc.collect.di.component.DaggerServiceComponent;
import com.kicc.collect.entity.CollectTaskItem;
import com.kicc.collect.entity.MapTask;
import com.kicc.collect.entity.MapTaskPreset;
import com.kicc.collect.entity.User;
import com.kicc.collect.service.MapLogisticsService;
import com.kicc.core.bus.SingleLiveEvent;
import com.kicc.core.http.api.ResultResponse;
import com.kicc.core.http.exception.ExceptionHandle;
import com.kicc.core.util.RxUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.internal.observers.DisposableLambdaObserver;
import io.reactivex.observers.DisposableObserver;

/**
 * <p>
 *     收样任务列表
 *     左滑显示查看 查看表单活动
 * </p>
 * @Author: liuSiXiang
 * @since: 2022/11/15
 */
public class CollectTaskListViewModel extends ToolbarViewModel {

    @Inject
    MapLogisticsService mapLogisticsService;

    public SingleLiveEvent<List<MapTask>> mapTaskData = new SingleLiveEvent<>();

    User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);

    public String userIds = user.getId();

    public CollectTaskListViewModel(@NonNull Application application) {
        super(application);
        // 注入依赖
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    public void initToolbar(Integer type) {
        switch (type){
            case 2:
                setTitleText("今日采样任务");
            break;
            case 4:
                setTitleText("历史采样记录");
            break;
            default:
                LogUtils.d("跳转数据异常");
        }
        loadData(type);
    }

    private void loadData(Integer type) {
        mapLogisticsService.getMapTaskList(userIds)
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                // 生命周期同步,ViewModel销毁时会清除异步观测
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showDialog())
                .subscribe(new DisposableObserver<ResultResponse<List<MapTask>>>() {
                    @Override
                    public void onNext(ResultResponse<List<MapTask>> R) {
                        if (R.getCode() == R.SUCCESS) {
                            mapTaskData.setValue(ObjectUtils.isNotEmpty(R.getData()) ? R.getData() : new ArrayList());
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
