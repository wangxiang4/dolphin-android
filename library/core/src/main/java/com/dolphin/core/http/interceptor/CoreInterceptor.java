package com.dolphin.core.http.interceptor;

import com.blankj.utilcode.util.StringUtils;
import com.dolphin.core.constant.AppConstant;
import com.tencent.mmkv.MMKV;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *<p>
 * 核心拦截器
 * 处理必要的核心请求拦截,列如token拦截,head头部数据处理等等
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class CoreInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        /** 自动添加token处理 */
        String accessToken = MMKV.defaultMMKV().getString(AppConstant.ACCESS_TOKEN_NAME,null);
        if (!StringUtils.isEmpty(accessToken)) {
            builder.header("Authorization", "Bearer " + accessToken);
        }
        return chain.proceed(builder.build());
    }

}