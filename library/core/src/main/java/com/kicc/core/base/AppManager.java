package com.kicc.core.base;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import java.util.Optional;
import java.util.Stack;

import lombok.AllArgsConstructor;

/**
 *<p>
 * 安卓活动与碎片管理
 * 内部采用 Stack 堆栈后进先出队列
 * 管理多界面打开销毁: https://developer.android.com/guide/components/activities/tasks-and-back-stack?hl=zh-cn#ManagingTasks
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/21
 */
@AllArgsConstructor
public class AppManager {

    /** 活动堆栈 */
    private static Stack<Activity> activityStack;

    /** 碎片堆栈 */
    private static Stack<Fragment> fragmentStack;

    /** 管理实例 */
    private static AppManager instance;

    public static AppManager getAppManager() {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    /** ----------------------------------------------------------- */

    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    public static Stack<Fragment> getFragmentStack() {
        return fragmentStack;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack();
        }
        activityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    public boolean isActivity() {
        if (activityStack != null) {
            return !activityStack.isEmpty();
        }
        return false;
    }

    /** 获取当前活动(堆栈中最后一个压入) */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /** 结束当前活动(堆栈中最后一个压入)*/
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /** 结束指定的活动 */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /** 结束指定类名的活动 */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /** 结束所有活动 */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                finishActivity(activityStack.get(i));
            }
        }
        activityStack.clear();
    }

    /** 获取指定的Activity */
    public Activity getActivity(Class<?> cls) {
        if (activityStack != null){
            Optional<Activity> activity = activityStack.stream().filter(item -> item.getClass().equals(cls)).findFirst();
            if(activity.isPresent()) return activity.get();
        }
        return null;
    }

    /** ----------------------------------------------------------- */

    public void addFragment(Fragment fragment) {
        if (fragmentStack == null) {
            fragmentStack = new Stack();
        }
        fragmentStack.add(fragment);
    }

    public void removeFragment(Fragment fragment) {
        if (fragment != null) {
            fragmentStack.remove(fragment);
        }
    }

    public boolean isFragment() {
        if (fragmentStack != null) {
            return !fragmentStack.isEmpty();
        }
        return false;
    }

    /** 获取当前碎片(堆栈中最后一个压入) */
    public Fragment currentFragment() {
        if (fragmentStack != null) {
            Fragment fragment = fragmentStack.lastElement();
            return fragment;
        }
        return null;
    }

}