package com.dolphin.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.dolphin.core.amap.NaviBaseActivity;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.entity.RoutePlanLatPoint;

/**
 *<p>
 * 骑行导航路线计算活动
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/8
 */
public class RideRouteCalculateActivity extends NaviBaseActivity {

    private LatLonPoint mOriginPoint;
    private LatLonPoint mDestinationPoint;

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
        setResult(RESULT_CANCELED, new Intent().putExtra(RoutePlanActivity.DEMO_RESULT_LAUNCHER_RESULT_KEY, "骑行导航失败！"));
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        if (mOriginPoint == null) {
            ToastUtil.show("起点未设置");
            return;
        }
        if (mDestinationPoint == null) {
            ToastUtil.show("终点未设置");
            return;
        }
        mAMapNavi.calculateRideRoute(new NaviLatLng(mOriginPoint.getLatitude(), mOriginPoint.getLongitude()), new NaviLatLng(mDestinationPoint.getLatitude(), mDestinationPoint.getLongitude()));
        setResult(RESULT_OK, new Intent().putExtra(RoutePlanActivity.DEMO_RESULT_LAUNCHER_RESULT_KEY, "骑行导航成功！"));
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        super.onCalculateRouteSuccess(aMapCalcRouteResult);
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }
}
