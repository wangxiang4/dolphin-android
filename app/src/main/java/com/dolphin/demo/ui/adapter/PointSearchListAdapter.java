package com.dolphin.demo.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dolphin.demo.R;
import com.dolphin.demo.entity.PoiSearchResult;

import java.util.ArrayList;

/**
 * 搜索结果适配器
 * @author liusixiang
 * @date 2022.9.23
 */
public class PointSearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private static final int TYPE_TRIP = 0;
    private ArrayList<PoiSearchResult> poiSearchResults = new ArrayList<>();

    public PointSearchListAdapter(Context context){
        this.mContext = context;
    }

    public void setData(ArrayList<PoiSearchResult> poiSearchResults){
        this.poiSearchResults = poiSearchResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kc_item_point_result, parent, false);
        return new PoiSearchViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,final int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_TRIP) {
            if (position < poiSearchResults.size()) {
                ((PoiSearchViewHolder) holder).bind(poiSearchResults.get(position));
            }
        }
        /** 添加点击事件 */
        holder.itemView.setOnClickListener(view -> {
            getListener.onClick(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_TRIP;
    }

    @Override
    public int getItemCount() {
        return poiSearchResults.size();
    }

    public static class PoiSearchViewHolder extends RecyclerView.ViewHolder {

        TextView poi_address;
        TextView poi_types;
        TextView poi_detail;

        public PoiSearchViewHolder(View itemView) {

            super(itemView);
            poi_address = itemView.findViewById(R.id.item_poi_address);
            poi_types = itemView.findViewById(R.id.item_poi_types);
            poi_detail = itemView.findViewById(R.id.item_poi_detail);
        }

        public void bind(PoiSearchResult poiSearchResult) {
            poi_address.setText(poiSearchResult.getAdress());
            poi_types.setText(poiSearchResult.getTypes());
            poi_detail.setText(poiSearchResult.getPeriphery());
        }
    }
    public interface GetListener {
        void onClick(int position);
    }
    private int mPosition;
    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }
    public int getmPosition() {
        return mPosition;
    }
    private GetListener getListener;
    public void setGetListener(GetListener getListener) {
        this.getListener = getListener;
    }
}
