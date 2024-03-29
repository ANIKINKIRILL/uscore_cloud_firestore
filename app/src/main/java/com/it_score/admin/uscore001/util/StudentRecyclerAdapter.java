package com.it_score.admin.uscore001.util;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.activities.StudentDetailPage;
import com.it_score.admin.uscore001.activities.StudentProfile_activity2;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.User;

import java.util.ArrayList;

/**
 * Адаптер для рейтинга учеников
 */

public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.StudentRecyclerViewHolder> implements Filterable {

    private static final String TAG = "StudentRecyclerAdapter";

    // Переменные
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Student> studentsCopy;
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";

    public class StudentRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar;
        TextView username, score, group, rating;
        LinearLayout cardViewLayout;
        String studentID;
        String image_path;
        View dividerLine;
        private StudentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            username = itemView.findViewById(R.id.username);
            score = itemView.findViewById(R.id.userScore);
            group = itemView.findViewById(R.id.userGroup);
            cardViewLayout = itemView.findViewById(R.id.cardViewLayout);
            rating = itemView.findViewById(R.id.userRating);
            dividerLine = itemView.findViewById(R.id.dividerLine);
        }
    }

    public StudentRecyclerAdapter(ArrayList<Student> students) {
        this.students = students;
        this.studentsCopy = new ArrayList<>(students);
    }

    @NonNull
    @Override
    public StudentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_recycler_cardview, viewGroup,false);
        StudentRecyclerViewHolder holder = new StudentRecyclerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentRecyclerViewHolder studentRecyclerViewHolder, int i) {
        final Student student = students.get(i);
        // Установка ID ученика
        studentRecyclerViewHolder.studentID = student.getId();
        // Установка Названия Группы
        User.getUserGroupName(new Callback() {
            @Override
            public void execute(Object data, String... params) {
                studentRecyclerViewHolder.group.setText((String)data);
            }
        }, student.getGroupID());
        // Установка рейтинга ученика
        studentRecyclerViewHolder.rating.setText(Integer.toString(students.indexOf(student) + 1));
        if(Integer.parseInt(studentRecyclerViewHolder.rating.getText().toString()) == students.size()){
            studentRecyclerViewHolder.dividerLine.setVisibility(View.GONE);
        }
        // Установка фото ученика
        studentRecyclerViewHolder.image_path = student.getImage_path();
        if(studentRecyclerViewHolder.image_path.isEmpty()){
            studentRecyclerViewHolder.image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
        }
        Glide.with(studentRecyclerViewHolder.cardViewLayout.getContext()).load(studentRecyclerViewHolder.image_path).into(studentRecyclerViewHolder.userAvatar);
        // Установка имя и фамилии
        studentRecyclerViewHolder.username.setText(student.getFirstName()+" "+student.getSecondName());
        // Установка очков
        studentRecyclerViewHolder.score.setText(Integer.toString(student.getScore()));
        // Выделение своего аккаунта
        if(student.getId().equals(Settings.getUserId()) && student.getStatusID().equals(Settings.getStatus())){
            studentRecyclerViewHolder.cardViewLayout.setBackgroundColor(
                    studentRecyclerViewHolder.cardViewLayout.getResources().getColor(R.color.currentStudentCardViewColor)
            );
        }
        // Нажатие на cardview
        studentRecyclerViewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Если пользователь нажал на свою cardview
                if(student.getId().equals(Settings.getUserId())){
                    Log.d(TAG, "пользователь нажал на свою cardview");
                    Intent intent = new Intent(studentRecyclerViewHolder.cardViewLayout.getContext(), StudentProfile_activity2.class);
                    studentRecyclerViewHolder.cardViewLayout.getContext().startActivity(intent);
                // Если пользователь нажал на чужую cardview
                }else {
                    Log.d(TAG, "пользователь нажал на чужую cardview");
                    Intent intent = new Intent(studentRecyclerViewHolder.cardViewLayout.getContext(), StudentDetailPage.class);
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentScore), studentRecyclerViewHolder.score.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentGroup), studentRecyclerViewHolder.group.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentUsername), studentRecyclerViewHolder.username.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentImage), studentRecyclerViewHolder.image_path);
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentEmail), student.getEmail());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentGroupID), student.getGroupID());
                    intent.putExtra("intentStudentID", student.getId());
                    studentRecyclerViewHolder.cardViewLayout.getContext().startActivity(intent);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Student> filteredStudents = new ArrayList<>();
            if(constraint.length() == 0 || constraint == null){
                filteredStudents.addAll(studentsCopy);
            }else{
                String studentName = constraint.toString().toLowerCase().trim();
                for(Student student : studentsCopy){
                    if(student.getFirstName().toLowerCase().trim().contains(studentName) || student.getSecondName().toLowerCase().trim().contains(studentName)){
                        filteredStudents.add(student);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredStudents;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            students.clear();
            students.addAll((ArrayList<Student>)results.values);
            notifyDataSetChanged();
        }
    };

}
