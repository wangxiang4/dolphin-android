package com.kicc.collect.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.route.DriveStep;
import com.kicc.collect.R;
import com.kicc.core.util.AMapCommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>
 * 驾车表格数据适配器
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/11
 */
public class DriveListAdapter extends BaseAdapter {

	private Context mContext;
	private List<DriveStep> mItemList = new ArrayList();

	public DriveListAdapter(Context context, List<DriveStep> list) {
		this.mContext = context;
		mItemList.add(new DriveStep());
		for (DriveStep driveStep : list) {
			mItemList.add(driveStep);
		}
		mItemList.add(new DriveStep());
	}

	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.kc_item_route_plan, null);
			holder.driveDirIcon = convertView.findViewById(R.id.route_dir_icon);
			holder.driveLineName = convertView.findViewById(R.id.route_line_name);
			holder.driveDirUp = convertView.findViewById(R.id.route_dir_icon_up);
			holder.driveDirDown = convertView.findViewById(R.id.route_dir_icon_down);
			holder.splitLine = convertView.findViewById(R.id.route_split_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final DriveStep item = mItemList.get(position);
		if (position == 0) {
			holder.driveDirIcon.setImageResource(R.drawable.kc_ic_orgin);
			holder.driveLineName.setText("出发");
			holder.driveDirUp.setVisibility(View.GONE);
			holder.driveDirDown.setVisibility(View.VISIBLE);
			holder.splitLine.setVisibility(View.GONE);
			return convertView;
		} else if (position == mItemList.size() - 1) {
			holder.driveDirIcon.setImageResource(R.drawable.kc_ic_destination);
			holder.driveLineName.setText("到达终点");
			holder.driveDirUp.setVisibility(View.VISIBLE);
			holder.driveDirDown.setVisibility(View.GONE);
			holder.splitLine.setVisibility(View.VISIBLE);
			return convertView;
		} else {
			String actionName = item.getAction();
			int resID = AMapCommonUtil.getDrivingActionResource(actionName);
			holder.driveDirIcon.setImageResource(resID);
			holder.driveLineName.setText(item.getInstruction());
			holder.driveDirUp.setVisibility(View.VISIBLE);
			holder.driveDirDown.setVisibility(View.VISIBLE);
			holder.splitLine.setVisibility(View.VISIBLE);
			return convertView;
		}
		
	}

	private class ViewHolder {
		TextView driveLineName;
		ImageView driveDirIcon;
		ImageView driveDirUp;
		ImageView driveDirDown;
		ImageView splitLine;
	}

}
