package com.dolphin.core.entity;


import android.os.Parcel;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * oss文件管理
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/10
 */
@Data
@Accessors
public class OssFile extends CommonEntity {

    /**
     * 编号
     */
    private String id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 容器名称
     */
    private String bucketName;

    /**
     * 原文件名
     */
    private String original;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件大小
     */
    private Long fileSize = 0L;

    /**
     * 有效原始路径
     */
    private String availablePath;

    /**
     * 视频时长
     */
    private Long duration = 0L;

    /**
     * 媒体资源类型
     */
    private String mimeType;

    public OssFile() {}

    /** 内存反序列化对象 */
    protected OssFile(Parcel in) {
        super(in);
        id = in.readString();
        fileName = in.readString();
        bucketName = in.readString();
        original = in.readString();
        type = in.readString();
        fileSize = in.readLong();
        availablePath = in.readString();
        duration = in.readLong();
        mimeType = in.readString();
    }

    /**
     * 内存序列化对象
     * @param dest 序列化对象 (包含序列化的一些操作)
     * @param flags 0或1 (1表示当前对象需要作为返回值返回,不能立即释放资源,几乎所有情况都为0)
     * @return void
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(fileName);
        dest.writeString(bucketName);
        dest.writeString(original);
        dest.writeString(type);
        dest.writeLong(fileSize);
        dest.writeString(availablePath);
        dest.writeLong(duration);
        dest.writeString(mimeType);
    }

    /**
     * unix系统文件描述符,一般情况下为0就行
     * 0:标准输入文件stdin
     * 1:标准输出文件stdout
     * 2:标准错误输出文件stderr
     */
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OssFile> CREATOR = new Creator() {
        @Override
        public OssFile createFromParcel(Parcel in) {
            return new OssFile(in);
        }

        @Override
        public OssFile[] newArray(int size) {
            return new OssFile[size];
        }
    };
}
