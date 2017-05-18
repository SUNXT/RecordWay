package com.sun.recordway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final long RIPPLE_DURATION = 250;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_menu)
    ImageView iv_menu;
    @BindView(R.id.activity_main)
    FrameLayout root;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.map)
    MapView  mMapView;

    private LinearLayout line_menu_mine;
    private LinearLayout line_menu_map;
    private LinearLayout line_menu_record;
    private LinearLayout line_menu_setting;

    private ImageView iv_menu_mine;
    private ImageView iv_menu_map;
    private ImageView iv_menu_record;
    private ImageView iv_menu_setting;
    private TextView  tv_menu_mine;
    private TextView  tv_menu_map;
    private TextView  tv_menu_record;
    private TextView  tv_menu_seeting;

    private GuillotineAnimation guillotineAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        AMap aMap = null;

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
    }

    private void initView(){

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.menu, null);
        root.addView(guillotineMenu);

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.iv_menu), iv_menu)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        iv_menu_mine = (ImageView) guillotineMenu.findViewById(R.id.menu_mine_img) ;
        iv_menu_map = (ImageView) guillotineMenu.findViewById(R.id.menu_map_img) ;
        iv_menu_record = (ImageView) guillotineMenu.findViewById(R.id.menu_record_img) ;
        iv_menu_setting = (ImageView) guillotineMenu.findViewById(R.id.menu_setting_img) ;
        tv_menu_mine = (TextView) guillotineMenu.findViewById(R.id.tv_menu_mine);
        tv_menu_map = (TextView) guillotineMenu.findViewById(R.id.tv_menu_map);
        tv_menu_record = (TextView) guillotineMenu.findViewById(R.id.tv_menu_record);
        tv_menu_seeting = (TextView) guillotineMenu.findViewById(R.id.tv_menu_setting);

        iv_menu_map.setSelected(true);
        tv_menu_map.setSelected(true);

        line_menu_mine = (LinearLayout) guillotineMenu.findViewById(R.id.line_menu_mine);
        line_menu_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMenuOfMine();
            }
        });

        line_menu_map = (LinearLayout) guillotineMenu.findViewById(R.id.line_menu_map);
        line_menu_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMenuOfMap();
            }
        });

        line_menu_record = (LinearLayout) guillotineMenu.findViewById(R.id.line_menu_record);
        line_menu_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMenuOfRecord();
            }
        });

        line_menu_setting = (LinearLayout) guillotineMenu.findViewById(R.id.line_menu_setting);
        line_menu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMenuOfSetting();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private void onClickMenuOfMine(){
        setMenuItemSelected(R.id.line_menu_mine);
        tv_title.setText(R.string.mine);
        guillotineAnimation.close();
    }

    private void onClickMenuOfMap(){
        setMenuItemSelected(R.id.line_menu_map);
        tv_title.setText(R.string.map);
        guillotineAnimation.close();
    }

    private void onClickMenuOfRecord(){
        setMenuItemSelected(R.id.line_menu_record);
        tv_title.setText(R.string.record);
        guillotineAnimation.close();
    }

    private void onClickMenuOfSetting(){
        setMenuItemSelected(R.id.line_menu_setting);
        tv_title.setText(R.string.setting);
        guillotineAnimation.close();
    }

    private void setMenuItemSelected(int lineId){
        iv_menu_mine.setSelected(false);
        iv_menu_map.setSelected(false);
        iv_menu_record.setSelected(false);
        iv_menu_setting.setSelected(false);
        tv_menu_mine.setSelected(false);
        tv_menu_map.setSelected(false);
        tv_menu_record.setSelected(false);
        tv_menu_seeting.setSelected(false);
        switch (lineId){
            case R.id.line_menu_mine:
                iv_menu_mine.setSelected(true);
                tv_menu_mine.setSelected(true);
                break;
            case R.id.line_menu_map:
                iv_menu_map.setSelected(true);
                tv_menu_map.setSelected(true);
                break;
            case R.id.line_menu_record:
                iv_menu_record.setSelected(true);
                tv_menu_record.setSelected(true);
                break;
            case R.id.line_menu_setting:
                iv_menu_setting.setSelected(true);
                tv_menu_seeting.setSelected(true);
                break;
        }
    }
}
