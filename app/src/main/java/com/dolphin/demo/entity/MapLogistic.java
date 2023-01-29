package com.dolphin.demo.entity;

import android.os.Parcel;

import com.dolphin.core.entity.CommonEntity;
import com.dolphin.core.entity.MapLogisticPoint;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/** 
 * <p>
 * 地图核心主任务表
 * </p>
 *
 * @author entfrm开发团队-王翔
 * @since 2022-07-22
 */
@Data
@Accessors
public class MapLogistic extends CommonEntity {

    /**  主键id */
    private String id;

    /**  主线物流名称 */
    private String name;

    /**  收样员ID */ 
    private String courierUserId;

    /** 收样员名称 */
    private String courierUserName;

    /** 收样员起点经度值 */
    private Double courierLng;

    /** 收样员起点纬度值 */
    private Double courierLat;

    /** 发单下级医院ID */
    private String sendOrderId;

    /** 发单下级医院名称 */
    private String sendOrderName;

    /** 发单任务类型 */
    private String sendOrderTaskType;

    /** 发单起点经度值 */
    private Double sendOrderLng;

    /** 发单起点纬度值 */
    private Double sendOrderLat;

    /** 客服上传做项目原始单文件ID */
    private String fileId;

    /** 预计时间 */
    private String estimateTime;

    /** 要求时间 */
    private String requireTime;

    /** 关联报告单生成的批次码 */
    private String batchCode;

    /** 版本控制 */
    private Integer version;

    /** 任务列表 */
    private List<MapTask> mapTask;

    /** 地图标记点列表 */
    private List<MapLogisticPoint> mapLogisticPoint;

    /** 地图转办任务预设标记点列表(获取上一个交接员设置的交接点) */
    private List<MapLogisticPoint> mapTaskPresetLogisticPoint;

    public MapLogistic() {
    }

    public MapLogistic(Parcel in) {
        super(in);
        id = in.readString();
        name = in.readString();
        courierUserId = in.readString();
        courierUserName = in.readString();
        courierLng = in.readDouble();
        courierLat = in.readDouble();
        sendOrderId = in.readString();
        sendOrderName = in.readString();
        sendOrderTaskType = in.readString();
        sendOrderLng = in.readDouble();
        sendOrderLat = in.readDouble();
        fileId = in.readString();
        estimateTime = in.readString();
        requireTime = in.readString();
        batchCode = in.readString();
        version = in.readInt();
        mapTask = in.createTypedArrayList(MapTask.CREATOR);
        mapLogisticPoint = in.createTypedArrayList(MapLogisticPoint.CREATOR);
        mapTaskPresetLogisticPoint = in.createTypedArrayList(MapLogisticPoint.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(courierUserId);
        dest.writeString(courierUserName);
        dest.writeDouble(courierLng);
        dest.writeDouble(courierLat);
        dest.writeString(sendOrderId);
        dest.writeString(sendOrderName);
        dest.writeString(sendOrderTaskType);
        dest.writeDouble(sendOrderLng);
        dest.writeDouble(sendOrderLat);
        dest.writeString(fileId);
        dest.writeString(estimateTime);
        dest.writeString(requireTime);
        dest.writeString(batchCode);
        dest.writeInt(version);
        dest.writeTypedList(mapTask);
        dest.writeTypedList(mapLogisticPoint);
        dest.writeTypedList(mapTaskPresetLogisticPoint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapLogistic> CREATOR = new Creator() {
        @Override
        public MapLogistic createFromParcel(Parcel in) {
            return new MapLogistic(in);
        }

        @Override
        public MapLogistic[] newArray(int size) {
            return new MapLogistic[size];
        }
    };

}
