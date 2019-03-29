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
import com.it_score.admin.uscore001.fragments.RulesBottomSheetFragment;
import com.it_score.admin.uscore001.models.Option;

/**
 * Актитвити для добавления поощрений и наказаний (Пользователь -> Админ)
 */

public class AddOptionActivity extends AppCompatActivity implements View.OnClickListener {

    // Виджеты
    private EditText optionName, optionScore;
    private Button addOptionButton, addPenaltyButton, releaseButton;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference OPTIONS$DB = firebaseFirestore.collection("OPTIONS$DB");

    // Постоянные переменные
    public static final String POSITIVE_OPTION = "a31J0nT0lYTRmvyp7T8F";            // Поощрения
    public static final String NEGATIVE_OPTION = "6oemB2Fxo1hyrWrrNQ07";            // Наказания

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_option_activity);
        init();
        initActionBar();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        optionName = findViewById(R.id.optionName);
        optionScore = findViewById(R.id.optionScore);
        addOptionButton = findViewById(R.id.addOptionButton);
        addPenaltyButton = findViewById(R.id.addPenaltyButton);
        releaseButton = findViewById(R.id.releaseButton);

        addOptionButton.setOnClickListener(this);
        addPenaltyButton.setOnClickListener(this);
        releaseButton.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Добавить поощрения, наказания");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    /**
     * Проверка на ошибки ввода данных
     */

    private boolean isValid(){
        if(optionName.getText().toString().trim().isEmpty()){
            return false;
        }
        if(optionScore.getText().toString().trim().isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addOptionButton:{
                if(isValid()){
                    Option new_option = new Option("", optionScore.getText().toString(), optionName.getText().toString());
                    OPTIONS$DB.document(POSITIVE_OPTION).collection("options").add(new_option).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                String id = task.getResult().getId();
                                task.getResult().update("id", id);
                                clearInputFields();
                                Toast.makeText(AddOptionActivity.this, "Поощрение добавлено", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    YoYo.with(Techniques.Shake).repeat(0).duration(1000).playOn(addOptionButton);
                    Toast.makeText(this, "Поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.addPenaltyButton:{
                if(isValid()){
                    Option new_option = new Option("", optionScore.getText().toString(), optionName.getText().toString());
                    OPTIONS$DB.document(NEGATIVE_OPTION).collection("options").add(new_option).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                String id = task.getResult().getId();
                                task.getResult().update("id", id);
                                clearInputFields();
                                Toast.makeText(AddOptionActivity.this, "Наказание добавлено", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    YoYo.with(Techniques.Shake).repeat(0).duration(1000).playOn(addPenaltyButton);
                    Toast.makeText(this, "Поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.releaseButton:{
                RulesBottomSheetFragment rulesBottomSheetFragment = new RulesBottomSheetFragment();
                rulesBottomSheetFragment.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
        }
    }

    /**
     * Отчистка полей ввода
     */

    private void clearInputFields(){
        optionScore.setText("");
        optionName.setText("");
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
