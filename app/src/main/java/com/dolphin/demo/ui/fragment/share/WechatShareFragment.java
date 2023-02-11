package com.dolphin.demo.ui.fragment.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.core.widget.DefaultItemDecoration;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentDemoBinding;
import com.dolphin.demo.ui.adapter.DemoRecyclerAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;

import java.util.List;

/**
 *<p>
 * 微信分享
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class WechatShareFragment extends BaseFragment<FragmentDemoBinding, ToolbarViewModel> implements DemoRecyclerAdapter.EventListener {

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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.setTitleText("微信分享");
        mRecyclerView = getView().findViewById(R.id.demo_recycler_view);
        List<DemoRecyclerAdapter.Entity> list = CollectionUtils.newArrayList(
                new DemoRecyclerAdapter.Entity().setCode("1").setTitle("纯文本"),
                new DemoRecyclerAdapter.Entity().setCode("2").setTitle("纯图片本地"),
                new DemoRecyclerAdapter.Entity().setCode("3").setTitle("纯图片http"),
                new DemoRecyclerAdapter.Entity().setCode("4").setTitle("链接（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("5").setTitle("链路跟踪链接（带root track code)"),
                new DemoRecyclerAdapter.Entity().setCode("6").setTitle("音乐（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("7").setTitle("视频（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("8").setTitle("微信表情"),
                new DemoRecyclerAdapter.Entity().setCode("9").setTitle("微信小程序")
        );
        final DemoRecyclerAdapter demoRecyclerAdapter = new DemoRecyclerAdapter(list);
        demoRecyclerAdapter.setEventListener(this);
        mAdapter = demoRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(requireContext(), R.color.common_divider_color)));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemViewClicked(DemoRecyclerAdapter.Entity entity) {
        switch (entity.code) {
            case "1":
                break;
            case "2":
                break;
            case "3":
                break;
            case "4":
                break;
            case "5":
                break;
            case "6":
                break;
            case "7":
                break;
            case "8":
                break;
            case "9":
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
    }

}
