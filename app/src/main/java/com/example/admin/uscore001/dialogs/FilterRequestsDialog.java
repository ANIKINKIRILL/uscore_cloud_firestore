package com.example.admin.uscore001.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RecentRequestItem;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.util.FilterStudentsArrayAdapter;
import com.example.admin.uscore001.util.RecentRequestsAdapter;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;

public class FilterRequestsDialog extends DialogFragment implements Spinner.OnItemSelectedListener{

    // vars
    String selectedTeacher;
    String currentTeacherRequestID;
    ArrayList<Student> allTeachersStudents = new ArrayList<>();

    // widgets
    com.rey.material.widget.Spinner spinner;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference requests$db = firebaseFirestore.collection("REQEUSTS$DB");
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter_reqeust, container, false);

        spinner = view.findViewById(R.id.spinner);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentTeacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");

        if (currentUser.getEmail().contains("teacher")) {
            getDialog().setTitle("Фильтр по Ученику");
            getAllTeachersStudents(currentTeacherRequestID);
        } else {
            getDialog().setTitle("Фильтр по Учителю");
        }
        return view;
    }

    public void getAllTeachersStudents(String currentTeacherRequestID){
        requests$db.document(currentTeacherRequestID).collection("STUDENTS").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot student : queryDocumentSnapshots.getDocuments()){
                    String studentID = student.toString();
                    students$DB.document(studentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Student student = task.getResult().toObject(Student.class);
                                allTeachersStudents.add(student);
                            }
                            FilterStudentsArrayAdapter arrayAdapter = new FilterStudentsArrayAdapter(getContext(), android.R.layout.simple_spinner_item, allTeachersStudents);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(arrayAdapter);
                            spinner.setOnItemSelectedListener(FilterRequestsDialog.this);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemSelected(Spinner parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner:{
                Student student = (Student)parent.getSelectedItem();
                Toast.makeText(getContext(), student.getFirstName(), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
