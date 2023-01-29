package com.dolphin.demo.service.Impl;

import com.blankj.utilcode.util.Utils;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.di.component.DaggerMapperComponent;
import com.dolphin.demo.entity.MapLogistic;
import com.dolphin.demo.entity.Message;
import com.dolphin.demo.mapper.MessageMapper;
import com.dolphin.demo.service.MessageService;
import com.dolphin.core.http.api.ResultResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * <p> 消息服务实现 </p>
 * @Author: liuSiXiang
 * @since: 2022/11/18
 */
public class MessageServiceImpl  implements MessageService {

    @Inject
    MessageMapper messageMapper;

    public MessageServiceImpl(){
        DaggerMapperComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public Observable<ResultResponse<List<Message>>> getMessageList(String status) {
        return messageMapper.getMessageList(status);
    }

    @Override
    public Observable<ResultResponse> confirmPresetPoint(Message message) {
        return messageMapper.confirmPresetPoint(message);
    }

    @Override
    public Observable<ResultResponse<MapLogistic>> getMapTaskById(String id) {
        return messageMapper.getMapTaskById(id);
    }
}
