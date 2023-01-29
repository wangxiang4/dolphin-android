package com.dolphin.demo.entity;

import android.os.Parcel;

import com.dolphin.core.entity.CommonEntity;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 *  地图任务
 * </p>
 *
 * @author entfrm开发团队-王翔
 * @since 2022-08-29
 */
@Data
@Accessors
public class MapTask extends CommonEntity {

    /** 主键id */
    private String id;

    /** 附属子任务名称 */
    private String name;

    /** 任务排序,为后面开发操作记录池做准备 */
    private Integer sort;

    /** 任务类型,1是普通任务,2是交接任务 */
    private String taskType;

    /** 医院id */
    private String hospitalId;

    /** 医院名称 */
    private String hospitalName;

    /** 医院经度值 */
    private Double hospitalLng;

    /** 医院纬度值 */
    private Double hospitalLat;

    /** 医检医院id */
    private String orgId;

    /** 医检医院名称 */
    private String orgName;

    /** 医检医院经度值 */
    private Double orgLng;

    /** 医检医院纬度值 */
    private Double orgLat;

    /** 收样员ID */
    private String courierUserId;

    /** 文件id */
    private String fileId;

    /** 预计时间 */
    private String estimateTime;

    /** 要求时间 */
    private String requireTime;

    /** 关联报告单生成的批次码 */
    private String batchCode;

    /** 地图主线物流ID */
    private String mapLogisticId;

    /** 版本控制 */
    private Integer version;

    /** 表格操作key */
    private String key;

    /** 地图交接预设任务列表 */
    private List<MapTaskPreset> mapTaskPreset;

    /** 回收视图滑动固定状态 */
    private Boolean pinned = false;


    public MapTask() {
    }

    /** 内存反序列化对象 */
    public MapTask(Parcel in) {
        super(in);
        id = in.readString();
        name = in.readString();
        sort = in.readInt();
        taskType = in.readString();
        hospitalId = in.readString();
        hospitalName = in.readString();
        hospitalLng = in.readDouble();
        hospitalLat = in.readDouble();
        orgId = in.readString();
        orgName = in.readString();
        orgLng = in.readDouble();
        orgLat = in.readDouble();
        courierUserId = in.readString();
        fileId = in.readString();
        estimateTime = in.readString();
        requireTime = in.readString();
        batchCode = in.readString();
        mapLogisticId = in.readString();
        version = in.readInt();
        mapTaskPreset = in.createTypedArrayList(MapTaskPreset.CREATOR);
        pinned = in.readBoolean();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(sort);
        dest.writeString(taskType);
        dest.writeString(hospitalId);
        dest.writeString(hospitalName);
        dest.writeDouble(hospitalLng);
        dest.writeDouble(hospitalLat);
        dest.writeString(orgId);
        dest.writeString(orgName);
        dest.writeDouble(orgLng);
        dest.writeDouble(orgLat);
        dest.writeString(courierUserId);
        dest.writeString(fileId);
        dest.writeString(estimateTime);
        dest.writeString(requireTime);
        dest.writeString(batchCode);
        dest.writeString(mapLogisticId);
        dest.writeInt(version);
        dest.writeTypedList(mapTaskPreset);
        dest.writeBoolean(pinned);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapTask> CREATOR = new Creator() {
        @Override
        public MapTask createFromParcel(Parcel in) {
            return new MapTask(in);
        }

        @Override
        public MapTask[] newArray(int size) {
            return new MapTask[size];
        }
    };

}
