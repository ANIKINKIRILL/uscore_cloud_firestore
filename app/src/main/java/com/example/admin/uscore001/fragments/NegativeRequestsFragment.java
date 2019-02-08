package com.example.admin.uscore001.fragments;

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
import android.widget.ProgressBar;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RecentRequestItem;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.util.RecentRequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NegativeRequestsFragment extends Fragment {

    // widgets
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // vars
    ArrayList<RecentRequestItem> negativeRequestsItems = new ArrayList<>();
    ArrayList<RecentRequestItem> negativeRequestsItemsTeacher = new ArrayList<>();

    // Firebase
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("RequestsAddingScore");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.negative_requests_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String teacherFullName = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");

        if(!currentUser.getEmail().contains("teacher")) {                                       // is a STUDENT
            loadAllUserRequests(currentUser.getEmail().replace(".", ""));
        }else{                                                                                  // is a TEACHER
            loadAllNegativeTeacherRequests(teacherFullName);
        }

        return view;
    }


    public void loadAllUserRequests(final String userEmail){
        negativeRequestsItems.clear();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                for(DataSnapshot teacher : dataSnapshot.getChildren()){
                    if(teacher.hasChild(userEmail)) {
//                        Long userRequestsForTeacher = teacher.child(userEmail).getChildrenCount();
//                        counter += userRequestsForTeacher;
                        String teacherName = teacher.getKey();
                        for(DataSnapshot request : teacher.child(userEmail).getChildren()){
                            boolean answer = request.getValue(RequestAddingScore.class).isAnswer();
                            boolean cancel = request.getValue(RequestAddingScore.class).isCancel();
                            String date = request.getValue(RequestAddingScore.class).getDate();
                            String score = Integer.toString(request.getValue(RequestAddingScore.class).getScore());
                            String result = "";
                            if(!answer && cancel) {
                                result = "Canceled";
                                negativeRequestsItems.add(new RecentRequestItem(score, date, result, teacherName));
                            }
                        }
                    }
                }

                RecentRequestsAdapter adapter = new RecentRequestsAdapter(negativeRequestsItems);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadAllNegativeTeacherRequests(String teacherFullName){
        negativeRequestsItemsTeacher.clear();
        mDatabaseRef.child(teacherFullName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot student : dataSnapshot.getChildren()){
                            for(DataSnapshot studentRequest : student.getChildren()){
                                boolean answer = studentRequest.getValue(RequestAddingScore.class).isAnswer();
                                boolean cancel = studentRequest.getValue(RequestAddingScore.class).isCancel();
                                String date = studentRequest.getValue(RequestAddingScore.class).getDate();
                                String score = Integer.toString(studentRequest.getValue(RequestAddingScore.class).getScore());
                                String result = "";
                                String requestStudentUsername = studentRequest.getValue(RequestAddingScore.class).getSenderUsername();
                                if(!answer && cancel) {
                                    result = "Canceled";
                                    RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, requestStudentUsername);
                                    negativeRequestsItemsTeacher.add(recentRequestItem);
                                }
                            }
                        }
                        RecentRequestsAdapter adapter = new RecentRequestsAdapter(negativeRequestsItemsTeacher);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
