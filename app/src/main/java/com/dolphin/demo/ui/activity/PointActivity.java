package com.dolphin.demo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.databinding.KcActivityPointBinding;
import com.dolphin.demo.entity.RoutePlanLatPoint;
import com.dolphin.demo.ui.adapter.SwipeableWithButtonPointAdapter;
import com.dolphin.demo.ui.vm.PointViewModel;
import com.dolphin.core.amap.LocationRequest;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.entity.MapLogisticPoint;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>
 * 地图行程任务
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/16
 */
public class PointActivity extends BaseActivity<KcActivityPointBinding, PointViewModel> {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

    private AMapLocation mLocation;
    private List<MapLogisticPoint> mapLogisticPoints = new ArrayList();
    private ActivityResultLauncher<MapLogisticPoint> launcherResult; // 跳转结果集（双向传输）

    private TextView tv_street;
    private TextView tv_city;

    public static String ARRIVE_MAP_TASK_ID = "ARRIVE_MAP_TASK_ID";

    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.kc_activity_point;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = findViewById(R.id.point_recycler_view);
        tv_street = findViewById(R.id.tv_point_street);
        tv_city = findViewById(R.id.tv_point_city);

        // 初始化滑动动作保护,防止解除滑动固定发生的抖动问题
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // 初始化视图滚动管理器
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        final SwipeableWithButtonPointAdapter myItemAdapter = new SwipeableWithButtonPointAdapter(mapLogisticPoints);
        myItemAdapter.setEventListener(new SwipeableWithButtonPointAdapter.EventListener() {
            @Override
            public void onItemPinned(int position) {
                // todo: 列表固定回调处理
            }

            @Override
            public void onItemViewClicked(View v) {
                handleOnItemViewClicked(v);
            }

            @Override
            public void onStartNavigation(View v) {
                handleStartNavigationClicked(v);
            }

            @Override
            public void onSetPresetPoint(View v) {
                handleSetPresetPointClicked(v);
            }

            @Override
            public void onReachDestination(View v) {
                handleOnReachDestinationClicked(v);
            }
        });
        mAdapter = myItemAdapter;
        // 创建滑动回收适配器
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(myItemAdapter);
        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
        // 禁用默认回收视图更改动画,以使滑动项的折回动画正常工作
        animator.setSupportsChangeAnimations(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        // 配置回收视图
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);
        // 配置滑动(优先级:触摸动作防护装置 > 滑动 > 拖放)
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mViewModel.mapLogistics.observe(this, mapLogistic ->{
            mapLogisticPoints.clear();
            mapLogisticPoints.addAll(mapLogistic.getMapLogisticPoint());
            mAdapter.notifyDataSetChanged();
        });

        if (ObjectUtils.isNotEmpty(LocationRequest.locationClient)){
            mLocation = LocationRequest.locationClient.getLastKnownLocation();
            tv_street.setText( mLocation.getStreet() + mLocation.getAoiName()+mLocation.getStreetNum() ); // 街道门牌号
            tv_city.setText( mLocation.getProvince()+mLocation.getCity()+mLocation.getDistrict() );
        } else ToastUtils.showShort("位置获取失败");

        launcherResult = registerForActivityResult(new ActivityResultContract<MapLogisticPoint, Void>() {
            @Override
            public Void parseResult(int resultCode, @Nullable Intent intent) {
                mViewModel.initData();
                return null;
            }

            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, MapLogisticPoint mapLogisticPoint) {
                Intent intent = new Intent(getApplication(), PointSearchActivity.class);
                intent.putExtra(CommonConstant.PRESET_PLAN_LAT_POINT, mapLogisticPoint);
                return intent;
            }
        }, result -> {});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
    }

    /** 处理点击项滑动视图取消固定 */
    private void handleOnItemViewClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        if (position != RecyclerView.NO_POSITION) {
            MapLogisticPoint item = mapLogisticPoints.get(position);
            if (item.getPinned()) {
                // 取消滑动固定
                item.setPinned(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    /** 开始导航点击事件 */
    private void handleStartNavigationClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        MapLogisticPoint item = mapLogisticPoints.get(position);
        // 获取目标定位
        RoutePlanLatPoint routePlanLatPoint = new RoutePlanLatPoint();
        routePlanLatPoint.setDestinationPoint(new LatLonPoint(item.getLat(),item.getLng()));
        // 获取当前定位
        if (ObjectUtils.isNotEmpty(mLocation)){
            routePlanLatPoint.setOriginPoint(new LatLonPoint(mLocation.getLatitude(),mLocation.getLongitude()));
            Intent intent = new Intent(this, RoutePlanActivity.class);
            intent.putExtra(CommonConstant.ROUTE_PLAN_LAT_POINT, routePlanLatPoint);
            startActivity(intent);
            // 取消滑动固定
            item.setPinned(false);
            mAdapter.notifyItemChanged(position);
        }else ToastUtils.showShort("位置获取失败，请重试");
    }

    /** 设置交接点点击事件 */
    private void handleOnReachDestinationClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        MapLogisticPoint item = mapLogisticPoints.get(position);
        item.setLng(0.00);
        item.setLat(0.00);
        launcherResult.launch(item);
        // 取消滑动固定
        item.setPinned(false);
        mAdapter.notifyItemChanged(position);
    }

    /** 已到达按钮点击事件 */
    private void handleSetPresetPointClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        MapLogisticPoint item = mapLogisticPoints.get(position);
        Intent intent = new Intent(getApplication(), TaskFormActivity.class);
        Bundle pointBundle = new Bundle();
        pointBundle.putString(ARRIVE_MAP_TASK_ID,item.getMapTaskId());
        intent.putExtras(pointBundle);
        startActivity(intent);
        // 取消滑动固定
        item.setPinned(false);
        mAdapter.notifyItemChanged(position);
    }

}
