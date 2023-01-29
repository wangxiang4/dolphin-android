package com.dolphin.core.amap.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;
import com.blankj.utilcode.util.ObjectUtils;
import com.dolphin.core.R;
import com.dolphin.core.util.AMapCommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>
 * 绘制驾车路线地图图层覆盖类
 * 经过分析高德api以及示列源码发现 SDK 9.5.0v 内部获取实时交通信息列表TMC已经废弃
 * 不管怎样请求都获取不到TMC,不能渲染规划路线交通颜色,但是使用未来多种驾车规划路线可以
 * 要渲染规划路线交通颜色只能降低版本,或者采用未来多种驾车规划路线但是这种没有途径点
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/3
 */
public class DrivingRouteOverlay extends RouteOverlay {

    /** 行驶路段 */
	private DrivePath drivePath;

    /** 路线途径点 */
    private List<LatLonPoint> throughPointList;

    /** 途径点标记集合 */
    private List<Marker> throughPointMarkerList = new ArrayList();

    /** 途径点标记可见 */
    private boolean throughPointMarkerVisible = true;

    /** 渲染路线设置  */
    private PolylineOptions mPolylineOptions;

    /**
     * 初始化 绘制驾车路线
     * @param context 活动上下文
     * @param amap 高德地图组件
     * @param drivePath 行驶路段
     * @param startPoint 起点
     * @param endPoint 终点
     * @param throughPointList 途径点集合
     * @return void
     */
    public DrivingRouteOverlay(Context context,
                               AMap amap,
                               DrivePath drivePath,
                               LatLonPoint startPoint,
                               LatLonPoint endPoint,
                               List<LatLonPoint> throughPointList) {
    	super(context);
        super.mAMap = amap;
        super.startPoint = AMapCommonUtil.convertToLatLng(startPoint);
        super.endPoint = AMapCommonUtil.convertToLatLng(endPoint);
        this.drivePath = drivePath;
        this.throughPointList = throughPointList;
    }

    /** 绘制驾车行驶路线 */
	public void drawDrivingRoute() {
        try {
            // 配置渲染路线设置
            mPolylineOptions = new PolylineOptions().color(getDriveColor())
                    .setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.icon_custtexture_driving))
                    .width(getRouteWidth());
            if (mAMap == null || drivePath == null) return;
            List<DriveStep> drivePaths = drivePath.getSteps();
            for (DriveStep step : drivePaths) {
                // 获取行驶路段点
                List<LatLonPoint> latLonPoints = step.getPolyline();
                for (LatLonPoint latlonpoint : latLonPoints) mPolylineOptions.add(AMapCommonUtil.convertToLatLng(latlonpoint));
            }
            // 绘制起点与终点标记
            if (startMarker != null) {
                startMarker.remove();
                startMarker = null;
            }
            if (endMarker != null) {
                endMarker.remove();
                endMarker = null;
            }
            addStartAndEndMarker();
            // 绘制途径点标记
            if (ObjectUtils.isNotEmpty(this.throughPointList)) {
                LatLonPoint latLonPoint;
                for (int i = 0; i < this.throughPointList.size(); i++) {
                    latLonPoint = this.throughPointList.get(i);
                    if (latLonPoint != null) {
                        throughPointMarkerList.add(mAMap
                            .addMarker((new MarkerOptions())
                                .position(AMapCommonUtil.convertToLatLng(latLonPoint))
                                .visible(throughPointMarkerVisible)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pathway))
                                .title("途径点")));
                    }
                }
            }
            // 添加渲染路线
            addPolyLine(mPolylineOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    /** 绘制完毕路线地图画布缩放范围 */
    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        if (ObjectUtils.isNotEmpty(this.throughPointList)) {
            for (int i = 0; i < this.throughPointList.size(); i++)
                b.include(AMapCommonUtil.convertToLatLng(this.throughPointList.get(i)));
        }
        return b.build();
    }

    /** 设置途经点标记是否显示 */
    public void setThroughPointIconVisibility(boolean visible) {
        try {
            throughPointMarkerVisible = visible;
            if (ObjectUtils.isNotEmpty( this.throughPointMarkerList)) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++)
                    this.throughPointMarkerList.get(i).setVisible(visible);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    /** 移除地图线段和标记覆盖物 */
    public void removeFromMap() {
        try {
            super.removeFromMap();
            if (ObjectUtils.isNotEmpty(this.throughPointMarkerList)) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).remove();
                }
                this.throughPointMarkerList.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}