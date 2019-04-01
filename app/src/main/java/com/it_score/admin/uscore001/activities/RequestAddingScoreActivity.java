package com.it_score.admin.uscore001.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Option;
import com.it_score.admin.uscore001.models.RequestAddingScore;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.EncouragementOptionArrayAdapter;
import com.it_score.admin.uscore001.util.RequestAddingScoreAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Окно для отправления заявки на добваление очков учителю (Пользователь -> ученик)
 */

public class RequestAddingScoreActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener{

    private static final String TAG = "DialogRequestAddingScor";

    // Виджеты
    Spinner teacherSpinner, options;
    EditText requestBody;
    TextView ok, cancel, score, scoreInvisible;
    RelativeLayout dialogLayout;
    ProgressDialog progressDialog;

    // Переменные
    String teacherName;
    String selectedOption;
    String addedDate;
    private String optionID;
    private String teacherRequestID;
    private static int counter = 0;
    private String studentLimit;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_adding_score_activity);
        init();
        initActionBar();
        setDateFormat();
        populateTeachersSpinner();
        populateOptionsSpinner();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        dialogLayout = findViewById(R.id.dialogLayout);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        options = findViewById(R.id.options);
        ok = findViewById(R.id.ok);
        cancel = findViewById(R.id.cancel);
        requestBody =findViewById(R.id.requestBody);
        score = findViewById(R.id.score);
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

    private void setDateFormat(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        addedDate = simpleDateFormat.format(new Date());
    }

    /**
     * Выгрузка всех учителей
     */

    private void populateTeachersSpinner(){
        Teacher.loadAllTeachersClasses(new Callback() {
            @Override
            public void execute(Object data, String... params) {
                ArrayList<Teacher> teachers = (ArrayList<Teacher>) data;
                RequestAddingScoreAdapter pickTeacherAdapter = new RequestAddingScoreAdapter(RequestAddingScoreActivity.this, android.R.layout.simple_spinner_item, teachers);
                pickTeacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                teacherSpinner.setAdapter(pickTeacherAdapter);
                teacherSpinner.setOnItemSelectedListener(RequestAddingScoreActivity.this);
            }
        });
    }

    /**
     * Наполнение опций в спиннер
     */

    private void populateOptionsSpinner(){
        User.getAllEncouragementsList(mGetAllEncouragementsCallback);
    }

    private Callback mGetAllEncouragementsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Option> encouragementsOptions = (ArrayList) data;
            EncouragementOptionArrayAdapter adapter = new EncouragementOptionArrayAdapter(RequestAddingScoreActivity.this, encouragementsOptions);
            options.setAdapter(adapter);
            options.setOnItemSelectedListener(RequestAddingScoreActivity.this);
        }
    };

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
        String currentUserGroupID = sharedPreferences.getString(Student.GROUP_ID, "");
        String currentStudentID = sharedPreferences.getString(Student.ID, "");
        String firstName = sharedPreferences.getString(Student.FIRST_NAME, "");
        String secondName = sharedPreferences.getString(Student.SECOND_NAME, "");
        String senderImage = sharedPreferences.getString(Student.IMAGE_PATH, "");
        studentLimit = sharedPreferences.getString(Student.LIMIT_SCORE, "");
        switch (v.getId()) {
            case R.id.ok: {
                if(Integer.parseInt(studentLimit) != 0) {
                    counter = 0;
                    String id = "";
                    String body = requestBody.getText().toString();
                    String date = addedDate;
                    String getter = teacherName;
                    String image_path = senderImage;
                    String senderEmail = currentUser.getEmail();
                    int scoreValue = Integer.parseInt(scoreInvisible.getText().toString());
                    String groupID = currentUserGroupID;
                    String requestID = teacherRequestID;
                    String option = optionID;
                    boolean answered = false;
                    boolean canceled = false;
                    String senderID = currentStudentID;
                    progressDialog = new ProgressDialog(RequestAddingScoreActivity.this);
                    progressDialog.setMessage("Отправляем запрос учителю...");
                    progressDialog.show();
                    // Уменьшаем баллы, которые даются на день
                    decreaseLimitScore(currentStudentID);
                    // Отправляем запрос
                    sendRequest(
                            id, body, date, getter, image_path, senderEmail,
                            firstName, secondName, "", scoreValue, groupID,
                            requestID, option, answered, canceled, currentStudentID
                    );
                }else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RequestAddingScoreActivity.this);
                    alertDialog.setTitle("Удаленный запрос учителю");
                    alertDialog.setMessage("Превышен лимит на удаленные запросы, Вы можете обратится к учителю с QR-кодом");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(RequestAddingScoreActivity.this, QRCODE_activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("ПОЗЖЕ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(RequestAddingScoreActivity.this, dashboard_activity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    alertDialog.show();
                }
                break;
            }
            case R.id.cancel: {
                finish();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.teacherSpinner:{
                Teacher teacher = (Teacher) parent.getSelectedItem();
                teacherRequestID = teacher.getRequestID();
                Log.d(TAG, teacher.getFirstName()+" "+teacher.getLastName() + " id: " + teacherRequestID);
                teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                break;
            }
            case R.id.options:{
                /*
                selectedOption = parent.getItemAtPosition(position).toString();
                getSelectedOptionScoreAndID(selectedOption);
                Toast.makeText(getApplicationContext(), selectedOption, Toast.LENGTH_LONG).show();
                */

                Option option = (Option) parent.getSelectedItem();
                selectedOption = option.getName();
                score.setText("Баллы: " + option.getPoints());
                scoreInvisible.setText(option.getPoints());
                optionID = option.getId();
                break;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Получить данные опции
     * @param optionName        название опции
     */

    private void getSelectedOptionScoreAndID(String optionName){
        User.getOptionData(new Callback() {
            @Override
            public void execute(Object data, String... params) {
                optionID = (String) data;
                String optionScore = params[0];
                score.setText("Баллы: " + optionScore);
                scoreInvisible.setText(optionScore);
            }
        }, optionName);
    }

    /**
     * Отправить запрос учителю на добавление очков
     *
     * @param id            id запроса
     * @param body          текст запроса
     * @param date          дата запроса
     * @param getter        фио учителя к кому был этот запрос отпрален
     * @param image_path    приклепленная фотография
     * @param senderEmail   email ученика
     * @param firstName     имя
     * @param secondName    фамилия
     * @param lastName      отчество
     * @param score         очки
     * @param groupID       id группы
     * @param requestID     requestID учителя
     * @param optionID      id опции за что ученик хочет чтобы ему начислели быллы
     * @param answered      принят ли запрос
     * @param canceled      отклонен ли запрос
     * @param senderID      id ученика
     */

    public void sendRequest(
            String id, String body, String date, String getter, String image_path, String senderEmail,
            String firstName, String secondName, String lastName, int score, String groupID,
            String requestID, String optionID, boolean answered, boolean canceled, String senderID)
    {
        RequestAddingScore request = new RequestAddingScore(
                id, body, date, getter, image_path, senderEmail,
                firstName, secondName, lastName, score, groupID,
                requestID, optionID, answered, canceled, senderID);
        Student.sendRequest(request, mSendRequestCallback);
    }

    private Callback mSendRequestCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            progressDialog.dismiss();
            // Иногда происходит краш, оборачиваем в try/catch
            try {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RequestAddingScoreActivity.this);
                alertDialog.setTitle("Запрос учителю");
                alertDialog.setMessage("Ваш запрос отправлен успешно, теперь можно посмотреть запрос в 'Недавниe'");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RequestAddingScoreActivity.this, RecentActionsPage.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("ГЛАВНОЕ МЕНЮ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RequestAddingScoreActivity.this, dashboard_activity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNeutralButton("ПРОДОЛЖИТЬ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /**
     * Вычитание баллов на день за отправку запроса учителю
     * @param studentId                 id ученика
     */

    public void decreaseLimitScore(String studentId){
        Teacher.decreaseStudentLimitScore(null, studentId);
    }

}
