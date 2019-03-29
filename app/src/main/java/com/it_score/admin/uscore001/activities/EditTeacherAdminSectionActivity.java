package com.it_score.admin.uscore001.activities;

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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Group;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.Subject;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.RegisterActivityGroupAdapter;
import com.it_score.admin.uscore001.util.RequestAddingScoreAdapter;
import com.it_score.admin.uscore001.util.SubjectArrayAdapter;

import java.util.ArrayList;

/**
 * Актитвити для изменения профиля учителя админом
 */

public class EditTeacherAdminSectionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = "EditTeacherAdminSection";

    // Виджеты
    private Spinner teacherSpinner, changeGroupSpinner, changeSubjectSpinner;
    private TextView groupName, subjectName, newGroupName, newSubjectName;
    private Button submitButton;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference TEACHERS$DB = firebaseFirestore.collection("TEACHERS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_teacher_admin_section_activity);
        init();
        initActionBar();
        populateTeacherSpinner();
        populateChangeGroupSpinner();
        populateChangeSubjectSpinner();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        teacherSpinner = findViewById(R.id.teacherSpinner);
        changeGroupSpinner = findViewById(R.id.changeGroupSpinner);
        changeSubjectSpinner = findViewById(R.id.changeSubjectSpinner);
        groupName = findViewById(R.id.groupName);
        subjectName = findViewById(R.id.subjectName);
        newGroupName = findViewById(R.id.newGroupName);
        newSubjectName = findViewById(R.id.newSubjectName);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Редактирование учителя");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    /**
     * Наполнить спиннер с учителми
     */

    private void populateTeacherSpinner(){
        Teacher.loadAllTeachersClasses(mLoadAllTeachersCallback);
    }

    private Callback mLoadAllTeachersCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Teacher> teachers = (ArrayList) data;
            RequestAddingScoreAdapter pickTeacherAdapter = new RequestAddingScoreAdapter(EditTeacherAdminSectionActivity.this, android.R.layout.simple_spinner_item, teachers);
            pickTeacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            teacherSpinner.setAdapter(pickTeacherAdapter);
            teacherSpinner.setOnItemSelectedListener(EditTeacherAdminSectionActivity.this);
        }
    };

    /**
     * Наполнить спиннер с группами, выбранная группа будет помена со старой группой учителя
     */

    private void populateChangeGroupSpinner(){
        User.getAllSchoolGroups(mGetAllSchoolGroupsCallback);
    }

    private Callback mGetAllSchoolGroupsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Group> groups = (ArrayList) data;
            RegisterActivityGroupAdapter groupAdapter = new RegisterActivityGroupAdapter(getApplicationContext(), groups);
            changeGroupSpinner.setAdapter(groupAdapter);
            changeGroupSpinner.setOnItemSelectedListener(EditTeacherAdminSectionActivity.this);
        }
    };

    /**
     * Наполнить спиннер с предметами, выбранный предмет будет помена со старым предметом учителя
     */

    private void populateChangeSubjectSpinner(){
        User.getAllSubjectsList(mGetSubjectCallback);
    }

    private Callback mGetSubjectCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Subject> subjects = (ArrayList) data;
            SubjectArrayAdapter adapter = new SubjectArrayAdapter(getApplicationContext(), subjects);
            changeSubjectSpinner.setAdapter(adapter);
            changeSubjectSpinner.setOnItemSelectedListener(EditTeacherAdminSectionActivity.this);
        }
    };



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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.teacherSpinner:{
                Teacher teacher = (Teacher) parent.getSelectedItem();
                String teacherSubjectId = teacher.getSubjectID();
                String teacherGroupId = teacher.getGroupID();
                if(!teacherGroupId.trim().isEmpty()) {
                    User.getUserGroupName(mGetGroupIDCallback, teacherGroupId);
                }else{
                    groupName.setText("Нет");
                }
                if(!teacherSubjectId.trim().isEmpty()) {
                    Teacher.getSubjectValueByID(mGetSubjectNameCallback, teacherSubjectId);
                }else{
                    subjectName.setText("Нет");
                }
                break;
            }
            case R.id.changeGroupSpinner:{
                Group selectedNewGroup = (Group) parent.getSelectedItem();
                String new_group_name = selectedNewGroup.getName();
                newGroupName.setText(new_group_name);
                Log.d(TAG, "onItemSelected: " + new_group_name);
                break;
            }
            case R.id.changeSubjectSpinner:{
                Subject selectedNewSubject = (Subject) parent.getSelectedItem();
                String new_subject_name = selectedNewSubject.getName();
                newSubjectName.setText(new_subject_name);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}

    private Callback mGetGroupIDCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String selectedTeacherGroupName = (String) data;
            groupName.setText(selectedTeacherGroupName);
        }
    };

    private Callback mGetSubjectNameCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String selectedTeacherSubjectName = (String) data;
            subjectName.setText(selectedTeacherSubjectName);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitButton:{
                Teacher teacher = (Teacher) teacherSpinner.getSelectedItem();
                Subject subject = (Subject) changeSubjectSpinner.getSelectedItem();
                TEACHERS$DB.document(teacher.getId()).update("subjectID", subject.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(EditTeacherAdminSectionActivity.this,
                                    String.format("%s.%s.%s поменялся предмет на -> %s", teacher.getSecondName(), teacher.getFirstName().substring(0,1), teacher.getLastName().substring(0,1), subject.getName()),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(EditTeacherAdminSectionActivity.this, "ERROR : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
        }
    }
}
