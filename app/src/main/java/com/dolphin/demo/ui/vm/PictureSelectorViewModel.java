package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.bus.SingleLiveEvent;
import com.dolphin.core.enums.FileObservableStatusEnum;
import com.dolphin.core.http.HttpFileRequest;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.http.file.UploadFile;
import com.dolphin.core.http.file.UploadParam;
import com.dolphin.core.http.observer.BaseUploadDisposableObserver;
import com.dolphin.core.util.RxUtil;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.entity.OssFile;
import com.dolphin.demo.ui.activity.PictureSelectorActivity;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.Map;
import java.util.UUID;

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
    private UploadFile percent;

    public final int demoNotificationId = 1025;

    public PictureSelectorViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("精选照片");
    }

    public void uploadFile(LocalMedia media) {
        File file = new File(media.getRealPath());
        initUploadPercentNotification();
        HttpFileRequest.upload("system_proxy/system/file/upload", new UploadParam("file", file))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .subscribe(new BaseUploadDisposableObserver() {
                @Override
                public void onNext(Object o) {
                    if (o instanceof UploadFile) percent = (UploadFile) o;
                    super.onNext(o);
                    if (o instanceof Map) {
                        Map result = (Map) o;
                        LogUtils.i("上传成功:", result);
                        int oldSize = mActivity.mAdapter.getData().size();
                        mActivity.mAdapter.getData().add(
                                // todo: 后续会优化后台上传接口直接返回OSSFile对象，目前暂时处理不支持媒体类型字段，待修改
                                new OssFile().setId(UUID.randomUUID().toString().replaceAll("-", ""))
                                        .setAvailablePath(String.format(CommonConstant.OSS_FILE_URL, result.get("bucketName"), result.get("fileName")))
                                        .setFileName(result.get("fileName").toString())
                                        .setBucketName(result.get("bucketName").toString())
                                        .setMimeType("")
                        );
                        mActivity.mAdapter.notifyItemRangeInserted(oldSize, 1);
                    }
                }

                @Override
                public void onComplete() {
                    updateNotification(-1);
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

    /**
     * 百分比刷新通知
     * @param progress 百分比,此值小于0时不刷新进度条
     */
    private void updateNotification(int progress) {
        if (builder == null) return;
        if (progress >= 0) {
            builder.setContentTitle("已上传(" + progress + "%)");
            builder.setProgress(100, progress, false);
        }
        if (percent.getStatus() == FileObservableStatusEnum.FAIL.getStatus()) {
            builder.setContentText("上传失败");
        } else if (percent.getStatus() == FileObservableStatusEnum.SUCCESS.getStatus()) {
            builder.setContentText("上传完成");
        }
        notificationManager.notify(demoNotificationId, builder.build());
    }

}
