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
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.util.StudentRecyclerAdapter;
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

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");
    private String currentUserGroupID;
    private String currentUserGroupName;

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
        students$DB
                .whereEqualTo("email", currentUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        DocumentSnapshot documentSnapshot = (DocumentSnapshot) queryDocumentSnapshots.getDocuments();
                        Student student = documentSnapshot.toObject(Student.class);
                        currentUserGroupID = student.getGroupID();
                        findGroupNameByGroupID(currentUserGroupID);
//                        loadCurrentUserGroupMembers(currentUserGroupID);
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

//    public void loadCurrentUserGroupMembers(String foundGroupID){
//        students.clear();
//        students$DB
//                .whereEqualTo("groupID", foundGroupID)
//                .orderBy("score", Query.Direction.DESCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                            Student student = documentSnapshot.toObject(Student.class);
//                            score = student.getScore();
//                            image_path = student.getImage_path();
//                            if (image_path.isEmpty()) {
//                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
//                            }
//                            username = student.getFirstName() + " " + student.getSecondName();
//                            Student studentClass = new Student(score, username, image_path, foundGroupID, "", "", "", "", "");
//                            students.add(studentClass);
//                        }
//                        adapter = new StudentRecyclerAdapter(students);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        recyclerView.setAdapter(adapter);
//                    }
//                });
//
//    }

    public class CompareStudentsByScore implements Comparator<Student> {
        @Override
        public int compare(Student s1, Student s2) {
//            String s1_score_str = s1.getScore();
//            String s2_score_str = s2.getScore();

//            int s1_score_int = Integer.parseInt(s1_score_str);
//            int s2_score_int = Integer.parseInt(s2_score_str);

//            return s2_score_int - s1_score_int ;
            return 0;
        }
    }

}
