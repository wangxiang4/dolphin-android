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

    public List<OssFile> listMessage = new ArrayList();

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

    /** 下拉刷新查询消息列表 */
    public void refreshListMessage(RefreshLayout refresh) {
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
                        listMessage.clear();
                        listMessage.addAll(R.getData());
                        mActivity.mAdapter.notifyDataSetChanged();
                        if (mActivity.mAdapter.getItemCount() <= R.getTotal()) {
                            refresh.finishRefresh();
                        } else refresh.finishLoadMoreWithNoMoreData();
                    } else {
                        ToastUtil.show(R.getMsg());
                        refresh.finishLoadMore(false);
                    }
                }
                @Override
                public void onError(Throwable e) {
                    refresh.finishLoadMore(false);
                    mActivity.mLoadingLayout.showError();
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {
                    mActivity.mLoadingLayout.showContent();
                }
            });
    }

    /** 上拉加载查询消息列表 */
    public void footerListMessage(RefreshLayout layout) {
        messageService.listMessage(MapUtils.newHashMap(Pair.create("current", pageCurrent += 1), Pair.create("size", pageCurrent)))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            .doOnSubscribe(this)
            .subscribe(new DisposableObserver<ResultResponse<List<OssFile>>>() {
                @Override
                public void onNext(ResultResponse<List<OssFile>> R) {
                    if (R.getCode() == R.SUCCESS) {
                        listMessage.addAll(R.getData());
                        mActivity.mAdapter.notifyDataSetChanged();
                        if (mActivity.mAdapter.getItemCount() <= R.getTotal()) {
                            layout.finishLoadMore();
                        } else layout.finishLoadMoreWithNoMoreData();
                    } else {
                        ToastUtil.show(R.getMsg());
                        layout.finishLoadMore(false);
                    }
                }
                @Override
                public void onError(Throwable e) {
                    layout.finishLoadMore(false);
                    mActivity.mLoadingLayout.showError();
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {}
            });
    }

}
