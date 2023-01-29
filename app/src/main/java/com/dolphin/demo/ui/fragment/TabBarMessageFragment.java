package com.dolphin.demo.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.KcFragmentTabBarMessageBinding;
import com.dolphin.demo.entity.Message;
import com.dolphin.demo.ui.adapter.TabBarMessageAdapter;
import com.dolphin.demo.ui.vm.TabBarMessageViewModel;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.bus.RxBus;
import com.dolphin.core.bus.RxSubscriptions;
import com.dolphin.core.entity.MapLogisticPoint;
import com.dolphin.core.util.RxUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 *<p>
 *  消息碎片页
 *</p>
 *
 * @Author: liuSiXiang
 * @since: 2022/10/25
 */
public class TabBarMessageFragment extends BaseFragment<KcFragmentTabBarMessageBinding, TabBarMessageViewModel> {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Message> messageData = new ArrayList<>();
    private Disposable mSubscription;

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.kc_fragment_tab_bar_message;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubscription = RxBus.getInstance().toObservableSticky(MapLogisticPoint.class)
                .compose(RxUtil.schedulersTransformer())
                .compose(RxUtil.exceptionTransformer())
                .subscribe(mapLogisticPoint -> {
                    // todo: 此处进行数据处理
//                    ToastUtil.show(getActivity(), new Gson().toJson(mapLogisticPoint));
//                    LogUtils.i(mapLogisticPoint);
//                    new Gson().toJson(mapLogisticPoint);
//                    String define = String.valueOf(mapLogisticPoint.getJSONObject("payload"));

                    new MaterialDialog.Builder(getActivity())
                            .title("新增交接点")
                            .content("确定将【" + "null" + "】设为交接点？")
                            .positiveText("取 消")
                            .negativeText("已 读")
                            .widgetColor(Color.RED)
                            .show();
                    // 接收到消息 处理完成之后 刷新列表
                });
        // 加入订阅管理防止内存泄露
        RxSubscriptions.add(mSubscription);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除RX监听防止内存泄露
        RxSubscriptions.remove(mSubscription);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = getActivity().findViewById(R.id.message_recycler_view);
        final TabBarMessageAdapter myItemAdapter = new TabBarMessageAdapter(messageData);
        myItemAdapter.setEventListener(new TabBarMessageAdapter.EventListener() {
            @Override
            public void onItemViewClicked(View v) {
                handleOnItemViewClicked(v);
            }
        });
        mAdapter = myItemAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        // 配置回收视图
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mViewModel.loadListData();
        mViewModel.itemDataList.observe(this, itemData -> {
            messageData.clear();

            LogUtils.i(itemData);
            messageData.addAll(itemData);
            mAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    /** 列表点击事件 */
    private void handleOnItemViewClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        Message item = messageData.get(position);
        String title;
        String str = item.getContent().trim();
        if ((str.startsWith("{") && str.endsWith("}")) || str.startsWith("[") && str.endsWith("]")){
            MapLogisticPoint mapLogisticPoint = new Gson().fromJson(item.getContent(),MapLogisticPoint.class);
            title = TextUtils.isEmpty(mapLogisticPoint.getHospitalName()) ? "确认已读当前信息？" : "确定将【"+item.getName()+"】的预设点设置为【"+mapLogisticPoint.getHospitalName()+"】？";
        }else title = "确认已读【" + item.getName() + "】信息？";
        new MaterialDialog.Builder(getActivity())
                .title("提示")
                .content(title)
                .positiveText("确 定")
                .negativeText("取 消")
                .widgetColor(Color.RED)
                .onPositive((dialog, which) -> {
                    /** 确认后发送退出请求 */
                      mViewModel.sendStatus(item);
                }).show();
    }
}
