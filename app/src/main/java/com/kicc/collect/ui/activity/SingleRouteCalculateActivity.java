package com.kicc.collect.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.amap.api.maps.AMapException;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.NaviSetting;
import com.amap.api.navi.ParallelRoadListener;
import com.amap.api.navi.enums.AMapNaviParallelRoadStatus;
import com.amap.api.navi.enums.NaviType;
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
import com.amap.api.navi.model.NaviLatLng;
import com.blankj.utilcode.util.ToastUtils;
import com.kicc.collect.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 单路线导航设置
 * create by liu 2022/8/3
 * @author liusixiang
 */
public class SingleRouteCalculateActivity extends Activity implements AMapNaviListener, AMapNaviViewListener, ParallelRoadListener {

    protected AMapNaviView mAMapNaviView;
    protected AMapNavi mAMapNavi;
    /** 起始点 */
    protected NaviLatLng mEndLatlng;
    protected NaviLatLng mStartLatlng;
    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();
    protected List<NaviLatLng> mWayPointList = new ArrayList<NaviLatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /** APP原有的标题栏-必须从一开始就处理 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kc_activity_basic_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        /** 先加载路径数据 */
        mEndLatlng = new NaviLatLng(28.326639, 112.891276);
        mStartLatlng = new NaviLatLng(28.287267, 112.931448);
        inits();
    }

    private void inits() {
        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setTilt(0); // 设置初始视角
        options.isTrafficLine();  // 显示交通路况
        options.setAfterRouteAutoGray(true);  //通过路线是否自动置灰
        options.setAutoLockCar(true);   // 6s后自动回到定位点
        mAMapNaviView.setViewOptions(options);
        /** 隐私政策 */
        NaviSetting.updatePrivacyShow(this, true, true);
        NaviSetting.updatePrivacyAgree(this, true);
        try {
            mAMapNavi = AMapNavi.getInstance(getApplicationContext());
            mAMapNavi.addAMapNaviListener(this);
            mAMapNavi.addParallelRoadListener(this);
            mAMapNavi.setUseInnerVoice(true, true);

            sList.add(mStartLatlng);
            eList.add(mEndLatlng);
            mAMapNavi.startNavi(NaviType.GPS);
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

        mAMapNavi.stopNavi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        if(mAMapNavi != null){
            mAMapNavi.stopNavi();
            AMapNavi.destroy();
        }
    }

    @Override
    public void onInitNaviFailure() {
        Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitNaviSuccess() {
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
         * 参数:
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        strategy = mAMapNavi.strategyConvert(true,false,false,false,false);
        mAMapNavi.calculateDriveRoute(sList,eList,mWayPointList,strategy);
    }

    @Override
    public void onStartNavi(int i) {
        // 开始导航回调
        ToastUtils.showShort("开始导航");
    }

    @Override
    public void onTrafficStatusUpdate() {
        // 当路线上的路况发生变化时会触发回调
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        // 当前位置回调
        Log.e("NaviChange--",aMapNaviLocation.toString());
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        //播报类型和播报文字回调
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {
        //结束模拟导航
    }

    @Override
    public void onArriveDestination() {
        // 到达目的地 - 可以进行签到
        ToastUtils.showShort("到达目的地 可以进行签到操作");
    }

    @Override
    public void onCalculateRouteFailure(int result) {

    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航后重新计算路线回调
        ToastUtils.showShort("您已偏航重新进行导航");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //  拥堵后重新计算路线回调
    }

    @Override
    public void onArrivedWayPoint(int i) {
        //到达途径点 - 到达途径点 - 拉起语音提示 - 进行打卡操作
        ToastUtils.showShort("您已到"+ i +"途径点");
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        if (!b) {
            ToastUtils.showShort("检测到GPS定位已关闭，定位信息存在偏差");
        }
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        // 导航过程中的信息更新，请看NaviInfo的具体说明
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {
        //服务区信息回调
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {
        // 显示车道信息
        Log.e("LaneInfo--", aMapLaneInfo.toString());
    }

    @Override
    public void hideLaneInfo() {
        // 隐藏车道信息
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        // 开始导航
        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult routeResult) {
        // 开始导航
        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult result) {
        //路线计算失败
        Log.e("dm", "--------------------------------------------");
        Log.i("dm", "路线计算失败：错误码=" + result.getErrorCode() + ",Error Message= " + result.getErrorDescription());
        Log.i("dm", "错误码详细链接见：http://lbs.amap.com/api/android-navi-sdk/guide/tools/errorcode/");
        Log.e("dm", "--------------------------------------------");
        Toast.makeText(this, "errorInfo：" + result.getErrorDetail() + ", Message：" + result.getErrorDescription(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onGpsSignalWeak(boolean b) {
        if (b) {
            ToastUtils.showShort("检测到GPS信号弱");
        }
    }

    @Override
    public void onNaviSetting() {
        // 底部导航设置点击回调 - 暂停|退出
    }

    @Override
    public void onNaviCancel() {
        /** 取消导航回调 */
        mAMapNavi.removeAMapNaviListener(this);
        AMapNavi.destroy();
        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        /** 用户点击一级退出 */
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {
        // 导航态车头模式，0:车头朝上状态；1:正北朝上模式
    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {
        // 全览按钮点击回调
    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {
        Log.d("wlx", "导航页面加载成功");
    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }

    @Override
    public void notifyParallelRoad(AMapNaviParallelRoadStatus aMapNaviParallelRoadStatus) {
        if (aMapNaviParallelRoadStatus.getmElevatedRoadStatusFlag() == 1) {
            Toast.makeText(this, "当前在高架上", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在高架上");
        } else if (aMapNaviParallelRoadStatus.getmElevatedRoadStatusFlag() == 2) {
            Toast.makeText(this, "当前在高架下", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在高架下");
        }

        if (aMapNaviParallelRoadStatus.getmParallelRoadStatusFlag() == 1) {
            Toast.makeText(this, "当前在主路", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在主路");
        } else if (aMapNaviParallelRoadStatus.getmParallelRoadStatusFlag() == 2) {
            Toast.makeText(this, "当前在辅路", Toast.LENGTH_SHORT).show();
            Log.d("wlx", "当前在辅路");
        }
    }
}
