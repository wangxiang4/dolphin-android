package com.dolphin.core.entity;

import android.os.Parcel;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 通用模型
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2021/12/28
 */
@Data
@Accessors
public class CommonEntity extends BaseEntity {

    /** 创建id */
    protected String createById;

    /** 创建者 */
    protected String createByName;

    /** 创建时间 */
    protected String createTime;

    /** 更新id */
    protected String updateById;

    /** 更新者 */
    protected String updateByName;

    /** 更新时间 */
    protected String updateTime;

    /** 备注 */
    protected String remarks;

    /** 删除标志（0代表存在 1代表删除）*/
    protected String delFlag;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;

    public CommonEntity() {
    }

    /** 内存反序列化对象 */
    public CommonEntity(Parcel in) {
        super(in);
        createById = in.readString();
        createByName = in.readString();
        createTime = in.readString();
        updateById = in.readString();
        updateByName = in.readString();
        updateTime = in.readString();
        remarks = in.readString();
        delFlag = in.readString();
        beginTime = in.readString();
        endTime = in.readString();
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
        dest.writeString(createById);
        dest.writeString(createByName);
        dest.writeString(createTime);
        dest.writeString(updateById);
        dest.writeString(updateByName);
        dest.writeString(updateTime);
        dest.writeString(remarks);
        dest.writeString(delFlag);
        dest.writeString(beginTime);
        dest.writeString(endTime);
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

    public static final Creator<CommonEntity> CREATOR = new Creator() {
        @Override
        public CommonEntity createFromParcel(Parcel in) {
            return new CommonEntity(in);
        }

        @Override
        public CommonEntity[] newArray(int size) {
            return new CommonEntity[size];
        }
    };


}
