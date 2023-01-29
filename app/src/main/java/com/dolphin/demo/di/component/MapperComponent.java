package com.dolphin.demo.di.component;

import com.dolphin.demo.di.module.MapperModule;
import com.dolphin.demo.di.scope.MapperScope;
import com.dolphin.demo.service.Impl.MapLogisticsServiceImpl;
import com.dolphin.demo.service.Impl.LoginServiceImpl;
import com.dolphin.demo.service.Impl.MessageServiceImpl;

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
