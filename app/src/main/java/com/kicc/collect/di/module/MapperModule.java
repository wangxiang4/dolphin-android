package com.kicc.collect.di.module;

import com.kicc.collect.di.scope.MapperScope;
import com.kicc.collect.mapper.LoginMapper;
import com.kicc.collect.mapper.MapLogisticsMapper;
import com.kicc.collect.mapper.MessageMapper;
import com.kicc.core.http.HttpRequest;

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
    MapLogisticsMapper provideMapLogisticsMapper() {
        return HttpRequest.getInstance().retrofit.create(MapLogisticsMapper.class);
    }

    @Provides
    @MapperScope
    MessageMapper provideMessageMapper(){
        return HttpRequest.getInstance().retrofit.create(MessageMapper.class);
    }

}
