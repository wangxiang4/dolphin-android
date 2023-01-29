package com.dolphin.core.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteRailwayItem;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.dolphin.core.R;

import java.text.DecimalFormat;
import java.util.List;

import lombok.experimental.UtilityClass;

/**
 *<p>
 * 高德地图通用工具
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/4
 */
@UtilityClass
public class AMapCommonUtil {

	/**
	 * 获取规划公里数
	 * @param distance 出行路线规划总距离
	 * @return 公里
	 */
	public String getPlanKilometer(int distance) {
		// 10 km
		if (distance > 10000) {
			int dis = distance / 1000;
			return dis + "公里";
		}

		// 1 km
		if (distance > 1000) {
			float dis = (float) distance / 1000;
			DecimalFormat decimalFormat = new DecimalFormat("##0.0");
			String str = decimalFormat.format(dis);
			return str + "公里";
		}

		if (distance > 100) {
			int dis = distance / 50 * 50;
			return dis + "公里";
		}

		int dis = distance / 10 * 10;
		if (dis == 0) {
			dis = 10;
		}

		return dis + "公里";
	}

	/**
	 * 获规划预计时间
	 * @param duration 持续时间
	 * @return 时间
	 */
	public String getPlanTime(int duration) {
		if (duration > 3600) {
			int hour = duration / 3600;
			int minutes = (duration % 3600) / 60;
			return hour + "小时" + minutes + "分钟";
		}
		if (duration >= 60) {
			int minutes = duration / 60;
			return minutes + "分钟";
		}
		return duration + "秒";
	}

	/**
	 * 获取行驶动作指示资源
	 * @param actionName 行驶动作名称
	 * @return 图片
	 */
	public int getDrivingActionResource(String actionName) {
		if ("左转".equals(actionName)) {
			return R.drawable.icon_driving2;
		}
		if ("右转".equals(actionName)) {
			return R.drawable.icon_driving1;
		}
		if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName)) {
			return R.drawable.icon_driving6;
		}
		if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName)) {
			return R.drawable.icon_driving5;
		}
		if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName)) {
			return R.drawable.icon_driving7;
		}
		if ("向右后方行驶".equals(actionName)) {
			return R.drawable.icon_driving8;
		}
		if ("直行".equals(actionName)) {
			return R.drawable.icon_driving3;
		}
		if ("减速行驶".equals(actionName)) {
			return R.drawable.icon_driving4;
		}
		return R.drawable.icon_driving3;
	}

	/**
	 * 获取步行动作指示资源
	 * @param actionName 步行动作名称
	 * @return 图片
	 */
	public int getWalkActionResource(String actionName) {
		if ("左转".equals(actionName)) {
			return R.drawable.icon_driving2;
		}
		if ("右转".equals(actionName)) {
			return R.drawable.icon_driving1;
		}
		if ("向左前方".equals(actionName) || "靠左".equals(actionName) || actionName.contains("向左前方")) {
			return R.drawable.icon_driving6;
		}
		if ("向右前方".equals(actionName) || "靠右".equals(actionName) || actionName.contains("向右前方")) {
			return R.drawable.icon_driving5;
		}
		if ("向左后方".equals(actionName)|| actionName.contains("向左后方")) {
			return R.drawable.icon_driving7;
		}
		if ("向右后方".equals(actionName)|| actionName.contains("向右后方")) {
			return R.drawable.icon_driving8;
		}
		if ("直行".equals(actionName)) {
			return R.drawable.icon_driving3;
		}
		if ("通过人行横道".equals(actionName)) {
			return R.drawable.icon_driving9;
		}
		if ("通过过街天桥".equals(actionName)) {
			return R.drawable.icon_driving11;
		}
		if ("通过地下通道".equals(actionName)) {
			return R.drawable.icon_driving10;
		}
		return R.drawable.icon_driving13;
	}


	/**
	 * 获取公交规划路线标题(包含多种规划到达路线)
	 * @param busPath 公交路线
	 * @return 标题
	 */
	public String getBusPathTitle(BusPath busPath) {
		if (ObjectUtils.isEmpty(busPath)) return "";
		// 公交规划线路行驶步骤
		List<BusStep> busSteps = busPath.getSteps();
		if (ObjectUtils.isEmpty(busSteps)) return "";
		// 规划路线标题
		StringBuffer sb = new StringBuffer();
		for (BusStep busStep : busSteps) {
			StringBuffer title = new StringBuffer();
		    if (busStep.getBusLines().size() > 0) {
				for (RouteBusLineItem busLine : busStep.getBusLines()) {
					if (busLine == null) continue;
					// 去掉详细地名
				    String busLineName = StringUtils.isTrimEmpty(busLine.getBusLineName()) ? ""
							: busLine.getBusLineName().replaceAll("\\(.*?\\)", "");
				    title.append(busLineName);
				    title.append(" / ");
			    }
				sb.append(title.substring(0, title.length() - 3));
				sb.append(" > ");
			}
			// 获取地铁名称
			if (busStep.getRailway() != null) {
				RouteRailwayItem railway = busStep.getRailway();
				sb.append(railway.getTrip()+"("+railway.getDeparturestop().getName() + " - "+railway.getArrivalstop().getName()+")");
				sb.append(" > ");
			}
		}
		// 返回并去掉最后的 >
		return sb.substring(0, sb.length() - 3);
	}

	/**
	 * 获取公交规划路线预计规划信息
	 * @param busPath 公交路线
	 * @return 预计规划信息
	 */
	public String getBusPathPlanInfo(BusPath busPath) {
		if (ObjectUtils.isEmpty(busPath)) return "";
		long duration = busPath.getDuration();
		String time = getPlanTime((int) duration);
		float subDistance = busPath.getDistance();
		String subDis = getPlanKilometer((int) subDistance);
		float walkDistance = busPath.getWalkDistance();
		String walkDis = getPlanKilometer((int) walkDistance);
		return time + " | " + subDis + " | 步行" + walkDis;
	}

	/**
	 * 经纬度点对象转经纬度地理坐标对象
	 * @param latLonPoint 经纬度点对象
	 * @return 经纬度地理坐标对象
	 */
	public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
	}

	/**
	 * 经纬度地理坐标对象转经纬度点对象
	 * @param latLng 经纬度地理坐标对象
	 * @return 经纬度点对象
	 */
	public static LatLonPoint convertToLatLonPoint(LatLng latLng) {
		return new LatLonPoint(latLng.latitude, latLng.longitude);
	}

}
