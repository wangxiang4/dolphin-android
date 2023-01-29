package com.dolphin.demo.mapper;

import com.dolphin.demo.entity.TokenEnhancer;
import com.dolphin.demo.entity.User;
import com.dolphin.core.http.api.ResultResponse;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 *<p>
 * 登录api
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/28
 */
public interface LoginMapper {

    @POST("auth_proxy/oauth/token")
    @FormUrlEncoded
    @Headers({
        "Content-Type: application/x-www-form-urlencoded; charset=utf-8",
        "Authorization: Basic dGVzdDp0ZXN0"
    })
    Observable<TokenEnhancer> login(@FieldMap Map<String, String> fields);

    @DELETE("auth_proxy/token/logout")
    Observable<ResultResponse> logout();

    @GET("system_proxy/system/user/info")
    Observable<ResultResponse<User>> getUserInfo();

}
