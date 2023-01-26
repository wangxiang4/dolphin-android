package com.kicc.collect.di.component;

import com.kicc.collect.di.module.MapperModule;
import com.kicc.collect.di.scope.MapperScope;
import com.kicc.collect.service.Impl.MapLogisticsServiceImpl;
import com.kicc.collect.service.Impl.LoginServiceImpl;
import com.kicc.collect.service.Impl.MessageServiceImpl;

import dagger.Component;

/**
 *<p>
 * Mapper组件媒介
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
@MapperScope
@Component(modules = {MapperModule.class}, dependencies = AppComponent.class)
public interface MapperComponent {

    void inject(LoginServiceImpl impl);

    void inject(MapLogisticsServiceImpl impl);

    void inject(MessageServiceImpl impl);

}
