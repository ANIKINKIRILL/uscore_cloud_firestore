package com.example.admin.uscore001.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.StudentRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

public class EntireSchoolTopScoreFragment extends Fragment {

    private static final String TAG = "EntireSchoolTopScoreFra";

    // Firebase STUFF
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference("Students");

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");

    // vars
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Student> students1 = new ArrayList<>();
    String score;
    String image_path;
    String username;
    String groupID;
    String email;
    StudentRecyclerAdapter adapter;
    static Student currentStudentClass;
    int currentStudentRateSchool;

    // widgets
    RecyclerView recyclerView;
    CardView cardView;
    TextView currentStudentRate;
    TextView title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_entireschool_topscore, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        currentStudentRate = view.findViewById(R.id.currentStudentRate);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String studentStatusID = sharedPreferences.getString(getString(R.string.studentStatusID), "");
        String teacherStatusID = sharedPreferences.getString(getString(R.string.teacherStatusID), "");


        if(!currentUser.getEmail().contains("teacher")) {
            try {
                loadStudent();
            }catch (Exception e){
                Log.d(TAG, "onCreateView: " + e.getMessage());
            }
        }else if(currentUser.getEmail().contains("teacher")){
            try {
                loadTeacher();
            }catch (Exception e){
                e.printStackTrace();
            }
            currentStudentRate.setText("");
        }

        return view;
    }

    public void loadStudent(){
        Log.d(TAG, "loadStudent: method started");
        students.clear();
        students$DB
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Student student = documentSnapshot.toObject(Student.class);
                            score = student.getScore();
                            groupID = student.getGroupID();
                            image_path = student.getImage_path();
                            username = student.getFirstName() + " " + student.getSecondName();
                            email = student.getEmail();
                            if (score.trim().isEmpty()) {
                                score = "In process...";
                            }
                            if (image_path.isEmpty()) {
                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            if(email.equals(currentUser.getEmail())){
                                currentStudentClass =
                                        new Student(
                                                score,
                                                username,
                                                image_path,
                                                groupID,
                                                email,
                                                student.getFirstName(),
                                                student.getSecondName(),
                                                "",
                                                ""
                                        );
                            }else{
                                Student new_student =
                                        new Student(
                                                score,
                                                username,
                                                image_path,
                                                groupID,
                                                email,
                                                student.getFirstName(),
                                                student.getSecondName(),
                                                "",
                                                ""
                                        );
                                students.add(new_student);
                            }
                        }
                        students.add(currentStudentClass);
                        bubbleSortStudents(students);
                        Collections.reverse(students);
                        String you_are_onText = currentStudentRate.getText().toString();
                        currentStudentRateSchool = students.indexOf(currentStudentClass)+1;
                        you_are_onText = you_are_onText + " " + Integer.toString(currentStudentRateSchool) + " ";
                        try {
                            currentStudentRate.setText(you_are_onText + getResources().getString(R.string.place_with) + " " + currentStudentClass.getScore() + " " + getResources().getString(R.string.points));
                        }catch (Exception e1){
                            Log.d(TAG, "onDataChange: " + e1.getMessage());
                        }
                        adapter = new StudentRecyclerAdapter(students);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    public void loadTeacher(){
        students1.clear();
//        teachers$DB
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                            Teacher teacher = documentSnapshot.toObject(Teacher.class);
//                            image_path = teacher.getImage_path();
//                            groupID = teacher.getGroupID();
//                            username = teacher.getFirstName() + " " + teacher.getLastName();
//                            email = teacher.getResponsible_email();
//                            if (image_path.isEmpty()) {
//                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
//                            }
//                            Student new_student = new Student(score, username, image_path, groupID, email, teacher.getFirstName(), "", teacher.getLastName(),"");
//                            students1.add(new_student);
//                        }
//                        bubbleSortStudents(students1);
//                        Collections.reverse(students1);
//                        adapter = new StudentRecyclerAdapter(students1);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        recyclerView.setAdapter(adapter);
//                    }
//                });

        students$DB
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            Student student = documentSnapshot.toObject(Student.class);
                            score = student.getScore();
                            groupID = student.getGroupID();
                            image_path = student.getImage_path();
                            username = student.getFirstName() + " " + student.getSecondName();
                            email = student.getEmail();
                            if (score.trim().isEmpty()) {
                                score = "In process...";
                            }
                            if (image_path.isEmpty()) {
                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            Student new_student =
                                    new Student(
                                            score,
                                            username,
                                            image_path,
                                            groupID,
                                            email,
                                            student.getFirstName(),
                                            student.getSecondName(),
                                            "",
                                            ""
                                    );
                            students1.add(new_student);
                        }
                        bubbleSortStudents(students1);
                        Collections.reverse(students1);
                        adapter = new StudentRecyclerAdapter(students1);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adapter);
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
