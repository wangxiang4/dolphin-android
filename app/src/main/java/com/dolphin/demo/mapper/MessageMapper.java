package com.dolphin.demo.mapper;

import com.dolphin.core.entity.OssFile;
import com.dolphin.core.http.api.ResultResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 *<p>
 * 消息api
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/2
 */
public interface MessageMapper {

    /** todo: 目前消息模块未使用，拿oss文件模块做演示 */
    @GET("system_proxy/system/file/list")
    Observable<ResultResponse<List<OssFile>>> listMessage(@QueryMap Map<String, Object> fields);

}
