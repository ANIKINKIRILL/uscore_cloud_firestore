package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;


public class login_activity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener,
        CompoundButton.OnCheckedChangeListener{


    private static final String TAG = "login_activity";


    // Firebase and Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //widgets
    EditText passwordView;
    Button signIn;
    TextView register;
    CheckBox checkBox;
    ProgressBar progressBar;
    Spinner groupsSpinner, studentsSpinner;

    //vars
    String pickedObject;
    ArrayList<String> allStudentsNamesFromPickedGroup = new ArrayList<>();
    ArrayList<String> allTeachersNames = new ArrayList<>();
    String pickedTeacherEmail;
    private String pickedGroupID;
    private String studentEmailFromPickedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){
        register = findViewById(R.id.register);
        passwordView = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);
        signIn.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        register.setOnClickListener(this);

        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(this);

        passwordView.setOnFocusChangeListener(this);

        groupsSpinner = findViewById(R.id.groupsPickerSpinner);
        studentsSpinner = findViewById(R.id.studentPickerSpinner);
        ArrayAdapter<CharSequence> groupsAdapter = ArrayAdapter.createFromResource(this, R.array.groups, android.R.layout.simple_spinner_item);
        groupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsSpinner.setAdapter(groupsAdapter);
        groupsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                allStudentsNamesFromPickedGroup.clear();
                allTeachersNames.clear();
                pickedObject = adapterView.getSelectedItem().toString();
                if(!pickedObject.equals("Учителя")) {// group
                    haveStudentsInGroup = false;
                    loadAllGroupStudents(pickedObject);
                }else{                                                      // teachers
                    loadAllTeachers();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.isChecked()){
            passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{
            passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(login_activity.this);
        passwordView.setText(sharedPreferences.getString(getString(R.string.password_pref), ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signIn:{
                if(isValid(passwordView)){
                    if(studentEmailFromPickedGroup != null) {
                        doSignIn(studentEmailFromPickedGroup, passwordView.getText().toString().trim());
                        signIn.setEnabled(false);
                    }else if(pickedTeacherEmail != null){
                        doSignIn(pickedTeacherEmail, passwordView.getText().toString().trim());
                        signIn.setEnabled(false);
                    }
                }
                break;
            }
            case R.id.register:{
                Intent goToRegister = new Intent(login_activity.this, register_activity.class);
                startActivity(goToRegister);
                break;
            }
        }
    }

    public boolean isValid(EditText passwordEditText){
        if(passwordEditText.getText().toString().trim().isEmpty()){
            passwordEditText.setError("Поле обязательно для ввода");
            passwordEditText.requestFocus();
            YoYo.with(Techniques.Swing).duration(1000).repeat(0).playOn(passwordEditText);
            return false;
        }
        return true;
    }

    public void doSignIn(final String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
//                    String tokenID = FirebaseInstanceId.getInstance().getToken();
//                    if(!email.contains("teacher")) {
//                        students$DB.whereEqualTo("email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                                    Map<String, Object> tokenIDMap = new HashMap<>();
//                                    tokenIDMap.put("deviceTokenID", tokenID);
//                                    documentSnapshot.getReference().update(tokenIDMap);
//                                }
//                            }
//                        });
//                    }else{
//                        teachers$DB.whereEqualTo("responsible_email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
//                                    Map<String, Object> tokenIDMap = new HashMap<>();
//                                    tokenIDMap.put("deviceTokenID", tokenID);
//                                    documentSnapshot.getReference().update(tokenIDMap);
//                                }
//                            }
//                        });
//                    }
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(login_activity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.password_pref), password);
                    editor.apply();
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent goToDashboard = new Intent(login_activity.this, dashboard_activity.class);
                    goToDashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToDashboard);
                    finish();
                    Toast.makeText(login_activity.this, "Вы вошли в свой аккаунт", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(login_activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    YoYo.with(Techniques.Shake).duration(750).repeat(0).playOn(findViewById(R.id.login_fields));
                }
            }
        });
    }

    boolean haveStudentsInGroup = false;

    public void loadAllGroupStudents(String pickedGroup){
        signIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        groups$DB.whereEqualTo("name", pickedGroup).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot groups : task.getResult().getDocuments()){
                    Group group = groups.toObject(Group.class);
                    String groupID = group.getId();
                    isHaveStudentsInGroup(groupID);
                }
            }
        });
    }

    private void isHaveStudentsInGroup(String pickedGroupID){
        final int[] studentsSize = new int[1];
        Log.d(TAG, "isHaveStudentsInGroup pickedGroupID: " + pickedGroupID);
        students$DB.whereEqualTo("groupID", pickedGroupID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                studentsSize[0] = task.getResult().size();
                Log.d(TAG, "isHaveStudentsInGroup studentsSize: " + studentsSize[0]);
                if(studentsSize[0] != 0){
                    students$DB.whereEqualTo("groupID", pickedGroupID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                Student student = documentSnapshot.toObject(Student.class);
                                allStudentsNamesFromPickedGroup.add(student.getFirstName() + " " + student.getSecondName());
                            }
                            createStudentsAdapter(allStudentsNamesFromPickedGroup, pickedGroupID);
                        }
                    });
                }else{
                    Toast.makeText(login_activity.this, "В этой группе нет учеников, попробуйте зарегистрироваться", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void createStudentsAdapter(ArrayList<String> students, String pickedGroupID){
        signIn.setEnabled(true);
        ArrayAdapter<String> studentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, students);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentsSpinner.setAdapter(studentsAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        studentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadPickedStudentEmail(pickedGroupID, adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void loadPickedStudentEmail(String pickedGroupID, String firstSecondName){
        String[] firstSecondNameWords = firstSecondName.split(" ");
        students$DB
                .whereEqualTo("firstName", firstSecondNameWords[0])
                .whereEqualTo("secondName", firstSecondNameWords[1])
                .whereEqualTo("groupID", pickedGroupID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot studentsSnapshots : queryDocumentSnapshots.getDocuments()) {
                    Student student = studentsSnapshots.toObject(Student.class);
                    studentEmailFromPickedGroup = student.getEmail();
                    signIn.setEnabled(true);
                }
                Log.d(TAG, "picked student email: " + studentEmailFromPickedGroup);
                Log.d(TAG, "firstName: " + firstSecondNameWords[0] + " secondName: " + firstSecondNameWords[1]);
            }
        });
        Log.d(TAG, "signInButton is enable");
    }

    public void loadAllTeachers(){
        signIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        teachers$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot teachers : queryDocumentSnapshots.getDocuments()){
                    Teacher teacherObj = teachers.toObject(Teacher.class);
                    allTeachersNames.add(teacherObj.getFirstName() + " " + teacherObj.getLastName());
                }
                createTeachersAdapter();
            }
        });
        signIn.setEnabled(true);
    }

    public void createTeachersAdapter(){
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allTeachersNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentsSpinner.setAdapter(teacherAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        studentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadPickedTeacherEmail(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void loadPickedTeacherEmail(String firstLastName){
        signIn.setEnabled(false);
        Log.d(TAG, "signInButton is not enable");
        try {
            String[] firstLastNameWords = firstLastName.split(" ");
            teachers$DB
                    .whereEqualTo("firstName", firstLastNameWords[0])
                    .whereEqualTo("lastName", firstLastNameWords[1])
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            try {
                                List<DocumentSnapshot> teachersSnapshot = queryDocumentSnapshots.getDocuments();
                                Teacher teacher = teachersSnapshot.get(0).toObject(Teacher.class);
                                pickedTeacherEmail = teacher.getResponsible_email();
                                signIn.setEnabled(true);
                                Log.d(TAG, "selectedTeacherEmail: " + pickedTeacherEmail);
                            }catch (Exception e1){
                                Log.d(TAG, "loadPickedTeacherEmail: " + e1.getMessage());
                            }
                        }
                    });
        }catch (Exception e){
            Log.d(TAG, "loadPickedTeacherEmail: " + e.getMessage());
        }
        Log.d(TAG, "signInButton is enable");
    }

}
