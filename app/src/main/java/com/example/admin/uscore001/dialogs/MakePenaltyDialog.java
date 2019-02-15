package com.example.admin.uscore001.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.Penalty;
import com.example.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MakePenaltyDialog extends DialogFragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "MakePenaltyDialog";

    // widgets
    private Spinner groupsPickerSpinner, studentPickerSpinner, optionSpinner;
    private TextView scoreTextView, ok, cancel, optionID, studentIDTextView;

    // vars
    private String selectedGroup;
    private String selectedStudent;
    private String selectedOption;
    private ArrayList<String> allStudentsFromPickedGroup = new ArrayList<>();
    private Context context;
    private String selectedEmailStudentFromPickedGroup;
    private int counter = 1;
    private String pickedGroupID;
    private String studentID;
    private String currentTeacherRequestID;
    private String currentTeacherID;

    // Firebase
    DatabaseReference mDatabaseOptionsRef = FirebaseDatabase.getInstance().getReference("DefaultPenalty");
    DatabaseReference mRefStudents = FirebaseDatabase.getInstance().getReference("Students");

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    CollectionReference options$DB = firebaseFirestore.collection("OPTIONS$DB");
    CollectionReference reqeusts$DB = firebaseFirestore.collection("REQEUSTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_penalty, container, false);
        try {
            context = inflater.getContext();
        }catch (Exception e){
            e.getMessage();
        }
        init(view);

        return view;
    }

    private void init(View view){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentTeacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");
        currentTeacherID = sharedPreferences.getString("teacherID", "");

        getDialog().setTitle("Понизить очки");

        groupsPickerSpinner = view.findViewById(R.id.groupsPickerSpinner);
        studentPickerSpinner = view.findViewById(R.id.studentPickerSpinner);
        optionSpinner = view.findViewById(R.id.optionSpinner);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        studentIDTextView = view.findViewById(R.id.studentIDTextView);
        optionID = view.findViewById(R.id.optionID);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.groupsWithoutTeachers, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsPickerSpinner.setAdapter(arrayAdapter);
        groupsPickerSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> pickOptionAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.defaultPenalty, android.R.layout.simple_spinner_item);
        pickOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionSpinner.setAdapter(pickOptionAdapter);
        optionSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.groupsPickerSpinner:{
                allStudentsFromPickedGroup.clear();
                selectedGroup = adapterView.getSelectedItem().toString();
                Log.d(TAG, "selected group: " + selectedGroup);
                getGroupIdByGroupName(selectedGroup);
                break;
            }
            case R.id.optionSpinner:{
                selectedOption = adapterView.getSelectedItem().toString();
                Toast.makeText(getContext(), selectedOption, Toast.LENGTH_SHORT).show();
                getSelectedOptionScore(selectedOption);
                break;
            }
        }
    }

    private void getGroupIdByGroupName(String selectedGroupName){
        groups$DB.whereEqualTo("name", selectedGroupName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Group group = documentSnapshot.toObject(Group.class);
                    pickedGroupID = group.getId();
                    loadAllGroupStudents(pickedGroupID);
                    Log.d(TAG, "picked groupID: " + pickedGroupID);
                }
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){}

    public void getSelectedOptionScore(String selectedOption){
        options$DB
                .document(getString(R.string.penaltiesID))
                .collection("options")
                .whereEqualTo("name", selectedOption)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            Option option = documentSnapshot.toObject(Option.class);
                            String optionScore = option.getPoints();
                            String optionIDValue = option.getId();
                            scoreTextView.setText(optionScore);
                            optionID.setText(optionIDValue);
                            Log.d(TAG, "optionScore: " + scoreTextView.getText().toString());
                            Log.d(TAG, "optionID: " + optionID.getText().toString());
                        }
                    }
                });
    }

    public void loadAllGroupStudents(String pickedGroupID){
        students$DB.whereEqualTo("groupID", pickedGroupID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot dataSnapshot : queryDocumentSnapshots.getDocuments()){
                    Student student = dataSnapshot.toObject(Student.class);
                    allStudentsFromPickedGroup.add(student.getFirstName() + " " + student.getSecondName());
                }
                createStudentsAdapter(pickedGroupID);
                Log.d(TAG, "groups student amount: " + allStudentsFromPickedGroup.size());
            }
        });
    }

    public void createStudentsAdapter(String pickedGroupID){
        Log.d(TAG, "createStudentsAdapter pickedGroupID: " + pickedGroupID);
        ArrayAdapter<String> studentsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, allStudentsFromPickedGroup);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentPickerSpinner.setAdapter(studentsAdapter);
        studentPickerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStudent = adapterView.getSelectedItem().toString();
                getStudentIDByGroupIDAndCredentials(pickedGroupID, selectedStudent);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPickedStudentEmail(pickedGroupID, selectedStudent);
                // add penalty to the history "recent page"
                String optionIDValue = optionID.getText().toString();
                addPenaltyToHistory(optionIDValue, pickedGroupID, studentIDTextView.getText().toString(), scoreTextView.getText().toString());
                getDialog().dismiss();
                Toast.makeText(context, "Вы оштрафовали " + selectedStudent + " на " + scoreTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getStudentIDByGroupIDAndCredentials(String pickedGroupID, String selectedStudent){
        String[] selectedStudentNameWords = selectedStudent.split(" ");
        students$DB
                .whereEqualTo("groupID", pickedGroupID)
                .whereEqualTo("firstName", selectedStudentNameWords[0])
                .whereEqualTo("secondName", selectedStudentNameWords[1])
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            Student student = documentSnapshot.toObject(Student.class);
                            studentIDTextView.setText(student.getId());
                        }
                    }
                });
    }

    private void addPenaltyToHistory(String optionID, String groupID, String studentID, String score){
        Log.d(TAG, "addPenaltyToHistory: optionId: " + optionID + "groupID: " + groupID + "studentID: " + studentID + "scoreID: " + score);
        Penalty penalty = new Penalty("", optionID, groupID, studentID, score, currentTeacherID);
        reqeusts$DB.document(currentTeacherRequestID).collection("STUDENTS").document(studentID).collection("PENALTY").add(penalty)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        String penaltyDocumentID = task.getResult().getId();
                        task.getResult().update("id", penaltyDocumentID);
                        Map<String, String> idField = new HashMap<>();
                        idField.put("id", penaltyDocumentID);
                        reqeusts$DB
                            .document(currentTeacherRequestID)
                            .collection("STUDENTS")
                            .document(studentID)
                            .collection("PENALTY")
                            .document(penaltyDocumentID)
                            .set(idField, SetOptions.merge());
                    }
                }
            });
    }

    private void decreaseStudentScore(String groupID, String points, String selectedEmailStudentFromPickedGroup){
        Log.d(TAG, "decreaseStudentScore: " + groupID + " " + selectedEmailStudentFromPickedGroup);
        students$DB
                .whereEqualTo("groupID", groupID)
                .whereEqualTo("email", selectedEmailStudentFromPickedGroup)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()) {
                            Student student = documentSnapshot.toObject(Student.class);
                            if (counter != 0) {
                                String currentScore = student.getScore();
                                String currentStudentID = student.getId();
                                int currentScoreInt = Integer.parseInt(currentScore);
                                int pointsInt = Integer.parseInt(points);
                                int result = currentScoreInt - pointsInt;
                                if (result < 0) {
                                    result = 0;
                                }
                                String resultString = Integer.toString(result);
//                                student.setScore(resultString);
                                students$DB.document(currentStudentID).update("score", resultString);
                                counter = 0;
                            }
                        }
                    }
                });
    }

    public void loadPickedStudentEmail(String pickedGroupID, String username){
        String[] fullNameWords = username.split(" ");
        Log.d(TAG, "loadPickedStudentEmail: " + fullNameWords[0] + " " + fullNameWords[1]);
        students$DB
            .whereEqualTo("groupID", pickedGroupID)
            .whereEqualTo("firstName", fullNameWords[0])
            .whereEqualTo("secondName", fullNameWords[1])
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Student student = documentSnapshot.toObject(Student.class);
                        selectedEmailStudentFromPickedGroup = student.getEmail();
                        Log.d(TAG, "selected student email: " + selectedEmailStudentFromPickedGroup);
                        decreaseStudentScore(pickedGroupID, scoreTextView.getText().toString(), selectedEmailStudentFromPickedGroup);
                    }
                }
            });
    }


}
