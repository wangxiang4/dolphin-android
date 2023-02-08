package com.dolphin.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.RideRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.dolphin.core.amap.overlay.DrivingRouteOverlay;
import com.dolphin.core.amap.overlay.RideRouteOverlay;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.util.AMapCommonUtil;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.databinding.ActivityRoutePlanBinding;
import com.dolphin.demo.entity.RoutePlanLatPoint;
import com.dolphin.demo.ui.vm.RoutePlanViewModel;

/**
 *<p>
 * 导航路线规划
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/9
 */
public class RoutePlanActivity extends BaseActivity<ActivityRoutePlanBinding, RoutePlanViewModel> implements OnRouteSearchListener {


	private AMap aMap;
	private MapView mapView;
	private RouteSearch mRouteSearch;
	private final int ROUTE_TYPE_DRIVE = 1;
	private final int ROUTE_TYPE_RIDE = 2;

	private RelativeLayout mRoutePlanBottomLayout;
	private RelativeLayout mRouteDriveLayout;
	private TextView mRouteTime;
	private ImageView mDrive;
	private ImageView mRide;
	private LinearLayout mRouteDetail;

	private LatLonPoint mOriginPoint;
	private LatLonPoint mDestinationPoint;
	private MaterialDialog mMaterialDialog;

	interface DefaultConfig {

		// 默认中心点长沙望城区域砂之船奥莱
		double mapCentreLat = 28.288623;
		double mapCentreLng = 112.919043;
		// 地图缩放级别
		float zoom  = 17f;
		// 3D地图倾斜度
		float tilt = 55f;
		// 正视前方可视区域方向
		float bearing = 300;

	}

	@Override
	public int setContentView(Bundle savedInstanceState) {
		return R.layout.activity_route_plan;
	}

	@Override
	public int setVariableId() {
		return BR.viewModel;
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// 获取传递参数
		RoutePlanLatPoint routePlanLatPoint = getIntent().getParcelableExtra(CommonConstant.ROUTE_PLAN_LAT_POINT);
		mOriginPoint = routePlanLatPoint.getOriginPoint();
		mDestinationPoint = routePlanLatPoint.getDestinationPoint();
		// 初始化高德地图
		mapView = findViewById(R.id.route_map);
		mapView.onCreate(bundle);
		aMap = mapView.getMap();
		aMap.getUiSettings().setLogoBottomMargin(-100);
		aMap.getUiSettings().setZoomControlsEnabled(false);
		try {
			mRouteSearch = new RouteSearch(this);
			mRouteSearch.setRouteSearchListener(this);
		} catch (AMapException e) {
			e.printStackTrace();
		}
		LatLng latLng = new LatLng(DefaultConfig.mapCentreLat, DefaultConfig.mapCentreLng);
		// 首次定位移动到地图中心点并修改一些默认属性
		aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, DefaultConfig.zoom, DefaultConfig.tilt, DefaultConfig.bearing)));
		mRoutePlanBottomLayout = findViewById(R.id.route_plan_bottom);
		mRouteDriveLayout = findViewById(R.id.route_drive_layout);
		mRouteTime = findViewById(R.id.route_time);
		mRouteDetail = findViewById(R.id.route_detail);
		mDrive = findViewById(R.id.route_drive);
		mRide = findViewById(R.id.route_ride);
		mRouteDriveLayout.performClick();
	}

	public void onDriveClick(View view) {
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DRIVING_SINGLE_DEFAULT);
		mDrive.setImageResource(R.drawable.icon_route_drive_select);
		mRide.setImageResource(R.drawable.icon_route_ride_normal);
	}

	public void onRideClick(View view) {
		searchRouteResult(ROUTE_TYPE_RIDE, RouteSearch.DRIVING_SINGLE_DEFAULT);
		mDrive.setImageResource(R.drawable.icon_route_drive_normal);
		mRide.setImageResource(R.drawable.icon_route_ride_select);
	}

	public void searchRouteResult(int routeType, int mode) {
		if (mOriginPoint == null) {
			ToastUtil.show("起点未设置");
			return;
		}
		if (mDestinationPoint == null) {
			ToastUtil.show("终点未设置");
		}
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mOriginPoint, mDestinationPoint);
		if (routeType == ROUTE_TYPE_DRIVE) {
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null, null, "");
			mRouteSearch.calculateDriveRouteAsyn(query);
		} else if (routeType == ROUTE_TYPE_RIDE) {
			RideRouteQuery query = new RideRouteQuery(fromAndTo, mode);
			mRouteSearch.calculateRideRouteAsyn(query);
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		closeProgressDialog();
		aMap.clear();
		if (errorCode != AMapException.CODE_AMAP_SUCCESS) {
			ToastUtil.showAmapError(errorCode);
			return;
		}
		if (result == null && result.getPaths() == null) {
			ToastUtil.show("对不起,没有搜索到相关数据！");
			return;
		}
		if (result.getPaths().size() > 0) {
			final DrivePath drivePath = result.getPaths().get(0);
			if (drivePath == null) return;
			DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
					this,
					aMap,
					drivePath,
					result.getStartPos(),
					result.getTargetPos(),
					null
			);
			drivingRouteOverlay.removeFromMap();
			drivingRouteOverlay.drawDrivingRoute();
			drivingRouteOverlay.zoomToRouteBounds();

			int distance = (int) drivePath.getDistance();
			int duration = (int) drivePath.getDuration();

			String routeDetail = AMapCommonUtil.getPlanTime(duration) + "(" + AMapCommonUtil.getPlanKilometer(distance) + ")";
			mRouteTime.setText(routeDetail);
			mRouteDetail.setOnClickListener(view -> {
				Intent intent = new Intent(this, DriveRouteDetailActivity.class);
				intent.putExtra("drive_path", drivePath);
				startActivity(intent);
			});
		}
	}

	@Override
	public void onRideRouteSearched(RideRouteResult result, int errorCode) {
		closeProgressDialog();
		aMap.clear();
		if (errorCode != AMapException.CODE_AMAP_SUCCESS) {
			ToastUtil.showAmapError(errorCode);
			return;
		}
		if (result == null && result.getPaths() == null) {
			ToastUtil.show("对不起,没有搜索到相关数据！");
			return;
		}
		if (result.getPaths().size() > 0) {
			final RidePath ridePath = result.getPaths().get(0);
			if (ridePath == null) return;
			RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
					this,
					aMap,
					ridePath,
					result.getStartPos(),
					result.getTargetPos()
			);
			rideRouteOverlay.removeFromMap();
			rideRouteOverlay.drawDrivingRoute();
			rideRouteOverlay.zoomToRouteBounds();

			int distance = (int) ridePath.getDistance();
			int duration = (int) ridePath.getDuration();

			String routeDetail = AMapCommonUtil.getPlanTime(duration) + "(" + AMapCommonUtil.getPlanKilometer(distance) + ")";
			mRouteTime.setText(routeDetail);
			mRouteDetail.setOnClickListener(view -> {
				Intent intent = new Intent(this, RideRouteDetailActivity.class);
				intent.putExtra("ride_path", ridePath);
				startActivity(intent);
			});
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	private void showProgressDialog() {
		if (mMaterialDialog == null) {
			mMaterialDialog= new MaterialDialog.Builder(this)
				.progress(true, 0)
				.progressIndeterminateStyle(true)
				.canceledOnTouchOutside(false)
				.backgroundColorRes(com.dolphin.core.R.color.white)
				.keyListener((dialog, keyCode, event) -> false).build();
		}
		mMaterialDialog = mMaterialDialog.getBuilder().title("正在搜索").build();
		mMaterialDialog.show();
	}

	private void closeProgressDialog() {
		if (mMaterialDialog != null) {
			mMaterialDialog.dismiss();
		}
	}


}

