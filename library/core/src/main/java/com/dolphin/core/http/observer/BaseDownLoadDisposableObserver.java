package com.dolphin.core.http.observer;


import com.dolphin.core.http.file.DownLoadFile;

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
public abstract class BaseDownLoadDisposableObserver extends DisposableObserver<DownLoadFile> {

    /** 计算百分比 */
    private int mPercent = 0;

    @Override
    public void onNext(DownLoadFile downLoadFile) {
        long bytesLoaded = downLoadFile.getBytesLoaded();
        long total = downLoadFile.getTotal();
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
