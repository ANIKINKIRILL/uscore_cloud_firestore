package com.example.admin.uscore001.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentProfile extends Fragment {

    // widgets
    TextView email, username, group, score;

    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("Students");

    // vars

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        email = view.findViewById(R.id.email);
        username = view.findViewById(R.id.username);
        group = view.findViewById(R.id.group);
        score = view.findViewById(R.id.score);

        setCurrentUserInfo();

        return view;
    }

    public void setCurrentUserInfo(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot groups: dataSnapshot.getChildren()){
                    for(DataSnapshot student : groups.getChildren()){
                        if(student.getKey().equals(currentUser.getEmail().replace(".", ""))){
                            String currentUserUsername = student.getValue(Student.class).getUsername();
                            String currentUserGroup = student.getValue(Student.class).getGroup();
                            String currentUserScore = student.getValue(Student.class).getScore();
                            email.setText(currentUser.getEmail());
                            username.setText(currentUserUsername);
                            group.setText(currentUserGroup);
                            if(currentUserScore.equals("")) {
                                score.setText("0");
                            }else{
                                score.setText(currentUserScore);
                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
