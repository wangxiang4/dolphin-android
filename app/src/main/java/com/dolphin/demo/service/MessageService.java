package com.dolphin.demo.service;

import com.dolphin.core.entity.OssFile;
import com.dolphin.core.http.api.ResultResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 *<p>
 * 消息服务
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/2
 */
public interface MessageService {

    /** 查询消息列表 */
    Observable<ResultResponse<List<OssFile>>> listMessage(Map<String, Object> fields);

}
