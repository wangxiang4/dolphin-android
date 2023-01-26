package com.kicc.collect.service;

import com.kicc.collect.entity.MapLogistic;
import com.kicc.collect.entity.MapTask;
import com.kicc.core.entity.MapLogisticPoint;
import com.kicc.core.http.api.ResultResponse;

import java.util.List;

import io.reactivex.Observable;

/**
 *<p>
 * 地图服务
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/1
 */
public interface MapLogisticsService {

    /** 上传手机gps定位数据 */
    Observable<ResultResponse> uploadGps(MapLogistic mapLogistic);

    /** 获取当前收样员地图数据 */
    Observable<ResultResponse<MapLogistic>> getMapDataByCourierUserId(String courierUserId);

    /** 分页查询地图任务列表 */
    Observable<ResultResponse<List<MapTask>>> getMapTaskList(String courierUserId);

    /** 设置预设点 */
    Observable<ResultResponse> setPresetPoint(MapLogisticPoint mapLogisticPoint);
}
