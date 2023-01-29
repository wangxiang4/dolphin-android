package com.dolphin.demo.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.blankj.utilcode.util.SPUtils;
import com.dolphin.demo.BR;
import com.dolphin.demo.R;
import com.dolphin.demo.constant.CommonConstant;
import com.dolphin.demo.databinding.KcActivityPointPearchBinding;
import com.dolphin.demo.entity.PoiSearchResult;
import com.dolphin.demo.ui.adapter.PointSearchListAdapter;
import com.dolphin.demo.ui.vm.PoiSearchViewModel;
import com.dolphin.demo.util.PointRecyclerViewLayoutUtil;
import com.dolphin.core.base.BaseActivity;
import com.dolphin.core.entity.MapLogisticPoint;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 搜索途径点 设置定位
 * @author liusixiang
 * @date 2022.9.21
 */
public class PointSearchActivity extends BaseActivity<KcActivityPointPearchBinding, PoiSearchViewModel>
        implements TextWatcher, PoiSearch.OnPoiSearchListener{

    private Button                      poi_cancel;     // 取消按钮
    private EditText                    poi_word;       // 输入框
    private TextView                    poi_result_tip; // 提示文字
    private String                      location_city;  // 搜索范围城市
    private MapLogisticPoint            mapLogisticPoint; // 空预设点

    private PoiSearch                   poiSearch;  // 高德搜索组件
    private PoiSearch.Query             poi_query;  // 搜索回执对象
    private RecyclerView                rvResultView;   // 列表视图
    private PointSearchListAdapter      pointSearchListAdapter;

    private ArrayList<PoiSearchResult>  poiSearchResultList = new ArrayList();  // 搜索结果

    @Override
    public int setContentView(Bundle savedInstanceState) {
        return R.layout.kc_activity_point_pearch;
    }

    @Override
    public int setVariableId() {
        return BR.viewModelTest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapLogisticPoint = (MapLogisticPoint)getIntent().getParcelableExtra(CommonConstant.PRESET_PLAN_LAT_POINT);
        initModel();
    }

    /** 组件初始化 */
    private void initModel() {
        poi_word = findViewById(R.id.poi_edit_word);
        poi_word.addTextChangedListener(this);
        poi_cancel = findViewById(R.id.poi_btn_cancel);
        poi_result_tip = findViewById(R.id.poi_tv_tip);
        rvResultView = findViewById(R.id.rv_poi_res);
        //获取所在城市-默认为长沙
        location_city = SPUtils.getInstance().getString(CommonConstant.USER_NOW_CITY, "长沙");

        PointRecyclerViewLayoutUtil.grid(this, 1,rvResultView);
        rvResultView.setHasFixedSize(true);    // 确保Item宽或者高不会变
        pointSearchListAdapter = new PointSearchListAdapter(this);
        rvResultView.setAdapter(pointSearchListAdapter);
        pointSearchListAdapter.setGetListener(position -> {
            pointSearchListAdapter.setmPosition(position);
            pointSearchListAdapter.notifyDataSetChanged();
            choseItemData(position);
        });
    }

    /** 结果列表点击事件 */
    private void choseItemData(int position) {
        PoiSearchResult poiItem = poiSearchResultList.get(position);
        mapLogisticPoint.setHospitalName(poiItem.getAdress());
        mapLogisticPoint.setLng(poiItem.getLongitudel());
        mapLogisticPoint.setLat(poiItem.getLatitude());
        new MaterialDialog.Builder(this)
                .title("提 示")
                .content( "确定将【" + poiItem.getAdress() + "】设置为预设点？")
                .positiveText("确 认")
                .negativeText("取 消")
                .widgetColor(Color.RED)
                .onPositive((dialog, which) -> {
                    /** 拉起控制层请求数据 */
                    mViewModel.setPresetRequest(mapLogisticPoint);
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /** 取消搜索 */
    public void poiSearchCancel(View view){
        poi_word.setText(null);
        poi_cancel.setVisibility(View.GONE);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        if (TextUtils.isEmpty(s)){
            // 输入框内容为空时 隐藏 取消按钮、列表 显示 提示文字
            poi_cancel.setVisibility(View.GONE);
            rvResultView.setVisibility(View.GONE);
            poi_result_tip.setVisibility(View.VISIBLE);
            poi_result_tip.setText("请输入目的地关键字");
            return;
        }
        // 输入框内容不为空时 显示 取消按钮、列表 隐藏 提示文字
        poi_cancel.setVisibility(View.VISIBLE);
        rvResultView.setVisibility(View.VISIBLE);
        poi_result_tip.setVisibility(View.GONE);
        poi_query = new PoiSearch.Query(s.toString(), null, location_city); // // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        poi_query.setPageNum(10);
        poi_query.setPageNum(1);
        try {
            poiSearch = new PoiSearch(this ,poi_query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPoiSearched(PoiResult result, int iCode) {
        if (iCode != 1000 && result == null) {
            poi_result_tip.setVisibility(View.VISIBLE);
            poi_result_tip.setText("搜索失败，请检查！");
            return;
        }
        poi_result_tip.setVisibility(View.GONE);
        ArrayList<PoiItem> poiResult = result.getPois();
        if (poiResult == null){
            poi_result_tip.setVisibility(View.VISIBLE);
            poi_result_tip.setText("未找到相关地点请重试");
            return;
        }
        poiSearchResultList = (ArrayList<PoiSearchResult>) poiResult.stream().map(item -> {
            LatLonPoint llp = item.getLatLonPoint();
            double lng = llp.getLongitude();
            double lat = llp.getLatitude();
            String name = item.getTitle();
            String type = item.getTypeDes().substring(0, item.getTypeDes().indexOf(";"));
            String periphery = item.getSnippet();
            return new PoiSearchResult(lng, lat, name, type, periphery);
        }).collect(Collectors.toList());

        pointSearchListAdapter.setData(poiSearchResultList);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
