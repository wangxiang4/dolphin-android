package com.dolphin.demo.service;

import com.dolphin.demo.entity.TokenEnhancer;
import com.dolphin.demo.entity.User;
import com.dolphin.core.entity.DolphinUser;
import com.dolphin.core.http.api.ResultResponse;

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
    Observable<TokenEnhancer> login(DolphinUser user);

    /** 用户登出 */
    Observable<ResultResponse> logout();

    /** 获取用户信息 */
    Observable<ResultResponse<User>> getUserInfo();

}
