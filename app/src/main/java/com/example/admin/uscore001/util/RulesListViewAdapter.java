package com.example.admin.uscore001.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Option;

import java.util.ArrayList;

public class RulesListViewAdapter extends BaseAdapter {

    ArrayList<Option> options = new ArrayList<>();
    Context context;

    public RulesListViewAdapter(ArrayList<Option> options, Context context) {
        this.options = options;
        this.context = context;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int i) {
        return options.get(i);
    }

    @Override
    public long getItemId(int i) {
        return options.indexOf(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View infalatedView = LayoutInflater.from(context).inflate(R.layout.rules_list_item, null);
        Option optionClass = options.get(i);
        TextView option = infalatedView.findViewById(R.id.option);
        TextView score = infalatedView.findViewById(R.id.score);
//        option.setText(optionClass.getOption());
//        score.setText(Integer.toString(optionClass.getScore()));
        return infalatedView;
    }



}
