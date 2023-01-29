package com.dolphin.core.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dolphin.core.R;
import com.dolphin.core.bus.WeakMessenger;
import com.dolphin.core.constant.AppConstant;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 *<p>
 * 安卓基础活动
 * 处理活动绑定视图模型,生命周期管理
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/17
 */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends RxAppCompatActivity {

    /** 视图 */
    protected V mView;

    /** 视图模型 */
    protected VM mViewModel;

    /** 视图模型绑定字段Id */
    private int mViewModelId;

    /** material风格对话框,避免重复初始化 */
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewDataBinding(savedInstanceState);
        registerUiObservable();
    }

    /**
     * 初始视图跟模型双向绑定
     * @param savedInstanceState 临时的活动数据
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        mView = DataBindingUtil.setContentView(this, setContentView(savedInstanceState));
        mViewModelId = setVariableId();
        Class modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            // 获取当前类泛型中的第二个参数(VM)
            modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
        } else {
            // 如果没有指定泛型参数,则默认使用BaseViewModel
            modelClass = BaseViewModel.class;
        }
        mViewModel = (VM) new ViewModelProvider(this).get(modelClass);
        // 绑定视图上的data变量
        mView.setVariable(mViewModelId, mViewModel);
        // 设置生命周期为当前活动,让当前活动支持双向绑定
        mView.setLifecycleOwner(this);
        // 让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(mViewModel);
    }

    /** 注册ui(活动跟碎片)处理订阅 */
    protected void registerUiObservable() {

        /** 显示加载弹出框 */
        mViewModel.getUiObservable().getShowDialogEvent().observe(this, title -> {
            if (mMaterialDialog != null) {
                mMaterialDialog = mMaterialDialog.getBuilder().title(title).build();
                mMaterialDialog.show();
            } else {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                        .title(title)
                        .progress(true, 0)
                        .progressIndeterminateStyle(true)
                        .canceledOnTouchOutside(false)
                        .backgroundColorRes(R.color.white)
                        .keyListener((dialog, keyCode, event) -> false);
                mMaterialDialog = builder.show();
            }
        });

        /** 关闭加载弹出框 */
        mViewModel.getUiObservable().getCloseDialogEvent().observe(this, v -> {
            if (mMaterialDialog != null && mMaterialDialog.isShowing()) mMaterialDialog.dismiss();
        });

        /** 进入活动 */
        mViewModel.getUiObservable().getStartActivityEvent().observe(this, params -> {
            Class<?> clz = (Class<?>) params.get(AppConstant.ACTIVITY_CLASS);
            Bundle bundle = (Bundle) params.get(AppConstant.BUNDLE);
            startActivity(clz, bundle);
        });

        /** 进入碎片容器活动 */
        mViewModel.getUiObservable().getStartFragmentContainerActivityEvent().observe(this, params -> {
            String containerFragmentName = (String) params.get(AppConstant.CONTAINER_FRAGMENT_NAME);
            Bundle bundle = (Bundle) params.get(AppConstant.BUNDLE);
            startFragmentContainerActivity(containerFragmentName, bundle);
        });

        /** 关闭界面 */
        mViewModel.getUiObservable().getFinishEvent().observe(this, v -> finish());

        /** 返回上一层 */
        mViewModel.getUiObservable().getOnBackPressedEvent().observe(this, v -> onBackPressed());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 解除Messenger全局组件通信
        WeakMessenger.getDefault().unregister(mViewModel);
        // 解除视图变量绑定
        if(mView != null) mView.unbind();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // 拦截活动并设置字体风格
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    /**
     * 进入活动
     * @param clz 进入活动类
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 进入活动
     * @param clz 进入活动类
     * @param bundle 进入活动所携带的数据
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 进入碎片容器活动
     * @param containerFragmentName 所跳转的目的Fragment名称
     */
    public void startFragmentContainerActivity(String containerFragmentName) {
        startFragmentContainerActivity(containerFragmentName, null);
    }

    /**
     * 进入碎片容器活动
     * @param containerFragmentName 所跳转的目的Fragment名称
     * @param bundle 进入碎片容器活动所携带的数据
     */
    public void startFragmentContainerActivity(String containerFragmentName, Bundle bundle) {
        Intent intent = new Intent(this, FragmentContainerActivity.class);
        intent.putExtra(AppConstant.CONTAINER_FRAGMENT_NAME, containerFragmentName);
        if (bundle != null) {
            intent.putExtra(AppConstant.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * 设置活动视图
     * @param savedInstanceState 临时的活动数据
     * @return 布局视图的id
     */
    public abstract int setContentView(Bundle savedInstanceState);

    /**
     * 设置活动视图的绑定变量id
     * @return 数据绑定id
     */
    public abstract int setVariableId();


}
