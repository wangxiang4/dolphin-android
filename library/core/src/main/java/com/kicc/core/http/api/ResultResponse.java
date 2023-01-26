package com.kicc.core.http.api;

import java.io.Serializable;

/**
 *<p>
 * 结果响应信息
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/18
 */
public class ResultResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标记
     */
    public static Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static Integer FAIL = 500;

    /**
     * 未认证
     */
    public static Integer UNAUTH = 401;

    /**
     * 状态编码
     */
    private int code;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 结果集数量统计
     */
    private long total;

    /**
     * 结果集
     */
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResultResponse<T> ok() {
        return restResult(null, SUCCESS, "成功");
    }

    public static <T> ResultResponse<T> ok(T data) {
        return restResult(data, SUCCESS, "成功");
    }

    public static <T> ResultResponse<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> ResultResponse<T> ok(T data, long total) {
        return restData(data, SUCCESS, null, total);
    }

    public static <T> ResultResponse<T> error() {
        return restResult(null, FAIL, "失败");
    }

    public static <T> ResultResponse<T> error(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> ResultResponse<T> error(T data) {
        return restResult(data, FAIL, null);
    }

    public static <T> ResultResponse<T> error(T data, String msg) {
        return restResult(data, FAIL, msg);
    }

    public static <T> ResultResponse<T> unAuth(String msg) {
        return restResult(null, UNAUTH, msg);
    }


    private static <T> ResultResponse<T> restResult(T data, int code, String msg) {
        ResultResponse<T> apiResult = new ResultResponse<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    private static <T> ResultResponse<T> restData(T data, int code, String msg, long total) {
        ResultResponse<T> apiData = new ResultResponse<>();
        apiData.setCode(code);
        apiData.setMsg(msg);
        apiData.setTotal(total);
        apiData.setData(data);
        return apiData;
    }

}
