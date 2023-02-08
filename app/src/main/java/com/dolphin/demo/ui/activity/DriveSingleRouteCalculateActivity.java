package com.dolphin.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.dolphin.core.amap.NaviBaseActivity;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.entity.RoutePlanLatPoint;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>
 * 驾车导航单路线计算活动
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/8
 */
public class DriveSingleRouteCalculateActivity extends NaviBaseActivity {

    private LatLonPoint mOriginPoint;
    private LatLonPoint mDestinationPoint;
    // 起点
    private List<NaviLatLng> sList = new ArrayList();
    // 终点
    private List<NaviLatLng> eList = new ArrayList();
    // 途经点
    private List<NaviLatLng> mWayPointList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_navi);
        // 获取传递参数
        RoutePlanLatPoint routePlanLatPoint = getIntent().getParcelableExtra(CommonConstant.ROUTE_PLAN_LAT_POINT);
        mOriginPoint = routePlanLatPoint.getOriginPoint();
        mDestinationPoint = routePlanLatPoint.getDestinationPoint();
        mAMapNaviView = findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setTilt(0);
        mAMapNaviView.setViewOptions(options);

        setResult(RESULT_CANCELED, new Intent().putExtra(RoutePlanActivity.DEMO_RESULT_LAUNCHER_RESULT_KEY, "驾车导航失败！"));
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        sList.add(new NaviLatLng(mOriginPoint.getLatitude(), mOriginPoint.getLongitude()));
        eList.add(new NaviLatLng(mDestinationPoint .getLatitude(), mDestinationPoint.getLongitude()));

        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
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
        try {
            // 再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);

        // 设置回调结果
        setResult(RESULT_OK, new Intent().putExtra(RoutePlanActivity.DEMO_RESULT_LAUNCHER_RESULT_KEY, "驾车导航成功！"));
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        super.onCalculateRouteSuccess(aMapCalcRouteResult);
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }

}
