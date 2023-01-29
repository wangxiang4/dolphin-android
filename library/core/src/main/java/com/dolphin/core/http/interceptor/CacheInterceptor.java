package com.dolphin.core.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** 
 *<p>
 * 设置缓存拦截器
 * 设置客户端缓存请求,避免大量重复请求后台接口
 * 缓存请求60秒
 * 更多详细请参考: https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Cache-Control
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */ 
public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        // 设置缓存时间为60秒
        int maxAge = 60;
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build();
    }

}
