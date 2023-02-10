package com.dolphin.core.widget;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 *<p>
 * 视图边框绘制
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/10
 */
class BorderDrawable {

    private final Drawable mDivider;
    private final int mWidth;
    private final int mHeight;

    public BorderDrawable(Drawable divider, int width, int height) {
        this.mDivider = divider;
        this.mWidth = width;
        this.mHeight = height;
    }

    /** 在视图的左侧绘制分隔线 */
    public void drawLeft(View view, Canvas c) {
        int left = view.getLeft() - mWidth;
        int top = view.getTop() - mHeight;
        int right = left + mWidth;
        int bottom = view.getBottom() + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }

    /** 在视图的顶部绘制分隔线 */
    public void drawTop(View view, Canvas c) {
        int left = view.getLeft() - mWidth;
        int top = view.getTop() - mHeight;
        int right = view.getRight() + mWidth;
        int bottom = top + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }

    /** 在视图的右侧绘制分隔线 */
    public void drawRight(View view, Canvas c) {
        int left = view.getRight();
        int top = view.getTop() - mHeight;
        int right = left + mWidth;
        int bottom = view.getBottom() + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }

    /** 在视图的低部绘制分隔线 */
    public void drawBottom(View view, Canvas c) {
        int left = view.getLeft() - mWidth;
        int top = view.getBottom();
        int right = view.getRight() + mWidth;
        int bottom = top + mHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }

}