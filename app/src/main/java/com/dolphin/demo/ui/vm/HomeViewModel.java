package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.Utils;
import com.dolphin.core.entity.DownLoadFile;
import com.dolphin.core.http.HttpFileRequest;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.http.observer.BaseDownLoadDisposableObserver;
import com.dolphin.core.util.RxUtil;
import com.dolphin.core.util.ToastUtil;

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

    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private DownLoadFile downLoadResult;

    public final int demoNotificationId = 1024;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("首页");
        initDownLoadPercentNotification();
    }

    public void onFileDownLoad() {
        HttpFileRequest.download("https://github.com/wangxiang4/dolphin-ios/blob/master/Dolphin.ipa")
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .subscribe(new BaseDownLoadDisposableObserver() {
                @Override
                public void onNext(DownLoadFile downLoadFile) {
                    super.onNext(downLoadFile);
                    downLoadResult = downLoadFile;
                }
                @Override
                public void onComplete() {
                    builder.setContentText("下载完成");
                    builder.setContentTitle(downLoadResult.getDestFileName());
                    builder.setProgress(100, 100, false);
                    notificationManager.notify(demoNotificationId, builder.build());
                    ToastUtil.show("当前文件保存目录" + downLoadResult.getDestFileDir() + File.separator + downLoadResult.getDestFileName());
                }
                @Override
                public void onError(Throwable e) {
                    builder.setContentText("下载失败");
                    builder.setContentTitle("服务器错误,请联系管理员!");
                    builder.setProgress(100, 0, false);
                    notificationManager.notify(demoNotificationId, builder.build());
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onProgress(Integer percent) {
                    if (percent >= 0) {
                        builder.setContentTitle("已下载(" + percent + "%)");
                        builder.setProgress(100, percent, false);
                        notificationManager.notify(demoNotificationId, builder.build());
                    }
                }
            });
    }

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
                .setContentTitle(Utils.getApp().getResources().getString(com.dolphin.core.R.string.app_name))
                .setContentText("初始化完毕")
                // 点击通知后自动取消
                .setAutoCancel(true)
                // 推送的时间
                .setWhen(System.currentTimeMillis())
                // 仅首次通知
                .setOnlyAlertOnce(true);
    }

}
