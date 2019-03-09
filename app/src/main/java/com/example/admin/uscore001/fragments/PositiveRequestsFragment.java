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

public class PositiveRequestsFragment extends Fragment {

    private static final String TAG = "PositiveRequestsFragmen";

    // widgets
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // vars
    ArrayList<RequestAddingScore> positiveRequestsItems = new ArrayList<>();
    ArrayList<RequestAddingScore> positiveRequestsItemsTeacher = new ArrayList<>();
    private String currentStudentID;

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");
    private String teacherRequestID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.positive_requests_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        String teacherFullName = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");
        String statusStudent = sharedPreferences.getString(getString(R.string.statusStudent), "");
        String statusTeacher = sharedPreferences.getString(getString(R.string.statusTeacher), "");
        currentStudentID = sharedPreferences.getString(getString(R.string.currentStudentID), "");
        teacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");
        Log.d(TAG, "currentStudentID: " + currentStudentID);
        if(!FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("teacher")) {                                       // is a STUDENT
            loadAllUserPositiveRequests(currentStudentID);
        }else{                                                                                  // is a TEACHER
//            loadAllPositiveTeacherRequests(teacherFullName);
        }

        return view;
    }

    public void loadAllUserPositiveRequests(final String currentStudentID){
        positiveRequestsItems.clear();
        requests$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot teachersRequestID : queryDocumentSnapshots.getDocuments()){
                    teachersRequestID
                        .getReference()
                        .collection("STUDENTS")
                        .document(currentStudentID)
                        .collection("REQUESTS")
                        .whereEqualTo("answered", true)
                        .whereEqualTo("canceled", false)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                for(DocumentSnapshot studentRequests : queryDocumentSnapshots.getDocuments()){
                                    RequestAddingScore request = studentRequests.toObject(RequestAddingScore.class);
                                    positiveRequestsItems.add(request);
                                }
                                RecentRequestsAdapter adapter = new RecentRequestsAdapter(positiveRequestsItems);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                }
            }
        });

    }


    public void loadAllPositiveTeacherRequests(String teacherFullName){
        positiveRequestsItemsTeacher.clear();
        requests$DB.document(teacherRequestID).collection("STUDENTS").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    documentSnapshot.getReference().collection("REQUESTS")
                            .whereEqualTo("answered", true)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            for(DocumentSnapshot requestDocSnapshot : queryDocumentSnapshots.getDocuments()){
                                RequestAddingScore request = requestDocSnapshot.toObject(RequestAddingScore.class);
                                positiveRequestsItemsTeacher.add(request);
                            }
                            RecentRequestsAdapter adapter = new RecentRequestsAdapter(positiveRequestsItemsTeacher);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

}
