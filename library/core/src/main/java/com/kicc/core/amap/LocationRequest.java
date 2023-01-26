package com.kicc.core.amap;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.PowerManager;
import android.os.SystemClock;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.blankj.utilcode.util.Utils;
import com.kicc.core.R;
import com.kicc.core.amap.service.BackgroundLocationKeepFrontService;
import com.kicc.core.constant.AppConstant;
import com.kicc.core.enums.LocationRequestEnum;

/**
 *<p>
 * LBS定位请求
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/27
 */
public class LocationRequest implements AMapLocationListener {

    /** 活动上下文 */
    private Context mContext;

    /** 前台服务通知 */
    public static volatile Notification notification;

    /** 后台定位请求状态 */
    private Integer locationStatus = LocationRequestEnum.TERMINATED.getStatus();;

    /** 手机电源管理 */
    private PowerManager powerManager;

    /** 屏幕唤醒 */
    private PowerManager.WakeLock wakeLock;

    /** 系统警报提醒服务 */
    private AlarmManager alarmManager;

    /** 警报待定异步意图 */
    private PendingIntent alarmPendingIntent;

    /** 屏幕唤醒锁定屏幕接收 */
    private BroadcastReceiver wakeLockScreenReceiver;

    /** 判断屏幕唤醒锁定屏幕接收是否注册 */
    private Boolean isRegisterPowerWakeLockReceiver = false;

    /** 位置接收 */
    private BroadcastReceiver locationReceiver;

    /** 判断位置接受是否注册 */
    private Boolean isRegisterLocationReceiver = false;

    /** 位置客户端,进行位置定位相关的操作 */
    public static AMapLocationClient locationClient;

    /** 后台位置保持服务意图 */
    private Intent backgroundLocationKeepFrontServiceIntent;

    /** 位置客户端定位结果监听 */
    private AMapLocationListener locationListen;

    /** 前台服务通知点击启动活动 */
    private Class<?> notificationClickStartClass;

    public LocationRequest(Class<?> notificationClickStartClass) {
        this(Utils.getApp(), notificationClickStartClass);
    }

    public LocationRequest(Context mContext, Class<?> notificationClickStartClass) {
        this.mContext = mContext;
        this.notificationClickStartClass = notificationClickStartClass;
        init();
    }

    /** 启动后台定位 */
    public void start() {
        if (locationStatus == LocationRequestEnum.TERMINATED.getStatus()) {
            locationClient.startLocation();
            registerService();
            locationStatus = LocationRequestEnum.RUNNABLE.getStatus();
        }
    }

    /** 停止后台定位 */
    public void stop() {
        if (locationStatus == LocationRequestEnum.RUNNABLE.getStatus()) {
            locationClient.onDestroy();
            unregisterService();
            locationStatus = LocationRequestEnum.TERMINATED.getStatus();
        }
    }

    /** 初始化服务 */
    private void init() {
        powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        notification = buildNotification();
        locationClient = initLocation(getDefaultOption());
        locationClient.setLocationListener(this);
    }

    /** 初始化位置定位 */
    public AMapLocationClient initLocation(AMapLocationClientOption aMapLocationClientOption) {
        try {
            AMapLocationClient locationClient = new AMapLocationClient(mContext);
            // 设置定位参数
            locationClient.setLocationOption(aMapLocationClientOption);
            return locationClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 高德地图位置定位配置 */
    public AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        // 可选,设置定位模式,可选的模式有高精度、仅设备、仅网络,默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 可选,设置首次是否gps优先,只在高精度模式下有效。默认关闭
        mOption.setGpsFirst(false);
        // 可选,设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setHttpTimeOut(30000);
        // 可选,设置定位间隔。默认为2秒
        mOption.setInterval(2000);
        // 可选,设置是否返回逆地理地址信息。默认是true
        mOption.setNeedAddress(true);
        // 可选,设置是否单次定位。默认是false
        mOption.setOnceLocation(false);
        // 可选,设置是否等待wifi刷新,默认为false.如果设置为true,会自动变为单次定位,持续定位时不要使用
        mOption.setOnceLocationLatest(false);
        // 可选,设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        // 可选,设置是否使用传感器。默认是false
        mOption.setSensorEnable(true);
        // 可选,设置是否开启wifi扫描。默认为true,如果设置为false会同时停止主动刷新,停止以后完全依赖于系统刷新,定位位置可能存在误差
        mOption.setWifiScan(true);
        // 可选,设置是否使用缓存定位,默认为true
        mOption.setLocationCacheEnable(true);
        // 可选,设置逆地理信息的语言,默认值为默认语言(根据所在地区选择语言)
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);
        return mOption;
    }

    /** 注册位置请求服务 */
    public void registerService() {
        registerLocationReceiver();
        startBackgroundLocationKeepFrontService();
        registerPowerWakeLockReceiver();
    }

    /** 取消位置请求服务 */
    public void unregisterService() {
        unregisterLocationReceiver();
        closeBackgroundLocationKeepFrontService();
        unregisterPowerWakeLockReceiver();
    }

    /** 启动后台定位保活前台服务 */
    public void startBackgroundLocationKeepFrontService() {
        BackgroundLocationKeepFrontService.startBackgroundLocationTask = true;
        backgroundLocationKeepFrontServiceIntent = new Intent(mContext, BackgroundLocationKeepFrontService.class);
        mContext.startForegroundService(backgroundLocationKeepFrontServiceIntent);
    }

    /** 关闭后台定位保活前台服务 */
    public void closeBackgroundLocationKeepFrontService() {
        BackgroundLocationKeepFrontService.startBackgroundLocationTask = false;
        if (null != backgroundLocationKeepFrontServiceIntent) mContext.stopService(backgroundLocationKeepFrontServiceIntent);
    }

    /** 注册位置监听广播 */
    public void registerLocationReceiver () {
        if (isRegisterLocationReceiver) return;
        isRegisterLocationReceiver = true;
        if (null == wakeLockScreenReceiver) {
            locationReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (null != locationClient && intent.getAction().equals(LocationManager.KEY_LOCATION_CHANGED)) {
                        locationClient.startLocation();
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.KEY_LOCATION_CHANGED);
        mContext.registerReceiver(locationReceiver, filter);
    }

    /** 取消位置监听广播 */
    public void unregisterLocationReceiver() {
        if (!isRegisterLocationReceiver) return;
        if (null != locationReceiver) mContext.unregisterReceiver(locationReceiver);
        isRegisterLocationReceiver = false;
    }

    /** 注册电源锁屏监听广播 */
    public void registerPowerWakeLockReceiver() {
        if (isRegisterPowerWakeLockReceiver) return;
        isRegisterPowerWakeLockReceiver = true;
        if (null == wakeLock) wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "kicc-collect:gps");
        if (null == wakeLockScreenReceiver) {
            wakeLockScreenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (null == wakeLock) return;
                    String action = intent.getAction();
                    if (Intent.ACTION_SCREEN_OFF.equals(action) && !wakeLock.isHeld()) {
                        wakeLock.acquire();
                        if (null != alarmPendingIntent) return;
                        // 支持后台熄屏,获取定位信息
                        Intent alarmIntent = new Intent();
                        alarmIntent.setAction(LocationManager.KEY_LOCATION_CHANGED);
                        // https://www.cnblogs.com/endv/p/11576121.html
                        alarmPendingIntent = PendingIntent.getBroadcast(mContext, AppConstant.PERMISSION_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
                        // 获取系统警报提醒服务
                        alarmManager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
                        // 设置一个闹钟,1秒之后每隔一段时间执行启动一次定位程序,防止后台冻结高德地图获取定位服务
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1 * 1000, AppConstant.LOCATION_TASK_INTERVAL_TIME, alarmPendingIntent);
                    } else if (Intent.ACTION_USER_PRESENT.equals(action) && wakeLock.isHeld()) {
                        wakeLock.release();
                        if (null == alarmManager) return;
                        alarmManager.cancel(alarmPendingIntent);
                        alarmPendingIntent = null;
                        alarmManager = null;
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(wakeLockScreenReceiver, filter);
    }

    /** 取消电源锁屏监听广播 */
    public void unregisterPowerWakeLockReceiver() {
        if (!isRegisterPowerWakeLockReceiver) return;
        if (null != wakeLockScreenReceiver) mContext.unregisterReceiver(wakeLockScreenReceiver);
        isRegisterPowerWakeLockReceiver = false;
    }

    /** 构建前台定位服务通知 */
    private Notification buildNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = mContext.getPackageName();
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, AppConstant.PERMISSION_REQUEST_CODE, new Intent(mContext, notificationClickStartClass), PendingIntent.FLAG_MUTABLE);
        NotificationChannel notificationChannel = new NotificationChannel(channelId, BackgroundLocationKeepFrontService.class.getName(), NotificationManager.IMPORTANCE_DEFAULT);
        // 是否在桌面icon右上角展示小圆点
        notificationChannel.enableLights(false);
        // 是否在久按桌面图标时显示此渠道的通知
        notificationChannel.setShowBadge(false);
        // 关闭通知震动
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification.Builder builder = new Notification.Builder(mContext, channelId);
        builder.setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.kc_ic_app))
                .setContentTitle(mContext.getResources().getString(R.string.app_name))
                .setContentText("正在后台运行")
                // 点击通知后自动取消
                .setAutoCancel(false)
                // 推送的时间
                .setWhen(System.currentTimeMillis())
                // 仅首次通知
                .setOnlyAlertOnce(true)
                // 点击通知触发异步意图
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        // 通知栏以不能清除的方式展示
        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && locationListen != null) {
            locationListen.onLocationChanged(aMapLocation);
        }
    }

    public PowerManager getPowerManager() {
        return powerManager;
    }

    public void setPowerManager(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    public PowerManager.WakeLock getWakeLock() {
        return wakeLock;
    }

    public void setWakeLock(PowerManager.WakeLock wakeLock) {
        this.wakeLock = wakeLock;
    }

    public AMapLocationClient getLocationClient() {
        return locationClient;
    }

    public void setLocationClient(AMapLocationClient locationClient) {
        this.locationClient = locationClient;
    }

    public AMapLocationListener getLocationListen() {
        return locationListen;
    }

    public void setLocationListen(AMapLocationListener locationListen) {
        this.locationListen = locationListen;
    }

    public Integer getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(Integer locationStatus) {
        this.locationStatus = locationStatus;
    }

}
