package com.kicc.collect.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.RidePath;
import com.kicc.collect.BR;
import com.kicc.collect.R;
import com.kicc.collect.databinding.KcActivityRoutePlanDetailBinding;
import com.kicc.collect.ui.adapter.RideListAdapter;
import com.kicc.collect.ui.vm.ToolbarViewModel;
import com.kicc.core.base.BaseActivity;
import com.kicc.core.util.AMapCommonUtil;

/**
 *<p>
 * 骑行路线详情活动
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/11
 */
public class RideRouteDetailActivity extends BaseActivity<KcActivityRoutePlanDetailBinding, ToolbarViewModel> {

	@Override
	public int setContentView(Bundle savedInstanceState) {
		return R.layout.kc_activity_route_plan_detail;
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
