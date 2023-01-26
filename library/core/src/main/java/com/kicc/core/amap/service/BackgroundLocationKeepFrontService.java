package com.kicc.core.amap.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.kicc.core.R;
import com.kicc.core.amap.LocationRequest;
import com.kicc.core.constant.AppConstant;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *<p>
 * 后台定位保活前台服务
 * 参考高德地图文档与百度地图文档后发现可行后台保活方案媒体锁
 * https://lbs.baidu.com/index.php?title=android-yingyan/guide/tracelive
 * https://developer.android.com/guide/components/services?hl=zh-cn
 * https://blog.csdn.net/u014410755/article/details/117673839
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/27
 */
public class BackgroundLocationKeepFrontService extends Service {

    /** 流媒体播放器 */
    private MediaPlayer mediaPlayer;

    /** 启动服务异步处理任务 */
    public static volatile Boolean startBackgroundLocationTask = false;

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
        ThreadUtils.executeByFixedAtFixRate(AppConstant.defaultThreadPoolSize,
                backgroundLocationTask, AppConstant.LOCATION_TASK_INTERVAL_TIME, TimeUnit.MILLISECONDS);
        return super.onStartCommand(intent, flags, startId);
    }

    /** 服务异步处理任务 */
    ThreadUtils.Task<String> backgroundLocationTask = new Utils.Task(result -> {}) {
        @Override
        public String doInBackground() {
            // 设置线程取消
            if (!startBackgroundLocationTask) cancel();
            if(isAppBackstage()){
                Intent intent = new Intent();
                intent.setAction(LocationManager.KEY_LOCATION_CHANGED);
                sendBroadcast(intent);
            }
            startForeground(AppConstant.LOCATION_FRONT_SERVICE_NOTIFICATION_ID, LocationRequest.notification);
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
