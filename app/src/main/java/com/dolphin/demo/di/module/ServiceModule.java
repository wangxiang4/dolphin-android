package com.dolphin.demo.di.module;

import com.dolphin.demo.di.scope.ServiceScope;
import com.dolphin.demo.service.Impl.LoginServiceImpl;
import com.dolphin.demo.service.Impl.MessageServiceImpl;
import com.dolphin.demo.service.LoginService;
import com.dolphin.demo.service.MessageService;

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
    MessageService provideMessageService() {
        return new MessageServiceImpl();
    }

}
