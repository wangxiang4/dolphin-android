package com.dolphin.demo.ui.activity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.widget.DefaultItemDecoration;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentDemoBinding;
import com.dolphin.demo.ui.adapter.DemoRecyclerAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;

import java.util.List;

/**
 *<p>
 * 友盟分享
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class DemoShareActivity extends BaseActivity<FragmentDemoBinding, ToolbarViewModel> implements DemoRecyclerAdapter.EventListener {

    private RecyclerView mRecyclerView;
    private DemoRecyclerAdapter mAdapter;

    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.fragment_demo;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel.setTitleText("友盟分享");
        mRecyclerView = findViewById(R.id.demo_recycler_view);
        List<DemoRecyclerAdapter.Entity> list = CollectionUtils.newArrayList(
                new DemoRecyclerAdapter.Entity().setCode("1").setTitle("QQ分享"),
                new DemoRecyclerAdapter.Entity().setCode("2").setTitle("微信分享")
        );
        final DemoRecyclerAdapter demoRecyclerAdapter = new DemoRecyclerAdapter(list);
        demoRecyclerAdapter.setEventListener(this);
        mAdapter = demoRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.common_divider_color)));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemViewClicked(DemoRecyclerAdapter.Entity entity) {
        switch (entity.code) {
            case "1":
                startFragmentContainerActivity("com.dolphin.demo.ui.fragment.share.QQShareFragment");
                break;
            case "2":
                startFragmentContainerActivity("com.dolphin.demo.ui.fragment.share.WechatShareFragment");
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
