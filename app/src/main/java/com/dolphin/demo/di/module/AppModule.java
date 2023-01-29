package com.dolphin.demo.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.inflationx.calligraphy3.CalligraphyConfig;

/**
 *<p>
 * app应用程序模块提供
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    CalligraphyConfig provideCalligraphyDefaultConfig() {
        return new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Oswald-Stencbab.ttf")
                .setFontAttrId(io.github.inflationx.calligraphy3.R.attr.fontPath)
                .build();
    }

}
