package com.dolphin.demo.service.Impl;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.di.component.DaggerMapperComponent;
import com.dolphin.demo.entity.TokenEnhancer;
import com.dolphin.demo.entity.User;
import com.dolphin.demo.mapper.LoginMapper;
import com.dolphin.demo.service.LoginService;
import com.dolphin.core.constant.SecurityConstant;
import com.dolphin.core.entity.DolphinUser;
import com.dolphin.core.http.api.ResultResponse;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import io.reactivex.Observable;

/**
 *<p>
 * 登录服务实现
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/28
 */
public class LoginServiceImpl implements LoginService {

    @Inject
    LoginMapper loginMapper;

    public LoginServiceImpl() {
        DaggerMapperComponent
                .builder()
                .appComponent(((AppApplication)Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public Observable<TokenEnhancer> login(DolphinUser user) {
        // 处理ASE加密
        byte[] passwordSecret = EncryptUtils.encryptAES(
                user.getPassword().getBytes(Charset.forName("UTF-8")),
                new SecretKeySpec(SecurityConstant.GATEWAY_ASE_ENCODE_SECRET.getBytes(), "AES").getEncoded(),
                "AES/CFB/NoPadding",
                new IvParameterSpec(SecurityConstant.GATEWAY_ASE_ENCODE_SECRET.getBytes()).getIV());
        Map<String, String> params = new HashMap(0);
        params.put("grant_type", "password");
        params.put("scope", "server");
        params.put("username", user.getUsername());
        params.put("password", EncodeUtils.base64Encode2String(passwordSecret));
        return loginMapper.login(params);
    }

    @Override
    public Observable<ResultResponse> logout() {
        return loginMapper.logout();
    }

    @Override
    public Observable<ResultResponse<User>> getUserInfo() {
        return loginMapper.getUserInfo();
    }

}
