package com.dolphin.core.http.exception;


import com.tencent.bugly.crashreport.CrashReport;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 自定义响应异常类
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
@Data
@Accessors
public class ResponseException extends Exception {

    public int code;

    public String message;

    public ResponseException(Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
        System.err.println("-------------------------------");
        System.err.println("错误码: " + code+ ", 错误信息: "+ message);
        System.err.println("-------------------------------");
        // 上报错误到 Bugly 上
        CrashReport.postCatchedException(new IllegalArgumentException("错误码: " + code+ ", 错误信息: "+ message));
    }
}
