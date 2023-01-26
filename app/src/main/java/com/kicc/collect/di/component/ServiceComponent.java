package com.kicc.collect.di.component;

import com.kicc.collect.di.module.ServiceModule;
import com.kicc.collect.di.scope.ServiceScope;
import com.kicc.collect.ui.vm.CollectTaskListViewModel;
import com.kicc.collect.ui.vm.TaskFormViewModel;
import com.kicc.collect.ui.vm.PoiSearchViewModel;
import com.kicc.collect.ui.vm.PointViewModel;
import com.kicc.collect.ui.vm.TabBarHomeViewModel;
import com.kicc.collect.ui.vm.LoginViewModel;
import com.kicc.collect.ui.vm.TabBarMessageViewModel;
import com.kicc.collect.ui.vm.TabBarUserViewModel;

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
