package com.kicc.collect.service;

import com.kicc.collect.entity.MapLogistic;
import com.kicc.collect.entity.Message;
import com.kicc.core.http.api.ResultResponse;

import java.util.List;

import io.reactivex.Observable;

/**
 * <p> 消息服务 </p>
 * @Author: liuSiXiang
 * @since: 2022/11/18
 */
public interface MessageService {

    /** 获取消息分页数据 */
    Observable<ResultResponse<List<Message>>> getMessageList(String status);

    /** 消息确认 */
    Observable<ResultResponse> confirmPresetPoint(Message message);

    /** 通过任务id查询地图任务数据
     * @return*/
    Observable<ResultResponse<MapLogistic>> getMapTaskById(String id);
}
