package com.kicc.collect.entity;

import android.os.Parcel;

import com.kicc.core.entity.CommonEntity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 地图交接预设点表
 * </p>
 *
 * @author entfrm开发团队-王翔
 * @since 2022-08-15
 */
@Data
@Accessors
public class MapTaskPreset extends CommonEntity {
    
    /** 主键id */
    private String id;

    /** 预设点任务名称 */
    private String name;

    /** 交接预设任务排序,为后面开发操作记录池做准备 */
    private Integer sort;

    /** 起始预设点id */
    private String orginPresetId;

    /** 起始预设点地址名称 */
    private String orginPresetName;

    /** 起始预设点经度值 */
    private Double orginPresetLng;

    /** 起始预设点纬度值 */
    private Double orginPresetLat;

    /** 终点预设点地址id */
    private String destinationPresetId;

    /** 终点预设点地址名称 */
    private String destinationPresetName;

    /** 终点预设点经度值 */
    private Double destinationPresetLng;

    /** 终点预设点纬度值 */
    private Double destinationPresetLat;

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

    /** 地图任务ID */
    private String mapTaskId;

    /** 表格操作key */
    private String key;

    /** 版本控制 */
    private Integer version;

    public MapTaskPreset() {
    }

    /** 内存反序列化对象 */
    public MapTaskPreset(Parcel in) {
        super(in);
        id = in.readString();
        name = in.readString();
        sort = in.readInt();
        orginPresetId = in.readString();
        orginPresetName = in.readString();
        orginPresetLng = in.readDouble();
        orginPresetLat = in.readDouble();
        destinationPresetId = in.readString();
        destinationPresetName = in.readString();
        destinationPresetLng = in.readDouble();
        destinationPresetLat = in.readDouble();
        courierUserId = in.readString();
        fileId = in.readString();
        estimateTime = in.readString();
        requireTime = in.readString();
        batchCode = in.readString();
        mapLogisticId = in.readString();
        mapTaskId = in.readString();
        version = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(sort);
        dest.writeString(orginPresetId);
        dest.writeString(orginPresetName);
        dest.writeDouble(orginPresetLng);
        dest.writeDouble(orginPresetLat);
        dest.writeString(destinationPresetId);
        dest.writeString(destinationPresetName);
        dest.writeDouble(destinationPresetLng);
        dest.writeDouble(destinationPresetLat);
        dest.writeString(courierUserId);
        dest.writeString(fileId);
        dest.writeString(estimateTime);
        dest.writeString(requireTime);
        dest.writeString(batchCode);
        dest.writeString(mapLogisticId);
        dest.writeString(mapTaskId);
        dest.writeInt(version);
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

    public static final Creator<MapTaskPreset> CREATOR = new Creator() {
        @Override
        public MapTaskPreset createFromParcel(Parcel in) {
            return new MapTaskPreset(in);
        }

        @Override
        public MapTaskPreset[] newArray(int size) {
            return new MapTaskPreset[size];
        }
    };
}
