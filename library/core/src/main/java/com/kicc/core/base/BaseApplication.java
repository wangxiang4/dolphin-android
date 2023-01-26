package com.kicc.core.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.kicc.core.BuildConfig;
import com.tencent.mmkv.MMKV;

/**
 *<p>
 * 基础应用程序初始化处理(必须)
 * 强制要求应用初始化子类继承
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/22
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // MMKV存储库初始化
        MMKV.initialize(this);
        // 是否启动日志
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
        // 注册监听每个activity的生命周期,用于堆栈管理
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.getAppManager().removeActivity(activity);
            }
        });
    }

}
