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
import android.text.method.PasswordTransformationMethod;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;


public class login_activity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener,
        CompoundButton.OnCheckedChangeListener{

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://fir-01-ff46b.firebaseio.com/");
    DatabaseReference mRefStudents = firebaseDatabase.getReference("Students");
    DatabaseReference mRefTeachers = firebaseDatabase.getReference("Teachers");

//    // Firestore
//    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
//    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");

    //widgets
    EditText passwordView;
    Button signIn;
    TextView register;
    CheckBox checkBox;
    ProgressBar progressBar;
    Spinner groupsSpinner, studentsSpinner;

    //vars
    String pickedGroup;
    ArrayList<String> allStudentsFromPickedGroup = new ArrayList<>();
    ArrayList<String> allTeachers = new ArrayList<>();
    String selectedEmailStudentFromPickedGroup;
    String pickedTeacherEmail;

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
                allStudentsFromPickedGroup.clear();
                allTeachers.clear();
                pickedGroup = adapterView.getSelectedItem().toString();
                if(!pickedGroup.equals("Учителя")) {                        // group
                    loadAllGroupStudents(pickedGroup);
                }else{                                                      // teachers
                    loadAllTeachers(pickedGroup);
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
                doValidationFields(passwordView);
                if(doValidationFields(passwordView)){
                    if(selectedEmailStudentFromPickedGroup != null) {
                        doSignIn(selectedEmailStudentFromPickedGroup, passwordView.getText().toString());
                    }else if(pickedTeacherEmail != null){
                        doSignIn(pickedTeacherEmail, passwordView.getText().toString());
                    }
                }else{
                    Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
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

    public boolean doValidationFields(EditText passwordEditText){
        if(passwordEditText.getText().toString().trim().isEmpty()){
            passwordEditText.setError("Field is required");
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
                    Toast.makeText(login_activity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent goToDashboard = new Intent(login_activity.this, dashboard_activity.class);
                    goToDashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(login_activity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.email_pref), email);
                    editor.putString(getResources().getString(R.string.password_pref), password);
                    editor.apply();
                    startActivity(goToDashboard);
                    finish();
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(login_activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    YoYo.with(Techniques.Shake).duration(750).repeat(0).playOn(findViewById(R.id.login_fields));
                }
            }
        });
    }

    public void loadAllGroupStudents(String pickedGroup){
        progressBar.setVisibility(View.VISIBLE);
        mRefStudents.child(pickedGroup)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot student : dataSnapshot.getChildren()){
                    allStudentsFromPickedGroup.add(student.getValue(Student.class).getUsername());
                    createStudentsAdapter();
                }
                if(allStudentsFromPickedGroup.size() == 0){
                    Toast.makeText(login_activity.this, "В этой группе пока нет учеников", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){}
        });


        /*

                    START USING FIRESTORE
         */

//        groups$DB.whereEqualTo("name", pickedGroup).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                DocumentSnapshot groupsDocumentSnapshot = (DocumentSnapshot) queryDocumentSnapshots.getDocuments();
//                Group group = groupsDocumentSnapshot.toObject(Group.class);
//                String groupID = group.getId();
//                students$DB.whereEqualTo("groupID", groupID).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        for(DocumentSnapshot studentDocumentSnapshot : queryDocumentSnapshots.getDocuments()){
//                            Student student = studentDocumentSnapshot.toObject(Student.class);
//                        }
//                    }
//                });
//            }
//        });


    }

    public void createStudentsAdapter(){
        ArrayAdapter<String> studentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allStudentsFromPickedGroup);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentsSpinner.setAdapter(studentsAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        studentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadPickedStudentEmail(pickedGroup, adapterView.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
    }

    public void loadPickedStudentEmail(String group, String username){
        mRefStudents.child(group)
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot student : dataSnapshot.getChildren()){
                    if(student.getValue(Student.class).getUsername().equals(username)){
                        selectedEmailStudentFromPickedGroup = student.getValue(Student.class).getEmail();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadAllTeachers(String teacher){
        progressBar.setVisibility(View.VISIBLE);
        mRefTeachers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot teacher : dataSnapshot.getChildren()){
                    allTeachers.add(teacher.getValue(Teacher.class).getFullname());
                    createTeachersAdapter();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

    public void createTeachersAdapter(){
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allTeachers);
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

    public void loadPickedTeacherEmail(String fullname){
        mRefTeachers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot teacher : dataSnapshot.getChildren()){
                    if(teacher.getValue(Teacher.class).getFullname().equals(fullname)){
                        pickedTeacherEmail = teacher.getValue(Teacher.class).getEmail();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

}
