package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.constant.CacheConstant;
import com.dolphin.demo.di.component.DaggerServiceComponent;
import com.dolphin.demo.entity.MapLogistic;
import com.dolphin.demo.entity.User;
import com.dolphin.demo.service.MapLogisticsService;
import com.dolphin.core.base.BaseViewModel;
import com.dolphin.core.binding.command.BindingCommand;
import com.dolphin.core.bus.SingleLiveEvent;
import com.dolphin.core.enums.FileObservableStatusEnum;
import com.dolphin.core.http.HttpFileRequest;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.http.file.DownLoadFile;
import com.dolphin.core.http.observer.BaseDownLoadDisposableObserver;
import com.dolphin.core.util.RxUtil;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.observers.DisposableObserver;

/**
 *<p>
 * 首页碎片视图模型层
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/1
 */
public class TabBarHomeViewModel extends BaseViewModel {

    @Inject
    public MapLogisticsService mapLogisticsService;

    /** 地图主线数据 */
    public SingleLiveEvent<MapLogistic> mapLogistic = new SingleLiveEvent();

    /** 界面点击事件监听 */
    public class HomeUiObservable {
        /** 已到达 */
        public SingleLiveEvent<Boolean> hasArrivedSwitchEvent = new SingleLiveEvent<>();
        /** 开始导航 */
        public SingleLiveEvent<Boolean> beginNavidSwitchEvent = new SingleLiveEvent<>();
    }

    public HomeUiObservable homeUiObservable = new HomeUiObservable();

    public TabBarHomeViewModel(@NonNull Application application) {
        super(application);
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    /** 已到达按钮 */
    public BindingCommand hasArrivedClickCommand =new BindingCommand(() ->{
        homeUiObservable.hasArrivedSwitchEvent.setValue(ObjectUtils.isEmpty(homeUiObservable.hasArrivedSwitchEvent.getValue()) || !homeUiObservable.hasArrivedSwitchEvent.getValue());
    });

    /** 点击开始导航 */
    public BindingCommand beginNavidClickCommand =new BindingCommand(() ->{
        homeUiObservable.beginNavidSwitchEvent.setValue(ObjectUtils.isEmpty(homeUiObservable.beginNavidSwitchEvent.getValue()) || !homeUiObservable.beginNavidSwitchEvent.getValue());
    });

    /** 请求当前收样快递员地图数据 */
    public void requestMapDataByCourierUser() {
        User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);
        mapLogisticsService.getMapDataByCourierUserId(user.getId())
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            .doOnSubscribe(this)
            .doOnSubscribe(disposable -> showDialog("正在加载地图数据..."))
            .subscribe(new DisposableObserver<ResultResponse<MapLogistic>>() {
                @Override
                public void onNext(ResultResponse<MapLogistic> R) {
                    if (R.getCode() == R.SUCCESS) {
                        mapLogistic.setValue(R.getData());
                    } else ToastUtils.showShort(R.getMsg());
                }
                @Override
                public void onError(Throwable e) {
                    closeDialog();
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {
                    closeDialog();
                }
            });
    }

    /** 后台上传GPS位置任务 */
    public ThreadUtils.Task backgroundUploadLocationGpsTask(AMapLocationClient locationClient) {
        User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);
        return new Utils.Task(result -> {}) {
            @Override
            public String doInBackground() {
                AMapLocation location = locationClient.getLastKnownLocation();
                if (ObjectUtils.isNotEmpty(location)) {
                    // todo: 利用CacheDiskUtils存储定位数据(省市区等String数据)
                    String proAddress = location.getProvince()+location.getCity()+location.getDistrict();
                    String detailAddress = location.getStreet()+location.getAoiName()+location.getStreetNum();
                    CacheDiskUtils.getInstance().put(CacheConstant.ADDRESS_APPROXIMATE_AUTH, proAddress);
                    CacheDiskUtils.getInstance().put(CacheConstant.ADDRESS_DETAIL_AUTH, detailAddress);

                    mapLogisticsService.uploadGps(new MapLogistic()
                                    .setCourierLng(location.getLongitude())
                                    .setCourierLat(location.getLatitude())
                                    .setCourierUserId(user.getId())
                                    .setCourierUserName(user.getUserName()))
                        .compose(RxUtil.schedulersTransformer())
                        .compose(RxUtil.exceptionTransformer())
                        .doOnSubscribe(TabBarHomeViewModel.this);
                }
                return null;
            }
        };
    }


    // todo:
    public SingleLiveEvent<Void> pictureSelectorClick = new SingleLiveEvent();
    public BindingCommand onPictureSelector =new BindingCommand(() -> pictureSelectorClick.call());

    // todo:
    public BindingCommand onFileDownLoad =new BindingCommand(() ->{
        initDownLoadPercentNotification();
        HttpFileRequest.download("http://gdown.baidu.com/data/wisegame/dc8a46540c7960a2/baidushoujizhushou_16798087.apk")
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            // 生命周期同步,ViewModel销毁时会清除异步观测
            .doOnSubscribe(this)
            .subscribe(new BaseDownLoadDisposableObserver() {
                @Override
                public void onNext(DownLoadFile downLoadFile) {
                    percent = downLoadFile;
                    super.onNext(downLoadFile);
                }

                @Override
                public void onComplete() {
                    updateNotification(-1);
                    ToastUtils.showLong("当前文件保存目录" + percent.getDestFileDir() + File.separator + percent.getDestFileName());
                }

                @Override
                public void onError(Throwable e) {
                    updateNotification(-1);
                    ExceptionHandle.baseExceptionMsg(e);
                }

                @Override
                public void onProgress(Integer percent) {
                    updateNotification(percent);
                }
            });
    });

    NotificationManager notificationManager;
    Notification.Builder builder;
    DownLoadFile percent;

    /** 初始化下载百分比通知栏 */
    private void initDownLoadPercentNotification() {
        // 使用通知栏百分比显示当前下载进度
        notificationManager = (NotificationManager) Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Utils.getApp().getPackageName();
        NotificationChannel notificationChannel = new NotificationChannel(channelId, "文件下载", NotificationManager.IMPORTANCE_DEFAULT);
        // 是否在桌面icon右上角展示小圆点
        notificationChannel.enableLights(false);
        // 是否在久按桌面图标时显示此渠道的通知
        notificationChannel.setShowBadge(false);
        // 关闭通知震动
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
        builder = new Notification.Builder(Utils.getApp(), channelId);
        builder.setSmallIcon(com.dolphin.core.R.drawable.umeng_push_notification_default_small_icon)
                .setLargeIcon(BitmapFactory.decodeResource(Utils.getApp().getResources(), com.dolphin.core.R.drawable.kc_ic_app))
                .setContentTitle("已下载(0%)")
                .setContentText("正在下载")
                // 点击通知后自动取消
                .setAutoCancel(true)
                // 推送的时间
                .setWhen(System.currentTimeMillis())
                // 仅首次通知
                .setOnlyAlertOnce(true)
                //设置进度条
                .setProgress(100, 0, false);
        notificationManager.notify(100, builder.build());
    }

    /**
     * 百分比刷新通知
     * @param progress 百分比,此值小于0时不刷新进度条
     */
    private void updateNotification(int progress) {
        if (builder == null) return;
        if (progress >= 0) {
            builder.setContentTitle("已下载(" + progress + "%)");
            builder.setProgress(100, progress, false);
        }
        if ((ObjectUtils.isNotEmpty(percent) ? percent.getStatus() : progress) == FileObservableStatusEnum.FAIL.getStatus()) {
            builder.setContentText("下载失败");
        } else if ((ObjectUtils.isNotEmpty(percent) ? percent.getStatus() : progress) == FileObservableStatusEnum.SUCCESS.getStatus()) {
            builder.setContentText("下载完成");
        }
        notificationManager.notify(100, builder.build());
    }


}
