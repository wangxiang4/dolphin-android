package com.dolphin.demo.ui.activity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.base.BaseViewModel;
import com.dolphin.core.constant.AppConstant;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.ActivityTabBarBinding;
import com.dolphin.demo.ui.fragment.HomeFragment;
import com.dolphin.demo.ui.fragment.MessageFragment;
import com.dolphin.demo.ui.fragment.UserFragment;
import com.dolphin.demo.ui.fragment.WorkbenchFragment;

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
public class TabBarActivity extends BaseActivity<ActivityTabBarBinding, BaseViewModel> {

    private List<Fragment> mFragments;
    private PageNavigationView pageNavigationView;

    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.activity_tab_bar;
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
        mFragments.add(new HomeFragment());
        mFragments.add(new WorkbenchFragment());
        mFragments.add(new MessageFragment());
        mFragments.add(new UserFragment());
        Integer defaultPoint = getIntent().getIntExtra(AppConstant.TAB_BAR_DEFAULT_INDEX, 0);
        commitAllowingStateLoss(defaultPoint);
        initBottomTab(defaultPoint);
    }

    private void initBottomTab(int position) {
        NavigationController navigationController = pageNavigationView.material()
                .addItem(R.drawable.icon_home, "首页")
                .addItem(R.drawable.icon_workbench, "工作台")
                .addItem(R.drawable.icon_message,"消息")
                .addItem(R.drawable.icon_user, "我的")
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
