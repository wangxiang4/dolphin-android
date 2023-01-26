package com.kicc.collect.ui.vm;


import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

//import com.kicc.collect.ui.activity.PointActivity;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kicc.collect.ui.activity.CollectTaskListActivity;
import com.kicc.collect.ui.activity.PointActivity;
import com.kicc.core.binding.command.BindingAction;
import com.kicc.core.binding.command.BindingCommand;
import com.kicc.core.bus.SingleLiveEvent;

/**
 *  Create by lsx 2022.6.20
 *  工作台 model 层 这里需要继承 ToolbarViewModel 实体类
 *  @author liusixiang
 */
public class TabBarWorkbenchViewModel extends ToolbarViewModel{

    public TabBarWorkbenchViewModel(@NonNull Application application) {
       super(application);
    }

    public class WorkUiObservable {
        public SingleLiveEvent<Boolean> collectHistorySwitchEvent = new SingleLiveEvent<>();    // 收样历史列表
        public SingleLiveEvent<Boolean> collectGoingSwitchEvent = new SingleLiveEvent<>();
    }

    public WorkUiObservable workUiObservable = new WorkUiObservable();

    /** 收样列表 */
    public BindingCommand midIconCollectList = new BindingCommand(() -> {
        workUiObservable.collectGoingSwitchEvent.setValue(ObjectUtils.isEmpty(workUiObservable.collectGoingSwitchEvent.getValue()) || !workUiObservable.collectGoingSwitchEvent.getValue());
    });
    /** 收样历史 */
    public BindingCommand midIconCollectHistory = new BindingCommand(() -> {
        workUiObservable.collectHistorySwitchEvent.setValue(ObjectUtils.isEmpty(workUiObservable.collectHistorySwitchEvent.getValue()) || !workUiObservable.collectHistorySwitchEvent.getValue());
    });
    /** 历史任务 */
    public BindingCommand midIconOrther = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startActivity(PointActivity.class);
        }
    });
}
