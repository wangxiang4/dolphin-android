package com.kicc.collect.listener;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 *<p>
 * 回收视图适配器Item长按回调处理
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/22
 */
public interface OnItemLongClickListener {

    void onItemLongClick(RecyclerView.ViewHolder holder, int position, View v);

}
