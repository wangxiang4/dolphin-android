package com.dolphin.core.amap.overlay;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.dolphin.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>
 * 基础绘制路线地图图层覆盖类
 * 包含(驾车,公交地铁,骑行,步行)
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/3
 */
public abstract class RouteOverlay {

	/** 绘制路线集合 */
	protected List<Polyline> allPolyLines = new ArrayList();

	/** 起点标记 */
	protected Marker startMarker;

	/** 终点标记 */
	protected Marker endMarker;

	/** 起点经纬度 */
	protected LatLng startPoint;

	/** 终点经纬度 */
	protected LatLng endPoint;

	/** 高德地图组件 */
	protected AMap mAMap;

	/** 活动上下文 */
	private Context mContext;

	public RouteOverlay(Context context) {
		mContext = context;
	}

	/** 移除地图线段和标记覆盖物 */
	public void removeFromMap() {
		if (startMarker != null) {
			startMarker.remove();
		}
		if (endMarker != null) {
			endMarker.remove();
		}
		for (Polyline line : allPolyLines) {
			line.remove();
		}
	}

	/** 获取起点标记icon */
	protected BitmapDescriptor getStartBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.drawable.icon_orgin);
	}

	/** 获取终点标记icon */
	protected BitmapDescriptor getEndBitmapDescriptor() {
		return BitmapDescriptorFactory.fromResource(R.drawable.icon_destination);
	}

	/** 添加起点与终点标记 */
	protected void addStartAndEndMarker() {
		startMarker = mAMap.addMarker((new MarkerOptions())
				.position(startPoint).icon(getStartBitmapDescriptor())
				.title("起点"));
		endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
				.icon(getEndBitmapDescriptor()).title("终点"));
	}

	/** 移动镜头到当前的路线范围视角 */
	public void zoomToRouteBounds() {
		if (startPoint == null || mAMap == null) return;
		try {
			LatLngBounds bounds = getLatLngBounds();
			mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/** 绘制完毕路线地图画布缩放范围 */
	protected LatLngBounds getLatLngBounds() {
		LatLngBounds.Builder b = LatLngBounds.builder();
		b.include(new LatLng(startPoint.latitude, startPoint.longitude));
		b.include(new LatLng(endPoint.latitude, endPoint.longitude));
		return b.build();
	}

	/** 添加渲染路线 */
	protected void addPolyLine(PolylineOptions options) {
		if(options == null) return;
		Polyline polyline = mAMap.addPolyline(options);
		if(polyline != null) allPolyLines.add(polyline);
	}

	/** 获取渲染路线宽度 */
	protected float getRouteWidth() {
		return 18f;
	}

	/** 获取骑行路线颜色 */
	protected int getRideColor() {
		return Color.parseColor("#2891FF");
	}

	/** 获取驾车行驶路线颜色 */
	protected int getDriveColor() {
		return Color.parseColor("#1FAC2D");
	}

}
