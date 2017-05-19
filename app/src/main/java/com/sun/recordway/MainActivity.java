package com.sun.recordway;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.sun.recordway.fragment.MapFragment;
import com.sun.recordway.fragment.MineFragment;
import com.sun.recordway.fragment.RecordFragment;
import com.sun.recordway.fragment.SettingFragment;
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
    private TextView tv_menu_setting;

    private MineFragment mMineFragment;
    private MapFragment mMapFragment;
    private RecordFragment mRecordFragment;
    private SettingFragment mSettingFragment;

    private GuillotineAnimation guillotineAnimation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
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
        tv_menu_setting = (TextView) guillotineMenu.findViewById(R.id.tv_menu_setting);

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

        mMapFragment = new MapFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment, mMapFragment);
        transaction.commit();
    }

    private void hideAllFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mMineFragment != null){
            transaction.hide(mMineFragment);
        }
        if (mMapFragment != null){
            transaction.hide(mMapFragment);
        }
        if (mRecordFragment != null){
            transaction.hide(mRecordFragment);
        }
        if (mSettingFragment != null){
            transaction.hide(mSettingFragment);
        }
        transaction.commit();
    }

    private void onClickMenuOfMine(){
        setMenuItemSelected(R.id.line_menu_mine);
        tv_title.setText(R.string.mine);
        guillotineAnimation.close();

        hideAllFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mMineFragment == null){
            mMineFragment = new MineFragment();
            transaction.add(R.id.fragment, mMineFragment);
        }else {
            transaction.show(mMineFragment);
        }
        transaction.commit();

    }

    private void onClickMenuOfMap(){
        setMenuItemSelected(R.id.line_menu_map);
        tv_title.setText(R.string.map);
        guillotineAnimation.close();

        hideAllFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mMapFragment == null){
            mMapFragment = new MapFragment();
            transaction.add(R.id.fragment, mMapFragment);
        }else {
            transaction.show(mMapFragment);
        }
        transaction.commit();
    }

    private void onClickMenuOfRecord(){
        setMenuItemSelected(R.id.line_menu_record);
        tv_title.setText(R.string.record);
        guillotineAnimation.close();

        hideAllFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mRecordFragment == null){
            mRecordFragment = new RecordFragment();
            transaction.add(R.id.fragment, mRecordFragment);
        }else {
            transaction.show(mRecordFragment);
        }
        transaction.commit();
    }

    private void onClickMenuOfSetting(){
        setMenuItemSelected(R.id.line_menu_setting);
        tv_title.setText(R.string.setting);
        guillotineAnimation.close();

        hideAllFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (mSettingFragment == null){
            mSettingFragment = new SettingFragment();
            transaction.add(R.id.fragment, mSettingFragment);
        }else {
            transaction.show(mSettingFragment);
        }
        transaction.commit();
    }

    private void setMenuItemSelected(int lineId){
        iv_menu_mine.setSelected(false);
        iv_menu_map.setSelected(false);
        iv_menu_record.setSelected(false);
        iv_menu_setting.setSelected(false);
        tv_menu_mine.setSelected(false);
        tv_menu_map.setSelected(false);
        tv_menu_record.setSelected(false);
        tv_menu_setting.setSelected(false);
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
                tv_menu_setting.setSelected(true);
                break;
        }
    }
}
