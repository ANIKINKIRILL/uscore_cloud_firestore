package com.example.admin.uscore001.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.Penalty;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.models.User;
import com.example.admin.uscore001.util.OptionNameArrayAdapter;
import com.example.admin.uscore001.util.RegisterActivityGroupAdapter;
import com.example.admin.uscore001.util.StudentNameArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

/**
 * Активити 'Оштрафовать ученика' (Пользователь -> Учитель)
 */

public class MakePenaltyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private static final String TAG = "MakePenaltyActivity";

    // Виджеты
    private Spinner groupsPickerSpinner, studentPickerSpinner, optionSpinner;
    private TextView scoreTextView, ok, cancel, optionID, scoreInvisible;

    // Переменные
    private String selectedOption;
    private Context context;
    private String currentTeacherRequestID;
    private String currentTeacherID;
    private String studentID;
    private String groupId;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference options$DB = firebaseFirestore.collection("OPTIONS$DB");
    CollectionReference reqeusts$DB = firebaseFirestore.collection("REQEUSTS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_penalty);
        try {
            // try catch блок из-за возникновения проблемы с context`ом, так как после закрытия активити context = null
            context = getApplicationContext();
        }catch (Exception e){
            e.getMessage();
        }

        init();
        initActionBar();
        getTeacherData();
        getSchoolGroups();
        getAllPenalties();
    }

    /**
     * Инициализация виджетов
     */
    private void init(){
        groupsPickerSpinner = findViewById(R.id.groupsPickerSpinner);
        studentPickerSpinner = findViewById(R.id.studentPickerSpinner);
        optionSpinner = findViewById(R.id.optionSpinner);
        scoreTextView = findViewById(R.id.scoreTextView);
        optionID = findViewById(R.id.optionID);
        ok = findViewById(R.id.ok);
        cancel = findViewById(R.id.cancel);
        scoreInvisible = findViewById(R.id.scoreInvisible);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Отправить запрос");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
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
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:{
                finish();
                break;
            }
            case R.id.ok:{
                if(!scoreTextView.getText().toString().trim().isEmpty()){
                    // Внесение данных/наказания в БД
                    addPenaltyToHistory(optionID.getText().toString(), groupId, studentID, scoreInvisible.getText().toString());
                    // Понижения баллов
                    decreaseStudentScore(studentID, Integer.parseInt(scoreInvisible.getText().toString()));
                }
                break;
            }
        }
    }

    /**
     * Получение requestID учителя
     * id учителя
     */

    private void getTeacherData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
        currentTeacherRequestID = sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "");
        currentTeacherID = sharedPreferences.getString(Teacher.TEACHER_ID, "");
    }

    /**
     * Получить список всех групп со школы
     */

    private void getSchoolGroups(){
        User.getAllSchoolGroups(mGetAllSchoolGroupsCallback);
    }

    private Callback mGetAllSchoolGroupsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            populateGroupSpinner((ArrayList<Group>) data);
        }
    };

    /**
     * Заполнение адаптера с выбором групп
     */

    private void populateGroupSpinner(ArrayList<Group> groups){
        // try catch блок из-за возникновения проблемы с context`ом, так как после закрытия активити context = null
        try {
            RegisterActivityGroupAdapter groupAdapter = new RegisterActivityGroupAdapter(context, groups);
            groupsPickerSpinner.setAdapter(groupAdapter);
            groupsPickerSpinner.setOnItemSelectedListener(this);
        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * Получить список наказаний
     */

    private void getAllPenalties(){
        User.getAllPenaltiesList(mGetAllPenalties);
    }

    private Callback mGetAllPenalties = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Option> penalties = (ArrayList) data;
            populatePenaltySpinner(penalties);
        }
    };

    /**
     * Заполнение адаптера с выбором наказаний
     */

    private void populatePenaltySpinner(ArrayList<Option> penalties){
        // try catch блок из-за возникновения проблемы с context`ом, так как после закрытия активити context = null
        try {
            /*
            ArrayAdapter<CharSequence> pickOptionAdapter = ArrayAdapter.createFromResource(context, R.array.defaultPenalty, android.R.layout.simple_spinner_item);
            pickOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            optionSpinner.setAdapter(pickOptionAdapter);
            optionSpinner.setOnItemSelectedListener(this);
            */
            OptionNameArrayAdapter adapter = new OptionNameArrayAdapter(this, penalties);
            optionSpinner.setAdapter(adapter);
            optionSpinner.setOnItemSelectedListener(this);
        }catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            // пользователь нажал на спиннер с выбором групп
            case R.id.groupsPickerSpinner:{
                Group group = (Group) adapterView.getSelectedItem();
                groupId = group.getId();
                Student.loadGroupStudentsByGroupID(groupId, null, mGetAllGroupStudents);
                break;
            }
            // пользователь нажал на спиннер с выбором наказаний
            case R.id.optionSpinner:{
                Option penalty = (Option) adapterView.getSelectedItem();
                selectedOption = penalty.getName();
                scoreTextView.setText("Штраф: " + penalty.getPoints());
                scoreInvisible.setText(penalty.getPoints());
                optionID.setText(penalty.getId());
                Toast.makeText(this, selectedOption, Toast.LENGTH_SHORT).show();
                break;
            }
            // пользователь нажал на спиннер с выбором ученика
            case R.id.studentPickerSpinner:{
                Student student = (Student) adapterView.getSelectedItem();
                studentID = student.getId();
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){}

    private Callback mGetAllGroupStudents = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Student> selectedGroupStudentsList = (ArrayList) data;
            StudentNameArrayAdapter adapter = new StudentNameArrayAdapter(context, selectedGroupStudentsList);
            studentPickerSpinner.setAdapter(adapter);
            studentPickerSpinner.setOnItemSelectedListener(MakePenaltyActivity.this);
        }
    };

    /**
     * Внесение данных/наказания в БД
     * @param optionID      id наказания
     * @param groupID       id группы ученика
     * @param studentID     id ученика
     * @param score         очки наказания
     */

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
                        HashMap<String, String> idMap = new HashMap<>();
                        idMap.put("id", studentID);
                        reqeusts$DB.document(currentTeacherRequestID).collection("STUDENTS").document(studentID).set(idMap,  SetOptions.merge());
                    }
                }
            });
    }

    /**
     * Оштрафовать ученика
     * @param studentID         id ученика
     * @param scoreToDecrease   очки на которые учитель штрафует учинка
     */

    private void decreaseStudentScore(String studentID, int scoreToDecrease){
        Teacher.decreaseStudentScore(studentID, scoreToDecrease);
        Toast.makeText(context, "Вы оштрафовали ученика. Для более детальной инфармации смотреть в своей истории", Toast.LENGTH_SHORT).show();
        finish();
    }
}
