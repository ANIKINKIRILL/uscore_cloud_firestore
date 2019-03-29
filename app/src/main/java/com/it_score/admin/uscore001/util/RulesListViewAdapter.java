package com.it_score.admin.uscore001.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Option;

import java.util.ArrayList;

/**
 * Адаптер для отображения поощрений
 */

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
        View inflatedView = view;
        ViewHolder viewHolder;
        Option optionClass = options.get(i);
        if(inflatedView != null){
            viewHolder = (ViewHolder) inflatedView.getTag();
        }else{
            inflatedView = LayoutInflater.from(context).inflate(R.layout.rules_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.option = inflatedView.findViewById(R.id.option);
            viewHolder.score = inflatedView.findViewById(R.id.score);
            inflatedView.setTag(viewHolder);
        }

        viewHolder.option.setText(optionClass.getName());
        viewHolder.score.setText(optionClass.getPoints());

        return inflatedView;
    }

    static class ViewHolder{
        TextView option;
        TextView score;
    }


}
