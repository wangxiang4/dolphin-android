package com.dolphin.demo.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.dolphin.core.entity.DownLoadFile;
import com.dolphin.core.http.HttpFileRequest;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.http.observer.BaseDownLoadDisposableObserver;
import com.dolphin.core.util.NotificationUtil;
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

    private DownLoadFile downLoadResult;

    public final int demoNotificationId = 1024;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("首页");
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
                    NotificationUtil.notify(demoNotificationId, builder -> builder
                            .setContentText("下载完成")
                            .setContentTitle(downLoadResult.getDestFileName()));
                    ToastUtil.show("当前文件保存目录" + downLoadResult.getDestFileDir() + File.separator + downLoadResult.getDestFileName());
                }
                @Override
                public void onError(Throwable e) {
                    NotificationUtil.notify(demoNotificationId, builder -> builder
                            .setContentText("下载失败")
                            .setContentTitle("服务器错误,请联系管理员!"));
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onProgress(Integer percent) {
                    if (percent >= 0) {
                        NotificationUtil.notify(demoNotificationId, builder -> builder
                            .setOnlyAlertOnce(true)
                            .setContentText("正在下载中")
                            .setContentTitle("已下载(" + percent + "%)")
                            .setProgress(100, percent, false));
                    }
                }
            });
    }

}
