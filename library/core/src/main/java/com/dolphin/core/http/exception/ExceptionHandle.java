package com.dolphin.core.http.exception;

import android.net.ParseException;

import com.dolphin.core.util.ToastUtil;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.dolphin.core.constant.ExceptionCodeConstant;
import com.dolphin.core.http.HttpRequest;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.util.PermissionUtil;
import com.dolphin.core.util.RxUtil;

import org.json.JSONException;

import java.net.ConnectException;

import io.reactivex.Observable;
import retrofit2.HttpException;
import retrofit2.http.DELETE;

/**
 *<p>
 * 请求错误转换处理类
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class ExceptionHandle {

    public static ResponseException handleException(Throwable e) {
        // 请求码异常处理
        if (e instanceof HttpException) {
            String exceptionMsg;
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case 401:
                    exceptionMsg = "用户没有权限（令牌、用户名、密码错误）";
                    break;
                case 403:
                    exceptionMsg = "用户得到授权,但是访问是被禁止的!";
                    break;
                case 404:
                    exceptionMsg = "资源不存在";
                    break;
                case 405:
                    exceptionMsg = "操作异常,不允许的请求方法";
                    break;
                case 408:
                    exceptionMsg = "网络请求超时";
                    break;
                case 424:
                    exceptionMsg = "令牌过期,请重新登录!";
                    PermissionUtil.logout();
                    HttpRequest.getInstance().retrofit.create(LoginMapper.class)
                            .logout().compose(RxUtil.schedulersTransformer());
                    break;
                case 426:
                    exceptionMsg = "用户名或密码错误或者当前用户不存在";
                    break;
                case 428:
                    exceptionMsg = "验证码错误,请重新输入!";
                    break;
                case 429:
                    exceptionMsg = "请求过频繁";
                    break;
                case 500:
                    exceptionMsg = "服务器错误,请联系管理员!";
                    break;
                case 501:
                    exceptionMsg = "网络未实现";
                    break;
                case 502:
                    exceptionMsg = "网络错误";
                    break;
                case 503:
                    exceptionMsg = "服务器不可用";
                    break;
                case 504:
                    exceptionMsg = "网络超时";
                    break;
                case 505:
                    exceptionMsg = "http版本不支持该请求!";
                    break;
                default:
                    exceptionMsg = "操作异常,请联系系统管理员!";
                    break;
            }
            return new ResponseException(e, httpException.code(), exceptionMsg);
        // 根据错误类型扩展异常处理
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException || e instanceof MalformedJsonException) {
            return new ResponseException(e, ExceptionCodeConstant.PARSE_ERROR, "解析错误");
        } else if (e instanceof ConnectException) {
            return new ResponseException(e, ExceptionCodeConstant.NETWORK_ERROR, "连接失败");
        } else if (e instanceof javax.net.ssl.SSLException) {
            return new ResponseException(e, ExceptionCodeConstant.SSL_ERROR, "证书验证失败");
        } else if (e instanceof java.net.SocketTimeoutException) {
            return new ResponseException(e, ExceptionCodeConstant.TIMEOUT_ERROR, "连接超时");
        } else if (e instanceof java.net.UnknownHostException) {
            return new ResponseException(e, ExceptionCodeConstant.HTTP_ERROR, "主机地址未知");
        } else {
            return new ResponseException(e, ExceptionCodeConstant.UNKNOWN, "未知错误");
        }

    }

    interface LoginMapper {

        @DELETE("auth_proxy/token/logout")
        Observable<ResultResponse<Boolean>> logout();

    }

    public static void baseExceptionMsg (Throwable e){
        e.printStackTrace();
        if (e instanceof ResponseException) {
            ToastUtil.show(((ResponseException) e).message);
            return;
        }
        ToastUtil.show("未知错误,请联系系统管理员!");
    }
}

