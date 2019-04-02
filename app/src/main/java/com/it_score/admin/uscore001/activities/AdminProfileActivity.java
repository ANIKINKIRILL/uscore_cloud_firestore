package com.it_score.admin.uscore001.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.dialogs.AdminSettingsDialog;
import com.it_score.admin.uscore001.dialogs.ImageDialog;
import com.it_score.admin.uscore001.dialogs.TeacherSettingsDialog;
import com.it_score.admin.uscore001.models.Admin;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.util.GlideApp;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Актитвити с профилем админа
 */

public class AdminProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // widgets
    CircleImageView imageView;
    TextView usernameView, status, positionView, emailView, roomNumber;
    Button profileSettings;

    // vars
    String email;
    private String lastName;
    private String firstName;
    private String teacherImagePath;
    private String secondName;
    private int roomNumberValue;
    private String realEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_profile_activity);
        getTeacherData();
        init();
        initActionBar();
        setTeacherData(teacherImagePath, firstName, secondName, lastName, email);
    }

    /**
     * Инициализация
     */

    private void init(){
        imageView = findViewById(R.id.imageView);
        emailView = findViewById(R.id.emailAddress);
        usernameView = findViewById(R.id.username);
        status = findViewById(R.id.status);
        positionView = findViewById(R.id.position);
        profileSettings = findViewById(R.id.profileSettings);
        roomNumber = findViewById(R.id.subject);
        imageView.setOnClickListener(this);
        profileSettings.setOnClickListener(this);
    }

    /**
     * Инициализация ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Мой профиль");
    }

    /**
     * Извлечение данных учителя из SharedPreferences
     */

    private void getTeacherData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Admin.ADMIN_DATA, MODE_PRIVATE);
        teacherImagePath = sharedPreferences.getString(Admin.ADMIN_IMAGE_PATH, "");
        firstName = sharedPreferences.getString(Admin.ADMIN_FIRST_NAME, "");
        secondName = sharedPreferences.getString(Admin.ADMIN_SECOND_NAME, "");
        lastName = sharedPreferences.getString(Admin.ADMIN_LAST_NAME, "");
        email = sharedPreferences.getString(Admin.ADMIN_EMAIL, "");
        realEmail = sharedPreferences.getString(Admin.ADMIN_REAL_EMAIL, "");
        roomNumberValue = sharedPreferences.getInt(Admin.ADMIN_ROOM_NUMBER, 0);
    }

    /**
     * Загрузка данных учителя в виджеты
     *
     * @param teacherImagePath  Аватарка
     * @param firstName         Имя
     * @param lastName          Отчество
     * @param email             Почта
     */

    public void setTeacherData(String teacherImagePath, String firstName, String secondName, String lastName, String email){
        GlideApp.with(this).load(teacherImagePath).centerCrop().into(imageView);
        usernameView.setText(String.format("%s %s %s", secondName, firstName, lastName));
        positionView.setText("Администрация");
        roomNumber.setText(String.format("Кабинет: %s", Integer.toString(roomNumberValue)));
        if(realEmail.equals("")){
            emailView.setText("Почта отсутствует");
        }else {
            emailView.setText(realEmail);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:{
                ImageDialog dialog = new ImageDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.profileSettings:{
                AdminSettingsDialog dialog = new AdminSettingsDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
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
