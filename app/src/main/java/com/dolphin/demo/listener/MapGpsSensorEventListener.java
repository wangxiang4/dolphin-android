package com.dolphin.demo.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.Marker;
import com.dolphin.demo.ui.fragment.TabBarHomeFragment;

/**
 *<p>
 * 地图gps方位旋转传感器监听
 * 参考:
 * https://blog.csdn.net/bob_fly1984/article/details/80717335?spm=1001.2014.3001.5506
 * https://blog.csdn.net/liu857279611/article/details/50606484?spm=1001.2014.3001.5506
 * https://github.com/amap-demo/android-location-rotation-effect/blob/master/app/src/main/java/com/amap/location/rotation/MainActivity.java
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/31
 */
public class MapGpsSensorEventListener implements SensorEventListener {

    /** 上次方向旋转传感器时间 */
    private long lastTime = 0;

    /** 传感器时间精度,控制旋转间隔 */
    private final int TIME_SENSOR = 100;

    /** gps位置标记 */
    private Marker mMarker;

    /** 当前活动上下文 */
    private Context mContext;

    /** 传感器管理 */
    private SensorManager mSensorManager;

    /** 传感器 */
    private Sensor magneticSensor, accelerometerSensor;

    /** 磁场传器数据 */
    private float[] gravity = new float[3];

    /** 加速传感器数据 */
    private float[] geomagnetic= new float[3];

    /** 手机旋转角度 */
    private float mAngle;

    /** 高德地图组件 */
    private AMap aMap;

    public MapGpsSensorEventListener(Context context) {
        this.mContext = context;
        // 初始化传感器
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // 计算手机方位根据加速度传感器和地磁场传感器计算获取
        magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void registerSensorListener() {
        mSensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    public void setGpsMarker(Marker marker) {
        mMarker = marker;
    }

    public void setAMap(AMap aMap) {
        this.aMap = aMap;
    }

    @Override
    /** 当有新的传感器事件时(手机方向改变时调用)调用 */
    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() - lastTime < TIME_SENSOR) return;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 用来保存手机的旋转弧度
            float[] values = new float[3];
            // 被填充的旋转矩阵
            float[] r = new float[9];
            // 传入gravity和geomagnetic,通过计算它们得到旋转矩阵R
            // 而第二个参数倾斜矩阵I是用于将磁场数据转换进实际的重力坐标系中的,一般默认设置为NULL即可
            SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
            // 根据旋转矩阵R计算设备的方向,将结果存储在values中
            // values[0]记录着手机围绕 Z 轴的旋转弧度
            // values[1]记录着手机围绕 X 轴的旋转弧度
            // values[2]记录着手机围绕 Y 轴的旋转弧度
            SensorManager.getOrientation(r, values);

            // 地心旋转弧度转为角度
            float x = (float) Math.toDegrees(values[0]);
            x += getScreenRotationOnPhone(mContext);
            x %= 360.0F;
            if (x > 180.0F)
                x -= 360.0F;
            else if (x < -180.0F)
                x += 360.0F;
            if (Math.abs(mAngle - x) < 3.0f) return;
            mAngle = Float.isNaN(x) ? 0 : x;
            x = (360 - mAngle);
            if (mMarker != null) mMarker.setRotateAngle(x);
            if (aMap != null && !TabBarHomeFragment.userMoveToLocationMark) aMap.moveCamera(CameraUpdateFactory.changeBearing(360 - mAngle));
            lastTime = System.currentTimeMillis();
        }
    }

    @Override
    /** 当注册传感器的精度发生变化时调用 */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 获取当前屏幕旋转角度
     * @param context 当前活动上下文
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static int getScreenRotationOnPhone(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;

            case Surface.ROTATION_90:
                return 90;

            case Surface.ROTATION_180:
                return 180;

            case Surface.ROTATION_270:
                return -90;
        }
        return 0;
    }

}
