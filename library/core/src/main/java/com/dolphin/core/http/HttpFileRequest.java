package com.dolphin.core.http;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.StringUtils;
import com.dolphin.core.http.file.DownLoadTransformer;
import com.dolphin.core.http.file.UploadParam;
import com.dolphin.core.http.file.UploadRequestBody;
import com.dolphin.core.http.file.UploadSubscribe;
import com.dolphin.core.util.CommonUtil;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 *<p>
 * http文件请求
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/24
 */
public class HttpFileRequest {

    /** 文件请求地址 */
    interface FileMapper {

        @GET
        @Streaming
        Observable<ResponseBody> download(@Url String fileUrl);

        @POST
        @Multipart
        Observable<Map> upload(@Url String url, @Part MultipartBody.Part file);

    }

    /** 创建请求服务 */
    public static <T> T createService(final Class<T> service) {
        return HttpRequest.getInstance().retrofit.create(service);
    }

    /**
     * 下载文件
     * @param url 请求地址
     * @return Observable 下载监听
     */
    public static Observable download(String url) {
       return HttpFileRequest.download(url, null, null);
    }

    /**
     * 下载文件
     * @param url 请求地址
     * @param path 文件存储路径
     * @param fileName 文件名称
     * @return Observable 下载监听
     */
    public static Observable download(String url, String path, String fileName) {
        if (StringUtils.isEmpty(path)) path = PathUtils.getExternalDownloadsPath();
        if (StringUtils.isEmpty(fileName)) fileName = FileUtils.getFileName(CommonUtil.getUrlLast(url));
        return HttpFileRequest.createService(FileMapper.class)
                .download(url)
                .compose(new DownLoadTransformer(path, fileName));
    }

    /**
     * 上传文件
     * @param url 请求地址
     * @param param 上传文件
     * @return Observable 上传监听
     */
    public static Observable upload(String url, UploadParam param) {
        UploadSubscribe uploadOnSubscribe = new UploadSubscribe();
        Observable progressObservable = Observable.create(uploadOnSubscribe);
        if (param.getFile() == null || !param.getFile().exists()) return null;
        uploadOnSubscribe.getUploadFile().setTotal(param.getFile().length());
        UploadRequestBody uploadRequestBody = new UploadRequestBody(param.getFile(), uploadOnSubscribe);
        MultipartBody.Part parts = MultipartBody.Part.createFormData(param.getName(), param.getFileName(), uploadRequestBody);
        Observable uploadObservable = HttpFileRequest.createService(FileMapper.class).upload(url, parts);
        return Observable.merge(progressObservable, uploadObservable);
    }

}
