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
import com.it_score.admin.uscore001.models.Position;
import com.it_score.admin.uscore001.models.Subject;

import java.util.ArrayList;

/**
 * Адаптер для отображения предмета учителя
 */

public class SubjectArrayAdapter extends ArrayAdapter{

    ArrayList<Subject> subjects = new ArrayList<>();
    Context context;

    public SubjectArrayAdapter(@NonNull Context context, ArrayList<Subject> subjects) {
        super(context, R.layout.register_activity_teacher_item);
        this.subjects = subjects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Override
    public Object getItem(int position) {
        return subjects.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int p, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        Subject subject = subjects.get(p);
        if(view != null){
            holder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.register_activity_teacher_item, null, false);
            holder = new ViewHolder();
            holder.subjectName = view.findViewById(R.id.teacherName);
            view.setTag(holder);
        }

        holder.subjectName.setText(subject.getName());
        holder.subjectName.setTextColor(getContext().getResources().getColor(R.color.grayColor));

        return view;
    }

    static class ViewHolder{
        TextView subjectName;
    }

}
