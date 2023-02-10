package com.dolphin.demo.service.Impl;

import com.blankj.utilcode.util.Utils;
import com.dolphin.core.entity.OssFile;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.di.component.DaggerMapperComponent;
import com.dolphin.demo.mapper.MessageMapper;
import com.dolphin.demo.service.MessageService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 *<p>
 * 消息服务实现
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/2
 */
public class MessageServiceImpl implements MessageService {

    @Inject
    MessageMapper messageMapper;

    public MessageServiceImpl() {
        DaggerMapperComponent
                .builder()
                .appComponent(((AppApplication)Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public Observable<ResultResponse<List<OssFile>>> listMessage(Map<String, Object> fields) {
        return messageMapper.listMessage(fields);
    }

}
