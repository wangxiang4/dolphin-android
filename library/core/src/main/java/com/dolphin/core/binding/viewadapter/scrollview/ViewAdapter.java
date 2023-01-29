package com.dolphin.core.binding.viewadapter.scrollview;

import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.databinding.BindingAdapter;
import com.dolphin.core.binding.command.BindingCommand;

import lombok.Data;

/**
 *<p>
 * ScrollView 组件扩展绑定适配器配置
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public final class ViewAdapter {

    @BindingAdapter({"onScrollChangeCommand"})
    public static void onScrollChangeCommand(final NestedScrollView nestedScrollView, final BindingCommand<NestScrollDataWrapper> onScrollChangeCommand) {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (onScrollChangeCommand != null) {
                onScrollChangeCommand.execute(new NestScrollDataWrapper(scrollX, scrollY, oldScrollX, oldScrollY));
            }
        });
    }

    @BindingAdapter({"onScrollChangeCommand"})
    public static void onScrollChangeCommand(final ScrollView scrollView, final BindingCommand<ScrollDataWrapper> onScrollChangeCommand) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (onScrollChangeCommand != null) {
                onScrollChangeCommand.execute(new ScrollDataWrapper(scrollView.getScrollX(), scrollView.getScrollY()));
            }
        });
    }

    @Data
    public static class ScrollDataWrapper {
        private float scrollX;
        private float scrollY;

        public ScrollDataWrapper(float scrollX, float scrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
        }
    }

    @Data
    public static class NestScrollDataWrapper {
        private int scrollX;
        private int scrollY;
        private int oldScrollX;
        private int oldScrollY;

        public NestScrollDataWrapper(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.oldScrollX = oldScrollX;
            this.oldScrollY = oldScrollY;
        }
    }
}
