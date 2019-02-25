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

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.StudentRegisterRequestModel;
import com.example.admin.uscore001.util.StudentRegisterRequestRecyclerViewAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * Фрагмент со всеми запросами на регистрацию
 */

public class AllRegisterRequests extends Fragment {

    // widgets
    RecyclerView recyclerView;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference student_register_requests = firebaseFirestore.collection("STUDENT_REGISTER_REQUESTS");

    // vars
    String teacherID;
    ArrayList<StudentRegisterRequestModel> requestModels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_register_requests, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        teacherID = sharedPreferences.getString("teacherID", "");
        init(view);
        loadAllRequests();
        return view;
    }

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    private void loadAllRequests(){
        student_register_requests.whereEqualTo("teacherID", teacherID).addSnapshotListener(allTeacherRegisterRequests);
    }

    EventListener<QuerySnapshot> allTeacherRegisterRequests = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
            requestModels.clear();
            for(DocumentSnapshot request : queryDocumentSnapshots.getDocuments()){
                StudentRegisterRequestModel model = request.toObject(StudentRegisterRequestModel.class);
                requestModels.add(model);
            }
            StudentRegisterRequestRecyclerViewAdapter adapter = new StudentRegisterRequestRecyclerViewAdapter(requestModels, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    };
}