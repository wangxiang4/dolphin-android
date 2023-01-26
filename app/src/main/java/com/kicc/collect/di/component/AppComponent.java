package com.kicc.collect.di.component;

import android.app.Application;

import com.kicc.collect.app.AppApplication;
import com.kicc.collect.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 *<p>
 * app应用程序组件媒介
 * 采用依赖注入了解参考: https://juejin.cn/post/6844903605548367885
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(AppApplication app);

    /** 参考: https://juejin.cn/post/6844903544567398408 */
    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
