package com.kicc.umeng;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kicc.core.bus.RxBus;
import com.kicc.core.constant.AppConstant;
import com.kicc.core.entity.MapLogisticPoint;
import com.kicc.umeng.enums.PlatformEnum;
import com.kicc.umeng.listener.UmengLoginListener;
import com.kicc.umeng.listener.UmengShareListener;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;

/**
 *<p>
 * 友盟客户端
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/15
 */
public final class UmengClient {

    /** 设备的唯一标识 */
    private static String sDeviceOaid;

    /**
     * 初始化友盟相关 SDK
     * @param application 应用程序上下文
     * @param logEnable 友盟日志开关
     */
    public static void init(Application application, boolean logEnable) {
        preInit(application, logEnable);
        // 初始化组件化基础库,统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        // https://developer.umeng.com/docs/66632/detail/101814#h1-u521Du59CBu5316u53CAu901Au7528u63A5u53E32
        UMConfigure.init(application, BuildConfig.UMENG_APP_KEY,"umeng", UMConfigure.DEVICE_TYPE_PHONE, BuildConfig.UMENG_APP_MASTER_SECRET);
        // 获取设备的oaid
        UMConfigure.getOaid(application, oaid -> sDeviceOaid = oaid);
        //QQ官方sdk授权
        Tencent.setIsPermissionGranted(true);
    }

    /**
     * 预初始化 SDK（在用户没有同意隐私协议前调用）
     * @param application 应用程序上下文
     * @param logEnable 友盟日志开关
     */
    public static void preInit(Application application, boolean logEnable) {
        UMConfigure.preInit(application, BuildConfig.UMENG_APP_KEY,"umeng");
        // 选用自动采集模式：https://developer.umeng.com/docs/119267/detail/118588#h1-u9875u9762u91C7u96C63
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        // 初始化各个平台的 ID 和 Key
        PlatformConfig.setWeixin(BuildConfig.WX_ID, BuildConfig.WX_SECRET);
        PlatformConfig.setQQZone(BuildConfig.QQ_ID, BuildConfig.QQ_SECRET);

        // 初始化各个平台的文件提供者（必须要初始化，否则会导致无法分享文件）
        String fileProvider = application.getPackageName() + ".provider";
        PlatformConfig.setWXFileProvider(fileProvider);
        PlatformConfig.setQQFileProvider(fileProvider);

        // 初始化友盟推送
        initUPush(application);

        // 是否开启日志打印
        UMConfigure.setLogEnabled(logEnable);
    }

    /**
     * 初始化友盟推送,设置默认参数
     * @param application 应用程序上下文
     */
    private static void initUPush(Application application) {
        // 友盟推送代理
        PushAgent pushAgent = PushAgent.getInstance(application);
        // 开启通知声音
        pushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // 设置显示通知的数量,超过移除第一条
        pushAgent.setDisplayNotificationNumber(1);
        // 开启呼吸灯点亮
        pushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // 推送平台多维度推送决策必须调用方法(需要同意隐私协议之后初始化完成调用)
        pushAgent.onAppStart();
        // 自定义行为的回调处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Intent intent = new Intent("com.android.kicc.collect.TabBarActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(AppConstant.TAB_BAR_DEFAULT_INDEX, 2);
                JsonObject jsonObject = new JsonObject();
                if (jsonObject.isJsonObject()) {
                    MapLogisticPoint mapLogisticPoint = new Gson().fromJson(msg.custom, MapLogisticPoint.class);
                    RxBus.getInstance().postSticky(mapLogisticPoint);
                    Utils.getApp().startActivity(intent);
                } else ToastUtils.showLong("传递消息参数数据格式错误!");
            }
        };
        // 使用自定义的NotificationHandler
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        // 注册推送服务 每次调用register都会回调该接口
        pushAgent.register(new UPushRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                LogUtils.i( "device token: " + deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                LogUtils.i("register failed: " + s + " " + s1);
            }
        });
    }


    /**
     * 第三方平台分享
     * @param activity Activity对象
     * @param platformEnum 分享平台
     * @param action 分享意图
     * @param listener 分享监听
     */
    public static void share(Activity activity, PlatformEnum platformEnum, ShareAction action, @Nullable UmengShareListener.OnShareListener listener) {
        if (!isAppInstalled(activity, platformEnum.getPackageName())) {
            // 当分享的平台软件可能没有被安装的时候
            if (listener == null) return;
            listener.onError(platformEnum, new PackageManager.NameNotFoundException("Is not installed"));
            return;
        }
        action.setPlatform(platformEnum.getThirdParty())
                .setCallback(new UmengShareListener(platformEnum.getThirdParty(), listener))
                .share();
    }

    /**
     * 第三方平台登录
     * @param activity Activity对象
     * @param platformEnum 登录平台
     * @param listener 登录监听
     */
    public static void login(Activity activity, PlatformEnum platformEnum, @Nullable UmengLoginListener.OnLoginListener listener) {
        if (!isAppInstalled(activity, platformEnum)) {
            // 当登录的平台软件可能没有被安装的时候
            if (listener == null) return;
            listener.onError(platformEnum, new PackageManager.NameNotFoundException("Is not installed"));
            return;
        }
        try {
            // 删除旧的第三方登录授权
            UMShareAPI.get(activity).deleteOauth(activity, platformEnum.getThirdParty(), null);
            // 要先等上面的代码执行完毕之后
            Thread.sleep(200);
            // 开启新的第三方登录授权
            UMShareAPI.get(activity).getPlatformInfo(activity, platformEnum.getThirdParty(), new UmengLoginListener(platformEnum.getThirdParty(), listener));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** 设置分享活动结果回调,不设置分享后不会调用onResult监听方法 */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        UMShareAPI.get(activity).onActivityResult(requestCode, resultCode, data);
    }

    /** 获取设备oaid */
    public static String getDeviceOaid() {
        return sDeviceOaid;
    }

    /** 判断App是否安装 */
    public static boolean isAppInstalled(Context context, PlatformEnum platformEnum) {
        return isAppInstalled(context, platformEnum.getPackageName());
    }

    private static boolean isAppInstalled(Context context, @NonNull String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}