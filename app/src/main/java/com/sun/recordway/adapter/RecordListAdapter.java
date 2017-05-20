package com.sun.recordway.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sun.recordway.R;
import com.sun.recordway.bean.RecordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SUN on 2017/5/20.
 */
public class RecordListAdapter extends BaseAdapter {

    List<RecordBean> mData;
    Context mContext;

    public RecordListAdapter(Context context){
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setmData(List<RecordBean> list){
        if (list != null){
            mData = list;
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public RecordBean getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.record_item, null);
            holder = new ViewHolder();
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_title.setText(getItem(i).getTitle());
        return view;
    }

    class ViewHolder{
        TextView tv_title;
    }
}
