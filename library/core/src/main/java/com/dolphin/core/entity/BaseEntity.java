package com.dolphin.core.entity;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 基础模型
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2021/12/28
 */
@Data
@Accessors
public class BaseEntity implements Parcelable {

    /** 多租户ID */
    protected String tenantId;

    /** 当前用户 */
    protected DolphinUser currentUser;

    public BaseEntity() {
    }

    /** 内存反序列化对象 */
    public BaseEntity(Parcel in) {
        tenantId = in.readString();
        currentUser = in.readTypedObject(DolphinUser.CREATOR);
    }

    /**
     * 内存序列化对象
     * @param dest 序列化对象 (包含序列化的一些操作)
     * @param flags 0或1 (1表示当前对象需要作为返回值返回,不能立即释放资源,几乎所有情况都为0)
     * @return void
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tenantId);
        dest.writeTypedObject(currentUser, flags);
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

    public static final Creator<BaseEntity> CREATOR = new Creator() {
        @Override
        public BaseEntity createFromParcel(Parcel in) {
            return new BaseEntity(in);
        }

        @Override
        public BaseEntity[] newArray(int size) {
            return new BaseEntity[size];
        }
    };

}
