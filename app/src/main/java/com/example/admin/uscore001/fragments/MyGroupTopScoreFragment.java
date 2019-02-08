package com.example.admin.uscore001.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyGroupTopScoreFragment extends Fragment {

    // Firebase STUFF
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference("Students");

    // vars
    ArrayList<Student> students = new ArrayList<>();
    String score;
    String image_path;
    String username;
    String group;
    String email;
    StudentRecyclerAdapter adapter;
    Student currentStudentClass;
    String currentStudentGroup;

    // widgets
    TextView title;
    RecyclerView recyclerView;
    TextView currentStudentRate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_mygroup_topscore, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        title = view.findViewById(R.id.title);
        currentStudentRate = view.findViewById(R.id.currentStudentRate);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentStudentGroup = sharedPreferences.getString(getString(R.string.currentStudentGroup), "not found");
        title.setText(currentStudentGroup);

        if(getActivity() != null && isAdded()) {
            loadCurrentUserGroupMembers(currentStudentGroup);
        }

        return view;
    }

    public void loadCurrentUserGroupMembers(String foundGroup){
        students.clear();
        mRef.child(foundGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot currentGroupMembers : dataSnapshot.getChildren()) {
                        if (currentGroupMembers.getKey().equals(currentUser.getEmail().replace(".", ""))) {
                            score = currentGroupMembers.getValue(Student.class).getScore();
                            group = currentGroupMembers.getValue(Student.class).getGroup();
                            image_path = currentGroupMembers.getValue(Student.class).getImage_path();
                            username = currentGroupMembers.getValue(Student.class).getUsername();
                            email = currentGroupMembers.getValue(Student.class).getEmail();
                            if (image_path.isEmpty()) {
                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            currentStudentClass = new Student(score, username, image_path, group, email);
                        } else {
                            score = currentGroupMembers.getValue(Student.class).getScore();
                            if (score.trim().isEmpty()) {
                                score = "In process...";
                            }
                            image_path = currentGroupMembers.getValue(Student.class).getImage_path();
                            if (image_path.isEmpty()) {
                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            group = currentGroupMembers.getValue(Student.class).getGroup();
                            username = currentGroupMembers.getValue(Student.class).getUsername();
                            email = currentGroupMembers.getValue(Student.class).getEmail();
                            Student student = new Student(score, username, image_path, group, email);
                            students.add(student);
                        }

                    }
                    students.add(currentStudentClass);
                    bubbleSortStudents(students);
                    Collections.reverse(students);
                    String you_are_onText = currentStudentRate.getText().toString();
                    int currentStudentRateGroup = students.indexOf(currentStudentClass)+1;
                    you_are_onText = you_are_onText + " " + Integer.toString(currentStudentRateGroup);
                    currentStudentRate.setText(you_are_onText+" "+getResources().getString(R.string.place_with)+" "+ currentStudentClass.getScore()+ " " + getResources().getString(R.string.points));
                    adapter = new StudentRecyclerAdapter(students);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
