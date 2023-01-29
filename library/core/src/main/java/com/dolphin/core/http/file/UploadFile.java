package com.dolphin.core.http.file;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class UploadFile {

    /** 文件总大小 */
    private long total = 0l;

    /** 已传输的文件大小 */
    private long bytesLoaded = 0l;

    /** 状态 */
    private Integer status;

}
