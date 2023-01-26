package com.kicc.collect.di.module;

import com.kicc.collect.di.scope.ServiceScope;
import com.kicc.collect.service.Impl.LoginServiceImpl;
import com.kicc.collect.service.Impl.MapLogisticsServiceImpl;
import com.kicc.collect.service.Impl.MessageServiceImpl;
import com.kicc.collect.service.LoginService;
import com.kicc.collect.service.MapLogisticsService;
import com.kicc.collect.service.MessageService;

import dagger.Module;
import dagger.Provides;

/**
 *<p>
 * 服务模块提供
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
@Module
public class ServiceModule {

    @Provides
    @ServiceScope
    LoginService provideLoginService() {
        return new LoginServiceImpl();
    }

    @Provides
    @ServiceScope
    MapLogisticsService provideMapLogisticsService() {
        return new MapLogisticsServiceImpl();
    }

    @Provides
    @ServiceScope
    MessageService provideMessageService() {
        return new MessageServiceImpl();
    }

}
