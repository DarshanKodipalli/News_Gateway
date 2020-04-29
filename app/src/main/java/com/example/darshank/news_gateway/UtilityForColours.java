package com.example.darshank.news_gateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UtilityForColours extends BaseAdapter {
    ArrayList<UtilityForContent> temp_list;
    Context context;

    public UtilityForColours(Context context, ArrayList<UtilityForContent> list) {
        this.context = context;
        temp_list = list;
    }

    @Override
    public int getCount() {
        return temp_list.size();
    }

    @Override
    public Object getItem(int position) {
        return temp_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = (LayoutInflater.from(context).inflate(R.layout.drawer_list, parent, false));
        }
        UtilityForContent drawerContent = temp_list.get(position);
        TextView textView = view.findViewById(R.id.textview_draw);
        textView.setTextColor(drawerContent.getColor());
        textView.setText(drawerContent.getName());
        return view;
    }
}

