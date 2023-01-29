package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.di.component.DaggerServiceComponent;
import com.dolphin.demo.entity.MapTask;
import com.dolphin.demo.service.MessageService;
import com.dolphin.core.binding.command.BindingCommand;
import com.dolphin.core.bus.SingleLiveEvent;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.util.RxUtil;
import com.dolphin.core.util.ToastUtil;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/***
 * 任务表单信息
 * @author liusixiang
 */
public class TaskFormViewModel extends ToolbarViewModel {

    @Inject
    MessageService messageService;

    public SingleLiveEvent<MapTask> mapTask = new SingleLiveEvent<>();

    public TaskFormViewModel(@NonNull Application application) {
        super(application);
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        setTitleText("信 息 录 入");
        super.onCreate(owner);
    }

    /** 提交按钮 */
    public BindingCommand submitClickCommand = new BindingCommand(() -> {
        ToastUtil.show(getApplication(), "提交成功");
        finish();
    });

    /** 加载任务数据
     * @param id*/
    public void loadTaskData(String id) {
        if (TextUtils.isEmpty(id)){
            ToastUtils.showShort("数据加载失败");
            return;
        }
        messageService.getMapTaskById(id)
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                .doOnSubscribe(this)
                .doOnSubscribe(disposable -> showDialog("数据加载中..."))
                .subscribe(new DisposableObserver<ResultResponse<MapTask>>() {
                    @Override
                    public void onNext(ResultResponse<MapTask> R) {
                        if (R.getCode() == R.SUCCESS) {
                            mapTask.setValue(R.getData());
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
