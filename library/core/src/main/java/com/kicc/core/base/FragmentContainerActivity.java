package com.kicc.core.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.blankj.utilcode.util.StringUtils;
import com.kicc.core.R;
import com.kicc.core.constant.AppConstant;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;
import java.lang.ref.WeakReference;

/**
 *<p>
 * 碎片容器活动
 * 碎片充当活动,避免创建一个活动就要取注册一次
 * https://juejin.cn/post/6998439234734391332#heading-26
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/17
 */
public class FragmentContainerActivity extends RxAppCompatActivity {

    protected WeakReference<Fragment> mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kc_activity_container);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = fragmentManager.getFragment(savedInstanceState, AppConstant.TEMP_CONTAINER_FRAGMENT_INSTANCE_STATE);
        }
        if (fragment == null) {
            fragment = createFragment(getIntent());
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        mFragment = new WeakReference(fragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前碎片实例,下次创建自身活动时候可以直接拿出上次保存过的使用
        getSupportFragmentManager().putFragment(outState, AppConstant.TEMP_CONTAINER_FRAGMENT_INSTANCE_STATE, mFragment.get());
    }

    /** 创建碎片页面 */
    protected Fragment createFragment(Intent data) {
        if (data == null) throw new RuntimeException("您必须提供页面信息才能显示!");
        try {
            String fragmentName = data.getStringExtra(AppConstant.CONTAINER_FRAGMENT_NAME);
            if (StringUtils.isTrimEmpty(fragmentName)) throw new IllegalArgumentException("找不到碎片页面名称!");
            Class<?> fragmentClass = Class.forName(fragmentName);
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            Bundle args = data.getBundleExtra(AppConstant.BUNDLE);
            // 创建碎片时传递参数
            if (args != null) fragment.setArguments(args);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("片段初始化失败!");
    }

}
