package com.example.admin.uscore001.fragments;

import android.content.Context;
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
import java.util.Comparator;

public class FragmentGroup extends Fragment {

    // widgets
    RecyclerView recyclerView;
    TextView title;

    // vars
    StudentRecyclerAdapter adapter;
    String currentUserGroup;
    String score;
    String image_path;
    String username;
    ArrayList<Student> students = new ArrayList<>();

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("Students");
    DatabaseReference mDatabaseCurrentGroupRef = mDatabase.getReference("Students");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        title = view.findViewById(R.id.title);
        recyclerView = view.findViewById(R.id.recyclerView);

        findCurrentUserGroup();

        return view;
    }

    public void findCurrentUserGroup(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot groups : dataSnapshot.getChildren()){
                    for(DataSnapshot student : groups.getChildren()){
                        if(student.getKey().equals(currentUser.getEmail().replace(".", ""))){
                            currentUserGroup = student.getValue(Student.class).getGroup();
                            title.setText(currentUserGroup);
                            loadCurrentUserGroupMembers(currentUserGroup);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadCurrentUserGroupMembers(String foundGroup){
        students.clear();
        mDatabaseCurrentGroupRef.child(foundGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot currentGroupMembers : dataSnapshot.getChildren()){
                    score = currentGroupMembers.getValue(Student.class).getScore();
                    if(score.trim().isEmpty()){
                        score = "working...";
                    }
                    image_path = currentGroupMembers.getValue(Student.class).getImage_path();
                    if(image_path.isEmpty()){
                        image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                    }
                    username = currentGroupMembers.getValue(Student.class).getUsername();
                    Student student = new Student(score, username, image_path, "", "");
                    students.add(student);
                }
//                CompareStudentsByScore comparator = new CompareStudentsByScore();
//                students.sort(comparator);
                adapter = new StudentRecyclerAdapter(students);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class CompareStudentsByScore implements Comparator<Student> {
        @Override
        public int compare(Student s1, Student s2) {
            String s1_score_str = s1.getScore();
            String s2_score_str = s2.getScore();

            int s1_score_int = Integer.parseInt(s1_score_str);
            int s2_score_int = Integer.parseInt(s2_score_str);

            return s2_score_int - s1_score_int ;
        }
    }

}
