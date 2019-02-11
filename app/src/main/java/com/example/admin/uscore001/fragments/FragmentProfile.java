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
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
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

public class FragmentProfile extends Fragment {

    // widgets
    TextView email, username, group, score;

    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("Students");

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");
    private String currentUserGroupName;

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
        students$DB
                .whereEqualTo("email", currentUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        DocumentSnapshot documentSnapshot = (DocumentSnapshot) queryDocumentSnapshots.getDocuments();
                        Student student = documentSnapshot.toObject(Student.class);
                        String currentUserUsername = student.getFirstName() + " " + student.getSecondName();
                        String currentUserGroupID = student.getGroupID();
                        findGroupNameByGroupID(currentUserGroupID);
                        String currentUserScore = student.getScore();
                        email.setText(currentUser.getEmail());
                        username.setText(currentUserUsername);
                        group.setText(currentUserGroupName);
                        if(currentUserScore.equals("")) {
                            score.setText("0");
                        }else{
                            score.setText(currentUserScore);
                        }
                    }
                });

    }

    private void findGroupNameByGroupID(String groupID){
        groups$DB.whereEqualTo("id", groupID)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    DocumentSnapshot documentSnapshot = (DocumentSnapshot) queryDocumentSnapshots.getDocuments();
                    Group group = documentSnapshot.toObject(Group.class);
                    currentUserGroupName = group.getName();
                }
            });
    }

}
