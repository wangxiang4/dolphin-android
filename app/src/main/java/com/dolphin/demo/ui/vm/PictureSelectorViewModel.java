package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.entity.OssFile;
import com.dolphin.core.entity.UploadFile;
import com.dolphin.core.entity.UploadParam;
import com.dolphin.core.http.HttpFileRequest;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.http.observer.BaseUploadDisposableObserver;
import com.dolphin.core.util.RxUtil;
import com.dolphin.demo.ui.activity.PictureSelectorActivity;
import com.google.gson.JsonObject;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;

/**
 *<p>
 * 照片选择器视图模型
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/8
 */
public class PictureSelectorViewModel extends ToolbarViewModel<PictureSelectorActivity> {

    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private UploadFile uploadResult;

    public final int demoNotificationId = 1025;

    public PictureSelectorViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("精选照片");
        initUploadPercentNotification();
    }

    public void uploadFile(LocalMedia media) {
        File file = new File(media.getRealPath());
        JsonObject ossFile = new JsonObject();
        ossFile.addProperty("duration", media.getDuration());
        HttpFileRequest.upload("system_proxy/system/file/upload", new UploadParam("file", file).setOssFile(GsonUtils.toJson(ossFile)))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .subscribe(new BaseUploadDisposableObserver() {
                @Override
                public void onNext(UploadFile uploadFile) {
                    super.onNext(uploadFile);
                    uploadResult = uploadFile;
                }

                @Override
                public void onComplete() {
                    LogUtils.i("上传成功:", uploadResult);
                    int oldSize = mActivity.mAdapter.getItemCount();
                    mActivity.mAdapter.getData().add(uploadResult);
                    mActivity.mAdapter.notifyItemRangeInserted(oldSize, 1);
                    builder.setContentText("上传完成");
                    notificationManager.notify(demoNotificationId, builder.build());
                }

                @Override
                public void onError(Throwable e) {
                    builder.setContentText("上传失败");
                    notificationManager.notify(demoNotificationId, builder.build());
                    ExceptionHandle.baseExceptionMsg(e);
                }

                @Override
                public void onProgress(Integer percent) {
                    if (percent >= 0) {
                        builder.setContentTitle("已上传(" + percent + "%)");
                        builder.setProgress(100, percent, false);
                        notificationManager.notify(demoNotificationId, builder.build());
                    }
                }
            });
    }

    /** 初始化上传百分比通知栏 */
    private void initUploadPercentNotification() {
        // 使用通知栏百分比显示当前下载进度
        notificationManager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Utils.getApp().getPackageName();
        NotificationChannel notificationChannel = new NotificationChannel(channelId, "文件上传", NotificationManager.IMPORTANCE_DEFAULT);
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
                .setContentTitle("已下上传(0%)")
                .setContentText("正在上传")
                // 点击通知后自动取消
                .setAutoCancel(true)
                // 推送的时间
                .setWhen(System.currentTimeMillis())
                // 仅首次通知
                .setOnlyAlertOnce(true)
                //设置进度条
                .setProgress(100, 0, false);
        notificationManager.notify(demoNotificationId, builder.build());
    }

}
