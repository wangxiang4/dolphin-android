package com.dolphin.core.http.file;

import com.dolphin.core.entity.DownLoadFile;
import com.dolphin.core.enums.FileObservableStatusEnum;

import java.io.File;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 *<p>
 * 文件下载监听回调
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/19
 */
public class DownloadSubscribe implements ObservableOnSubscribe {

    /** 文件传输进度流 */
    private Source mProgressSource;

    /** 缓存槽 */
    private BufferedSink mSink;

    /** 可观测监听 */
    private ObservableEmitter mObservableEmitter;

    private DownLoadFile downLoadFile = new DownLoadFile();

    public DownloadSubscribe(ResponseBody responseBody, String path, String fileName) throws IOException {
        downLoadFile.setDestFileDir(path);
        downLoadFile.setDestFileName(fileName);
        downLoadFile.setDestFileNameTmp(fileName + ".tmp");
        // 创建临时目标文件
        File file = new File(downLoadFile.getDestFileDir());
        if (!file.exists()) file.mkdirs();
        File tmpFile = new File(downLoadFile.getDestFileDir() + File.separator + downLoadFile.getDestFileNameTmp());
        if (!tmpFile.exists()) tmpFile.createNewFile();
        // 初始化流数据
        downLoadFile.setTotal(responseBody.contentLength());
        mProgressSource = new ForwardingSource(responseBody.source()){
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long read = super.read(sink, byteCount);
                // 设置传输进度
                long bytesLoaded =  downLoadFile.getBytesLoaded();
                bytesLoaded += read == -1 ? 0 : read;
                downLoadFile.setBytesLoaded(bytesLoaded);
                downLoadFile.setStatus(FileObservableStatusEnum.RUNNABLE.getStatus());
                if (bytesLoaded >= downLoadFile.getTotal()) downLoadFile.setBytesLoaded(downLoadFile.getTotal());
                if (mObservableEmitter != null) mObservableEmitter.onNext(downLoadFile);
                return read;
            }
        };
        mSink = Okio.buffer(Okio.sink(new File(downLoadFile.getDestFileDir() +File.separator + downLoadFile.getDestFileNameTmp())));
    }

    @Override
    public void subscribe(ObservableEmitter emitter) {
        this.mObservableEmitter = emitter;
        try {
            mSink.writeAll(Okio.buffer(mProgressSource));
            mSink.close();
            File tmpFile = new File(downLoadFile.getDestFileDir() + File.separator + downLoadFile.getDestFileNameTmp());
            File file = new File(downLoadFile.getDestFileDir() + File.separator + downLoadFile.getDestFileName());
            if (file.exists()) file.delete();
            tmpFile.renameTo(new File(downLoadFile.getDestFileDir() + File.separator + downLoadFile.getDestFileName()));
            downLoadFile.setStatus(FileObservableStatusEnum.SUCCESS.getStatus());
            mObservableEmitter.onComplete();
        } catch (IOException e) {
            downLoadFile.setStatus(FileObservableStatusEnum.FAIL.getStatus());
            mObservableEmitter.onError(e);
        }
    }

}