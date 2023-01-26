package com.kicc.collect.service;

import com.kicc.collect.entity.TokenEnhancer;
import com.kicc.collect.entity.User;
import com.kicc.core.entity.KiccUser;
import com.kicc.core.http.api.ResultResponse;

import io.reactivex.Observable;

/**
 *<p>
 * 登录服务
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/28
 */
public interface LoginService {

    /** 用户登录 */
    Observable<TokenEnhancer> login(KiccUser user);

    /** 用户登出 */
    Observable<ResultResponse> logout();

    /** 获取用户信息 */
    Observable<ResultResponse<User>> getUserInfo();

}
