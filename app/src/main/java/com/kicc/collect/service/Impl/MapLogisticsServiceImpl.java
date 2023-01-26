package com.kicc.collect.service.Impl;

import com.blankj.utilcode.util.Utils;
import com.kicc.collect.app.AppApplication;
import com.kicc.collect.di.component.DaggerMapperComponent;
import com.kicc.collect.entity.MapLogistic;
import com.kicc.collect.entity.MapTask;
import com.kicc.collect.mapper.MapLogisticsMapper;
import com.kicc.collect.service.MapLogisticsService;
import com.kicc.core.entity.MapLogisticPoint;
import com.kicc.core.http.api.ResultResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 *<p>
 * 地图服务实现
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/1
 */
public class MapLogisticsServiceImpl implements MapLogisticsService {

    @Inject
    MapLogisticsMapper mapLogisticsMapper;

    public MapLogisticsServiceImpl() {
        DaggerMapperComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public Observable<ResultResponse> uploadGps(MapLogistic mapLogistic) {
        return mapLogisticsMapper.uploadGps(mapLogistic);
    }

    @Override
    public Observable<ResultResponse<MapLogistic>> getMapDataByCourierUserId(String courierUserId) {
        return mapLogisticsMapper.getMapDataByCourierUserId(courierUserId);
    }

    @Override
    public Observable<ResultResponse<List<MapTask>>> getMapTaskList(String courierUserId) {
        return mapLogisticsMapper.getMapTaskList(courierUserId);
    }

    @Override
    public Observable<ResultResponse> setPresetPoint(MapLogisticPoint mapLogisticPoint) {
        return mapLogisticsMapper.setPresetPoint(mapLogisticPoint);
    }


}
