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
import com.it_score.admin.uscore001.models.Teacher;

import java.util.ArrayList;

/**
 * Адаптер для отображения item учителя при регистрации ученика системе
 */

public class RegisterActivityTeacherAdapter extends ArrayAdapter {

    ArrayList<Teacher> teachers = new ArrayList<>();
    Context context;

    public RegisterActivityTeacherAdapter(@NonNull Context context, ArrayList<Teacher> teachers) {
        super(context, R.layout.register_activity_teacher_item);
        this.teachers = teachers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return teachers.size();
    }

    @Override
    public Object getItem(int position) {
        return teachers.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        Teacher teacher = teachers.get(position);
        if(view != null){
            holder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.register_activity_teacher_item, null, false);
            holder = new ViewHolder();
            holder.teacherName = view.findViewById(R.id.teacherName);
            view.setTag(holder);
        }

        holder.teacherName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        holder.teacherName.setTextSize(20);

        return view;
    }

    static class ViewHolder{
        TextView teacherName;
    }

}
