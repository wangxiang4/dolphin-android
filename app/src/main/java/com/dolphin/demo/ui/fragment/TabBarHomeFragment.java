package com.dolphin.demo.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
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
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CacheConstant;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.databinding.KcFragmentTabBarHomeBinding;
import com.dolphin.demo.entity.RoutePlanLatPoint;
import com.dolphin.demo.entity.User;
import com.dolphin.demo.listener.MapGpsSensorEventListener;
import com.dolphin.demo.ui.activity.PictureSelectorActivity;
import com.dolphin.demo.ui.activity.RoutePlanActivity;
import com.dolphin.demo.ui.activity.TabBarActivity;
import com.dolphin.demo.ui.vm.TabBarHomeViewModel;
import com.dolphin.core.amap.LocationRequest;
import com.dolphin.core.amap.overlay.DrivingRouteOverlay;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.constant.AppConstant;
import com.dolphin.core.entity.MapLogisticPoint;
import com.dolphin.core.util.AMapCommonUtil;
import com.dolphin.core.util.PermissionUtil;
import com.dolphin.core.util.ToastUtil;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushAliasCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *<p>
 * 主页地图
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class TabBarHomeFragment extends BaseFragment<KcFragmentTabBarHomeBinding, TabBarHomeViewModel> implements LocationSource, AMap.OnMapTouchListener, RouteSearch.OnRouteSearchListener {

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

    /** 路线标记点 */
    private List<Marker> routeMarker = new ArrayList();

    /** 中间途径点 */
    private List<LatLonPoint> throughPoints;

    /** 路线规划活动结果处理(双向传递数据) */
    private ActivityResultLauncher<RoutePlanLatPoint> launcherResult;

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
        return R.layout.kc_fragment_tab_bar_home;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationRequest = new LocationRequest(TabBarActivity.class);
        mapGpsSensorEventListener = new MapGpsSensorEventListener(getActivity());
        locationRequest.setLocationListen(locationListener);
        singleAMapLocationClient();
        launcherResult = registerForActivityResult(new ActivityResultContract<RoutePlanLatPoint, Void>() {
            @Override
            public Void parseResult(int resultCode, @Nullable Intent intent) {
                return null;
            }
            @Override
            public Intent createIntent(@NonNull Context context, RoutePlanLatPoint routePlanLatPoint) {
                Intent intent = new Intent(getActivity(), RoutePlanActivity.class);
                intent.putExtra(CommonConstant.ROUTE_PLAN_LAT_POINT, routePlanLatPoint);
                return intent;
            }
        }, result -> {});
        // 登录进来获取到用户对象设置友盟消息推送别名,后台需要别名推送
        User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);
        if (!StringUtils.isTrimEmpty(user.getId())) {
            PushAgent mPushAgent = PushAgent.getInstance(getActivity());
            mPushAgent.addAlias(user.getId(), CommonConstant.UMENG_PUSH_USER_ALIAS_TYPE, (UPushAliasCallback) (success, message) ->{
                String msg;
                if (success) {
                    msg = "add alias success! type:" + CommonConstant.UMENG_PUSH_USER_ALIAS_TYPE + " alias:" + user.getId();
                } else {
                    msg = "add alias failure! msg:" + message;
                }
                LogUtils.i(msg);
            });
        } else throw new RuntimeException("用户对象为空请退出重写登录!");
    }

    AMapLocationListener locationListener = location -> {
        if (null != location && aMap != null && location.getErrorCode() == 0) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (locationMarker == null) {
                locationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kc_ic_map_gps))
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
            LogUtils.e("定位失败!");
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
        User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);
        LatLng latLng = new LatLng(DefaultConfig.mapCentreLat, DefaultConfig.mapCentreLng);
        if(!StringUtils.isTrimEmpty(user.getMapCenter()) && user.getMapCenter().indexOf(",") != -1){
            // 设置用户个性化自定义的中心点
            BigDecimal lat = new BigDecimal(user.getMapCenter().split(",")[1]);
            BigDecimal lng = new BigDecimal(user.getMapCenter().split(",")[0]);
            latLng = new LatLng(lat.doubleValue(), lng.doubleValue());
        }
        // 首次定位移动到地图中心点并修改一些默认属性
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, DefaultConfig.zoom, DefaultConfig.tilt, DefaultConfig.bearing)));
        mapGpsSensorEventListener.registerSensorListener();
        locationRequest.start();
        // 间隔上传gps数据
        ThreadUtils.executeByFixedAtFixRate(AppConstant.defaultThreadPoolSize,
                mViewModel.backgroundUploadLocationGpsTask(locationRequest.getLocationClient()),
                AppConstant.LOCATION_TASK_INTERVAL_TIME,
                TimeUnit.MILLISECONDS);
        try {
            mRouteSearch = new RouteSearch(getActivity());
            mRouteSearch.setRouteSearchListener(this);
            // 获取当前用户地图数据
            mViewModel.requestMapDataByCourierUser();
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 监听地图数据发生变化处理
        mViewModel.mapLogistic.observe(this, mapLogistic -> {
            routeMarker.clear();
            try {
                List<MapLogisticPoint> mapLogisticPoint = mapLogistic.getMapLogisticPoint();
                List<MapLogisticPoint> mapTaskPresetLogisticPoint = mapLogistic.getMapTaskPresetLogisticPoint();
                setMapDataPointMarker(new LatLng(mapLogistic.getCourierLat(), mapLogistic.getCourierLng()), 2);
                mapLogisticPoint.addAll(mapTaskPresetLogisticPoint);
                mapLogisticPoint.forEach(item -> {
                    if(ObjectUtils.isNotEmpty(item.getLat()) && ObjectUtils.isNotEmpty(item.getLng())){
                        setMapDataPointMarker(new LatLng(item.getLat(),item.getLng()), Integer.valueOf(item.getType()));
                    }
                });
                if (ObjectUtils.isNotEmpty(routeMarker)) {
                    Marker origin = routeMarker.get(0);
                    Marker destination = routeMarker.get(routeMarker.size() - 1);
                    List<Marker> throughMarker = routeMarker.subList(1, routeMarker.size() - 1);
                    throughPoints = throughMarker.stream().map(item -> AMapCommonUtil.convertToLatLonPoint(item.getPosition())).collect(Collectors.toList());
                    // 渲染导航路线
                    final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapCommonUtil.convertToLatLonPoint(origin.getPosition()), AMapCommonUtil.convertToLatLonPoint(destination.getPosition()));
                    // 第一个参数表示路径规划的起点和终点,第二个参数表示驾车模式,第三个参数表示途经点,第四个参数表示避让区域,第五个参数表示避让道路
                    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, throughPoints, null, "");
                    mRouteSearch.calculateDriveRouteAsyn(query);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "当前没有地图数据,请联系管理员配置!", Toast.LENGTH_LONG).show();
            }
        });
        // 已到达
        mViewModel.homeUiObservable.hasArrivedSwitchEvent.observe(this, click -> {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("确认已到达【" + "目的地" + "】？");
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage(spannableStringBuilder)
                    .setCancelable(true)
                    .setPositiveButton("确认",(dialogInterface, i) ->{
                        Intent intents = new Intent(getActivity(), TaskFormActivity.class);
                        startActivity(intents);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("取消",((dialogInterface, i) -> {
                        ToastUtils.showShort("取消到达");
                    })).show();
        });
        // 开始导航
        mViewModel.homeUiObservable.beginNavidSwitchEvent.observe(this, click -> {
            if (ObjectUtils.isNotEmpty(routeMarker)) {
                RoutePlanLatPoint routePlanLatPoint = new RoutePlanLatPoint();
                AMapLocation location = locationRequest.getLocationClient().getLastKnownLocation();
                routePlanLatPoint.setOriginPoint(new LatLonPoint(location.getLatitude(), location.getLongitude()));
                Marker taskPoint = routeMarker.get(1);
                routePlanLatPoint.setDestinationPoint(AMapCommonUtil.convertToLatLonPoint(taskPoint.getPosition()));
                launcherResult.launch(routePlanLatPoint);
            } else ToastUtils.showShort("当前没有地图数据");
        });
        // todo:
        mViewModel.pictureSelectorClick.observe(this, v -> {
            ArrayList<LocalMedia> list = new ArrayList();
            list.add(LocalMedia.generateHttpAsLocalMedia("https://wx2.sinaimg.cn/mw2000/0073ozWdly1h0afoipj8xj30kw3kmwru.jpg"));
            list.add(LocalMedia.generateHttpAsLocalMedia("https://wx4.sinaimg.cn/mw2000/0073ozWdly1h0afoj5q8ij30u04gqkb1.jpg"));
            list.add(LocalMedia.generateHttpAsLocalMedia("https://ww1.sinaimg.cn/bmiddle/bcd10523ly1g96mg4sfhag20c806wu0x.gif"));
            pictureSelectorLauncherResult.launch(list);
        });
    }

    public void setMapDataPointMarker(LatLng latlng, Integer type) {
        int resourcesId;
        switch (type) {
            case 0:
                resourcesId = R.drawable.kc_ic_hospital;
                break;
            case 1:
                resourcesId = R.drawable.kc_ic_red_flag;
                break;
            case 2:
                resourcesId = R.drawable.kc_ic_medical_kit;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        MarkerOptions markerOptions = new MarkerOptions()
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
        routeMarker.add(marker);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {}

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        if (errorCode != AMapException.CODE_AMAP_SUCCESS) {
            ToastUtil.showAmapError(getActivity(), errorCode);
            return;
        }
        if (result == null && result.getPaths() == null) {
            ToastUtils.showLong("对不起,没有搜索到相关数据！");
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
                    throughPoints
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
                        ToastUtils.showLong("手机中没有GPS模块提供,无法进行GPS定位,请更换设备");
                        break;
                    case AMapLocationQualityReport.GPS_STATUS_OFF:
                        PermissionUtil.openApplicationSettings(getActivity(), "GPS关闭,建议开启GPS,提高定位质量");
                        break;
                    case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                        PermissionUtil.openApplicationSettings(getActivity(), "没有GPS定位权限,建议开启gps定位权限");
                        break;
                }
            } else ToastUtils.showLong("定位失败,请联系系统管理员!");
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

    private ActivityResultLauncher<ArrayList<LocalMedia>> pictureSelectorLauncherResult = registerForActivityResult(new ActivityResultContract<ArrayList<LocalMedia>, ArrayList<LocalMedia>>() {
        @Override
        public ArrayList<LocalMedia> parseResult(int resultCode, @Nullable Intent intent) {
            if (intent == null) {
                return null;
            }
            return intent.getParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION);
        }
        @Override
        public Intent createIntent(@NonNull Context context, ArrayList<LocalMedia> localMediaList) {
            Intent intent = new Intent(getActivity(), PictureSelectorActivity.class);
            intent.putExtra(PictureConfig.EXTRA_RESULT_SELECTION, localMediaList);
            return intent;
        }
    }, result -> {
        // todo: OssFile数据保存自定义处理
        ToastUtil.show(getActivity(), "回调成功数据:" + new Gson().toJson(result));
        LogUtils.i("选择文件上传成功回调数据", result);
    });;


}
