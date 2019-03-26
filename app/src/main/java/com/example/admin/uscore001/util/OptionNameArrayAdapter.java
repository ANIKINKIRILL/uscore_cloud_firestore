package com.example.admin.uscore001.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.Penalty;
import com.example.admin.uscore001.models.Student;

import java.util.ArrayList;

/**
 * Адаптер для отображения item опции
 */

public class OptionNameArrayAdapter extends ArrayAdapter{

    ArrayList<Option> penalties = new ArrayList<>();
    Context context;

    public OptionNameArrayAdapter(@NonNull Context context, ArrayList<Option> penalties) {
        super(context, R.layout.register_activity_teacher_item);
        this.penalties = penalties;
        this.context = context;
    }

    @Override
    public int getCount() {
        return penalties.size();
    }

    @Override
    public Object getItem(int position) {
        return penalties.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        Option penalty = penalties.get(position);
        if(view != null){
            holder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.register_activity_teacher_item, null, false);
            holder = new ViewHolder();
            holder.optionName = view.findViewById(R.id.teacherName);
            view.setTag(holder);
        }

        holder.optionName.setText(penalty.getName());
        holder.optionName.setTextColor(getContext().getResources().getColor(R.color.grayColor));

        return view;
    }

    static class ViewHolder{
        TextView optionName;
    }

}
