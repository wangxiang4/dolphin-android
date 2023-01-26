package com.kicc.collect.mapper;

import com.kicc.collect.entity.MapLogistic;
import com.kicc.collect.entity.MapTask;
import com.kicc.core.entity.MapLogisticPoint;
import com.kicc.core.http.api.ResultResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 *<p>
 * 地图API
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/1
 */
public interface MapLogisticsMapper {

    @POST("common_proxy/common/mapLogistic/uploadGps")
    Observable<ResultResponse> uploadGps(@Body MapLogistic mapLogistic);

    @GET("common_proxy/common/mapLogistic/getMapDataByCourierUserId/{courierUserId}")
    Observable<ResultResponse<MapLogistic>> getMapDataByCourierUserId(@Path("courierUserId") String courierUserId);

    @Headers({
            "Content-Type: application/x-www-form-urlencoded; charset=utf-8"
    })
    @GET("common_proxy/common/mapTask/list")
    Observable<ResultResponse<List<MapTask>>> getMapTaskList(@Query("mapTaskPreset[0].courierUserId") String courierUserId);

    @PUT("common_proxy/common/mapTask/setPresetPoint")
    Observable<ResultResponse> setPresetPoint(@Body MapLogisticPoint mapLogisticPoint);
}
