package com.dolphin.core.http;

import com.dolphin.core.BuildConfig;
import com.dolphin.core.http.interceptor.CacheInterceptor;
import com.dolphin.core.http.interceptor.CoreInterceptor;
import com.dolphin.core.util.HttpsUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *<p>
 * http请求
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/19
 */
public class HttpRequest {

    /** 客户端配置 */
    interface ClientConfig {

        /** 超时时间(单位秒) */
        int DEFAULT_TIMEOUT = 20;

        /** 字节的缓存(10MB) */
        int CACHE_SIZE = 10 * 1024 * 1024;

    }

    private static volatile HttpRequest instance;

    public static HttpRequest getInstance(){
        if(instance == null){
            synchronized(HttpRequest.class){
                if(instance == null){
                    instance = new HttpRequest();
                }
            }
        }
        return instance;
    }

    /** 请求客户端 */
    public OkHttpClient okHttpClient;

    /** 网络请求 */
    public Retrofit retrofit;

    /** 初始化请求构造函数 */
    private HttpRequest() {
        HttpsUtil.SSLParams sslParams = HttpsUtil.getSslSocketFactory();
        okHttpClient = new OkHttpClient.Builder()
                // 缓存响应数据,开发环境关闭,生产环境打开
                //.cache(new Cache(new File(PathUtils.getInternalAppCachePath(), "dolphin_cache"), ClientConfig.CACHE_SIZE))
                .addInterceptor(new CoreInterceptor())
                .addInterceptor(new CacheInterceptor())
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .connectTimeout(ClientConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ClientConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(24, 15, TimeUnit.SECONDS))
                .build();
        // https://www.jianshu.com/p/308f3c54abdd
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                // 内部核心 TypeAdapter 转换原理: https://heapdump.cn/article/3537255
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.HOST_URL)
                .build();
    }

}
