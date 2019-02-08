package com.example.admin.uscore001.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.activities.StudentDetailPage;
import com.example.admin.uscore001.activities.StudentProfile_activity2;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.StudentRecyclerViewHolder> {

    ArrayList<Student> students = new ArrayList<>();

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    public class StudentRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar;
        TextView username, score, group;
        RelativeLayout cardViewLayout;
        public StudentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            username = itemView.findViewById(R.id.username);
            score = itemView.findViewById(R.id.userScore);
            group = itemView.findViewById(R.id.userGroup);
            cardViewLayout = itemView.findViewById(R.id.cardViewLayout);
        }
    }

    public StudentRecyclerAdapter(ArrayList<Student> students) {
        this.students = students;
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
        final String image_path = student.getImage_path();
        Glide.with(studentRecyclerViewHolder.cardViewLayout.getContext()).load(image_path).into(studentRecyclerViewHolder.userAvatar);
        studentRecyclerViewHolder.username.setText(student.getUsername());
        studentRecyclerViewHolder.score.setText(student.getScore());
        studentRecyclerViewHolder.group.setText(student.getGroup());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(studentRecyclerViewHolder.cardViewLayout.getContext());
        final String currentStudentUsername = sharedPreferences.getString(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.currentStudentUsername), "");
        final String currentStudentGroup = sharedPreferences.getString(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.currentStudentGroup), "");


        if(student.getUsername().equals(currentStudentUsername) && student.getGroup().equals(currentStudentGroup) && !mUser.getEmail().contains("teacher")){
            studentRecyclerViewHolder.cardViewLayout.setBackgroundColor(
                    studentRecyclerViewHolder.cardViewLayout.getResources().getColor(R.color.currentStudentCardViewColor)
            );
        }

        studentRecyclerViewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studentRecyclerViewHolder.username.getText().toString().equals(currentStudentUsername)){
                    Intent intent = new Intent(studentRecyclerViewHolder.cardViewLayout.getContext(), StudentProfile_activity2.class);
                    studentRecyclerViewHolder.cardViewLayout.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(studentRecyclerViewHolder.cardViewLayout.getContext(), StudentDetailPage.class);
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentScore), studentRecyclerViewHolder.score.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentGroup), studentRecyclerViewHolder.group.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentUsername), studentRecyclerViewHolder.username.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentImage), image_path);
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentEmail), student.getEmail());
                    studentRecyclerViewHolder.cardViewLayout.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return students.size();
    }
}
