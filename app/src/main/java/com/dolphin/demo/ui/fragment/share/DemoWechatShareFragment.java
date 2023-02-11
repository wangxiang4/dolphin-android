package com.dolphin.demo.ui.fragment.share;

import android.content.Intent;
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
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;

import java.util.List;

/**
 *<p>
 * 微信常用分享
 * 更多分享方式请参考友盟官网案例:https://github.com/umeng/MultiFunctionAndroidDemo
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class DemoWechatShareFragment extends BaseFragment<FragmentDemoBinding, ToolbarViewModel> implements DemoRecyclerAdapter.EventListener, UmengShareListener.OnShareListener {

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
                new DemoRecyclerAdapter.Entity().setCode("5").setTitle("音乐（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("6").setTitle("视频（有标题，有内容）"),
                new DemoRecyclerAdapter.Entity().setCode("7").setTitle("微信表情"),
                new DemoRecyclerAdapter.Entity().setCode("8").setTitle("微信小程序")
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
                UmengClient.share(getActivity(), PlatformEnum.WECHAT, new ShareAction(getActivity()).withText("完全免费开源,包括高级功能,打造一个web端,安卓端,ios端的一个海豚架构平台生态圈"), this);
                break;
            case "2":
                UMImage imageLocal = new UMImage(getActivity(), R.drawable.icon_logo);
                UmengClient.share(getActivity(), PlatformEnum.QQ, new ShareAction(getActivity()).withMedia(imageLocal), this);
                break;
            case "3":
                UMImage imageUrl = new UMImage(getActivity(), "https://godolphinx.org/dolphin1024x1024.png");
                UmengClient.share(getActivity(), PlatformEnum.WECHAT, new ShareAction(getActivity()).withMedia(imageUrl), this);
                break;
            case "4":
                UMWeb web = new UMWeb("https://godolphinx.org");
                web.setTitle("This is web title");
                web.setThumb(new UMImage(getActivity(), R.drawable.icon_app));
                web.setDescription("my description");
                UmengClient.share(getActivity(), PlatformEnum.WECHAT, new ShareAction(getActivity()).withMedia(web), this);
                break;
            case "5":
                UMusic music = new UMusic("https://y.qq.com/n/yqq/song/108782194_num.html?ADTAG=h5_playsong&no_redirect=1");
                music.setTitle("This is music title");
                music.setThumb(new UMImage(getActivity(), R.drawable.icon_app));
                music.setDescription("my description");
                UmengClient.share(getActivity(), PlatformEnum.WECHAT, new ShareAction(getActivity()).withMedia(music), this);
                break;
            case "6":
                UMVideo video = new UMVideo("http://video.sina.com.cn/p/sports/2020-01-15/detail-iihnzhha2647094.d.html");
                video.setTitle("This is video title");
                video.setThumb(new UMImage(getActivity(), R.drawable.icon_app));
                video.setDescription("my description");
                UmengClient.share(getActivity(), PlatformEnum.WECHAT, new ShareAction(getActivity()).withMedia(video), this);
                break;
            case "7":
                UMEmoji emoji = new UMEmoji(getActivity(), R.drawable.icon_tutu);
                emoji.setThumb(new UMImage(getActivity(), R.drawable.icon_app));
                UmengClient.share(getActivity(), PlatformEnum.WECHAT, new ShareAction(getActivity()).withMedia(emoji), this);
                break;
            case "8":
                UMMin umMin = new UMMin("https://godolphinx.org");
                umMin.setThumb(new UMImage(getActivity(), R.drawable.icon_app));
                umMin.setTitle("【友盟+】社会化组件U-Share");
                umMin.setDescription("欢迎使用【友盟+】社会化组件U-Share，SDK包最小，集成成本最低，助力您的产品开发、运营与推广");
                umMin.setPath("pages/page10007/page10007");
                umMin.setUserName("gh_3ac2059ac66f");
                UmengClient.share(getActivity(), PlatformEnum.WECHAT, new ShareAction(getActivity()).withMedia(umMin), this);
                break;
        }
    }

    @Override
    public void onStart(PlatformEnum platformEnum) {
        mViewModel.showDialog();
    }

    @Override
    public void onSucceed(PlatformEnum platformEnum) {
        mViewModel.closeDialog();
        ToastUtil.show("成功了");
    }

    @Override
    public void onError(PlatformEnum platformEnum, Throwable t) {
        mViewModel.closeDialog();
        ToastUtil.show("失败" + t.getMessage());    }

    @Override
    public void onCancel(PlatformEnum platformEnum) {
        mViewModel.closeDialog();
        ToastUtil.show("取消了");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
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
