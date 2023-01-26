package com.kicc.core.http.file;


import com.kicc.core.enums.FileObservableStatusEnum;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 *<p>
 * 文件上传监听回调
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/19
 */
public class UploadSubscribe implements ObservableOnSubscribe {

    private UploadFile uploadFile = new UploadFile();

    private ObservableEmitter mObservableEmitter;

    public UploadFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public void onRead(long read) {
        long bytesLoaded =  uploadFile.getBytesLoaded();
        bytesLoaded += read == -1 ? 0 : read;
        uploadFile.setBytesLoaded(bytesLoaded);
        uploadFile.setStatus(FileObservableStatusEnum.RUNNABLE.getStatus());
        if (bytesLoaded >= uploadFile.getTotal()) uploadFile.setBytesLoaded(uploadFile.getTotal());
        if (mObservableEmitter != null) mObservableEmitter.onNext(uploadFile);
    }

    @Override
    public void subscribe(ObservableEmitter emitter) throws Exception {
        mObservableEmitter = emitter;
    }

}
