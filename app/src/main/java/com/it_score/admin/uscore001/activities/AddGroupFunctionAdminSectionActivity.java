package com.it_score.admin.uscore001.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Group;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.GroupListRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Активити с функцией админа 'Добвить группу/класс'
 */

public class AddGroupFunctionAdminSectionActivity extends AppCompatActivity implements View.OnClickListener {

    // Виджеты
    private EditText groupName;
    private Button createGroupButton;
    private RecyclerView groupList;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference GROUPS$DB = firebaseFirestore.collection("GROUPS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group_function_admin_section_activity);
        init();
        initActionBar();
        populateGroupList();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        groupName = findViewById(R.id.groupName);
        createGroupButton = findViewById(R.id.createGroupButton);
        groupList = findViewById(R.id.groupList);
        createGroupButton.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Добавить группу");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    /**
     * Наполнить список групп
     */

    private void populateGroupList(){
        User.getAllSchoolGroups(mGetAllSchoolGroupCallback);
    }

    private Callback mGetAllSchoolGroupCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Group> groups = (ArrayList)data;
            GroupListRecyclerViewAdapter adapter = new GroupListRecyclerViewAdapter(groups);
            groupList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            groupList.setAdapter(adapter);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createGroupButton:{
                // Извлекаем данные с edittext
                String groupNameInput = groupName.getText().toString();
                // Проверка на првильный ввод
                if(!groupNameInput.trim().isEmpty() && groupNameInput.trim().contains("-")) {
                    // Создание группы в бд
                    Group new_group = new Group("", groupNameInput, "");
                    GROUPS$DB.add(new_group).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getId();
                                task.getResult().update("id", id);
                                groupName.setText("");
                                Toast.makeText(AddGroupFunctionAdminSectionActivity.this,
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
