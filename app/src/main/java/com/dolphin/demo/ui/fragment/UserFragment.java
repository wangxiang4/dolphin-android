package com.dolphin.demo.ui.fragment;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.CollectionUtils;
import com.dolphin.core.base.BaseFragment;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.FragmentUserBinding;
import com.dolphin.demo.ui.activity.AboutActivity;
import com.dolphin.demo.ui.adapter.UserRecyclerAdapter;
import com.dolphin.demo.ui.vm.UserViewModel;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;

import java.util.List;

/**
 *<p>
 * 我的
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/7/15
 */
public class UserFragment extends BaseFragment<FragmentUserBinding, UserViewModel> implements UserRecyclerAdapter.EventListener {

    private RecyclerView mRecyclerView;
    private UserRecyclerAdapter mAdapter;

    @Override
    public int setContentView(LayoutInflater inflater, @Nullable ViewGroup parentContainer, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_user;
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
        ImageView toolbarBack = getView().findViewById(R.id.iv_back);
        toolbarBack.setVisibility(View.INVISIBLE);
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        List<UserRecyclerAdapter.UserEntity> list = CollectionUtils.newArrayList(
                new UserRecyclerAdapter.UserEntity().setCode("1").setTitle("关于我们").setBadge(0).setImage(R.drawable.icon_about),
                new UserRecyclerAdapter.UserEntity().setCode("2").setTitle("退出登录").setBadge(0).setImage(R.drawable.icon_exit)
        );
        final UserRecyclerAdapter userRecyclerAdapter = new UserRecyclerAdapter(list);
        userRecyclerAdapter.setEventListener(this);
        mAdapter = userRecyclerAdapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.icon_list_divider_h), true));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemViewClicked(UserRecyclerAdapter.UserEntity userEntity) {
        switch (userEntity.code) {
            case "1":
                startActivity(AboutActivity.class);
                break;
            case "2":
                new MaterialDialog.Builder(getActivity())
                    .title("温馨提示")
                    .content(R.string.login_out)
                    .positiveText("确认")
                    .negativeText("取消")
                    .widgetColor(Color.RED)
                    .onPositive((dialog, which) -> mViewModel.loginOut()).show();
                break;
        }
    }

}
