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

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.StudentRegisterRequestModel;
import com.example.admin.uscore001.models.Teacher;
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
 * Активити для регистарции ученкиов в системе
 */

public class register_activity extends AppCompatActivity {

    private static final String TAG = "register_activity";

    // widgets
    EditText firstName,secondName,lastName,email;
    Button register;
    Spinner groupsPickerSpinner, teacherPickerSpinner;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference teacher$db = firebaseFirestore.collection("TEACHERS$DB");
    CollectionReference groups$db = firebaseFirestore.collection("GROUPS$DB");
    CollectionReference student_register_requests = firebaseFirestore.collection("STUDENT_REGISTER_REQUESTS");

    // vars
    ArrayList<Teacher> teachers = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    String selectedTeacherID;
    private String selectedGroupID;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Регистрация ученика");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
        actionBar.setElevation(0);

        init();
    }

    /**
     * Находим виджеты
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

        setItemsIntoSpinners();

    }

    /**
     * Установим items в spinners
     */

    private void setItemsIntoSpinners(){
        // Выгружаем группы из бд и загружаем в groupsPickerSpinner
        groups$db.addSnapshotListener(groups$dbEventListener);
    }
    /**
     * Listener для выгрузки учителей из бд
     */

    EventListener<QuerySnapshot> teacher$dbEventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
            for(DocumentSnapshot teacher : queryDocumentSnapshots.getDocuments()){
                Teacher teacherObj = teacher.toObject(Teacher.class);
                teachers.add(teacherObj);
            }
            RegisterActivityTeacherAdapter adapter = new RegisterActivityTeacherAdapter(register_activity.this, teachers);
            teacherPickerSpinner.setAdapter(adapter);
            teacherPickerSpinner.setOnItemSelectedListener(teacherSpinnerOnItemSelectedListener);
        }
    };

    /**
     * Listener для выгрузыки групп из бд
     */

    EventListener<QuerySnapshot> groups$dbEventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
            for(DocumentSnapshot group : queryDocumentSnapshots.getDocuments()){
                Group groupObj = group.toObject(Group.class);
                groups.add(groupObj);
            }
            RegisterActivityGroupAdapter adapter = new RegisterActivityGroupAdapter(register_activity.this, groups);
            groupsPickerSpinner.setAdapter(adapter);
            groupsPickerSpinner.setOnItemSelectedListener(groupsSpinnerOnItemSelectedListener);
        }
    };

    AdapterView.OnItemSelectedListener teacherSpinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
                StudentRegisterRequestModel model = new StudentRegisterRequestModel(firstNameValue, secondNameValue,
                        lastNameValue, emailValue, groupID, teacherID, confirmed, denied);
                student_register_requests.add(model).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        task.getResult().update("id", task.getResult().getId());
                        Toast.makeText(register_activity.this, "Вы успешно отправили запрос на регистрацию", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
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
                alertDialog
                .setMessage("Для начала мониторинга своих очков и баллов. Тебе следует зарегистрироваться. Выбирай свою группу и продолжай побеждать");
                alertDialog.setTitle("Ознакомление");
                alertDialog.setPositiveButton("Спасибо", positivieButtonOnClickListener);
                alertDialog.show();
                break;
            }
        }
        return true;
    }

    DialogInterface.OnClickListener positivieButtonOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

}
