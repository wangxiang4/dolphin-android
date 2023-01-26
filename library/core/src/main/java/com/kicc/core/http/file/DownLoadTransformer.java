package com.kicc.core.http.file;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 *<p>
 * 流转换
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/24
 */
public class DownLoadTransformer implements ObservableTransformer<ResponseBody, Object> {

    /** 目标本地文件存放路径 */
    private String destFileDir;

    /** 目标文件名 */
    private String destFileName;

    public DownLoadTransformer(String path, String fileName) {
        this.destFileDir = path;
        this.destFileName = fileName;
    }

    @Override
    public ObservableSource<Object> apply(Observable<ResponseBody> upstream) {
        return upstream.flatMap(responseBody -> Observable.create(new DownloadSubscribe(responseBody, destFileDir, destFileName)));
    }
}
