package com.dolphin.demo.di.component;

import com.dolphin.demo.di.module.ServiceModule;
import com.dolphin.demo.di.scope.ServiceScope;
import com.dolphin.demo.ui.vm.TabBarHomeViewModel;
import com.dolphin.demo.ui.vm.LoginViewModel;
import com.dolphin.demo.ui.vm.TabBarMessageViewModel;
import com.dolphin.demo.ui.vm.TabBarUserViewModel;

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

    void inject(TabBarHomeViewModel viewModel);

    void inject(TabBarMessageViewModel viewModel);

    void inject(TabBarUserViewModel viewModel);

    void inject(PointViewModel viewModel);

    void inject(CollectTaskListViewModel viewModel);

    void inject(PoiSearchViewModel viewModel);

    void inject(TaskFormViewModel viewModel);
}
