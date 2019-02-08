package com.example.admin.uscore001.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.util.StudentRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

public class EntireSchoolTopScoreFragment extends Fragment {

    private static final String TAG = "EntireSchoolTopScoreFra";

    // Firebase STUFF
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference("Students");

    // vars
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Student> students1 = new ArrayList<>();
    String score;
    String image_path;
    String username;
    String group;
    String email;
    StudentRecyclerAdapter adapter;
    static Student currentStudentClass;
    int currentStudentRateSchool;

    // widgets
    RecyclerView recyclerView;
    CardView cardView;
    TextView currentStudentRate;
    TextView title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_entireschool_topscore, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        currentStudentRate = view.findViewById(R.id.currentStudentRate);

        if(!currentUser.getEmail().contains("teacher")) {
            try {
                loadStudent();
            }catch (Exception e){
                Log.d(TAG, "onCreateView: " + e.getMessage());
            }
        }else{
            try {
                loadTeacher();
            }catch (Exception e){
                e.printStackTrace();
            }
            currentStudentRate.setText("");
        }

        return view;
    }

    public void loadStudent(){
        students.clear();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot groups : dataSnapshot.getChildren()) {
                        for (DataSnapshot student : groups.getChildren()) {
                            if (student.getKey().equals(currentUser.getEmail().replace(".", ""))) {
                                score = student.getValue(Student.class).getScore();
                                group = student.getValue(Student.class).getGroup();
                                image_path = student.getValue(Student.class).getImage_path();
                                username = student.getValue(Student.class).getUsername();
                                email = student.getValue(Student.class).getEmail();
                                if (score.trim().isEmpty()) {
                                    score = "In process...";
                                }
                                if (image_path.isEmpty()) {
                                    image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                                }
                                currentStudentClass = new Student(score, username, image_path, group, email);
                            } else {
                                score = student.getValue(Student.class).getScore();
                                if (score.trim().isEmpty()) {
                                    score = "In process...";
                                }
                                image_path = student.getValue(Student.class).getImage_path();
                                if (image_path.isEmpty()) {
                                    image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                                }
                                group = student.getValue(Student.class).getGroup();
                                username = student.getValue(Student.class).getUsername();
                                email = student.getValue(Student.class).getEmail();
                                Student new_student = new Student(score, username, image_path, group, email);
                                students.add(new_student);

                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                students.add(currentStudentClass);
                bubbleSortStudents(students);
                Collections.reverse(students);
                String you_are_onText = currentStudentRate.getText().toString();
                currentStudentRateSchool = students.indexOf(currentStudentClass)+1;
                you_are_onText = you_are_onText + " " + Integer.toString(currentStudentRateSchool) + " ";
                try {
                    currentStudentRate.setText(you_are_onText + getResources().getString(R.string.place_with) + " " + currentStudentClass.getScore() + " " + getResources().getString(R.string.points));
                }catch (Exception e ){
                    Log.d(TAG, "onDataChange: " + e.getMessage());
                }
                adapter = new StudentRecyclerAdapter(students);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadTeacher(){
        students1.clear();
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot groups : dataSnapshot.getChildren()) {
                        for (DataSnapshot student : groups.getChildren()) {
                            score = student.getValue(Student.class).getScore();
                            if (score.trim().isEmpty()) {
                                score = "In process...";
                            }
                            image_path = student.getValue(Student.class).getImage_path();
                            if (image_path.isEmpty()) {
                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            group = student.getValue(Student.class).getGroup();
                            username = student.getValue(Student.class).getUsername();
                            email = student.getValue(Student.class).getEmail();
                            Student new_student = new Student(score, username, image_path, group, email);
                            students1.add(new_student);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                bubbleSortStudents(students1);
                Collections.reverse(students1);
                adapter = new StudentRecyclerAdapter(students1);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void bubbleSortStudents(ArrayList<Student> students){
        int size = students.size();
        Student temp;
        for(int i = 0; i < size; i++){
            for(int j = 1; j < size; j++){
                if(Integer.parseInt(students.get(j-1).getScore()) > Integer.parseInt(students.get(j).getScore())) {
                    temp = students.get(j-1);
                    students.set(j-1, students.get(j));
                    students.set(j, temp);
                }
            }
        }
    }

}
