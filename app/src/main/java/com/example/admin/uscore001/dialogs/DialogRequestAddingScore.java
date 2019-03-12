package com.example.admin.uscore001.dialogs;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.RequestAddingScoreAdapter;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.value.FieldValue;
import com.google.firebase.firestore.model.value.FieldValueOptions;

import java.lang.reflect.Executable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class DialogRequestAddingScore extends DialogFragment implements AdapterView.OnItemSelectedListener,
                                                                View.OnClickListener, View.OnFocusChangeListener{

    private static final String TAG = "DialogRequestAddingScor";

    // widgets
    Spinner teacherSpinner, options;
    EditText requestBody;
    TextView ok, cancel, score;
    RelativeLayout dialogLayout;

    // vars
    String optionValue;
    String teacherName;
    String selectedOption;
    boolean isValid = true;
    String senderImage;
    String currentUserGroup;
    String currentUserUsername;
    String addedDate;
    int optionScore;
    boolean isDone = false;
    private int counter = 1;
    private int counter1 = 0;
    private ArrayList<Teacher> teachers = new ArrayList<>();
    private String optionID;
    private String firstName;
    private String secondName;
    private String currentStudentID;

    // Firebase
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("RequestsAddingScore");
    DatabaseReference mDatabaseOptionsRef = mDatabase.getReference("Options");
    DatabaseReference mDatabaseStudentsRef = mDatabase.getReference("Students");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference requests$db = firebaseFirestore.collection("REQEUSTS$DB");
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");
    CollectionReference students$db = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference options$DB = firebaseFirestore.collection("OPTIONS$DB");
    private String currentUserGroupID;
    private String teacherRequestID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.request_adding_score_dialog, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        dialogLayout = view.findViewById(R.id.dialogLayout);

        getDialog().setTitle(getResources().getString(R.string.make_request));

        teacherSpinner = view.findViewById(R.id.teacherSpinner);
        options = view.findViewById(R.id.options);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);
        requestBody = view.findViewById(R.id.requestBody);
        score = view.findViewById(R.id.score);

        loadAllTeachers();

        ArrayAdapter<CharSequence> pickOptionAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.options, android.R.layout.simple_spinner_item);
        pickOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(pickOptionAdapter);
        options.setOnItemSelectedListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        senderImage = sharedPreferences.getString(getString(R.string.intentSenderImage), "");
        currentUserGroup = sharedPreferences.getString("groupName", "");
//        currentUserGroupID = sharedPreferences.getString(getString(R.string.currentStudentGroupID), "");
//        currentUserUsername = sharedPreferences.getString(getString(R.string.currentStudentUsername), "");
        currentStudentID = sharedPreferences.getString(getString(R.string.currentStudentID), "");

        String[] currentUserUsernameWords = currentUserUsername.split(" ");
        firstName = currentUserUsernameWords[0];
        secondName = currentUserUsernameWords[1];

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        requestBody.setOnFocusChangeListener(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        addedDate = simpleDateFormat.format(new Date());
    }

    private void loadAllTeachers(){
        try {
            teachers$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Teacher teacher = documentSnapshot.toObject(Teacher.class);
                        teachers.add(teacher);
                    }
                    RequestAddingScoreAdapter pickTeacherAdapter = new RequestAddingScoreAdapter(getContext(), android.R.layout.simple_spinner_item, teachers);
                    pickTeacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    teacherSpinner.setAdapter(pickTeacherAdapter);
                    teacherSpinner.setOnItemSelectedListener(DialogRequestAddingScore.this);
                }
            });
        }catch (Exception e){
            Log.d(TAG, "loadAllTeachers: " + e.getMessage());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {}

    public void sendRequest(String id,
                            String body,
                            String date,
                            String getter,
                            String image_path,
                            String senderEmail,
                            String firstName,
                            String secondName,
                            String lastName,
                            int score,
                            String groupID,
                            String requestID,
                            String optionID,
                            boolean answered,
                            boolean canceled,
                            String senderID)
    {
        RequestAddingScore request = new RequestAddingScore(
                id,
                body,
                date,
                getter,
                image_path,
                senderEmail,
                firstName,
                secondName,
                lastName,
                score,
                groupID,
                requestID,
                optionID,
                answered,
                canceled,
                senderID);
        if(counter1 == 0) {
            requests$db
                    .document(teacherRequestID)
                    .collection("STUDENTS")
                    .document(currentStudentID)
                    .collection("REQUESTS")
                    .add(request)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "sendRequest: " + "teacherRequestID:" + teacherRequestID + "; currentStudentID:" + currentStudentID);
                                String requestDocumentID = task.getResult().getId();
                                task.getResult().update("id", requestDocumentID);
                                Map<String, String> idField = new HashMap<>();
                                idField.put("id", currentStudentID);
                                requests$db
                                        .document(teacherRequestID)
                                        .collection("STUDENTS")
                                        .document(currentStudentID)
                                        .set(idField, SetOptions.merge());
                            } else {
                                Toast.makeText(getContext(), "Запрос не был отправлен", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            counter1 = 1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok: {
                Log.d(TAG, "onClick: ok button was clicked");
                String id = "";
                String body = requestBody.getText().toString();
                String date = addedDate;
                String getter = teacherName;
                String image_path = senderImage;
                String senderEmail = currentUser.getEmail();
                int scoreValue = Integer.parseInt(score.getText().toString());
                String groupID = currentUserGroupID;
                String requestID = teacherRequestID;
                String option = optionID;
                boolean answered = false;
                boolean canceled = false;
                String senderID = currentStudentID;
                students$db.document(currentStudentID)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            Student student = documentSnapshot.toObject(Student.class);
                            int currentLimitScore = Integer.parseInt(student.getLimitScore());
                            Log.d(TAG, "currentStudentLimitScore: " + currentLimitScore);
                            if (currentLimitScore + 5 < scoreValue) {
                                try {
                                    Toast.makeText(getContext(), "Ваш лимит меньше, чем запрашиваемые очки", Toast.LENGTH_SHORT).show();
                                    YoYo.with(Techniques.Shake).repeat(0).duration(1000).playOn(dialogLayout);
                                }catch (Exception e1){
                                    Log.d(TAG, "onEvent: " + e1.getMessage());
                                }
                            } else if (currentLimitScore + 5 >= scoreValue) {
                                decreaseLimitScore(scoreValue);
                                sendRequest(
                                        id,
                                        body,
                                        date,
                                        getter,
                                        image_path,
                                        senderEmail,
                                        firstName,
                                        secondName,
                                        "",
                                        scoreValue,
                                        groupID,
                                        requestID,
                                        option,
                                        answered,
                                        canceled,
                                        currentStudentID
                                );
                                try {
                                    Toast.makeText(getContext(), "Успешно отправленно " + teacherName, Toast.LENGTH_SHORT).show();
                                    getDialog().dismiss();
                                }catch (Exception e1){
                                    Log.d(TAG, "toast message: " + e1.getMessage());
                                }
                            }
                        }
                    });
                break;
            }
            case R.id.cancel: {
                getDialog().dismiss();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.teacherSpinner:{
                Teacher teacher = (Teacher) parent.getSelectedItem();
                teacherRequestID = teacher.getRequestID();
                Log.d(TAG, teacher.getFirstName()+" "+teacher.getLastName() + " id: " + teacherRequestID);
                teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                break;
            }
            case R.id.options:{
                selectedOption = parent.getItemAtPosition(position).toString();
                getSelectedOptionScoreAndID(selectedOption);
                Toast.makeText(getContext(), selectedOption, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    //    public void getSelectedOptionScore(String selectedOption){
//        mDatabaseOptionsRef.child(selectedOption).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                try {
//                    optionValue = dataSnapshot.getValue(Option.class).getOption();
//                    optionScore = dataSnapshot.getValue(Option.class).getScore();
//                    Log.d(TAG, "onDataChange: " + optionValue + "/" + optionScore);
//                    score.setText(Integer.toString(optionScore));
//                }catch (Exception e){
//                    Log.d(TAG, "onDataChange: " + e.getMessage());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void getSelectedOptionScoreAndID(String optionName){
        options$DB
                .document(getString(R.string.promotionsID))
                .collection("options")
                .whereEqualTo("name", optionName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    Option option = documentSnapshot.toObject(Option.class);
                    String points = option.getPoints();
                    score.setText(points);
                    optionID = option.getId();
                    Log.d(TAG, "selected optionID: " + optionID);
                }
            }
        });
    }

    public void decreaseLimitScore(int requestedScoreValue){
        students$db
            .document(currentStudentID)
            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    Student student = documentSnapshot.toObject(Student.class);
                    if(!isDone) {
                        String limitScore = student.getLimitScore();
                        int limitScoreInteger = Integer.parseInt(limitScore);
                        int result = limitScoreInteger - requestedScoreValue;
                        String resultString = Integer.toString(result);
                        if(result <= 0){
                            resultString = "0";
                            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
                            Date currentDate = calendar.getTime();
                            Map<String, Date> map = new HashMap<>();
                            map.put("spendLimitScoreDate", currentDate);
                            students$db.document(currentStudentID).set(map, SetOptions.merge());
                        }
                        students$db.document(currentStudentID).update("limitScore", resultString);
                        Log.d(TAG, "decreaseLimitScore: " + resultString);
                        isDone = true;
                    }
                }
            });
    }

}
