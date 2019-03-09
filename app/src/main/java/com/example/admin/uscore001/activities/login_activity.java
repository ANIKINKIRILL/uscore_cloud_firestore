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
import com.example.admin.uscore001.AsyncTaskArguments;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.Settings;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.models.User;
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

/**
 * Авторизация пользователя
 */

public class login_activity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener,
        CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "login_activity";

    // Виджеты
    private EditText passwordView;
    private Button signIn;
    private TextView register;
    private CheckBox checkBox;
    public static ProgressBar progressBar;
    private Spinner groupsSpinner, usersSpinner;

    // Переменные
    private String pickedObject;
    String email;

    // Постоянные переменные

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        populateGroupSpinnerAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(User.isAuthenticated){
            Log.d(TAG, "onStart: already authenticated");
            Intent goToDashboard = new Intent(login_activity.this, dashboard_activity.class);
            goToDashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(goToDashboard);
            finish();
        }else{
            Log.d(TAG, "onStart: not authenticated");
        }
    }

    /**
     * Инициализация
     */

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
        usersSpinner = findViewById(R.id.studentPickerSpinner);

    }

    /**
     * Наполняем спиннер с группами и устанавливаем OnItemSelectedListener
     */

    public void populateGroupSpinnerAdapter(){
        ArrayAdapter<CharSequence> groupsAdapter = ArrayAdapter.createFromResource(this, R.array.groups, android.R.layout.simple_spinner_item);
        groupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsSpinner.setAdapter(groupsAdapter);
        groupsSpinner.setOnItemSelectedListener(groupsSpinnerOnItemSelectedListener);
    }

    AdapterView.OnItemSelectedListener groupsSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            pickedObject = adapterView.getSelectedItem().toString();
            if(!pickedObject.equals("Учителя")) {                                   // Выбрали Группу
                loadGroupStudents(pickedObject);
            }else{
                loadAllTeachers();                                                  // Выбрали Учителей
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView){}
    };

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signIn:{
                if(isValid()){
                    if(email != null || !email.trim().isEmpty()){
                        doSignIn(email, passwordView.getText().toString().trim(), mAuthCallback);
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

    /**
     * Проверка на ошибки ввода при авторизации пользователя
     * Если все верно -> @return true
     * Если ошибка -> @return false
     */

    public boolean isValid(){
        if(passwordView.getText().toString().trim().isEmpty()){
            passwordView.setError("Поле обязательно для ввода");
            passwordView.requestFocus();
            YoYo.with(Techniques.Swing).duration(1000).repeat(0).playOn(passwordView);
            return false;
        }
        return true;
    }

    /**
     * Авторизация пользователя
     * @param email
     * @param password
     */

    public void doSignIn(final String email, String password, Callback callback){
        signIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        User.authenticate(email, password, callback);
    }

    /**
     * Callback, вызываемый после авторизации пользователя
     */

    Callback mAuthCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            boolean successful = (boolean) data;
            if(successful){
                // Сохранение логина и пароля в Настройках
                Settings.setLogin(params[0]);
                Settings.setPassword(params[1]);
                // Извлечение статуса пользователя
                User.getUserStatus(params[0], mGetStatusCallback);
                User.isAuthenticated = true;

                Intent goToDashboard = new Intent(login_activity.this, dashboard_activity.class);
                goToDashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goToDashboard);
                finish();
                Log.d(TAG, "signedIn: with login:" + Settings.getLogin() + "\n" +
                                     "password: " + Settings.getPassword() + "\n");
                signIn.setEnabled(true);
                String pickedGroup = (String) groupsSpinner.getSelectedItem();
                Settings.setGroupName(pickedGroup);
            }else{
                String failureReason = params[0];
                Toast.makeText(login_activity.this, failureReason, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                signIn.setEnabled(true);
                Log.d(TAG, "signIn failed");
            }
        }
    };

    /**
     * Callback, полсе получения статуса
     */

    Callback mGetStatusCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String statusID = data.toString();
            String studentStatus = getString(R.string.studentStatusValue);
            String teacherStatus = getString(R.string.teacherStatusValue);
            // Сохранение статуса пользователя
            if(statusID.equals(studentStatus)){
                Settings.setStatus(studentStatus);
            }
            if(statusID.equals(teacherStatus)){
                Settings.setStatus(teacherStatus);
            }
            Log.d(TAG, "statusID: " + Settings.getStatus());
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * Выгружение всех учеинков класса
     * @param pickedGroup
     */

    public void loadGroupStudents(String pickedGroup){
        signIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        passwordView.setText("");
        Student.loadGroupStudents(pickedGroup, mLoadGroupStudentsCallback);
    }

    /**
     * Callback, после выгрузки всех студентов из группы
     */

    Callback mLoadGroupStudentsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<String> students = (ArrayList) data;
            if(students.size() != 0){
                String groupID = params[0];
                signIn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                initStudentsSpinner(students, groupID);
            }else{
                Toast.makeText(login_activity.this, "В классе пока нет учеников, попробуйте зарегистрироваться", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                // Делаем так чтобы спиннер был пустой
                usersSpinner.setAdapter(new ArrayAdapter<String>(login_activity.this, android.R.layout.simple_spinner_item, new ArrayList<>()));
            }
        }
    };

    /**
     * Инициализация спиннера для учеников и OnItemSelectedListener
     * @param students              // Учеинки группы
     * @param pickedGroupID         // id группы
     */

    public void initStudentsSpinner(ArrayList<String> students, String pickedGroupID){
        ArrayAdapter<String> studentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, students);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usersSpinner.setAdapter(studentsAdapter);
        usersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                passwordView.setText("");
                getPickedStudentEmail(pickedGroupID, adapterView.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
    }

    /**
     * Извлечение email ученика
     * @param pickedGroupID             // id группы
     * @param firstSecondStudentName    // Данные ученика
     */

    public void getPickedStudentEmail(String pickedGroupID, String firstSecondStudentName){
        signIn.setEnabled(false);
        String firstSecondNameSplittedWithSpace[] = firstSecondStudentName.split(" ");
        String firstName = firstSecondNameSplittedWithSpace[0];
        String secondName = firstSecondNameSplittedWithSpace[1];
        Student.getStudentEmail(pickedGroupID, firstName, secondName, mGetStudentEmailCallback);
    }

    /**
     * Callback, после выгрузки всех студентов из группы
     */
    
    Callback mGetStudentEmailCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            email = (String) data;
            signIn.setEnabled(true);
        }
    };

    /**
     * Выгрузка всех учителей
     */

    public void loadAllTeachers(){
        signIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        passwordView.setText("");
        Teacher.loadAllTeachers(mLoadAllTeachersCallback);
    }

    /**
     * Callback, после выгрузки всех учителей
     */

    Callback mLoadAllTeachersCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<String> teachers = (ArrayList) data;
            initTeachersSpinner(teachers);
        }
    };

    /**
     * Инициализация спиннера для учителей и OnItemSelectedListener
     * @param teachers          // Список учителей
     */

    public void initTeachersSpinner(ArrayList<String> teachers){
        signIn.setEnabled(true);
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teachers);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usersSpinner.setAdapter(teacherAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        usersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                passwordView.setText("");
                getPickedTeacherEmail(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Извлечение email учителя
     * @param firstLastName         // Данные учителя
     */

    public void getPickedTeacherEmail(String firstLastName){
        signIn.setEnabled(false);
        String[] firstLastNameSplittedWithSpace = firstLastName.split(" ");
        String firstName = firstLastNameSplittedWithSpace[0];
        String lastName = firstLastNameSplittedWithSpace[1];
        Teacher.getTeacherEmail(firstName, lastName, mGetTeacherEmailCallback);
    }

    /**
     * Callback после получения email учителя
     */

    Callback mGetTeacherEmailCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            email = (String) data;
            signIn.setEnabled(true);
        }
    };

}
