package com.dolphin.core.service;

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
import android.os.PowerManager;
import android.os.SystemClock;

import com.blankj.utilcode.util.Utils;
import com.dolphin.core.R;
import com.dolphin.core.constant.AppConstant;

/**
 *<p>
 * 应用保持活跃
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/6
 */
public class AppKeepActive {

    /** 活动上下文 */
    private Context mContext;

    /** 前台服务通知 */
    public static volatile Notification notification;

    /** 后台位置保持服务意图 */
    private Intent backgroundKeepActiveFrontServiceIntent;

    /** 系统警报提醒服务 */
    private AlarmManager alarmManager;

    /** 警报待定异步意图 */
    private PendingIntent alarmPendingIntent;

    /** 屏幕唤醒锁定屏幕接收 */
    private BroadcastReceiver wakeLockScreenReceiver;

    /** 判断屏幕唤醒锁定屏幕接收是否注册 */
    private Boolean isRegisterPowerWakeLockReceiver = false;

    /** 手机电源管理 */
    private PowerManager powerManager;

    /** 屏幕唤醒 */
    private PowerManager.WakeLock wakeLock;

    /** 前台服务通知点击启动活动 */
    private Class<?> notificationClickStartClass;

    public AppKeepActive(Class<?> notificationClickStartClass) {
        this(Utils.getApp(), notificationClickStartClass);
    }

    public AppKeepActive(Context mContext, Class<?> notificationClickStartClass) {
        this.mContext = mContext;
        this.notificationClickStartClass = notificationClickStartClass;
        powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        notification = buildNotification();
    }

    /** 注册电源锁屏监听广播 */
    public void registerPowerWakeLockReceiver() {
        if (isRegisterPowerWakeLockReceiver) return;
        isRegisterPowerWakeLockReceiver = true;
        if (null == wakeLock) wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "dolphin:keep-active");
        if (null == wakeLockScreenReceiver) {
            wakeLockScreenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (null == wakeLock) return;
                    String action = intent.getAction();
                    if (Intent.ACTION_SCREEN_OFF.equals(action) && !wakeLock.isHeld()) {
                        wakeLock.acquire();
                        if (null != alarmPendingIntent) return;
                        // 支持后台熄屏,定时执行后台持续活跃任务
                        Intent alarmIntent = new Intent();
                        alarmIntent.setAction(AppConstant.KEEP_ACTIVE_TASK_BROADCAST_UPDATE);
                        // https://www.cnblogs.com/endv/p/11576121.html
                        alarmPendingIntent = PendingIntent.getBroadcast(mContext, AppConstant.PERMISSION_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
                        // 获取系统警报提醒服务
                        alarmManager = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
                        // 设置一个闹钟,1秒之后每隔一段时间执行启动一次后台持续活跃任务,防止冻结后台任务
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1 * 1000, AppConstant.KEEP_ACTIVE_TASK_INTERVAL_TIME, alarmPendingIntent);
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

    /** 启动后台活跃前台服务 */
    public void startBackgroundKeepActiveFrontService() {
        BackgroundKeepActiveFrontService.startBackgroundKeepActiveTask = true;
        backgroundKeepActiveFrontServiceIntent = new Intent(mContext, BackgroundKeepActiveFrontService.class);
        mContext.startForegroundService(backgroundKeepActiveFrontServiceIntent);
    }

    /** 关闭后台活跃前台服务 */
    public void closeBackgroundKeepActiveFrontService() {
        BackgroundKeepActiveFrontService.startBackgroundKeepActiveTask = false;
        if (null != backgroundKeepActiveFrontServiceIntent) mContext.stopService(backgroundKeepActiveFrontServiceIntent);
    }

    /** 注册应用保持活跃服务 */
    public void registerService() {
        startBackgroundKeepActiveFrontService();
        registerPowerWakeLockReceiver();
    }

    /** 解绑应用保持活跃服务 */
    public void unregisterService() {
        closeBackgroundKeepActiveFrontService();
        unregisterPowerWakeLockReceiver();
    }

    /** 构建前台服务通知 */
    private Notification buildNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = mContext.getPackageName();
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, AppConstant.PERMISSION_REQUEST_CODE, new Intent(mContext, notificationClickStartClass), PendingIntent.FLAG_MUTABLE);
        NotificationChannel notificationChannel = new NotificationChannel(channelId, BackgroundKeepActiveFrontService.class.getName(), NotificationManager.IMPORTANCE_DEFAULT);
        // 是否在桌面icon右上角展示小圆点
        notificationChannel.enableLights(false);
        // 是否在久按桌面图标时显示此渠道的通知
        notificationChannel.setShowBadge(false);
        // 关闭通知震动
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification.Builder builder = new Notification.Builder(mContext, channelId);
        builder.setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.umeng_push_notification_default_large_icon))
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

}
