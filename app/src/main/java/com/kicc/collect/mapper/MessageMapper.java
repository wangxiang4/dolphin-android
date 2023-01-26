package com.kicc.collect.mapper;

import com.kicc.collect.entity.MapLogistic;
import com.kicc.collect.entity.Message;
import com.kicc.core.http.api.ResultResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * <p> 消息API </p>
 * @Author: liuSiXiang
 * @since: 2022/11/18
 */
public interface MessageMapper {

    @GET("common_proxy/common/message/list")
    Observable<ResultResponse<List<Message>>> getMessageList(@Query("status") String status);

    @PUT("common_proxy/common/mapTask/confirmPresetPoint")
    Observable<ResultResponse> confirmPresetPoint(@Body Message message);

    @GET("common_proxy/common/mapTask/{id}")
    Observable<ResultResponse<MapLogistic>> getMapTaskById(@Path("id") String id);
}
