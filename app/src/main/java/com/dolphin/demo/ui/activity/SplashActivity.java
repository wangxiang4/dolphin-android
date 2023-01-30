package com.dolphin.demo.ui.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Process;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.amap.api.maps.MapsInitializer;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CacheConstant;
import com.tencent.mmkv.MMKV;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.inapp.InAppMessageManager;
import com.umeng.message.inapp.UmengSplashMessageActivity;

/**
 *<p>
 * 飞溅效果引导活动
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/21
 */
public class SplashActivity extends UmengSplashMessageActivity {

    @Override
    public boolean onCustomPretreatment() {
        if (MMKV.defaultMMKV().getInt(CacheConstant.SOFTWARE_PRIVACY_AGREEMENT_AUTH, 1) == 0) {
            InAppMessageManager mInAppMessageManager = InAppMessageManager.getInstance(SplashActivity.this);
            mInAppMessageManager.setMainActivityPath("com.dolphin.collect.ui.activity.LoginActivity");
            return super.onCustomPretreatment();
        } else {
            softwarePrivacyAgreement();
            return true;
        }
    }

    /** 软件隐私协议 */
    private void softwarePrivacyAgreement() {
        MapsInitializer.updatePrivacyShow(SplashActivity.this,true,true);
        SpannableStringBuilder spannable = new SpannableStringBuilder(getResources().getString(R.string.privacy_agreement));
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), 108, 126, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.RED), 206, 225, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlertDialog builder =new AlertDialog.Builder(this, R.style.alert_dialog_white_background)
                .setTitle("隐私政策")
                .setMessage(spannable)
                .setPositiveButton("同意", (dialogInterface, listener) -> {
                    MapsInitializer.updatePrivacyAgree(SplashActivity.this,true);
                    MMKV.defaultMMKV().putInt(CacheConstant.SOFTWARE_PRIVACY_AGREEMENT_AUTH, 0);
                    // 友盟移动统计,账号统计隐私协议
                    UMConfigure.submitPolicyGrantResult(getApplicationContext(), true);
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("不同意", (dialogInterface, listener) -> {
                    MapsInitializer.updatePrivacyAgree(SplashActivity.this,false);
                    // 友盟移动统计,账号统计隐私协议
                    UMConfigure.submitPolicyGrantResult(getApplicationContext(), false);
                    // 不同意隐私协议,退出app
                    Process.killProcess(Process.myPid());
                }).show();
        builder.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
        builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(18);
    }

}