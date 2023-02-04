package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.util.RxUtil;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.app.AppApplication;
import com.dolphin.demo.di.component.DaggerServiceComponent;
import com.dolphin.demo.entity.OssFile;
import com.dolphin.demo.service.MessageService;
import com.dolphin.demo.ui.fragment.MessageFragment;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import io.reactivex.observers.DisposableObserver;

/**
 *<p>
 * 消息视图模型层
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @since: 2022/11/1
 */
public class MessageViewModel extends ToolbarViewModel<MessageFragment> {

    @Inject
    MessageService messageService;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        // 注入服务组件
        DaggerServiceComponent
                .builder()
                .appComponent(((AppApplication) Utils.getApp().getApplicationContext()).appComponent)
                .build().inject(this);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("消息");
    }

    /** 下拉刷新 */
    public void refresh(RefreshLayout refresh) {
        super.pageCurrent = 1;
        messageService.listMessage(MapUtils.newHashMap(Pair.create("current", pageCurrent), Pair.create("size", pageSize)))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            .doOnSubscribe(this)
            .doOnSubscribe(disposable -> mActivity.mLoadingLayout.showLoading())
            .subscribe(new DisposableObserver<ResultResponse<List<OssFile>>>() {
                @Override
                public void onNext(ResultResponse<List<OssFile>> R) {
                    if (R.getCode() == R.SUCCESS) {
                        mActivity.mAdapter.refresh(R.getData());
                        if (mActivity.mAdapter.getItemCount() < R.getTotal()) {
                            refresh.finishRefresh();
                        } else refresh.finishRefreshWithNoMoreData();
                    } else {
                        refresh.finishRefresh(false);
                        ToastUtil.show(R.getMsg());
                    }
                }
                @Override
                public void onError(Throwable e) {
                    refresh.finishRefresh(false);
                    mActivity.mLoadingLayout.showEmpty();
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {
                    if (mActivity.mAdapter.getItemCount() > 0) {
                        mActivity.mLoadingLayout.showContent();
                    } else mActivity.mLoadingLayout.showEmpty();
                }
            });
    }

    /** 加载更多 */
    public void loadMore(RefreshLayout layout) {
        messageService.listMessage(MapUtils.newHashMap(Pair.create("current", pageCurrent += 1), Pair.create("size", pageSize)))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            .doOnSubscribe(this)
            .subscribe(new DisposableObserver<ResultResponse<List<OssFile>>>() {
                @Override
                public void onNext(ResultResponse<List<OssFile>> R) {
                    if (R.getCode() == R.SUCCESS) {
                        mActivity.mAdapter.loadMore(R.getData());
                        if (mActivity.mAdapter.getItemCount() < R.getTotal()) {
                            layout.finishLoadMore();
                        } else layout.finishLoadMoreWithNoMoreData();
                    } else {
                        layout.finishLoadMore(false);
                        ToastUtil.show(R.getMsg());
                    }
                }
                @Override
                public void onError(Throwable e) {
                    layout.finishLoadMore(false);
                    mActivity.mLoadingLayout.showEmpty();
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {
                    if (mActivity.mAdapter.getItemCount() > 0) {
                        mActivity.mLoadingLayout.showContent();
                    } else mActivity.mLoadingLayout.showEmpty();
                }
            });
    }

}
