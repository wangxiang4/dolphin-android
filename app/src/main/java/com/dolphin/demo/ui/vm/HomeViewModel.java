package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.binding.command.BindingCommand;
import com.dolphin.core.enums.FileObservableStatusEnum;
import com.dolphin.core.http.HttpFileRequest;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.http.file.DownLoadFile;
import com.dolphin.core.http.observer.BaseDownLoadDisposableObserver;
import com.dolphin.core.util.RxUtil;

import java.io.File;

/**
 *<p>
 * 首页视图模型层
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/1
 */
public class HomeViewModel extends ToolbarViewModel {

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public BindingCommand onFileDownLoad =new BindingCommand(() -> {
        initDownLoadPercentNotification();
        HttpFileRequest.download("http://gdown.baidu.com/data/wisegame/dc8a46540c7960a2/baidushoujizhushou_16798087.apk")
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .subscribe(new BaseDownLoadDisposableObserver() {
                @Override
                public void onNext(DownLoadFile downLoadFile) {
                    percent = downLoadFile;
                    super.onNext(downLoadFile);
                }

                @Override
                public void onComplete() {
                    updateNotification(-1);
                    ToastUtils.showLong("当前文件保存目录" + percent.getDestFileDir() + File.separator + percent.getDestFileName());
                }

                @Override
                public void onError(Throwable e) {
                    updateNotification(-1);
                    ExceptionHandle.baseExceptionMsg(e);
                }

                @Override
                public void onProgress(Integer percent) {
                    updateNotification(percent);
                }
            });
    });

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("首页");
    }

    NotificationManager notificationManager;
    Notification.Builder builder;
    DownLoadFile percent;

    /** 初始化下载百分比通知栏 */
    private void initDownLoadPercentNotification() {
        // 使用通知栏百分比显示当前下载进度
        notificationManager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Utils.getApp().getPackageName();
        NotificationChannel notificationChannel = new NotificationChannel(channelId, "文件下载", NotificationManager.IMPORTANCE_DEFAULT);
        // 是否在桌面icon右上角展示小圆点
        notificationChannel.enableLights(false);
        // 是否在久按桌面图标时显示此渠道的通知
        notificationChannel.setShowBadge(false);
        // 关闭通知震动
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
        builder = new Notification.Builder(Utils.getApp(), channelId);
        builder.setSmallIcon(com.dolphin.core.R.drawable.umeng_push_notification_default_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(Utils.getApp().getResources(), com.dolphin.core.R.drawable.umeng_push_notification_default_large_icon))
                .setContentTitle("已下载(0%)")
                .setContentText("正在下载")
                // 点击通知后自动取消
                .setAutoCancel(true)
                // 推送的时间
                .setWhen(System.currentTimeMillis())
                // 仅首次通知
                .setOnlyAlertOnce(true)
                //设置进度条
                .setProgress(100, 0, false);
        notificationManager.notify(100, builder.build());
    }

    /**
     * 百分比刷新通知
     * @param progress 百分比,此值小于0时不刷新进度条
     */
    private void updateNotification(int progress) {
        if (builder == null) return;
        if (progress >= 0) {
            builder.setContentTitle("已下载(" + progress + "%)");
            builder.setProgress(100, progress, false);
        }
        if ((ObjectUtils.isNotEmpty(percent) ? percent.getStatus() : progress) == FileObservableStatusEnum.FAIL.getStatus()) {
            builder.setContentText("下载失败");
        } else if ((ObjectUtils.isNotEmpty(percent) ? percent.getStatus() : progress) == FileObservableStatusEnum.SUCCESS.getStatus()) {
            builder.setContentText("下载完成");
        }
        notificationManager.notify(100, builder.build());
    }


}
