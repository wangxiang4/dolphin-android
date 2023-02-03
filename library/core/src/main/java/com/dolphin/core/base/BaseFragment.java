package com.dolphin.core.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dolphin.core.R;
import com.dolphin.core.bus.WeakMessenger;
import com.dolphin.core.constant.AppConstant;
import com.trello.rxlifecycle4.components.support.RxFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *<p>
 * 安卓基础碎片
 * 处理碎片绑定视图模型,生命周期管理
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/10/17
 */
public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends RxFragment {

    /** 视图 */
    protected V mView;

    /** 视图模型 */
    protected VM mViewModel;

    /** 视图模型绑定字段Id */
    private int mViewModelId;

    /** material风格对话框,避免重复初始化 */
    private MaterialDialog mMaterialDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        mView = DataBindingUtil.inflate(inflater, setContentView(inflater, parentContainer, savedInstanceState), parentContainer, false);
        return mView.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewDataBinding();
        registerUiObservable();
    }

    /** 初始视图跟模型双向绑定 */
    private void initViewDataBinding() {
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
                MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
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
        mViewModel.getUiObservable().getFinishEvent().observe(this, v -> getActivity().finish());

        /** 返回上一层 */
        mViewModel.getUiObservable().getOnBackPressedEvent().observe(this, v -> getActivity().onBackPressed());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 解除Messenger全局组件通信
        WeakMessenger.getDefault().unregister(mViewModel);
        // 解除视图变量绑定
        if(mView != null) mView.unbind();
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
        Intent intent = new Intent(getContext(), clz);
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
        Intent intent = new Intent(getContext(), FragmentContainerActivity.class);
        intent.putExtra(AppConstant.CONTAINER_FRAGMENT_NAME, containerFragmentName);
        if (bundle != null) {
            intent.putExtra(AppConstant.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * 设置父级容器内容视图
     * @param inflater 布局扩充对象,设置视图
     * @param parentContainer 父级容器视图组
     * @param savedInstanceState 临时的活动数据
     * @return 布局视图的id
     */
    public abstract int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState);

    /**
     * 设置活动视图的绑定变量id
     * @return 数据绑定id
     */
    public abstract int setVariableId();

}
