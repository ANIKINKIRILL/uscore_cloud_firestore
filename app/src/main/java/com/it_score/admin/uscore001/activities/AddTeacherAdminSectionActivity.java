package com.it_score.admin.uscore001.activities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Group;
import com.it_score.admin.uscore001.models.Position;
import com.it_score.admin.uscore001.models.Subject;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.PositionArrayAdapter;
import com.it_score.admin.uscore001.util.RegisterActivityGroupAdapter;
import com.it_score.admin.uscore001.util.SubjectArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Панель Администратора с добавлением учителя
 */

public class AddTeacherAdminSectionActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "AddTeacherAdminSectionA";

    // Виджеты
    private EditText firstName, secondName, lastName, email, requestID;
    private Button addTeacherButton;
    private Spinner positionSpinner, subjectSpinner, groupSpinner;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CollectionReference TEACHERS$DB = firebaseFirestore.collection("TEACHERS$DB");
    private CollectionReference GROUPS$DB = firebaseFirestore.collection("GROUPS$DB");
    private CollectionReference REQEUSTS$DB = firebaseFirestore.collection("REQEUSTS$DB");

    // Переменные
    private Context context;
    private AddTeacherAdminSectionActivity classInstance;

    // Постоянные переменные
    private final String TEACHER_HELPER = "biifVUnTG9o1mFCLcG9F";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_teacher_admin_section);
        getContext();
        getInstance();
        init();
        initActionBar();
        populatePositionSpinner();
        populateSubjectSpinner();
        populateGroupSpinner();
    }

    /**
     * Получить Context Activity
     */

    private void getContext(){
        context = getApplicationContext();
    }

    /**
     * Получить instance класса
     */

    private void getInstance(){
        classInstance = new AddTeacherAdminSectionActivity();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        firstName = findViewById(R.id.firstName);
        secondName = findViewById(R.id.secondName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        requestID = findViewById(R.id.requestID);
        addTeacherButton = findViewById(R.id.addTeacherButton);
        positionSpinner = findViewById(R.id.positionSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        groupSpinner = findViewById(R.id.groupSpinner);

        addTeacherButton.setOnClickListener(this);

    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Добавить учителя в систему");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    /**
     * Наполнить спиннер с выбором должности
     */

    private void populatePositionSpinner(){
        User.getAllPositionsList(mGetAllPositionsCallback);
    }

    private Callback mGetAllPositionsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Position> positions = (ArrayList) data;
            PositionArrayAdapter adapter = new PositionArrayAdapter(context, positions);
            positionSpinner.setAdapter(adapter);
        }
    };

    /**
     * Наполнить спиннер с выбором предмета
     */

    private void populateSubjectSpinner(){
        User.getAllSubjectsList(mGetAllSubjectsCallback);
    }

    private Callback mGetAllSubjectsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Subject> subjects = (ArrayList) data;
            SubjectArrayAdapter adapter = new SubjectArrayAdapter(context, subjects);
            subjectSpinner.setAdapter(adapter);
        }
    };

    /**
     * Наполнить спиннер с выбором группы
     */

    private void populateGroupSpinner(){
        User.getAllSchoolGroups(mGetAllSchoolGroupsCallback);
    }

    private Callback mGetAllSchoolGroupsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Group> groups = (ArrayList) data;
            RegisterActivityGroupAdapter adapter = new RegisterActivityGroupAdapter(context, groups);
            groupSpinner.setAdapter(adapter);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addTeacherButton:{
                // Получить данные из edittext
                String firstNameValue = firstName.getText().toString();
                String secondNameValue = secondName.getText().toString();
                String lastNameValue = lastName.getText().toString();
                String emailValue = email.getText().toString();
                String requestIDValue = requestID.getText().toString();
                String statusID = "PGIg1vm8SrHN6YLeN0TD";
                Position position = (Position) positionSpinner.getSelectedItem();
                String positionName = position.getId();
                Subject subject = (Subject) subjectSpinner.getSelectedItem();
                String subjectName = subject.getId();
                if(position.getId().trim().equals(TEACHER_HELPER)){
                    subjectName = "";
                    statusID = "BpYvYudLYGkfZLspkctl";
                    positionName = TEACHER_HELPER;
                }
                Group group = (Group) groupSpinner.getSelectedItem();
                // Создание на основе собранных данных класса учителя
                Teacher new_teacher = new Teacher(
                        emailValue,
                        "",
                        positionName,
                        subjectName,
                        firstNameValue,
                        secondNameValue,
                        lastNameValue,
                        group.getId(),
                        "",
                        requestIDValue,
                        statusID
                );
                // Создание учителя в бд
                TEACHERS$DB.add(new_teacher).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            clearInputFields();
                            String id = task.getResult().getId();
                            task.getResult().update("id", id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Добавка учителя к выбранной группе
                                    GROUPS$DB.document(group.getId()).update("teacherID", id);
                                    // Добавка учительсокого requestID
                                    Map<String, Object> requestConfigMap = new HashMap<>();
                                    requestConfigMap.put("id", requestIDValue);
                                    requestConfigMap.put("teacher", id);
                                    REQEUSTS$DB.document(requestIDValue).set(requestConfigMap, SetOptions.merge());
                                    // Создание пароля и логина для учителя
                                    String login = emailValue;
                                    String password = emailValue.substring(0, emailValue.indexOf("@"));
                                    firebaseAuth.createUserWithEmailAndPassword(login, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(context, "Учитель создан и добавлен к " + group.getName() + " зарегистирован", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(AddTeacherAdminSectionActivity.this,
                                                        "Учитель создан и добавлен к " + group.getName() + ", но не зарегистирован из-за " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
                break;
            }
        }
    }

    /**
     *                                  Обработка нажатия на спиннеры
     * @param parent        спиннер
     * @param view          окошко спиннера
     * @param position      позиция выбранного item
     * @param id            id
     */
    /*
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.positionSpinner:{
                Position selectedPositionClass = (Position) parent.getSelectedItem();
                selectedPosition = selectedPositionClass.getId();
                break;
            }
            case R.id.subjectSpinner:{
                Subject selectedSubjectClass = (Subject) parent.getSelectedItem();
                selectedSubject = selectedSubjectClass.getId();
                break;
            }
            case R.id.groupSpinner:{
                Group selectedGroupClass = (Group) parent.getSelectedItem();
                selectedGroup = selectedGroupClass.getId();
                selectedGroupName = selectedGroupClass.getName();
                Log.d(TAG, "groupSpinnerOnItemSelected: " + selectedGroup);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
    */

    /**
     * Отчистка всех edittext
     */

    private void clearInputFields(){
        firstName.setText("");
        secondName.setText("");
        lastName.setText("");
        firstName.setText("");
        email.setText("");
        requestID.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
