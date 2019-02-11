package com.example.admin.uscore001.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NegativeRequestsFragment extends Fragment {

    private static final String TAG = "NegativeRequestsFragmen";

    // widgets
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // vars
    ArrayList<RecentRequestItem> negativeRequestsItems = new ArrayList<>();
    ArrayList<RecentRequestItem> negativeRequestsItemsTeacher = new ArrayList<>();
    private String currentStudentID;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.negative_requests_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String teacherFullName = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");
        currentStudentID = sharedPreferences.getString(getString(R.string.currentStudentID), "");
        Log.d(TAG, "currentStudentID: " + currentStudentID);

        if(!currentUser.getEmail().contains("teacher")) {                                       // is a STUDENT
            loadAllUserNegativeRequests(currentStudentID);
        }else{                                                                                  // is a TEACHER
            loadAllNegativeTeacherRequests(teacherFullName);
        }

        return view;
    }


    public void loadAllUserNegativeRequests(String currentStudentID){
        negativeRequestsItems.clear();
        requests$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot teachersRequestID : queryDocumentSnapshots.getDocuments()){
                    teachersRequestID
                        .getReference()
                        .collection("STUDENTS")
                        .document(currentStudentID)
                        .collection("REQUESTS")
                        .whereEqualTo("canceled", true)
                        .whereEqualTo("answered", false)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                for(DocumentSnapshot studentRequests : queryDocumentSnapshots.getDocuments()){
                                    RequestAddingScore request = studentRequests.toObject(RequestAddingScore.class);
                                    String score = Integer.toString(request.getScore());
                                    String date = request.getDate();
                                    String teacherName = request.getGetter();
                                    String result = "";
                                    result = "Canceled";
                                    negativeRequestsItems.add(new RecentRequestItem(score, date, result, teacherName));
                                }
                                RecentRequestsAdapter adapter = new RecentRequestsAdapter(negativeRequestsItems);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                }
            }
        });


    }

    public void loadAllNegativeTeacherRequests(String teacherRequestID){
        negativeRequestsItemsTeacher.clear();
//        requests$DB.child(teacherFullName)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for(DataSnapshot student : dataSnapshot.getChildren()){
//                            for(DataSnapshot studentRequest : student.getChildren()){
//                                boolean answer = studentRequest.getValue(RequestAddingScore.class).isAnswer();
//                                boolean cancel = studentRequest.getValue(RequestAddingScore.class).isCancel();
//                                String date = studentRequest.getValue(RequestAddingScore.class).getDate();
//                                String score = Integer.toString(studentRequest.getValue(RequestAddingScore.class).getScore());
//                                String result = "";
//                                String requestStudentUsername = studentRequest.getValue(RequestAddingScore.class).getSenderUsername();
//                                if(!answer && cancel) {
//                                    result = "Canceled";
//                                    RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, requestStudentUsername);
//                                    negativeRequestsItemsTeacher.add(recentRequestItem);
//                                }
//                            }
//                        }
//                        RecentRequestsAdapter adapter = new RecentRequestsAdapter(negativeRequestsItemsTeacher);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        recyclerView.setAdapter(adapter);
//                        progressBar.setVisibility(View.GONE);
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
    }

}
