package com.dolphin.demo.ui.fragment.share;

import android.app.ProgressDialog;
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
import com.dolphin.core.util.ToastUtil;
import com.dolphin.core.widget.DefaultItemDecoration;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentDemoBinding;
import com.dolphin.demo.ui.adapter.DemoRecyclerAdapter;
import com.dolphin.demo.ui.vm.ToolbarViewModel;
import com.dolphin.umeng.UmengClient;
import com.dolphin.umeng.enums.PlatformEnum;
import com.dolphin.umeng.listener.UmengShareListener;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.List;

/**
 *<p>
 * QQ分享
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class QQShareFragment extends BaseFragment<FragmentDemoBinding, ToolbarViewModel> implements DemoRecyclerAdapter.EventListener, UmengShareListener.OnShareListener {

    private RecyclerView mRecyclerView;
    private DemoRecyclerAdapter mAdapter;
    private ProgressDialog dialog;

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
        dialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.setTitleText("QQ分享");
        mRecyclerView = getView().findViewById(R.id.demo_recycler_view);
        List<DemoRecyclerAdapter.Entity> list = CollectionUtils.newArrayList(
                new DemoRecyclerAdapter.Entity().setCode("1").setTitle("纯图片本地"),
                new DemoRecyclerAdapter.Entity().setCode("2").setTitle("纯图片http"),
                new DemoRecyclerAdapter.Entity().setCode("3").setTitle("链接（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("4").setTitle("音乐（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("5").setTitle("视频（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("6").setTitle("QQ小程序")
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
                UmengClient.share(getActivity(), PlatformEnum.QQ, new ShareAction(getActivity())
                        .withText("欢迎使用【友盟+】社会化组件U-Share，SDK包最小，集成成本最低，助力您的产品开发、运营与推广"), this);
                break;
            case "2":
                break;
            case "3":
                break;
            case "4":
                break;
            case "5":
                break;
        }
    }

    @Override
    public void onStart(PlatformEnum platformEnum) {
        SocializeUtils.safeShowDialog(dialog);
    }

    @Override
    public void onSucceed(PlatformEnum platformEnum) {
        SocializeUtils.safeCloseDialog(dialog);
        ToastUtil.show("成功了");
    }

    @Override
    public void onError(PlatformEnum platformEnum, Throwable t) {
        SocializeUtils.safeCloseDialog(dialog);
        ToastUtil.show("失败" + t.getMessage());
    }

    @Override
    public void onCancel(PlatformEnum platformEnum) {
        SocializeUtils.safeCloseDialog(dialog);
        ToastUtil.show("取消了");
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
