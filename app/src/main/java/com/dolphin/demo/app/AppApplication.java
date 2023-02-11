package com.dolphin.demo.app;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.dolphin.core.BuildConfig;
import com.dolphin.core.base.BaseApplication;
import com.dolphin.core.crash.CaocConfig;
import com.dolphin.demo.R;
import com.dolphin.demo.di.component.AppComponent;
import com.dolphin.demo.di.component.DaggerAppComponent;
import com.dolphin.demo.ui.activity.LoginActivity;
import com.dolphin.demo.util.TimeFormatUtil;
import com.dolphin.umeng.UmengClient;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.bugly.crashreport.CrashReport;

import javax.inject.Inject;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 *<p>
 * 初始化应用程序
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/16
 */
public class AppApplication extends BaseApplication {

    @Inject
    CalligraphyConfig mCalligraphyConfig;

    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // 注入应用程序上下文
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        appComponent.inject(this);
        // 字体配置使用安卓第一个开源的字体库SourceSansPro
        ViewPump.init(ViewPump.builder()
            .addInterceptor(new CalligraphyInterceptor(mCalligraphyConfig)).build());
        // 初始化全局异常崩溃
        CaocConfig.Builder.create()
            // 当应用程序处于后台时崩溃,默默地关闭程序
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
            // 是否启动全局异常捕获
            .enabled(true)
            // 是否显示错误详细信息
            .showErrorDetails(true)
            // 是否显示重启按钮
            .showRestartButton(true)
            // 是否跟踪Activity
            .trackActivities(true)
            // 崩溃的间隔时间(毫秒)定义应用程序崩溃之间的最短时间,以确定我们不在崩溃循环中,比如:在规定的时间内再次崩溃,框架将不处理,让系统处理
            .minTimeBetweenCrashesMs(2000)
            // 错误图标
            .errorDrawable(R.drawable.icon_app)
            // 重新启动后的activity
            .restartActivity(LoginActivity.class)
            .apply();

        // Bugly 异常捕捉
        CrashReport.initCrashReport(this, BuildConfig.BUGLY_ID, BuildConfig.DEBUG);

        // 友盟统计、登录、分享 SDK
        // todo: 内部调用了过时的amdc请求接口导致频繁打印错误信息，等待后续版本修复，目前关闭日志打印
        UmengClient.init(this, false);

        // 设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            // 开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
            layout.setReboundDuration(300);
            layout.setFooterHeight(100);
            layout.setEnableAutoLoadMore(false);
            layout.setDisableContentWhenRefresh(false);
            layout.setDisableContentWhenLoading(false);
            layout.setEnableLoadMoreWhenContentNotFull(true);
        });
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.common_app_them, R.color.white);
            // 指定为经典Header，默认是 贝塞尔雷达Header
            return new ClassicsHeader(context)
                    .setTimeFormat(new TimeFormatUtil("上次更新 %s"));
        });
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            // 指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context)
                    .setDrawableSize(20)
                    .setPrimaryColor(ContextCompat.getColor(this, R.color.common_app_them))
                    .setAccentColor(ContextCompat.getColor(this, R.color.white));
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        Glide.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        Glide.get(this).onTrimMemory(level);
    }

}
