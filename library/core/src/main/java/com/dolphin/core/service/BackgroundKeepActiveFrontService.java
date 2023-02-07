package com.dolphin.core.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.R;
import com.dolphin.core.constant.AppConstant;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *<p>
 * 前台服务
 * 采用媒体锁持续保持后台活跃
 * https://developer.android.com/guide/components/services?hl=zh-cn
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/6
 */
public class BackgroundKeepActiveFrontService extends Service {

    /** 流媒体播放器 */
    private MediaPlayer mediaPlayer;

    /** 启动服务异步处理任务 */
    public static volatile Boolean startBackgroundKeepActiveTask = false;

    /** 判断APP当前是否处于后台运行 */
    private boolean isAppBackstage() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.media_lock);
        // 禁止播放程序进入休眠,熄灭屏幕后唤醒cpu
        mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null) mediaPlayer.start();
        ThreadUtils.executeByFixedAtFixRate(AppConstant.DEFAULT_THREAD_POOL_SIZE,
                backgroundKeepActiveTask, AppConstant.KEEP_ACTIVE_TASK_INTERVAL_TIME, TimeUnit.MILLISECONDS);
        return super.onStartCommand(intent, flags, startId);
    }

    /** 后台异步处理持续活跃任务 */
    ThreadUtils.Task<String> backgroundKeepActiveTask = new Utils.Task(result -> {}) {
        @Override
        public String doInBackground() {
            // 设置线程取消
            if (!startBackgroundKeepActiveTask) cancel();
            if(isAppBackstage()){
                Intent intent = new Intent();
                intent.setAction(AppConstant.BACKGROUND_KEEP_ACTIVE_TASK_SCHEDULING);
                sendBroadcast(intent);
            }
            startForeground(AppConstant.KEEP_ACTIVE_FRONT_SERVICE_NOTIFICATION_ID, AppKeepActive.notification);
            return null;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
