package com.dolphin.core.amap.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideStep;
import com.dolphin.core.R;
import com.dolphin.core.util.AMapCommonUtil;

import java.util.List;

/**
 *<p>
 * 绘制骑行路线地图图层覆盖类
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/3
 */
public class RideRouteOverlay extends RouteOverlay {

	/** 渲染路线设置  */
	private PolylineOptions mPolylineOptions;

	/** 骑行路段 */
	private RidePath ridePath;

	/**
	 * 初始化 绘制骑行路线
	 * @param context 活动上下文
	 * @param amap 高德地图组件
	 * @param ridePath 行驶路段
	 * @param start 起点
	 * @param end 终点
	 * @return void
	 */
	public RideRouteOverlay(Context context,
							AMap amap,
							RidePath ridePath,
                            LatLonPoint start,
							LatLonPoint end) {
		super(context);
		this.mAMap = amap;
		this.ridePath = ridePath;
		startPoint = AMapCommonUtil.convertToLatLng(start);
		endPoint = AMapCommonUtil.convertToLatLng(end);
	}

	/** 绘制骑行路线 */
	public void drawDrivingRoute() {
		try {
			mPolylineOptions = new PolylineOptions().color(getRideColor())
					.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.icon_custtexture_ride))
					.width(getRouteWidth());
			if (mAMap == null || ridePath == null) return;
			List<RideStep> ridePaths = ridePath.getSteps();
			for (RideStep step : ridePaths) {
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
			// 添加渲染路线
			addPolyLine(mPolylineOptions);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
