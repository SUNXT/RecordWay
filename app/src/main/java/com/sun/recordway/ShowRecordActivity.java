package com.sun.recordway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowRecordActivity extends AppCompatActivity {

    private boolean isShowRecordList = false;

    //点击返回
    @OnClick(R.id.iv_back)
    public void backAction(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isShowRecordList", isShowRecordList);
        startActivity(intent);
        finish();
    }

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.tv_duration)
    TextView tv_duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            backAction();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView(){
        Intent data = getIntent();
        if (data != null){
            tv_title.setText(data.getStringExtra("title"));
            tv_distance.setText(data.getStringExtra("distance") + "米");
            tv_duration.setText(data.getStringExtra("duration"));
            if (data.getBooleanExtra("isFromMap", false)){
                //是从地图打开的
                isShowRecordList = false;
            }else {
                //是从显示列表中打开的，回到之前的页面
                isShowRecordList = true;
            }
        }
    }
}
