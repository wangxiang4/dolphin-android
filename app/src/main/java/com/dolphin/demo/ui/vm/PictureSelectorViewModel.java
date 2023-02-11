package com.dolphin.demo.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.CloneUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dolphin.core.entity.OssFile;
import com.dolphin.core.entity.UploadFile;
import com.dolphin.core.entity.UploadParam;
import com.dolphin.core.http.HttpFileRequest;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.http.observer.BaseUploadDisposableObserver;
import com.dolphin.core.util.NotificationUtil;
import com.dolphin.core.util.RxUtil;
import com.dolphin.demo.constant.CommonConstant;
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

    private UploadFile uploadResult;

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
        JsonObject ossFile = new JsonObject();
        ossFile.addProperty("duration", media.getDuration());
        HttpFileRequest.upload("system_proxy/system/file/upload", new UploadParam("file", file)
                        .setOssFile(GsonUtils.toJson(ossFile)).setMimeType(media.getMimeType()))
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
                    OssFile clone = CloneUtils.deepClone(uploadResult, GsonUtils.getType(UploadFile.class, OssFile.class));
                    clone.setAvailablePath(String.format(CommonConstant.OSS_FILE_URL, clone.getBucketName(), clone.getFileName()));
                    mActivity.mAdapter.getData().add(clone);
                    mActivity.mAdapter.notifyItemRangeInserted(oldSize, 1);
                    NotificationUtil.notify(demoNotificationId, builder -> builder
                            .setContentText("上传完成")
                            .setContentTitle(uploadResult.getOriginal()));
                }
                @Override
                public void onError(Throwable e) {
                    NotificationUtil.notify(demoNotificationId, builder -> builder
                            .setContentText("上传失败")
                            .setContentTitle("服务器错误,请联系管理员!"));
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onProgress(Integer percent) {
                    if (percent >= 0) {
                        NotificationUtil.notify(demoNotificationId, builder -> builder
                                .setOnlyAlertOnce(true)
                                .setContentText("正在上传中")
                                .setContentTitle("已上传(" + percent + "%)")
                                .setProgress(100, percent, false));
                    }
                }
            });
    }

}
