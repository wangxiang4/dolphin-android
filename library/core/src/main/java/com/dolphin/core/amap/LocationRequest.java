package com.dolphin.core.amap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.constant.AppConstant;
import com.dolphin.core.enums.LocationRequestEnum;
import com.dolphin.core.service.AppKeepActive;

/**
 *<p>
 * LBS定位请求
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/27
 */
public class  LocationRequest implements AMapLocationListener {

    /** 活动上下文 */
    private Context mContext;

    /** 后台定位请求状态 */
    private Integer locationStatus = LocationRequestEnum.TERMINATED.getStatus();

    /** 位置接收 */
    private BroadcastReceiver locationReceiver;

    /** 判断位置接受是否注册 */
    private Boolean isRegisterLocationReceiver = false;

    /** 位置客户端,进行位置定位相关的操作 */
    public static AMapLocationClient locationClient;

    /** 位置客户端定位结果监听 */
    private AMapLocationListener locationListen;

    /** 应用保持活跃 */
    private AppKeepActive appKeepActive;

    public LocationRequest(Class<?> notificationClickStartClass) {
        this(Utils.getApp(), notificationClickStartClass);
    }

    public LocationRequest(Context mContext, Class<?> notificationClickStartClass) {
        this.mContext = mContext;
        appKeepActive = new AppKeepActive(mContext, notificationClickStartClass);
        init();
    }

    /** 启动后台定位 */
    public void start() {
        if (locationStatus == LocationRequestEnum.TERMINATED.getStatus()) {
            locationClient.startLocation();
            registerService();
            locationStatus = LocationRequestEnum.RUNNABLE.getStatus();
        }
    }

    /** 停止后台定位 */
    public void stop() {
        if (locationStatus == LocationRequestEnum.RUNNABLE.getStatus()) {
            locationClient.onDestroy();
            unregisterService();
            locationStatus = LocationRequestEnum.TERMINATED.getStatus();
        }
    }

    /** 初始化服务 */
    private void init() {
        locationClient = initLocation(getDefaultOption());
        locationClient.setLocationListener(this);
    }

    /** 初始化位置定位 */
    public AMapLocationClient initLocation(AMapLocationClientOption aMapLocationClientOption) {
        try {
            AMapLocationClient locationClient = new AMapLocationClient(mContext);
            // 设置定位参数
            locationClient.setLocationOption(aMapLocationClientOption);
            return locationClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 高德地图位置定位配置 */
    public AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        // 可选,设置定位模式,可选的模式有高精度、仅设备、仅网络,默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 可选,设置首次是否gps优先,只在高精度模式下有效。默认关闭
        mOption.setGpsFirst(false);
        // 可选,设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setHttpTimeOut(30000);
        // 可选,设置定位间隔。默认为2秒
        mOption.setInterval(2000);
        // 可选,设置是否返回逆地理地址信息。默认是true
        mOption.setNeedAddress(true);
        // 可选,设置是否单次定位。默认是false
        mOption.setOnceLocation(false);
        // 可选,设置是否等待wifi刷新,默认为false.如果设置为true,会自动变为单次定位,持续定位时不要使用
        mOption.setOnceLocationLatest(false);
        // 可选,设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        // 可选,设置是否使用传感器。默认是false
        mOption.setSensorEnable(true);
        // 可选,设置是否开启wifi扫描。默认为true,如果设置为false会同时停止主动刷新,停止以后完全依赖于系统刷新,定位位置可能存在误差
        mOption.setWifiScan(true);
        // 可选,设置是否使用缓存定位,默认为true
        mOption.setLocationCacheEnable(true);
        // 可选,设置逆地理信息的语言,默认值为默认语言(根据所在地区选择语言)
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);
        return mOption;
    }

    /** 注册位置请求服务 */
    public void registerService() {
        registerLocationReceiver();
        appKeepActive.registerService();
    }

    /** 取消位置请求服务 */
    public void unregisterService() {
        unregisterLocationReceiver();
        appKeepActive.unregisterService();
    }

    /** 注册位置监听广播 */
    public void registerLocationReceiver () {
        if (isRegisterLocationReceiver) return;
        isRegisterLocationReceiver = true;
        if (null == locationReceiver) {
            locationReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (null != locationClient && intent.getAction().equals(AppConstant.KEEP_ACTIVE_TASK_BROADCAST_UPDATE)) {
                        locationClient.startLocation();
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.KEEP_ACTIVE_TASK_BROADCAST_UPDATE);
        mContext.registerReceiver(locationReceiver, filter);
    }

    /** 取消位置监听广播 */
    public void unregisterLocationReceiver() {
        if (!isRegisterLocationReceiver) return;
        if (null != locationReceiver) mContext.unregisterReceiver(locationReceiver);
        isRegisterLocationReceiver = false;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && locationListen != null) {
            locationListen.onLocationChanged(aMapLocation);
        }
    }

    public AMapLocationClient getLocationClient() {
        return locationClient;
    }

    public void setLocationClient(AMapLocationClient locationClient) {
        this.locationClient = locationClient;
    }

    public AMapLocationListener getLocationListen() {
        return locationListen;
    }

    public void setLocationListen(AMapLocationListener locationListen) {
        this.locationListen = locationListen;
    }

    public Integer getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(Integer locationStatus) {
        this.locationStatus = locationStatus;
    }

}
