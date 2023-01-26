package com.kicc.core.http.file;

import java.io.File;

import io.reactivex.annotations.NonNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 上传参数
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/24
 */
@Data
@Accessors
public class UploadParam {

    /** 文件参数接口字段名 */
    private String name;

    /** 文件 */
    private File file;

    /** 文件名称 */
    private String fileName;

    public UploadParam(@NonNull String name, @NonNull File file) {
        this.name = name;
        this.file = file;
        this.fileName = file.getName();
    }

}
