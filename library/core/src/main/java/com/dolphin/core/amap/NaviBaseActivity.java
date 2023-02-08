package com.dolphin.core.amap;

import android.os.Bundle;
import android.view.Window;

import com.amap.api.maps.AMapException;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.ParallelRoadListener;
import com.amap.api.navi.enums.AMapNaviParallelRoadStatus;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.blankj.utilcode.util.LogUtils;
import com.dolphin.core.util.ToastUtil;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

/**
 *<p>
 * 导航基础活动
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/8
 */
public class NaviBaseActivity extends RxAppCompatActivity implements AMapNaviListener, AMapNaviViewListener, ParallelRoadListener {

    protected AMapNaviView mAMapNaviView;
    protected AMapNavi mAMapNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            mAMapNavi = AMapNavi.getInstance(getApplicationContext());
            mAMapNavi.addAMapNaviListener(this);
            mAMapNavi.addParallelRoadListener(this);
            mAMapNavi.setUseInnerVoice(true, true);

            //设置模拟导航的行车速度
            mAMapNavi.setEmulatorNaviSpeed(75);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        if (mAMapNavi!=null){
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
        }
    }

    @Override
    public void onInitNaviFailure() {
        ToastUtil.show("init navi Failed");
    }

    @Override
    public void onInitNaviSuccess() {
        // 初始化成功
    }

    @Override
    public void onStartNavi(int type) {
        // 开始导航回调
    }

    @Override
    public void onTrafficStatusUpdate() {
        // 交通状态更新
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        // 当前位置更新回调
    }

    @Override
    public void onGetNavigationText(int type, String text) {
        // 播报类型和播报文字回调
    }

    @Override
    public void onGetNavigationText(String s) {
        // 获取导航文本回调
    }

    @Override
    public void onEndEmulatorNavi() {
        // 结束模拟导航
    }

    @Override
    public void onArriveDestination() {
        // 到达目的地
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        // 计算路线规划错误回调
    }

    @Override
    public void onReCalculateRouteForYaw() {
        // 偏航后重新计算路线回调
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        // 拥堵后重新计算路线回调
    }

    @Override
    public void onArrivedWayPoint(int wayID) {
        // 到达途径点
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
        // GPS开关状态回调
    }

    @Override
    public void onNaviSetting() {
        // 底部导航设置点击回调
    }

    @Override
    public void onNaviMapMode(int naviMode) {
        // 导航态车头模式，0:车头朝上状态；1:正北朝上模式。
    }

    @Override
    public void onNaviCancel() {
        finish();
    }


    @Override
    public void onNaviTurnClick() {
        // 转弯view的点击回调
    }

    @Override
    public void onNextRoadClick() {
        // 下一个道路View点击回调
    }


    @Override
    public void onScanViewButtonClick() {
        // 全览按钮点击回调
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {
        // 更新可视区域回调
    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {
        // 更新服务区域回调
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        // 导航过程中的信息更新，请看NaviInfo的具体说明
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        // 已过时
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        // 显示放大图回调
    }

    @Override
    public void hideCross() {
        // 隐藏放大图回调
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
        // 显示车道信息

    }

    @Override
    public void hideLaneInfo() {
        // 隐藏车道信息
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        // 多路径算路成功回调
    }

    @Override
    public void notifyParallelRoad(int i) {
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        //更新交通设施信息
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        //更新巡航模式的统计信息
    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        //更新巡航模式的拥堵信息
    }

    @Override
    public void onPlayRing(int i) {

    }


    @Override
    public void onLockMap(boolean isLock) {
        // 锁地图状态发生变化时回调
    }

    @Override
    public void onNaviViewLoaded() {
        LogUtils.d("导航页面加载成功");
        LogUtils.d("请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
    }

    @Override
    public void onMapTypeChanged(int i) {
    }

    @Override
    public void onNaviViewShowMode(int i) {
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }


    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {
    }

    @Override
    public void hideModeCross() {
    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {
    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult result) {
        //路线计算失败
        LogUtils.e("dm", "--------------------------------------------");
        LogUtils.i("dm", "路线计算失败：错误码=" + result.getErrorCode() + ",Error Message= " + result.getErrorDescription());
        LogUtils.i("dm", "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/");
        LogUtils.e("dm", "--------------------------------------------");
        ToastUtil.show("errorInfo：" + result.getErrorDetail() + ", Message：" + result.getErrorDescription());
    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {
    }

    @Override
    public void onGpsSignalWeak(boolean b) {
    }

    @Override
    public void notifyParallelRoad(AMapNaviParallelRoadStatus aMapNaviParallelRoadStatus) {
        if (aMapNaviParallelRoadStatus.getmElevatedRoadStatusFlag() == 1) {
            ToastUtil.show("当前在高架上");
            LogUtils.d("当前在高架上");
        } else if (aMapNaviParallelRoadStatus.getmElevatedRoadStatusFlag() == 2) {
            ToastUtil.show("当前在高架下");
            LogUtils.d("当前在高架下");
        }

        if (aMapNaviParallelRoadStatus.getmParallelRoadStatusFlag() == 1) {
            ToastUtil.show("当前在主路");
            LogUtils.d("当前在主路");
        } else if (aMapNaviParallelRoadStatus.getmParallelRoadStatusFlag() == 2) {
            ToastUtil.show("当前在辅路");
            LogUtils.d("当前在辅路");
        }
    }

}
