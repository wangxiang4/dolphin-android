package com.dolphin.demo.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.KcActivityLoginBinding;
import com.dolphin.demo.ui.vm.LoginViewModel;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.constant.AppConstant;
import com.dolphin.core.util.PermissionUtil;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.umeng.UmengClient;
import com.dolphin.umeng.enums.PlatformEnum;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *<p>
 * 登录活动
 * 提供模板规范代码参考,请尽量保证编写代码风格跟模板规范代码一致
 * 采用 rxJava 可观测编写
 * Copyright © 2020-2022 <a href="http://www.entfrm.com/">entfrm</a> All rights reserved.
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
public class LoginActivity extends BaseActivity<KcActivityLoginBinding, LoginViewModel> {

    /** 请求权限检查 */
    private boolean requestPermissionNeedCheck = true;

    /** 软件申请动态权限,必须设置不设置不让进软件 */
    private String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    
    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.kc_activity_login;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!StringUtils.isTrimEmpty(MMKV.defaultMMKV().getString(AppConstant.ACCESS_TOKEN_NAME,null))){
            startActivity(TabBarActivity.class);
            finish();
        }
        mViewModel.passwordSwitchEvent.observe(this, visible -> {
            if (visible) {
                mView.ivSwitchPassword.setImageResource(R.drawable.kc_ic_password_hide);
                mView.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                mView.ivSwitchPassword.setImageResource(R.drawable.kc_ic_password_show);
                mView.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    /** 微信登录点击事件 */
    public void wechatLoginClick(View view) {
        if(!UmengClient.isAppInstalled(this, PlatformEnum.WECHAT)) {
            ToastUtil.show(this, "当前没有安装微信!");
            return;
        }
        UmengClient.login(this, PlatformEnum.WECHAT, (platformEnum, data) -> {
            if (ObjectUtils.isEmpty(data)) return;
            ToastUtils.showLong(new Gson().toJson(data));
        });
    }

    /** QQ登录点击事件 */
    public void qqLoginClick(View view) {
        if(!UmengClient.isAppInstalled(this, PlatformEnum.QQ)) {
            ToastUtil.show(this, "当前没有安装QQ!");
            return;
        }
        UmengClient.login(this, PlatformEnum.QQ, (platformEnum, data) -> {
            if (ObjectUtils.isEmpty(data)) return;
            ToastUtils.showLong(new Gson().toJson(data));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestPermissionNeedCheck) checkPermissions(needPermissions);
    }

    /** 检查软件所需要的权限,没有则弹出软件权限申请框申请 */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissionList = findDeniedPermissions(permissions);
        if (needRequestPermissionList.size() > 0) {
            requestPermissions(needRequestPermissionList.toArray(new String[needRequestPermissionList.size()]), AppConstant.PERMISSION_REQUEST_CODE);
        }
    }

    /** 查找被用户拒绝以及需要动态申请的权限 */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList();
        Arrays.stream(permissions).forEach(item -> {
            if (checkSelfPermission(item) != PackageManager.PERMISSION_GRANTED || shouldShowRequestPermissionRationale(item)) needRequestPermissionList.add(item);
        });
        return needRequestPermissionList;
    }

    @Override
    // https://www.cnblogs.com/ganchuanpu/p/6798682.html
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        super.onRequestPermissionsResult(requestCode, permissions, paramArrayOfInt);
        if (requestCode == AppConstant.PERMISSION_REQUEST_CODE) {
            if (Arrays.stream(paramArrayOfInt).anyMatch(item -> item != PackageManager.PERMISSION_GRANTED)) {
                requestPermissionNeedCheck = false;
                PermissionUtil.openApplicationSettings(this, "当前应用缺少必要权限。\n请点击设置-权限-打开所需权限!");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengClient.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

}
