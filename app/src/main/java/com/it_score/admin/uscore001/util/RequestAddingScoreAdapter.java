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

public class RequestAddingScoreAdapter extends ArrayAdapter {

    private static final String TAG = "RequestAddingScoreAdapt";

    private ArrayList<Teacher> teachers = new ArrayList<>();
    private Context context;

    public RequestAddingScoreAdapter(Context context, int resource, ArrayList<Teacher> teachers) {
        super(context, resource, teachers);
        this.teachers = teachers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return teachers.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return teachers.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_item, null);
        try {
            TextView teacherName = view.findViewById(R.id.teacherName);
            Teacher teacher = teachers.get(position);
            teacherName.setText(teacher.getSecondName() + " " + teacher.getFirstName() + " " + teacher.getLastName());
        }catch (Exception e){
            e.getMessage();
        }
        return view;
    }
}
