package com.it_score.admin.uscore001.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Group;

/**
 * Панель Администратора с добавлением группы
 */

public class AdminSectionActivity extends AppCompatActivity implements View.OnClickListener {

    // Виджеты
    private EditText groupName, teacherFullName;
    private Button createGroupButton, addTeacherButton;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference GROUPS$DB = firebaseFirestore.collection("GROUPS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_section_activity);
        init();
        initActionBar();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        groupName = findViewById(R.id.groupName);
        teacherFullName = findViewById(R.id.teacherFullName);
        createGroupButton = findViewById(R.id.createGroupButton);
        addTeacherButton = findViewById(R.id.addTeacherButton);

        createGroupButton.setOnClickListener(this);
        addTeacherButton.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Админка");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createGroupButton:{
                // Извлекаем данные с edittext
                String groupNameInput = groupName.getText().toString();
                String teacherFullNameInput = teacherFullName.getText().toString();
                // Проверка на првильный ввод
                if(!groupNameInput.trim().isEmpty()) {
                    // Создание группы в бд
                    Group new_group = new Group("", groupNameInput, "");
                    GROUPS$DB.add(new_group).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getId();
                                task.getResult().update("id", id);
                                groupName.setText("");
                                teacherFullName.setText("");
                                Toast.makeText(AdminSectionActivity.this,
                                        "Группа: " + groupNameInput + " добавлена",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(groupName);
                    groupName.setError("Поле должно быть заполнено");
                    groupName.requestFocus();
                }
                break;
            }
            case R.id.addTeacherButton:{
                Intent intent = new Intent(this, AddTeacherAdminSectionActivity.class);
                startActivity(intent);
                break;
            }
        }
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
