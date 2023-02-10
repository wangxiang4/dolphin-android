package com.dolphin.demo.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.bus.WeakMessenger;
import com.dolphin.core.util.ToastUtil;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentDemoBinding;
import com.dolphin.demo.entity.MessengerDemo;
import com.dolphin.demo.entity.MessengerSubDemo;
import com.dolphin.demo.entity.MessengerSubSubDemo;
import com.dolphin.demo.ui.adapter.DemoRecyclerAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;

import java.util.List;

/**
 *<p>
 * 信使
 *</p>
 *
 * @Author: wangxiang4
 * @since: 2023/2/6
 */
public class DemoMessengerFragment extends BaseFragment<FragmentDemoBinding, ToolbarViewModel> implements DemoRecyclerAdapter.EventListener {

    private RecyclerView mRecyclerView;
    private DemoRecyclerAdapter mAdapter;

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_demo;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeakMessenger.getDefault().register(MessengerDemo.class,  () -> ToastUtil.show("MessengerDemo.class，接收没有消息动作(不带令牌)"));
        WeakMessenger.getDefault().register(MessengerSubDemo.class, true, () -> ToastUtil.show("MessengerSubDemo.class，接收没有消息动作(不带令牌"));
        WeakMessenger.getDefault().register(MessengerSubSubDemo.class, true, () -> ToastUtil.show("MessengerSubSubDemo.class，接收没有消息动作(不带令牌"));


        WeakMessenger.getDefault().register(MessengerDemo.class,  "2023", () -> ToastUtil.show("MessengerDemo.class，接收没有消息动作(带令牌"));
        WeakMessenger.getDefault().register(MessengerSubDemo.class, "2023" ,true, () -> ToastUtil.show("MessengerSubDemo.class，接收没有消息动作(带令牌"));
        WeakMessenger.getDefault().register(MessengerSubSubDemo.class, "2023" ,true, () -> ToastUtil.show("MessengerSubSubDemo.class，接收没有消息动作(带令牌"));


        WeakMessenger.getDefault().register(MessengerDemo.class, true,  MessengerDemo.class, msg -> {
            ToastUtil.show("MessengerDemo.class，接收有消息动作(不带令牌");
            LogUtils.i(GsonUtils.toJson(msg));
        });
        WeakMessenger.getDefault().register(MessengerSubDemo.class, true, MessengerSubDemo.class, msg -> {
            ToastUtil.show("MessengerSubDemo.class，接收有消息动作(不带令牌");
            LogUtils.i(GsonUtils.toJson(msg));
        });
        WeakMessenger.getDefault().register(MessengerSubSubDemo.class, true, MessengerSubSubDemo.class, msg -> {
            ToastUtil.show("MessengerSubSubDemo.class，接收有消息动作(不带令牌");
            LogUtils.i(GsonUtils.toJson(msg));
        });


        WeakMessenger.getDefault().register(MessengerDemo.class, "2023", true, msg -> {
            ToastUtil.show("MessengerDemo.class，接收有消息动作(带令牌");
            LogUtils.i(GsonUtils.toJson(msg));
        }, MessengerDemo.class);
        WeakMessenger.getDefault().register(MessengerSubDemo.class, "2023" ,true, msg -> {
            ToastUtil.show("MessengerSubDemo.class，接收有消息动作(带令牌");
            LogUtils.i(GsonUtils.toJson(msg));
        }, MessengerSubDemo.class);
        WeakMessenger.getDefault().register(MessengerSubSubDemo.class, "2023" ,true, msg -> {
            ToastUtil.show("MessengerSubSubDemo.class，接收有消息动作(带令牌");
            LogUtils.i(GsonUtils.toJson(msg));
        }, MessengerSubSubDemo.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.setTitleText("信使演示");
        mRecyclerView = getView().findViewById(R.id.demo_recycler_view);
        List<DemoRecyclerAdapter.Entity> list = CollectionUtils.newArrayList(
                new DemoRecyclerAdapter.Entity().setCode("1").setTitle("匹配持有对象发送无消息,支持子类传递"),
                new DemoRecyclerAdapter.Entity().setCode("2").setTitle("匹配令牌发送无消息,支持子类传递"),
                new DemoRecyclerAdapter.Entity().setCode("3").setTitle("匹配持有对象与令牌发送无消息,支持子类传递"),
                new DemoRecyclerAdapter.Entity().setCode("4").setTitle("匹配发送对象有消息,支持子类传递"),
                new DemoRecyclerAdapter.Entity().setCode("5").setTitle("匹配发送对象与令牌发送有消息,支持子类传递"),
                new DemoRecyclerAdapter.Entity().setCode("6").setTitle("匹配发送对象与持有对象发送有消息,支持子类传递")
        );
        final DemoRecyclerAdapter demoRecyclerAdapter = new DemoRecyclerAdapter(list);
        demoRecyclerAdapter.setEventListener(this);
        mAdapter = demoRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.icon_list_divider_h), true));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemViewClicked(DemoRecyclerAdapter.Entity entity) {
        switch (entity.code) {
            case "1":
                WeakMessenger.getDefault().sendNoMsgToTarget(MessengerDemo.class);
                break;
            case "2":
                WeakMessenger.getDefault().sendNoMsg("2023");
                break;
            case "3":
                WeakMessenger.getDefault().sendNoMsgToTargetWithToken("2023", MessengerDemo.class);
                break;
            case "4":
                WeakMessenger.getDefault().send(new MessengerSubSubDemo().setTitle("匹配发送对象有消息").setDescription("匹配发送对象有消息,支持子类传递"));
                break;
            case "5":
                WeakMessenger.getDefault().send(new MessengerSubSubDemo().setTitle("匹配发送对象与令牌发送有消息").setDescription("匹配发送对象与令牌发送有消息,支持子类传递"), "2023");
                break;
            case "6":
                WeakMessenger.getDefault().sendToTarget(new MessengerSubSubDemo().setTitle("匹配发送对象与持有对象发送有消息").setDescription("匹配发送对象与持有对象发送有消息,支持子类传递"), MessengerDemo.class);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WeakMessenger.getDefault().unregister(MessengerDemo.class);
        WeakMessenger.reset();
    }

}
