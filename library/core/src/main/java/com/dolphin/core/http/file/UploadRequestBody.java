package com.dolphin.core.http.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
public class UploadRequestBody extends RequestBody {

    private File file;

    private UploadSubscribe uploadSubscribe;

    public UploadRequestBody(File file, UploadSubscribe uploadSubscribe) {
        this.file = file;
        this.uploadSubscribe = uploadSubscribe;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("multipart/form-data");
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[2048];
        FileInputStream in = new FileInputStream(file);
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                if(uploadSubscribe != null) uploadSubscribe.onRead(read);
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

}
