package com.dolphin.core.http.file;

import com.dolphin.core.entity.UploadFile;
import com.dolphin.core.enums.FileObservableStatusEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 *<p>
 * 上传请求体
 * 支持上传进度条
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/24
 */
public class UploadRequestBody extends RequestBody implements ObservableOnSubscribe {

    private File mFile;

    private String mediaType = "image/jpeg";

    /** 可观测监听 */
    private ObservableEmitter mObservableEmitter;

    private UploadFile uploadFile = new UploadFile();

    public UploadRequestBody(File file, String mediaType) {
        this.mFile = file;
        this.mediaType = mediaType;
        uploadFile.setTotal(contentLength());
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(mediaType);
    }

    @Override
    public long contentLength() {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[2048];
        FileInputStream in = new FileInputStream(mFile);
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                long bytesLoaded = uploadFile.getBytesLoaded();
                bytesLoaded += read == -1 ? 0 : read;
                uploadFile.setBytesLoaded(bytesLoaded);
                uploadFile.setStatus(FileObservableStatusEnum.RUNNABLE.getStatus());
                if (bytesLoaded >= uploadFile.getTotal()) uploadFile.setBytesLoaded(uploadFile.getTotal());
                if (mObservableEmitter != null) mObservableEmitter.onNext(uploadFile);
                sink.write(buffer, 0, read);
            }
            uploadFile.setStatus(FileObservableStatusEnum.SUCCESS.getStatus());
            mObservableEmitter.onComplete();
        } catch (IOException e) {
            uploadFile.setStatus(FileObservableStatusEnum.FAIL.getStatus());
            mObservableEmitter.onError(e);
        } finally {
            in.close();
        }
    }

    @Override
    public void subscribe(ObservableEmitter emitter) {
        this.mObservableEmitter = emitter;
    }

}
