package com.it_score.admin.uscore001.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.dialogs.ChangePasswordDialog;
import com.it_score.admin.uscore001.models.Student;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Активити ученика (Одноклассник, ученик с параллели и тд)  -> !Мой Профиль
 */

public class StudentDetailPage extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "StudentDetailPage";

    // Виджеты
    CircleImageView circleImageView;
    TextView emailAddress, score, group, username;
    TextView rateInGroup, rateInSchool, status;
    Button addCommentButton;
    TextView showAllComments;


    // Переменные
    String intentEmail;
    private String intentImageView;
    private String intentGroup;
    private String intentUsername;
    private String intentScore;
    private String intentGroupID;
    private String intentStudentID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile2);
        init();
        initActionBar();
        getDetailData();
        setStudentDetailData();
        getStudentRating();

    }

    /**
     * Рейтинг ученика в группе и школе
     */

    private void getStudentRating(){
        // рейтинг ученика в группы
        Student.loadGroupStudentsByGroupID(intentGroupID, intentStudentID, mGetGroupRating);
        // рейтинг ученика в школе
        Student.loadAllStudents(mGetSchoolRating, intentStudentID);
    }

    Callback mGetGroupRating = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String rateStudentInGroup = params[0];
            rateInGroup.setText(rateStudentInGroup);
        }
    };

    Callback mGetSchoolRating = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String rateStudentInSchool = params[0];
            rateInSchool.setText(rateStudentInSchool);
        }
    };

    /**
     * Инициалицазия виджетов
     */

    private void init(){
        circleImageView = findViewById(R.id.imageView);
        emailAddress = findViewById(R.id.emailAddress);
        score = findViewById(R.id.score);
        group = findViewById(R.id.group);
        username = findViewById(R.id.username);
        rateInGroup = findViewById(R.id.rateInGroup);
        rateInSchool = findViewById(R.id.rateInSchool);
        addCommentButton = findViewById(R.id.addCommentButton);
        status = findViewById(R.id.status);
        showAllComments = findViewById(R.id.showAllComments);

        addCommentButton.setOnClickListener(this);
        showAllComments.setOnClickListener(this);
    }

    /**
     * Инициалицазия ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Профиль ученика");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    /**
     * Получение данных ученика
     */

    private void getDetailData(){
        Bundle intent = getIntent().getExtras();
        intentImageView = intent.getString(getString(R.string.intentImage));
        intentUsername = intent.getString(getString(R.string.intentUsername));
        intentScore = intent.getString(getString(R.string.intentScore));
        intentGroup = intent.getString(getString(R.string.intentGroup));
        intentEmail = intent.getString(getString(R.string.intentEmail));
        intentGroupID = intent.getString(getString(R.string.intentGroupID));
        intentStudentID = intent.getString("intentStudentID");
        /*
                TEST DATA
         */

        Log.d(TAG, "getDetailData: " +
                        "image: " + intentImageView + "\n" +
                        "username: " + intentUsername + "\n" +
                        "score: " + intentScore + "\n" +
                        "group: " + intentGroup + "\n" +
                        "email: " + intentEmail + "\n" +
                        "groupID" + intentGroupID + "\n" +
                        "id: " + intentStudentID + "\n");

    }

    /**
     * Установка данных просматриваемого ученика
     */

    private void setStudentDetailData(){
        if(intentImageView.isEmpty()) {
            Glide.with(getApplicationContext()).load("https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png").into(circleImageView);
        }else {
            Glide.with(getApplicationContext()).load(intentImageView).into(circleImageView);
        }
        emailAddress.setText(intentEmail);
        username.setText(intentUsername);
        score.setText(intentScore + " (Очков)");
        group.setText(intentGroup + " (Группа)");
        status.setText("Ученик");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addCommentButton:{
                Intent intent = new Intent(StudentDetailPage.this, CommentsPage.class);
                intent.putExtra("to_whom_send_email", emailAddress.getText().toString());
                startActivity(intent);
                break;
            }
            case R.id.showAllComments:{
                Intent intent = new Intent(StudentDetailPage.this, CommentsPage.class);
                intent.putExtra("to_whom_send_email", emailAddress.getText().toString());
                startActivity(intent);
                break;
            }
        }
    }
}
