package com.dolphin.core.http.file;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors
public class DownLoadFile {

    /** 目标本地文件存放路径 */
    private String destFileDir;

    /** 目标文件名 */
    private String destFileName;

    /** 临时目标文件名,防止写入文件出错,导致文件删除了,先在临时文件中写完在替换 */
    private String destFileNameTmp;

    /** 文件总大小 */
    private long total;

    /** 已传输的文件大小 */
    private long bytesLoaded = 0l;

    /** 状态 */
    private Integer status;

}
