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
import android.widget.Toast;

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

public class AllRequestsFragment extends Fragment {

    // widgets
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // vars
    String currentUserEmail;
    Long counter;
    ArrayList<RecentRequestItem> recentRequestItems = new ArrayList<>();
    ArrayList<RecentRequestItem> confirmedRequestsItems = new ArrayList<>();
    ArrayList<RecentRequestItem> deniedRequestsItems = new ArrayList<>();
    ArrayList<RecentRequestItem> filteredRequestItems = new ArrayList<>();
    String selectedTeacher;

    // teacher arraylists
    ArrayList<RecentRequestItem> recentRequestItemsTeacher = new ArrayList<>();

    // Firebase
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("RequestsAddingScore");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_requests_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        getCurrentUserInfo();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String teacherFullName = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");
        selectedTeacher = sharedPreferences.getString(getString(R.string.selectedTeacher), "");

        if(!currentUser.getEmail().contains("teacher") && selectedTeacher.isEmpty()) { // is a STUDENT
            loadAllUserRequests(currentUserEmail);
        }else if(!currentUser.getEmail().contains("teacher") && !selectedTeacher.isEmpty()){
            filterSelectedTeacherRequests(selectedTeacher);
        }else if(currentUser.getEmail().contains("teacher")){                                            // is a TEACHER
            loadAllTeacherRequests(teacherFullName);
        }

        return view;
    }

    public void getCurrentUserInfo(){
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "");
    }

    public void loadAllUserRequests(final String userEmail){
        recentRequestItems.clear();
        confirmedRequestsItems.clear();
        deniedRequestsItems.clear();
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
                            if(answer && !cancel){
                                result = "Added";
                                confirmedRequestsItems.add(new RecentRequestItem(score, date, result, teacherName));
                            }else if(!answer && cancel){
                                result = "Canceled";
                                deniedRequestsItems.add(new RecentRequestItem(score, date, result, teacherName));
                            }else{
                                result = "In Process...";
                            }
                            RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, teacherName);
                            recentRequestItems.add(recentRequestItem);

                        }
                    }
                }

                RecentRequestsAdapter adapter = new RecentRequestsAdapter(recentRequestItems);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadAllTeacherRequests(final String teacherFullName){
        recentRequestItemsTeacher.clear();
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
                        if(answer && !cancel){
                            result = "Added";
                        }else if(!answer && cancel){
                            result = "Canceled";
                        }else{
                            result = "In Process...";
                        }
                        RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, requestStudentUsername);
                        recentRequestItemsTeacher.add(recentRequestItem);
                    }
                }
                RecentRequestsAdapter adapter = new RecentRequestsAdapter(recentRequestItemsTeacher);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void filterSelectedTeacherRequests(final String selectedTeacherName){

        if(selectedTeacherName.equals("Все")){
            loadAllUserRequests(currentUserEmail);
        }else {
            selectedTeacher = "";
            filteredRequestItems.clear();
            mDatabaseRef.child(selectedTeacherName).child(currentUser.getEmail().replace(".", ""))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot request : dataSnapshot.getChildren()) {
                                boolean answer = request.getValue(RequestAddingScore.class).isAnswer();
                                boolean cancel = request.getValue(RequestAddingScore.class).isCancel();
                                String date = request.getValue(RequestAddingScore.class).getDate();
                                String score = Integer.toString(request.getValue(RequestAddingScore.class).getScore());
                                String result = "";
                                if (answer && !cancel) {
                                    result = "Added";
                                } else if (!answer && cancel) {
                                    result = "Canceled";
                                } else {
                                    result = "In Process...";
                                }
                                RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, selectedTeacherName);
                                filteredRequestItems.add(recentRequestItem);
                            }

                            RecentRequestsAdapter adapter = new RecentRequestsAdapter(filteredRequestItems);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

}
