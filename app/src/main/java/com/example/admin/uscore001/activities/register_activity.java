package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register_activity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener,
        AdapterView.OnItemSelectedListener{

    // Firebase STUFF
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference();

    // widgets
    EditText email, password, username, group;
    Button registerButton;
    TextView loginPage, welcomeTitle;
    Spinner groupsPickerSpinner;

    // vars
    String emailText, passwordText, usernameText, groupText;
    boolean isChecked = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
//        group = findViewById(R.id.group);
        registerButton = findViewById(R.id.register);
        loginPage = findViewById(R.id.loginPage);
        loginPage.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        groupsPickerSpinner = findViewById(R.id.groupsPickerSpinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.groups, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsPickerSpinner.setAdapter(arrayAdapter);
        groupsPickerSpinner.setOnItemSelectedListener(this);

        welcomeTitle = findViewById(R.id.welcomeTitle);
        YoYo.with(Techniques.BounceIn).duration(800).repeat(0).playOn(welcomeTitle);

        email.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        username.setOnFocusChangeListener(this);
//        group.setOnFocusChangeListener(this);

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:{
                getTextFromViews(email, password, username);
                registerStudent(emailText, passwordText, v);
                break;
            }
            case R.id.loginPage:{
                Intent intent = new Intent(register_activity.this, login_activity.class);
                startActivity(intent);
                break;
            }
        }
    }

    public void getTextFromViews(EditText email, EditText password, EditText username){
        emailText = email.getText().toString().toLowerCase();
        passwordText = password.getText().toString();
        usernameText = username.getText().toString();
    }

    public void registerStudent(final String emailValue, String passwordValue, View v){

        doValidationFields(email, password, v);
        if(isChecked) {
            mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(register_activity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                        String uID = mRef.push().getKey();
                        Student student = new Student(emailValue, usernameText, groupText, "", "0", uID, "5000", "", getString(R.string.studentStatusValue));
                        mRef.child("Students").child(groupText).child(emailValue.replace(".", "")).setValue(student);
                        Intent intent = new Intent(register_activity.this, login_activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(register_activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void doValidationFields(EditText emailEditText, EditText passwordEditText, View view){
        isChecked = true;
        if(emailEditText.getText().toString().trim().isEmpty()){
            emailEditText.setError("Field is required");
            emailEditText.requestFocus();
            isChecked = false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()){
            emailEditText.setError("Wrong input...");
            emailEditText.requestFocus();
            isChecked = false;
        }
        if(passwordEditText.getText().toString().trim().isEmpty()){
            passwordEditText.setError("Field is required");
            passwordEditText.requestFocus();
            isChecked = false;
        }
        if(usernameText.trim().isEmpty()){
            username.setError("Field is required");
            isChecked = false;
            username.requestFocus();
        }
//        if(groupText.trim().isEmpty() || !groupText.contains("-")){
//            group.setError("Field is required/ Wrong input");
//            Toast.makeText(getApplicationContext(), "Example: 10-6 (must contain '-')", Toast.LENGTH_SHORT).show();
//            group.requestFocus();
//            isChecked = false;
//        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        groupText = adapterView.getSelectedItem().toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
