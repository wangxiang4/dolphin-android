package com.kicc.core.listener;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.kicc.core.bus.SingleLiveEvent;

import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * ui(活动跟碎片)处理订阅发布监听处理
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/16
 */
@Data
@Accessors
public class UiObservable extends SingleLiveEvent {

    /** 显示弹出框 */
    private SingleLiveEvent<String> showDialogEvent = new SingleLiveEvent();

    /** 关闭弹出框 */
    private SingleLiveEvent<Void> closeDialogEvent = new SingleLiveEvent();

    /** 进入活动 */
    private SingleLiveEvent<Map<String, Object>> startActivityEvent = new SingleLiveEvent();

    /** 进入碎片容器活动 */
    private SingleLiveEvent<Map<String, Object>> startFragmentContainerActivityEvent = new SingleLiveEvent();

    /** 关闭界面 */
    private SingleLiveEvent<Void> finishEvent = new SingleLiveEvent();

    /** 返回上一层 */
    private SingleLiveEvent<Void> onBackPressedEvent = new SingleLiveEvent();

    @Override
    public void observe(LifecycleOwner owner, Observer observer) {
        super.observe(owner, observer);
    }

}
