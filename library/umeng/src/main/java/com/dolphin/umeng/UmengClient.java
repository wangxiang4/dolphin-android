package com.dolphin.umeng;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.bus.RxBus;
import com.dolphin.core.constant.AppConstant;
import com.dolphin.core.util.NotificationUtil;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.umeng.entity.CustomMsgDemo;
import com.dolphin.umeng.enums.PlatformEnum;
import com.dolphin.umeng.listener.UmengLoginListener;
import com.dolphin.umeng.listener.UmengShareListener;
import com.google.gson.Gson;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;

import lombok.experimental.UtilityClass;

/**
 *<p>
 * 友盟客户端
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/15
 */
@UtilityClass
public final class UmengClient {

    /** 设备的唯一标识 */
    private String sDeviceOaid;

    /**
     * 初始化友盟相关 SDK
     * @param application 应用程序上下文
     * @param logEnable 友盟日志开关
     */
    public void init(Application application, boolean logEnable) {
        preInit(application, logEnable);
        // 初始化组件化基础库,统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        // https://developer.umeng.com/docs/119267/detail/118588#title-yb4-p8i-jdc
        UMConfigure.init(application, BuildConfig.UMENG_APP_KEY, Utils.getApp().getString(R.string.app_name), UMConfigure.DEVICE_TYPE_PHONE, BuildConfig.UMENG_APP_MASTER_SECRET);
        // 获取设备的oaid
        UMConfigure.getOaid(application, oaid -> sDeviceOaid = oaid);
        // QQ官方sdk授权
        Tencent.setIsPermissionGranted(true);
    }

    /**
     * 预初始化 SDK（在用户没有同意隐私协议前调用）
     * @param application 应用程序上下文
     * @param logEnable 友盟日志开关
     */
    public static void preInit(Application application, boolean logEnable) {
        UMConfigure.preInit(application, BuildConfig.UMENG_APP_KEY, Utils.getApp().getString(R.string.app_name));
        // 选用自动采集模式：https://developer.umeng.com/docs/119267/detail/118588#h1-u9875u9762u91C7u96C63
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        // 初始化各个平台的 ID 和 Key
        PlatformConfig.setWeixin(BuildConfig.WX_ID, BuildConfig.WX_SECRET);
        PlatformConfig.setQQZone(BuildConfig.QQ_ID, BuildConfig.QQ_SECRET);

        // 初始化各个平台的文件提供者（必须要初始化，否则会导致无法分享文件）
        String fileProvider = application.getPackageName() + ".fileProvider";
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
        pushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
        // 开启呼吸灯点亮
        pushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);
        // 开启振动
        pushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SERVER);
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
                // 自定义消息处理
                if (!StringUtils.isTrimEmpty(msg.custom)) {
                    Intent intent = new Intent("com.android.dolphin.demo.TabBarActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(AppConstant.TAB_BAR_DEFAULT_INDEX, 1);
                    CustomMsgDemo customMsgDemo = new Gson().fromJson(msg.custom, CustomMsgDemo.class);
                    RxBus.getInstance().postSticky(customMsgDemo);
                    Utils.getApp().startActivity(intent);
                }
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        // 消息通知的回调处理
        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                // 调用super，会展示通知，不调用super，则不展示通知
                super.dealWithNotificationMessage(context, msg);
            }

            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                ToastUtil.showCenter("刚刚收到了一条自定义消息:" + msg.custom);
            }

            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        RemoteViews customNotificationView = new RemoteViews(context.getPackageName(), R.layout.layout_notification_view);
                        customNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        customNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        customNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        customNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        return NotificationUtil.defaultNotificationBuilder().setCustomHeadsUpContentView(customNotificationView).build();
                    default:
                        // 默认为0，若填写的builder_id并不存在，也使用默认
                        return super.getNotification(context, msg);
                }
            }
        };
        pushAgent.setMessageHandler(messageHandler);

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