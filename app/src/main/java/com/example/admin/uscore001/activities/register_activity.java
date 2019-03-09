package com.example.admin.uscore001.activities;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.StudentRegisterRequestModel;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.models.User;
import com.example.admin.uscore001.util.RegisterActivityGroupAdapter;
import com.example.admin.uscore001.util.RegisterActivityTeacherAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Активити для регистарции учеников в системе
 */

public class register_activity extends AppCompatActivity {

    private static final String TAG = "register_activity";

    // Виджеты
    EditText firstName,secondName,lastName,email;
    Button register;
    Spinner groupsPickerSpinner, teacherPickerSpinner;

    // Переменные
    String selectedTeacherID;
    private String selectedGroupID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initActionBar();
        init();
        populateSpinner();
    }

    /**
     * Инизиализация ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Регистрация ученика");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
        actionBar.setElevation(0);
    }

    /**
     * Инизиализация
     */

    private void init(){
        firstName = findViewById(R.id.firstName);
        secondName = findViewById(R.id.secondName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        register = findViewById(R.id.register);
        register.setOnClickListener(sendRegistrationRequestOnClickListener);
        groupsPickerSpinner = findViewById(R.id.groupsPickerSpinner);
        teacherPickerSpinner = findViewById(R.id.teacherPickerSpinner);
    }

    /**
     * Установка items в spinner
     */

    private void populateSpinner(){
        // Выгружаем группы из бд и загружаем в groupsPickerSpinner, установка setOnItemSelectedListener
        User.getAllSchoolGroups(mGetAllSchoolGroupsCallback);
    }

    /**
     * Callback, который вызывиться после получения всех групп с Сервера
     */

    Callback mGetAllSchoolGroupsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Group> groups = (ArrayList) data;
            RegisterActivityGroupAdapter adapter = new RegisterActivityGroupAdapter(register_activity.this, groups);
            groupsPickerSpinner.setAdapter(adapter);
            groupsPickerSpinner.setOnItemSelectedListener(groupsSpinnerOnItemSelectedListener);
        }
    };

    /**
     * GroupsSpinner OnItemSelectedListener
     */

    AdapterView.OnItemSelectedListener groupsSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Group group = (Group) parent.getSelectedItem();
            selectedGroupID = group.getId();
            selectedTeacherID = group.getTeacherID(); // id классного рукаводителя
            Log.d(TAG, "selected groupID: " + selectedGroupID);
            Log.d(TAG, "selected teacherID: " + selectedTeacherID);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * Listener кнопки для отправления заявки на регистрацию
     */

    View.OnClickListener sendRegistrationRequestOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isValid()){
                // отправка запрса на регистрацию классному рукаводителю
                String firstNameValue = firstName.getText().toString();
                String secondNameValue = secondName.getText().toString();
                String lastNameValue = lastName.getText().toString();
                String emailValue = email.getText().toString();
                String groupID = selectedGroupID;
                String teacherID = selectedTeacherID;
                boolean confirmed = false;
                boolean denied = false;
                Student.sendRegistrationRequest(mSendRegistrationRequestCallback, firstNameValue, secondNameValue, lastNameValue, emailValue, groupID, teacherID, confirmed, denied);
            }
        }
    };

    /**
     * Callback, который вызывиться после отправления заяввки на регистарцию
     */

    Callback mSendRegistrationRequestCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String message = (String) data;
            Toast.makeText(register_activity.this, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    /**
     *  Проверка на ошибки ввода данных для регистрации
     */
    private boolean isValid(){
        if(firstName.getText().toString().trim().isEmpty()){
            firstName.setError("Поле обязательно");
            firstName.requestFocus();
            return false;
        }
        if(secondName.getText().toString().trim().isEmpty()){
            secondName.setError("Поле обязательно");
            secondName.requestFocus();
            return false;
        }
        if(email.getText().toString().trim().isEmpty()){
            email.setError("Поле обязательно");
            email.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
            email.setError("Неверная почта");
            email.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.info:{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Ознакомление");
                alertDialog
                .setMessage("Для начала мониторинга своих очков и баллов. Тебе следует зарегистрироваться. Выбирай свою группу и продолжай побеждать");
                alertDialog.setPositiveButton("Спасибо", positiveButtonOnClickListener);
                alertDialog.show();
                break;
            }
        }
        return true;
    }

    /**
     * Нажатие кнопки Спасибо
     */

    DialogInterface.OnClickListener positiveButtonOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

}
