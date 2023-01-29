package com.dolphin.core.http.observer;


import com.dolphin.core.http.file.UploadFile;

import io.reactivex.observers.DisposableObserver;

/**
 *<p>
 * 基础下载监听
 * 提供进度条基础回调
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/24
 */
public abstract class BaseUploadDisposableObserver extends DisposableObserver {

    /** 计算百分比 */
    private int mPercent = 0;

    @Override
    public void onNext(Object o) {
        if (o instanceof UploadFile) {
            UploadFile uploadFile = (UploadFile) o;
            long bytesLoaded = uploadFile.getBytesLoaded();
            long total = uploadFile.getTotal();
            int percent = (int) (bytesLoaded*100f / total);
            if (percent < 0) {
                percent = 0;
            }
            if (percent > 100) {
                percent = 100;
            }
            if (percent == mPercent) {
                return;
            }
            mPercent = percent;
            onProgress(mPercent);
            return;
        }
    }

    @Override
    public void onError(Throwable e) {}

    @Override
    public void onComplete() {}

    /**
     * 进度条(内部处理计算)
     * @param percent 进度百分比
     * @return void
     */
    public void onProgress(Integer percent) {}

}
