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
import com.it_score.admin.uscore001.models.Group;

import java.util.ArrayList;

/**
 * Адаптер для отображения item учителя при регистрации ученика системе
 */

public class RegisterActivityGroupAdapter extends ArrayAdapter {

    ArrayList<Group> groups = new ArrayList<>();
    Context context;

    public RegisterActivityGroupAdapter(@NonNull Context context, ArrayList<Group> groups) {
        super(context, R.layout.register_activity_group_item);
        this.groups = groups;
        this.context = context;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public Object getItem(int position) {
        return groups.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        Group group = groups.get(position);
        if(view != null){
            holder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.register_activity_group_item, null, false);
            holder = new ViewHolder();
            holder.groupName = view.findViewById(R.id.groupName);
            view.setTag(holder);
        }
        holder.groupName.setText(group.getName());
        holder.groupName.setTextColor(getContext().getResources().getColor(android.R.color.white));
        return view;
    }

    static class ViewHolder{
        TextView groupName;
    }

}
