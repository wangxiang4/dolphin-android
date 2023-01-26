package com.kicc.collect.util;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *<p>
 * 标记点回收视图布局设置工具类
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
public class PointRecyclerViewLayoutUtil {
    public static void vertical(Context context, RecyclerView rv) {
        rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
    }
    public static void horizontal(Context context,RecyclerView rv) {
        rv.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
    }
    public static void grid(Context context,int spanCount,RecyclerView rv) {
        rv.setLayoutManager(new GridLayoutManager(context,spanCount, GridLayoutManager.VERTICAL,false));
    }
}
