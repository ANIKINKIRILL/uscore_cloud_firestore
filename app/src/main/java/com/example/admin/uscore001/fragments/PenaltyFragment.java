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
import com.example.admin.uscore001.models.Penalty;
import com.example.admin.uscore001.util.PenaltyRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PenaltyFragment extends Fragment {

    // widgets
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");

    // vars
    private String currentUserEmail = currentUser.getEmail();
    private String teacherRequestID;
    private ArrayList<Penalty> teacherPenaltiesList = new ArrayList<>();
    private ArrayList<Penalty> studentPenaltiesList = new ArrayList<>();
    private String currentStudentID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.penalty_fragment, container, false);
        init(view);
        loadSharedPreferences();
        loadPenalties();
        return view;
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void loadSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        teacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");
        currentStudentID = sharedPreferences.getString(getString(R.string.currentStudentID), "");
    }

    private void loadPenalties(){
        teacherPenaltiesList.clear();
        studentPenaltiesList.clear();

        /*
                    TEACHER
         */

        if(isTeacher()){
            progressBar.setVisibility(View.VISIBLE);
            requests$DB
                .document(teacherRequestID)
                .collection("STUDENTS")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot studentsDocumentSnapshot : queryDocumentSnapshots.getDocuments()){
                            studentsDocumentSnapshot
                                .getReference()
                                .collection("PENALTY")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                        for(DocumentSnapshot penaltyDocumentSnapshot : queryDocumentSnapshots.getDocuments()){
                                            Penalty penalty = penaltyDocumentSnapshot.toObject(Penalty.class);
                                            teacherPenaltiesList.add(penalty);
                                        }
                                        PenaltyRecyclerViewAdapter adapter = new PenaltyRecyclerViewAdapter(teacherPenaltiesList, true, false);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        recyclerView.setAdapter(adapter);
                                    }
                                });
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
        }

        /*
                    STUDENT
         */

        if(isStudent()){
            progressBar.setVisibility(View.VISIBLE);
            requests$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot teacherRequestID : queryDocumentSnapshots.getDocuments()){
                        teacherRequestID.getReference().collection("STUDENTS").document(currentStudentID).collection("PENALTY").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                for(DocumentSnapshot studentPenaltyDocumentSnapshot : queryDocumentSnapshots.getDocuments()){
                                    Penalty penalty = studentPenaltyDocumentSnapshot.toObject(Penalty.class);
                                    studentPenaltiesList.add(penalty);
                                }
                                PenaltyRecyclerViewAdapter adapter = new PenaltyRecyclerViewAdapter(studentPenaltiesList, false, true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                            }
                        });
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private boolean isTeacher(){
        if(currentUserEmail.contains("teacher")){
            return true;
        }
        return false;
    }

    private boolean isStudent(){
        if(!currentUserEmail.contains("teacher")){
            return true;
        }
        return false;
    }

}
