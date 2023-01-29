package com.dolphin.core.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.base.AppManager;
import com.dolphin.core.constant.AppConstant;
import com.tencent.mmkv.MMKV;

import lombok.experimental.UtilityClass;

/**
 *<p>
 * 应用权限权限工具
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/26
 */
@UtilityClass
public class PermissionUtil {

    /** 强制打开应用设置 */
    public void openApplicationSettings(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", (dialog, which) -> AppManager.getAppManager().finishAllActivity())
                .setPositiveButton("设置", (dialog, which) -> PermissionUtils.launchAppDetailsSettings())
                .setCancelable(false)
                .show();
    }

    /** 用户登出 */
    public void logout() {
        MMKV.defaultMMKV().remove(AppConstant.ACCESS_TOKEN_NAME);
        MMKV.defaultMMKV().remove(AppConstant.REFRESH_TOKEN_NAME);
        AppManager.getAppManager().finishAllActivity();
        Intent intent = new Intent("com.android.dolphin.collect.LoginActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Utils.getApp().startActivity(intent);
    }

}
