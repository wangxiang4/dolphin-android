package com.dolphin.demo.ui.vm;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.MapUtils;
import com.dolphin.core.http.api.ResultResponse;
import com.dolphin.core.http.exception.ExceptionHandle;
import com.dolphin.core.util.RxUtil;
import com.dolphin.core.util.ToastUtil;
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
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        super.onCreate(owner);
        super.setTitleText("消息");
    }

    /** 下拉刷新查询消息列表 */
    public void refreshListMessage(RefreshLayout layout,
                                   MessageFragment messageFragment) {
        super.pageCurrent = 1;
        messageService.listMessage(MapUtils.newHashMap(Pair.create("current", pageCurrent), Pair.create("size", pageCurrent)))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            .doOnSubscribe(this)
            .doOnSubscribe(disposable -> showDialog())
            .subscribe(new DisposableObserver<ResultResponse<List<OssFile>>>() {
                @Override
                public void onNext(ResultResponse<List<OssFile>> R) {
                    if (R.getCode() == R.SUCCESS) {
                        listMessage.clear();
                        listMessage.addAll(R.getData());
                        messageFragment.mAdapter.notifyDataSetChanged();
                        /*if (mAdapter.getItemCount() <= R.getTotal()) {
                            //还有多的数据
                            layout.finishLoadMore();
                        } else {
                            //将不会再次触发加载更多事件
                            layout.finishLoadMoreWithNoMoreData();
                        }*/
                    } else ToastUtil.show(R.getMsg());
                }
                @Override
                public void onError(Throwable e) {
                    closeDialog();
                    layout.finishLoadMore(false);
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {
                    closeDialog();
                }
            });
    }

    /** 上拉加载查询消息列表 */
    public void footerListMessage(RefreshLayout layout,
                                  RecyclerView.Adapter mAdapter) {
        messageService.listMessage(MapUtils.newHashMap(Pair.create("current", pageCurrent += 1), Pair.create("size", pageCurrent)))
            .compose(RxUtil.schedulersTransformer())
            .compose(RxUtil.exceptionTransformer())
            .doOnSubscribe(this)
            .doOnSubscribe(disposable -> showDialog())
            .subscribe(new DisposableObserver<ResultResponse<List<OssFile>>>() {
                @Override
                public void onNext(ResultResponse<List<OssFile>> R) {
                    if (R.getCode() == R.SUCCESS) {
                        listMessage.addAll(R.getData());
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() <= R.getTotal()) {
                            //还有多的数据
                            layout.finishLoadMore();
                        } else {
                            //将不会再次触发加载更多事件
                            layout.finishLoadMoreWithNoMoreData();
                        }
                    } else ToastUtil.show(R.getMsg());
                }
                @Override
                public void onError(Throwable e) {
                    closeDialog();
                    layout.finishLoadMore(false);
                    ExceptionHandle.baseExceptionMsg(e);
                }
                @Override
                public void onComplete() {
                    closeDialog();
                }
            });
    }

}
