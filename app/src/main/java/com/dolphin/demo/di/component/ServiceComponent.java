package com.dolphin.demo.di.component;

import com.dolphin.demo.di.module.ServiceModule;
import com.dolphin.demo.di.scope.ServiceScope;
import com.dolphin.demo.ui.vm.HomeViewModel;
import com.dolphin.demo.ui.vm.LoginViewModel;
import com.dolphin.demo.ui.vm.MessageViewModel;
import com.dolphin.demo.ui.vm.UserViewModel;

import dagger.Component;

/**
 *<p>
 * 服务组件媒介
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/14
 */
@ServiceScope
@Component(modules = {ServiceModule.class}, dependencies = AppComponent.class)
public interface ServiceComponent {

    void inject(LoginViewModel viewModel);

    void inject(HomeViewModel viewModel);

    void inject(MessageViewModel viewModel);

    void inject(UserViewModel viewModel);

}
