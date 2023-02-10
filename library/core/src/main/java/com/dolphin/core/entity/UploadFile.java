package com.dolphin.core.entity;

import android.os.Parcel;

import com.dolphin.core.enums.FileObservableStatusEnum;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 上传文件状态
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/10
 */
@Data
@Accessors
public class UploadFile extends OssFile {

    /** 文件总大小 */
    private Long total = 0L;

    /** 已传输的文件大小 */
    private Long bytesLoaded = 0L;

    /** 状态 */
    private Integer status = FileObservableStatusEnum.FAIL.getStatus();

    public UploadFile() {
    }

    /** 内存反序列化对象 */
    public UploadFile(Parcel in) {
        super(in);
        total = in.readLong();
        bytesLoaded = in.readLong();
        status = in.readInt();
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
        dest.writeLong(total);
        dest.writeLong(bytesLoaded);
        dest.writeInt(status);
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

    public static final Creator<UploadFile> CREATOR = new Creator() {
        @Override
        public UploadFile createFromParcel(Parcel in) {
            return new UploadFile(in);
        }

        @Override
        public UploadFile[] newArray(int size) {
            return new UploadFile[size];
        }
    };

}
