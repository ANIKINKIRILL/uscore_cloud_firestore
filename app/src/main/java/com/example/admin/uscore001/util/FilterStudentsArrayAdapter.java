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
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;

import java.util.ArrayList;
import java.util.List;

public class FilterStudentsArrayAdapter extends ArrayAdapter<Student> {

    ArrayList<Student> students = new ArrayList<>();
    Context context;

    public FilterStudentsArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Student> objects) {
        super(context, resource, objects);
        this.students = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Nullable
    @Override
    public Student getItem(int position) {
        return students.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        FilterStudentsArrayAdapter.ViewHolder holder;
        Student student = students.get(position);
        if(view != null){
            holder = (FilterStudentsArrayAdapter.ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.register_activity_group_item, null, false);
            holder = new FilterStudentsArrayAdapter.ViewHolder();
            holder.studentName = view.findViewById(R.id.groupName);
            view.setTag(holder);
        }

        holder.studentName.setText(student.getFirstName()+" "+student.getSecondName());
        holder.studentName.setTextSize(18);

        return view;
    }

    static class ViewHolder{
        TextView studentName;
    }
}
