package com.dolphin.demo.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CacheConstant;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.databinding.FragmentHomeBinding;
import com.dolphin.demo.entity.User;
import com.dolphin.demo.ui.adapter.HomeRecyclerAdapter;
import com.dolphin.demo.ui.vm.HomeViewModel;
import com.umeng.message.PushAgent;

import java.util.List;

/**
 *<p>
 * 首页
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> implements HomeRecyclerAdapter.EventListener {

    private RecyclerView mRecyclerView;
    private HomeRecyclerAdapter mAdapter;

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 登录进来获取到用户对象设置友盟消息推送别名,后台需要别名推送
        User user = CacheDiskUtils.getInstance().getParcelable(CacheConstant.USER_INFO, User.CREATOR);
        if (!StringUtils.isTrimEmpty(user.getId())) {
            PushAgent mPushAgent = PushAgent.getInstance(getActivity());
            mPushAgent.addAlias(user.getId(), CommonConstant.UMENG_PUSH_USER_ALIAS_TYPE, (success, message) ->{
                String msg;
                if (success) {
                    msg = "add alias success! type:" + CommonConstant.UMENG_PUSH_USER_ALIAS_TYPE + " alias:" + user.getId();
                } else {
                    msg = "add alias failure! msg:" + message;
                }
                LogUtils.i(msg);
            });
        } else throw new RuntimeException("用户对象为空请重新登录!");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = getView().findViewById(R.id.home_recycler_view);
        List<HomeRecyclerAdapter.HomeEntity> list = CollectionUtils.newArrayList(
               new HomeRecyclerAdapter.HomeEntity(
                       "1",
                       "高德地图",
                       null,
                       null,
                       0,
                       0,
                       null
               ),
                new HomeRecyclerAdapter.HomeEntity(
                        "2",
                        "可滑动列表",
                        null,
                        null,
                        0,
                        0,
                        null
                ),
                new HomeRecyclerAdapter.HomeEntity(
                        "3",
                        "后台保活",
                        null,
                        null,
                        0,
                        0,
                        null
                ),
                new HomeRecyclerAdapter.HomeEntity(
                        "4",
                        "地图路线规划",
                        null,
                        null,
                        0,
                        0,
                        null
                ),
                new HomeRecyclerAdapter.HomeEntity(
                        "5",
                        "文件上传",
                        null,
                        null,
                        0,
                        0,
                        null
                ),
                new HomeRecyclerAdapter.HomeEntity(
                        "6",
                        "文件下载",
                        null,
                        null,
                        0,
                        0,
                        null
                ),
                new HomeRecyclerAdapter.HomeEntity(
                        "7",
                        "图片选择器",
                        null,
                        null,
                        0,
                        0,
                        null
                ),
                new HomeRecyclerAdapter.HomeEntity(
                        "8",
                        "消息通知",
                        null,
                        null,
                        0,
                        0,
                        null
                )
        );
        final HomeRecyclerAdapter homeRecyclerAdapter = new HomeRecyclerAdapter(list);
        homeRecyclerAdapter.setEventListener(this);
        mAdapter = homeRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemViewClicked(HomeRecyclerAdapter.HomeEntity homeEntity) {
        switch (homeEntity.code) {
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
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
