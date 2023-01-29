package com.dolphin.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.services.core.LatLonPoint;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *<p>
 * 路线规划活动传输点
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/11
 */
@Data
@Accessors
public class RoutePlanLatPoint implements Parcelable {

    /** 起点 */
    private LatLonPoint originPoint;

    /** 终点 */
    private LatLonPoint destinationPoint;

    public RoutePlanLatPoint() {
    }

    public RoutePlanLatPoint(Parcel in) {
        originPoint = in.readTypedObject(LatLonPoint.CREATOR);
        destinationPoint = in.readTypedObject(LatLonPoint.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(originPoint, flags);
        dest.writeTypedObject(destinationPoint, flags);
    }

    public static final Creator<RoutePlanLatPoint> CREATOR = new Creator() {
        @Override
        public RoutePlanLatPoint createFromParcel(Parcel in) {
            return new RoutePlanLatPoint(in);
        }

        @Override
        public RoutePlanLatPoint[] newArray(int size) {
            return new RoutePlanLatPoint[size];
        }
    };
}
