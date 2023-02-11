package com.dolphin.core.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import com.blankj.utilcode.util.Utils;
import com.dolphin.core.R;
import com.dolphin.core.constant.AppConstant;

import lombok.experimental.UtilityClass;

/**
 *<p>
 * 消息通知工具
 * 参考官网: https://developer.android.com/reference/android/app/NotificationManager
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/11
 */
@UtilityClass
public class NotificationUtil {

    public final NotificationManager notificationManager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);

    /** 默认消息通道配置 */
    public NotificationChannel defaultNotificationChannel() {
        String channelId = Utils.getApp().getPackageName();
        NotificationChannel notificationChannel = new NotificationChannel(channelId,
                Utils.getApp().getResources().getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(ContextCompat.getColor(Utils.getApp(), R.color.common_app_them));
        notificationChannel.setBypassDnd(true);
        notificationChannel.setShowBadge(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setAllowBubbles(true);
        return notificationChannel;
    }

    /** 默认消息构建 */
    public Notification.Builder defaultNotificationBuilder() {
        String channelId = Utils.getApp().getPackageName();
        PendingIntent pendingIntent = PendingIntent.getActivity(Utils.getApp(), AppConstant.PERMISSION_REQUEST_CODE, new Intent("com.android.dolphin.demo.TabBarActivity"), PendingIntent.FLAG_MUTABLE);
        Notification.Builder builder = new Notification.Builder(Utils.getApp(), channelId);
        builder.setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(Utils.getApp().getResources(), R.drawable.umeng_push_notification_default_large_icon))
                .setContentText(Utils.getApp().getResources().getString(R.string.app_name))
                // 点击通知后自动取消
                .setAutoCancel(true)
                // 推送的时间
                .setWhen(System.currentTimeMillis())
                // 点击通知触发异步意图
                .setContentIntent(pendingIntent)
                .setShowWhen(true);
        return builder;
    }

    /**
     * 消息通知
     * @param id 唯一id
     * @param consumer 消息构建函数
     */
    public void notify(int id, Consumer<Notification.Builder> consumer) {
        Notification.Builder builder = defaultNotificationBuilder();
        consumer.accept(builder);
        notify(null, id, defaultNotificationChannel(), builder.build());
    }

    /**
     * 消息通知
     * @param id 唯一id
     * @param channelConsumer 通知通道函数
     * @param builderConsumer 通知构建函数
     */
    public void notify(int id, Consumer<NotificationChannel> channelConsumer, Consumer<Notification.Builder> builderConsumer) {
        NotificationChannel notificationChannel = defaultNotificationChannel();
        channelConsumer.accept(notificationChannel);
        Notification.Builder builder = defaultNotificationBuilder();
        builderConsumer.accept(builder);
        notify(null, id, notificationChannel, builder.build());
    }

    /**
     * 消息通知
     * @param tag 类别标签
     * @param id 唯一id
     * @param channelConfig 消息通道配置
     * @param build 消息构建
     */
    public void notify(String tag, int id, NotificationChannel channelConfig, Notification build) {
        notificationManager.createNotificationChannel(channelConfig);
        notificationManager.notify(tag, id, build);
    }

    /**
     * 消息取消
     * @param tag 类别标签
     * @param id 唯一id
     */
    public void cancel(String tag, final int id) {
        notificationManager.cancel(tag, id);
    }

    /**
     * 消息取消
     * @param id 唯一id
     */
    public void cancel(final int id) {
        notificationManager.cancel(id);
    }

    /** 全部取消 */
    public void cancelAll() {
        notificationManager.cancelAll();
    }

}
