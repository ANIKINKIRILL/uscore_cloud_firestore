package com.it_score.admin.uscore001.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Option;
import com.it_score.admin.uscore001.models.Student;

import java.util.ArrayList;

/**
 * Адаптер для отображения поощрения
 */

public class EncouragementOptionArrayAdapter extends ArrayAdapter{

    ArrayList<Option> options = new ArrayList<>();
    Context context;

    public EncouragementOptionArrayAdapter(@NonNull Context context, ArrayList<Option> options) {
        super(context, R.layout.register_activity_teacher_item);
        this.options = options;
        this.context = context;
    }

    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int position) {
        return options.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        try {
            ViewHolder holder;
            Option option = options.get(position);
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.register_activity_teacher_item, null, false);
                holder = new ViewHolder();
                holder.optionName = view.findViewById(R.id.teacherName);
                view.setTag(holder);
            }

            holder.optionName.setText(option.getName());
        }catch (Exception e){
            e.getMessage();
        }
//        holder.optionName.setTextColor(getContext().getResources().getColor(R.color.grayColor));

        return view;
    }

    static class ViewHolder{
        TextView optionName;
    }

}
