package com.kicc.collect.ui.activity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kicc.collect.BR;
import com.kicc.collect.R;
import com.kicc.collect.databinding.KcActivityTabBarBinding;
import com.kicc.collect.ui.fragment.TabBarHomeFragment;
import com.kicc.collect.ui.fragment.TabBarMessageFragment;
import com.kicc.collect.ui.fragment.TabBarUserFragment;
import com.kicc.collect.ui.fragment.TabBarWorkbenchFragment;
import com.kicc.core.base.BaseActivity;
import com.kicc.core.base.BaseViewModel;
import com.kicc.core.constant.AppConstant;

import java.util.ArrayList;
import java.util.List;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

/**
 *<p>
 * 底部标签栏活动
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/21
 */
public class TabBarActivity extends BaseActivity<KcActivityTabBarBinding, BaseViewModel> {

    private List<Fragment> mFragments;
    private PageNavigationView pageNavigationView;

    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.kc_activity_tab_bar;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNavigationView = findViewById(R.id.pager_bottom_tab);
        initFragment();

    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new TabBarHomeFragment());
        mFragments.add(new TabBarWorkbenchFragment());
        mFragments.add(new TabBarMessageFragment());
        mFragments.add(new TabBarUserFragment());
        Integer defaultPoint = getIntent().getIntExtra(AppConstant.TAB_BAR_DEFAULT_INDEX, 0);
        commitAllowingStateLoss(defaultPoint);
        initBottomTab(defaultPoint);
    }

    private void initBottomTab(int position) {
        NavigationController navigationController = pageNavigationView.material()
                .addItem(R.drawable.kc_ic_tab_map, "地图")
                .addItem(R.drawable.kc_ic_tab_workbench, "任务")
                .addItem(R.drawable.kc_ic_tab_message,"消息")
                .addItem(R.drawable.kc_ic_tab_user, "个人")
                .setDefaultColor(ContextCompat.getColor(this, R.color.black))
                .build();
        // 设置默认底部按钮选中
        navigationController.setSelect(position);
        // 底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                commitAllowingStateLoss(index);
            }

            @Override
            public void onRepeat(int index) {
            }
        });
    }

    private void commitAllowingStateLoss(int position) {
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            currentFragment = mFragments.get(position);
            transaction.add(R.id.frameLayout, currentFragment, position + "");
        }
        transaction.commitAllowingStateLoss();
    }

    // 隐藏所有Fragment
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
