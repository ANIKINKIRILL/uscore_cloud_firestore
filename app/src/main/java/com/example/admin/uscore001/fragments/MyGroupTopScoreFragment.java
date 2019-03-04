package com.example.admin.uscore001.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.util.StudentRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.Comparator;

public class MyGroupTopScoreFragment extends Fragment {

    private static final String TAG = "MyGroupTopScoreFragment";

    // vars
    ArrayList<Student> students = new ArrayList<>();
    String score;
    String image_path;
    String username;
    public static StudentRecyclerAdapter adapter;
    Student currentStudentClass;
    String currentStudentGroupID;
    private String currentUserGroupName;
    private String teacherGroupID;

    // widgets
    TextView title;
    RecyclerView recyclerView;
    TextView currentStudentRate;

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_mygroup_topscore, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        title = view.findViewById(R.id.title);
        currentStudentRate = view.findViewById(R.id.currentStudentRate);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentStudentGroupID = sharedPreferences.getString(getString(R.string.currentStudentGroupID), "not found");
        teacherGroupID = sharedPreferences.getString("teacherGroupID", "");

        if(getActivity() != null && isAdded()) {
            if(!currentUser.getEmail().contains("teacher")) {
                findGroupNameByGroupID(currentStudentGroupID);
                loadCurrentUserGroupMembers(currentStudentGroupID);
            }else{
                findGroupNameByGroupID(teacherGroupID);
                loadCurrentUserGroupMembersTeacher(teacherGroupID);
                currentStudentRate.setVisibility(View.GONE);
            }
        }

        return view;
    }

    public static StudentRecyclerAdapter getAdapter() {
        return adapter;
    }

    private void findGroupNameByGroupID(String groupID){
        groups$DB.document(groupID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                currentUserGroupName = task.getResult().get("name").toString();
                title.setText("Рейтинг учеников по группе " + currentUserGroupName);
            }
        });
    }

    public void loadCurrentUserGroupMembersTeacher(String foundGroupID){
        students.clear();
        students$DB
                .whereEqualTo("groupID", foundGroupID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        students.clear();
                        Log.d(TAG, "onEvent: " + queryDocumentSnapshots.getDocuments().size());
                        try {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                Student student = documentSnapshot.toObject(Student.class);
                                score = student.getScore();
                                image_path = student.getImage_path();
                                if (image_path.isEmpty()) {
                                    image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                                }
                                username = student.getFirstName() + " " + student.getSecondName();
                                Student studentClass = new Student(score, username, image_path, foundGroupID, student.getEmail(), student.getFirstName(), student.getSecondName(), "","");
                                students.add(studentClass);
                            }
                            bubbleSortStudents(students);
                            Collections.reverse(students);
                            adapter = new StudentRecyclerAdapter(students);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        }catch (Exception e1){
                            Log.d(TAG, "onEvent: " + e1.getMessage());
                        }
                    }
                });
    }

    public void loadCurrentUserGroupMembers(String foundGroupID){
        students.clear();
        students$DB
            .whereEqualTo("groupID", foundGroupID)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    students.clear();
                    Log.d(TAG, "onEvent: " + queryDocumentSnapshots.getDocuments().size());
                    try {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Student student = documentSnapshot.toObject(Student.class);
                            score = student.getScore();
                            image_path = student.getImage_path();
                            if (image_path.isEmpty()) {
                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            username = student.getFirstName() + " " + student.getSecondName();
                            if(student.getEmail().equals(currentUser.getEmail())) {
                                currentStudentClass = new Student(score, username, image_path, foundGroupID, student.getEmail(), student.getFirstName(), student.getSecondName(), "","");
                            }else{
                                Student studentClass = new Student(score, username, image_path, foundGroupID, student.getEmail(), student.getFirstName(), student.getSecondName(), "","");
                                students.add(studentClass);
                            }
                        }
                        students.add(currentStudentClass);
                        bubbleSortStudents(students);
                        Collections.reverse(students);
                        String you_are_onText = currentStudentRate.getText().toString();
                        int currentStudentRateGroup = students.indexOf(currentStudentClass)+1;
                        you_are_onText = you_are_onText + " " + Integer.toString(currentStudentRateGroup);
                        currentStudentRate.setText(you_are_onText+" "+" месте с"+ " " + currentStudentClass.getScore() + " очками");
                        adapter = new StudentRecyclerAdapter(students);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }catch (Exception e1){
                        Log.d(TAG, "onEvent: " + e1.getMessage());
                    }
                }
            });
    }

    public void bubbleSortStudents(ArrayList<Student> students){
        int size = students.size();
        Student temp;
        for(int i = 0; i < size; i++){
            for(int j = 1; j < size; j++){
                if(Integer.parseInt(students.get(j-1).getScore()) > Integer.parseInt(students.get(j).getScore())) {
                    temp = students.get(j-1);
                    students.set(j-1, students.get(j));
                    students.set(j, temp);
                }
            }
        }
    }

}
