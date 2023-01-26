package com.kicc.collect.ui.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.JsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.kicc.collect.R;
import com.kicc.collect.entity.Message;
import com.kicc.core.entity.MapLogisticPoint;

import java.util.List;

public class TabBarMessageAdapter extends RecyclerView.Adapter<TabBarMessageAdapter.MyViewHolder> {

    /** 回调事件 */
    private EventListener mEventListener;

    /** 视图集合数据 */
    List<Message> mItemList;

    public interface EventListener {
        /** 视图容器点击 */
        void onItemViewClicked(View v);
    }

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {

        public LinearLayout mContainer;
        public TextView tv_title;
        public TextView tv_content;
        public TextView tv_status;

        public MyViewHolder(@NonNull View v) {
            super(v);
            mContainer = v.findViewById(R.id.behind_view_message);
            tv_title = v.findViewById(R.id.tv_message_title);
            tv_content = v.findViewById(R.id.tv_message_content);
            tv_status = v.findViewById(R.id.tit_status);
        }

        @NonNull
        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public TabBarMessageAdapter(List<Message> dataList){
        this.mItemList = dataList;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getId().hashCode();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.kc_item_message_result, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.mContainer.setOnClickListener(view -> onItemViewClick(view));

        final Message item = mItemList.get(position);
        holder.tv_title.setText(item.getName());
        holder.tv_status.setBackgroundColor(item.getStatus().equals("1") ? Color.WHITE : Color.TRANSPARENT); // 0-未读 1-已读

        // todo:判断content是否为json数据 Color.WHITE
        String str = item.getContent().trim();
        if ((str.startsWith("{") && str.endsWith("}")) || str.startsWith("[") && str.endsWith("]")){
            holder.tv_content.setText("您有新的交接点任务，请及时处理。");
        }else {
            holder.tv_content.setText(item.getContent());
        }
//        holder.tv_time.setText();
    }


    private void onItemViewClick(View v) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v));
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public EventListener getEventListener(){
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }
}
