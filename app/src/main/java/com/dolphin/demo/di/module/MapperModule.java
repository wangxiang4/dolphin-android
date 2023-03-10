package com.dolphin.demo.di.module;

import com.dolphin.core.http.HttpRequest;
import com.dolphin.demo.di.scope.MapperScope;
import com.dolphin.demo.mapper.LoginMapper;
import com.dolphin.demo.mapper.MessageMapper;

import dagger.Module;
import dagger.Provides;

/**
 *<p>
 * Mapper模块提供
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
@Module
public class MapperModule {

    @Provides
    @MapperScope
    LoginMapper provideLoginMapper() {
        return HttpRequest.getInstance().retrofit.create(LoginMapper.class);
    }

    @Provides
    @MapperScope
    MessageMapper provideMessageMapper() {
        return HttpRequest.getInstance().retrofit.create(MessageMapper.class);
    }

}
