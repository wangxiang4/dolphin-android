package com.dolphin.demo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.util.AMapCommonUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.ActivityRoutePlanDetailBinding;
import com.dolphin.demo.ui.adapter.DriveListAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;

/**
 *<p>
 * 驾车路线详情活动
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/11
 */
public class DriveRouteDetailActivity extends BaseActivity<ActivityRoutePlanDetailBinding, ToolbarViewModel> {

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
		DrivePath mDrivePath = intent.getParcelableExtra("drive_path");
		mViewModel.setTitleText("驾车路线详情");
		TextView mRouteTime = findViewById(R.id.route_time);
		int distance = (int) mDrivePath.getDistance();
		int duration = (int) mDrivePath.getDuration();
		String routeDetail = AMapCommonUtil.getPlanTime(duration) + "(" + AMapCommonUtil.getPlanKilometer(distance) + ")";
		mRouteTime.setText(routeDetail);
		// 配置列表视图数据
		ListView mDriveList = findViewById(R.id.route_list);
		DriveListAdapter mDriveListAdapter = new DriveListAdapter(this.getApplicationContext(), mDrivePath.getSteps());
		mDriveList.setAdapter(mDriveListAdapter);
	}

}
