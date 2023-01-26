package com.kicc.core.constant;

/**
 *<p>
 * 异常扩展相应错误码
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
public interface ExceptionCodeConstant {

    /**
     * 未知错误
     */
    int UNKNOWN = 1000;

    /**
     * 解析错误
     */
    int PARSE_ERROR = 1001;

    /**
     * 网络错误
     */
    int NETWORK_ERROR = 1002;

    /**
     * 协议出错
     */
    int HTTP_ERROR = 1003;

    /**
     * 证书出错
     */
    int SSL_ERROR = 1005;

    /**
     * 连接超时
     */
    int TIMEOUT_ERROR = 1006;

}
