package com.dolphin.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.RidePath;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.util.AMapCommonUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.ActivityRoutePlanDetailBinding;
import com.dolphin.demo.ui.adapter.RideListAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;

/**
 *<p>
 * 骑行路线详情活动
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/11
 */
public class RideRouteDetailActivity extends BaseActivity<ActivityRoutePlanDetailBinding, ToolbarViewModel> {

	@Override
	public int setContentView(Bundle savedInstanceState) {
		return R.layout.activity_route_plan_detail;
	}

	@Override
	public int setVariableId() {
		return BR.viewModel;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent == null) return;
		RidePath mRidePath = intent.getParcelableExtra("ride_path");
		mViewModel.setTitleText("骑行路线详情");
		TextView mRouteTime = findViewById(R.id.route_time);
		int distance = (int) mRidePath.getDistance();
		int duration = (int) mRidePath.getDuration();
		String routeDetail = AMapCommonUtil.getPlanTime(duration) + "(" + AMapCommonUtil.getPlanKilometer(distance) + ")";
		mRouteTime.setText(routeDetail);
		// 配置列表视图数据
		ListView mDriveList = findViewById(R.id.route_list);
		RideListAdapter mRideListAdapter = new RideListAdapter(this.getApplicationContext(), mRidePath.getSteps());
		mDriveList.setAdapter(mRideListAdapter);
	}


}
