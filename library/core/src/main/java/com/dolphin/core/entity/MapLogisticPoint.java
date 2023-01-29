package com.dolphin.core.entity;

import android.os.Parcel;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 地图核心主任务表
 * </p>
 *
 * @author entfrm开发团队-王翔
 * @since 2022-08-29
 */
@Data
@Accessors
public class MapLogisticPoint extends CommonEntity {

    /** 主键id */
    private String id;

    /** 标记点医院ID(医检与医院) */
    private String hospitalId;

    /** 标记点医院名称(医检与医院) */
    private String hospitalName;

    /** 经度值 */
    private Double lng;

    /** 纬度值 */
    private Double lat;

    /** 标记点排序,为后面开发操作记录池做准备 */
    private Integer sort;

    /** 区分是医院还是医检标记点,0医院,1医检 */
    private String type;

    /** 任务类型,1是普通任务,2是交接任务 */
    private String taskType;

    /** 关联报告单生成的批次码 */
    private String batchCode;

    /** 收样员ID */
    private String courierUserId;

    /** 地图主线物流ID */
    private String mapLogisticId;

    /** 地图任务ID */
    private String mapTaskId;

    /** 版本控制 */
//    private Integer version;

    /** 回收视图滑动固定状态 */
    private Boolean pinned = false;

    public MapLogisticPoint() {
    }

    /** 内存反序列化对象 */
    public MapLogisticPoint(Parcel in) {
        super(in);
        id = in.readString();
        hospitalId = in.readString();
        hospitalName = in.readString();
        lng = in.readDouble();
        lat = in.readDouble();
        sort = in.readInt();
        type = in.readString();
        taskType = in.readString();
        batchCode = in.readString();
        courierUserId = in.readString();
        mapLogisticId = in.readString();
        mapTaskId = in.readString();
//        version = in.readInt();
        pinned = in.readBoolean();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(hospitalId);
        dest.writeDouble(lng);
        dest.writeDouble(lat);
        dest.writeInt(sort);
        dest.writeString(type);
        dest.writeString(taskType);
        dest.writeString(batchCode);
        dest.writeString(courierUserId);
        dest.writeString(mapLogisticId);
        dest.writeString(mapTaskId);
//        dest.writeInt(version);
        dest.writeBoolean(pinned);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapLogisticPoint> CREATOR = new Creator() {
        @Override
        public MapLogisticPoint createFromParcel(Parcel in) {
            return new MapLogisticPoint(in);
        }

        @Override
        public MapLogisticPoint[] newArray(int size) {
            return new MapLogisticPoint[size];
        }
    };


}
