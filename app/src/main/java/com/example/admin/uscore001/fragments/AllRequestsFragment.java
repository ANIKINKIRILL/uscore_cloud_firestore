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
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RecentRequestItem;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Teacher;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllRequestsFragment extends Fragment {

    private static final String TAG = "AllRequestsFragment";

    // widgets
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // vars
    String currentUserEmail;
    ArrayList<RecentRequestItem> recentRequestItems = new ArrayList<>();
    ArrayList<RecentRequestItem> confirmedRequestsItems = new ArrayList<>();
    ArrayList<RecentRequestItem> deniedRequestsItems = new ArrayList<>();
    ArrayList<RecentRequestItem> filteredRequestItems = new ArrayList<>();
    String selectedTeacher;
    private String result;
    private String selectedTeacherRequestID;
    String currentStudentID;

    // teacher arraylists
    ArrayList<RecentRequestItem> recentRequestItemsTeacher = new ArrayList<>();
    private String teacherRequestID;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_requests_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        getCurrentUserInfo();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String statusStudent = sharedPreferences.getString(getString(R.string.studentStatusID), "");
        String statusTeacher = sharedPreferences.getString(getString(R.string.teacherStatusID), "");
        currentStudentID = sharedPreferences.getString(getString(R.string.currentStudentID), "");
        selectedTeacher = sharedPreferences.getString(getString(R.string.selectedTeacher), "");
        teacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");

        if(!currentUser.getEmail().contains("teacher")) { // is a STUDENT
            loadAllUserRequests();
        }else if(!currentUser.getEmail().contains("teacher") && !selectedTeacher.isEmpty()){
            filterSelectedTeacherRequests(selectedTeacher);
        }else if(currentUser.getEmail().contains("teacher")){   // is a TEACHER
            loadAllTeacherRequests();
        }

        return view;
    }

    private void selectedTeacherRequestID(String teacherID){
        String[] selectedTeacherNameWords = selectedTeacher.split(" ");
        teachers$DB
            .whereEqualTo("firstName", selectedTeacherNameWords[0])
            .whereEqualTo("lastName", selectedTeacherNameWords[1])
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Teacher teacher = documentSnapshot.toObject(Teacher.class);
                        selectedTeacherRequestID = teacher.getRequestID();
                    }
                }
            });
    }

    public void getCurrentUserInfo(){
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public void loadAllUserRequests(){
        recentRequestItems.clear();
        confirmedRequestsItems.clear();
        deniedRequestsItems.clear();
        requests$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot teachersRequestID : queryDocumentSnapshots.getDocuments()){
                    teachersRequestID
                        .getReference()
                        .collection("STUDENTS")
                        .document(currentStudentID)
                        .collection("REQUESTS")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot queryDocumentSnapshots,FirebaseFirestoreException e) {
                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                                    RequestAddingScore request = documentSnapshot.toObject(RequestAddingScore.class);
                                    String score = Integer.toString(request.getScore());
                                    String date = request.getDate();
                                    String teacherName = request.getGetter();
                                    if(request.isAnswered() && !request.isCanceled()){
                                        result = "Confirmed";
                                        confirmedRequestsItems.add(new RecentRequestItem(score, date, result, teacherName));
                                    }else if(!request.isAnswered() && request.isCanceled()){
                                        result = "Denied";
                                        deniedRequestsItems.add(new RecentRequestItem(score, date, result, teacherName));
                                    }else{
                                        result = "In Process...";
                                    }
                                    RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, teacherName);
                                    recentRequestItems.add(recentRequestItem);
                                }
                                RecentRequestsAdapter adapter = new RecentRequestsAdapter(recentRequestItems);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                }
            }
        });
    }

    public void loadAllTeacherRequests(){
        recentRequestItemsTeacher.clear();
        requests$DB.document(teacherRequestID).collection("STUDENTS").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    documentSnapshot.getReference().collection("REQUESTS").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            for(DocumentSnapshot requestDocSnapshot : queryDocumentSnapshots.getDocuments()){
                                RequestAddingScore request = requestDocSnapshot.toObject(RequestAddingScore.class);
                                String score = Integer.toString(request.getScore());
                                String date = request.getDate();
                                String teacherName = request.getGetter();
                                String requestStudentUsername = request.getFirstName() + " " + request.getSecondName();
                                if(request.isAnswered() && !request.isCanceled()){
                                    result = "Confirmed";
                                }else if(!request.isAnswered() && request.isCanceled()){
                                    result = "Denied";
                                }else{
                                    result = "In Process...";
                                }
                                RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, requestStudentUsername);
                                recentRequestItemsTeacher.add(recentRequestItem);
                            }
                            RecentRequestsAdapter adapter = new RecentRequestsAdapter(recentRequestItemsTeacher);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    public void filterSelectedTeacherRequests(final String selectedTeacherName){

        if(selectedTeacherName.equals("Все")){
            loadAllUserRequests();
        }else {
            selectedTeacher = "";
            filteredRequestItems.clear();
            requests$DB
                    .document(selectedTeacherRequestID)
                    .collection("STUDENTS")
                    .whereEqualTo("responsible_email", currentUserEmail)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            for (DocumentSnapshot studentRequests : queryDocumentSnapshots.getDocuments()) {
                                RequestAddingScore request = studentRequests.toObject(RequestAddingScore.class);
                                String score = Integer.toString(request.getScore());
                                String date = request.getDate();
                                String teacherName = request.getGetter();
                                if (request.isAnswered() && !request.isCanceled()) {
                                    result = "Added";
                                } else if (!request.isAnswered() && request.isCanceled()) {
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
                    });
        }
    }
}
