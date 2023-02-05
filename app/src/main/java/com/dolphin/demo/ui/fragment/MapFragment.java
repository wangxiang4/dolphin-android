package com.dolphin.demo.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.dolphin.core.amap.LocationRequest;
import com.dolphin.core.amap.overlay.DrivingRouteOverlay;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.util.PermissionUtil;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentMapBinding;
import com.dolphin.demo.listener.MapGpsSensorEventListener;
import com.dolphin.demo.ui.activity.TabBarActivity;
import com.dolphin.demo.ui.vm.MapViewModel;

import java.util.List;

/**
 *<p>
 * 高德地图
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class MapFragment extends BaseFragment<FragmentMapBinding, MapViewModel> implements LocationSource, AMap.OnMapTouchListener, RouteSearch.OnRouteSearchListener {

    /** 高德地图组件 */
    private AMap aMap;

    /** 高德地图ui视图 */
    private MapView mapView;

    /** LBS定位请求 */
    private LocationRequest locationRequest;

    /** 当前位置gps标记 */
    private Marker locationMarker;

    /** 用户手动移动地图标记 */
    public static boolean userMoveToLocationMark = true;

    /** 地图旋转gps方向传感监听 */
    private MapGpsSensorEventListener mapGpsSensorEventListener;

    /** 路线搜索 */
    private RouteSearch mRouteSearch;

    /** 默认地图配置 */
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
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_map;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationRequest = new LocationRequest(getActivity().getClass());
        mapGpsSensorEventListener = new MapGpsSensorEventListener(getActivity());
        locationRequest.setLocationListen(locationListener);
        singleAMapLocationClient();
    }

    AMapLocationListener locationListener = location -> {
        if (null != location && aMap != null && location.getErrorCode() == 0) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (locationMarker == null) {
                locationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_gps))
                        .anchor(0.5f, 0.5f));
                mapGpsSensorEventListener.setGpsMarker(locationMarker);
                mapGpsSensorEventListener.setAMap(aMap);
            } else if (userMoveToLocationMark){
                locationMarker.setPosition(latLng);
            } else {
                locationMarker.setPosition(latLng);
                // 地图画布动画移动的时间,最好不要比定位间隔长,如果定位间隔2000ms动画移动时间最好小于2000ms,可以使用1000ms
                // 如果超过了，需要在myCancelCallback中进行处理被打断的情况
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng), 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        locationMarker.setPosition(latLng);
                    }
                    @Override
                    public void onCancel() {
                        locationMarker.setPosition(latLng);
                    }
                });
            }
        } else {
            ToastUtil.showAmapError(location.getErrorCode());
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = getView().findViewById(R.id.amap);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        aMap.getUiSettings().setLogoBottomMargin(-100);
        // 设置高德地图ui交互
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 打开定位图层
        aMap.setMyLocationEnabled(true);
        // 自定义地图模式
        aMap.setLocationSource(this);
        aMap.setOnMapTouchListener(this);
        // 设置地图默认中心点
        LatLng latLng = new LatLng(DefaultConfig.mapCentreLat, DefaultConfig.mapCentreLng);
        // 首次定位移动到地图中心点并修改一些默认属性
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, DefaultConfig.zoom, DefaultConfig.tilt, DefaultConfig.bearing)));
        mapGpsSensorEventListener.registerSensorListener();
        locationRequest.start();
        try {
            mRouteSearch = new RouteSearch(getActivity());
            mRouteSearch.setRouteSearchListener(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    class PointData {
        String id;
        String label;
        Double mapLng;
        Double mapLat;
        public PointData(String id, String label, Double mapLng, Double mapLat) {
            this.id = id;
            this.label = label;
            this.mapLng = mapLng;
            this.mapLat = mapLat;
        }
    }


    List<PointData> carPointData = CollectionUtils.newArrayList(
            new PointData("001","小黄车", 112.918119, 28.282891),
            new PointData("002","小绿车", 112.918919, 28.282991),
            new PointData("003","小红车", 112.918019, 28.283991)
    );

    List<PointData> gasStationPointData = CollectionUtils.newArrayList(
            new PointData("001","地沟油加油站", 112.919043, 28.288623),
            new PointData("002","一路平安加油站", 112.919165, 28.289924),
            new PointData("003","一把火加油站", 112.919965, 28.289924)
    );

    @Override
    public void onStart() {
        super.onStart();
        try {
            carPointData.forEach(item -> {
                if(ObjectUtils.isNotEmpty(item.mapLat) && ObjectUtils.isNotEmpty(item.mapLng)){
                    setMapDataPointMarker(new LatLng(item.mapLat, item.mapLng), item.label, 0);
                }
            });
            gasStationPointData.forEach(item -> {
                if(ObjectUtils.isNotEmpty(item.mapLat) && ObjectUtils.isNotEmpty(item.mapLng)){
                    setMapDataPointMarker(new LatLng(item.mapLat, item.mapLng), item.label, 1);
                }
            });
            // 渲染导航路线
            final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(carPointData.get(0).mapLat, carPointData.get(0).mapLng), new LatLonPoint(gasStationPointData.get(0).mapLat, gasStationPointData.get(0).mapLng));
            // 第一个参数表示路径规划的起点和终点,第二个参数表示驾车模式,第三个参数表示途经点,第四个参数表示避让区域,第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMapDataPointMarker(LatLng latlng, String title, Integer type) {
        int resourcesId;
        switch (type) {
            case 0:
                resourcesId = R.drawable.icon_car;
                break;
            case 1:
                resourcesId = R.drawable.icon_gasstation;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(resourcesId))
                .position(latlng).anchor(0.5f, 0.5f);
        Marker marker = aMap.addMarker(markerOptions);
        // 设置生长动画
        Animation animation = new ScaleAnimation(0,1,0,1);
        animation.setInterpolator(new LinearInterpolator());
        // 整个移动所需要的时间
        animation.setDuration(1000);
        // 设置动画
        marker.setAnimation(animation);
        // 开始动画
        marker.startAnimation();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {}

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
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
            if(drivePath == null) return;
            DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                    getActivity(),
                    aMap,
                    drivePath,
                    result.getStartPos(),
                    result.getTargetPos(),
                    null
            );
            drivingRouteOverlay.removeFromMap();
            drivingRouteOverlay.drawDrivingRoute();
            drivingRouteOverlay.zoomToRouteBounds();
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {}

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int errorCode) {}

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapGpsSensorEventListener.registerSensorListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mapGpsSensorEventListener.unRegisterSensorListener();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //fixme 内部高德视图组件销毁修改了内存标记,安卓11后不让修改,会导致内存漏动
        // 等待后面高德库更新修复这个问题,目前只能关闭堆指针标记
        // https://source.android.com/devices/tech/debug/tagged-pointers
        mapView.onDestroy();
    }

    /** 单次定位,用于判断设备权限以及缺少GPS的硬件,提示不能使用此软件 */
    private void singleAMapLocationClient() {
        AMapLocationClient singleLocationClient = locationRequest.initLocation(locationRequest.getDefaultOption().setOnceLocation(true));
        singleLocationClient.startLocation();
        singleLocationClient.setLocationListener(location -> {
            if (null != location) {
                switch (location.getLocationQualityReport().getGPSStatus()){
                    case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                        ToastUtil.show("手机中没有GPS模块提供,无法进行GPS定位,请更换设备");
                        break;
                    case AMapLocationQualityReport.GPS_STATUS_OFF:
                        PermissionUtil.openApplicationSettings(getActivity(), "GPS关闭,建议开启GPS,提高定位质量");
                        break;
                    case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                        PermissionUtil.openApplicationSettings(getActivity(), "没有GPS定位权限,建议开启gps定位权限");
                        break;
                }
            } else ToastUtil.show("定位失败,请联系系统管理员!");
            singleLocationClient.onDestroy();
        });
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        userMoveToLocationMark = false;
        if (locationRequest.getLocationClient() == null) {
            locationRequest.getLocationClient().startLocation();
        }
    }

    @Override
    public void deactivate() {
       LogUtils.i("地图销毁时不结束后台定位,让后台定位即使后台关闭app也能运行!");
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        userMoveToLocationMark = true;
    }
}
