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
import com.example.admin.uscore001.models.Student;

import java.util.ArrayList;

/**
 * Адаптер для отображения item ученика
 */

public class StudentNameArrayAdapter extends ArrayAdapter{

    ArrayList<Student> students = new ArrayList<>();
    Context context;

    public StudentNameArrayAdapter(@NonNull Context context, ArrayList<Student> students) {
        super(context, R.layout.register_activity_teacher_item);
        this.students = students;
        this.context = context;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        Student student = students.get(position);
        if(view != null){
            holder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.register_activity_teacher_item, null, false);
            holder = new ViewHolder();
            holder.studentName = view.findViewById(R.id.teacherName);
            view.setTag(holder);
        }

        holder.studentName.setText(student.getFirstName() + " " + student.getSecondName());
        holder.studentName.setTextColor(getContext().getResources().getColor(R.color.grayColor));

        return view;
    }

    static class ViewHolder{
        TextView studentName;
    }

}
