package com.it_score.admin.uscore001.activities;

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
import com.it_score.admin.uscore001.models.Subject;

/**
 * Активити с функцией админа 'Добвить предмет'
 */

public class AddSubjectFunctionAdminSectionActivity extends AppCompatActivity implements View.OnClickListener {

    // Виджеты
    private EditText subjectName;
    private Button createSubjectButton;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference SUBJECTS$DB = firebaseFirestore.collection("SUBJECTS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject_function_admin_section);
        init();
        initActionBar();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        subjectName = findViewById(R.id.subjectName);
        createSubjectButton = findViewById(R.id.createSubjectButton);
        createSubjectButton.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Добавить предмет");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createSubjectButton:{
                // Извлекаем данные с edittext
                String subjectNameInput = subjectName.getText().toString();
                // Проверка на првильный ввод
                if(!subjectNameInput.trim().isEmpty()) {
                    // Создание предмета в бд
                    Subject new_subject = new Subject("", subjectNameInput);
                    SUBJECTS$DB.add(new_subject).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getId();
                                task.getResult().update("id", id);
                                subjectName.setText("");
                                Toast.makeText(AddSubjectFunctionAdminSectionActivity.this,
                                        "Предмет: " + subjectNameInput + " добавлен",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(subjectName);
                    subjectName.setError("Поле должно быть заполнено");
                    subjectName.requestFocus();
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
