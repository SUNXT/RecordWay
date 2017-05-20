package com.sun.recordway.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sun.recordway.R;
import com.sun.recordway.ShowRecordActivity;
import com.sun.recordway.adapter.RecordListAdapter;
import com.sun.recordway.bean.RecordBean;
import com.sun.recordway.database.Database;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SUN on 2017/5/19.
 */

public class RecordFragment extends Fragment {


    @BindView(R.id.lv_show_record)
    ListView listView;

    private RecordListAdapter adapter;
    private List<RecordBean> mData = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    private void initView(){
        Database database = Database.getInstance(getActivity());
        mData = database.getAllItems();
        adapter = new RecordListAdapter(getActivity());
        adapter.setmData(mData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                RecordBean recordBean = adapter.getItem(pos);
                Intent intent = new Intent(getActivity(), ShowRecordActivity.class);
                intent.putExtra("title", recordBean.getTitle());
                intent.putExtra("duration", recordBean.getDuration());
                intent.putExtra("distance", recordBean.getDistance());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
