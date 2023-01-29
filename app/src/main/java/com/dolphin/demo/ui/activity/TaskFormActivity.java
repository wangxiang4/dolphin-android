package com.dolphin.demo.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.databinding.KcActivityTaskFormBinding;
import com.dolphin.demo.entity.MapTask;
import com.dolphin.demo.ui.vm.TaskFormViewModel;
import com.dolphin.core.base.BaseActivity;

/**
 * 报告单生成页
 * 可选择列表
 * 拍照上传以及提交
 * @author liusixiang
 * @date 2022/9/7
 */
public class TaskFormActivity extends BaseActivity<KcActivityTaskFormBinding, TaskFormViewModel> {

    private String mapTaskId;
    private MapTask mapTaskData = new MapTask();



    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.kc_activity_task_form;
    }

    @Override
    public int setVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapTaskId = (String)getIntent().getStringExtra(PointActivity.ARRIVE_MAP_TASK_ID);
        mViewModel.loadTaskData(mapTaskId);
        mViewModel.mapTask.observe(this, mapTasks -> {
            mapTaskData = mapTasks;
            renderData(mapTaskData);
        });

    }

    /**
     * 渲染数据
     * @param mapTaskData  */
    private void renderData(MapTask mapTaskData) {
        if (TextUtils.isEmpty(mapTaskData.getTenantId())) return;

    }

}