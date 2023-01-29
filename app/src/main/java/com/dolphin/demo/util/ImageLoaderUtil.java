package com.dolphin.demo.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 *<p>
 * 图片选择器加载工具类
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/23
 */
public class ImageLoaderUtil {

    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper){
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity){
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    private static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }

}
