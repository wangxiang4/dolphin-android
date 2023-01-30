package com.dolphin.demo.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.route.RideStep;
import com.dolphin.demo.R;
import com.dolphin.core.util.AMapCommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *<p>
 * 骑行表格数据适配器
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/11
 */
public class RideListAdapter extends BaseAdapter {

	private Context mContext;
	private List<RideStep> mItemList = new ArrayList();

	public RideListAdapter(Context context, List<RideStep> list) {
		this.mContext = context;
		mItemList.add(new RideStep());
		for (RideStep rideStep : list) {
			mItemList.add(rideStep);
		}
		mItemList.add(new RideStep());
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
			convertView = View.inflate(mContext, R.layout.item_route_plan, null);
			holder.lineName = convertView.findViewById(R.id.route_line_name);
			holder.dirIcon = convertView.findViewById(R.id.route_dir_icon);
			holder.dirUp = convertView.findViewById(R.id.route_dir_icon_up);
			holder.dirDown = convertView.findViewById(R.id.route_dir_icon_down);
			holder.splitLine = convertView.findViewById(R.id.route_split_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final RideStep item = mItemList.get(position);
		if (position == 0) {
			holder.dirIcon.setImageResource(R.drawable.icon_orgin);
			holder.lineName.setText("出发");
			holder.dirUp.setVisibility(View.INVISIBLE);
			holder.dirDown.setVisibility(View.VISIBLE);
			holder.splitLine.setVisibility(View.INVISIBLE);
			return convertView;
		} else if (position == mItemList.size() - 1) {
			holder.dirIcon.setImageResource(R.drawable.icon_destination);
			holder.lineName.setText("到达终点");
			holder.dirUp.setVisibility(View.VISIBLE);
			holder.dirDown.setVisibility(View.INVISIBLE);
			return convertView;
		} else {
			holder.splitLine.setVisibility(View.VISIBLE);
			holder.dirUp.setVisibility(View.VISIBLE);
			holder.dirDown.setVisibility(View.VISIBLE);
			String actionName = item.getAction();
			int resID = AMapCommonUtil.getWalkActionResource(actionName);
			holder.dirIcon.setImageResource(resID);
			holder.lineName.setText(item.getInstruction());
			return convertView;
		}
	}

	private class ViewHolder {
		TextView lineName;
		ImageView dirIcon;
		ImageView dirUp;
		ImageView dirDown;
		ImageView splitLine;
	}

}
