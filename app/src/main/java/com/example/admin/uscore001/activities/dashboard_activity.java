package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.Settings;
import com.example.admin.uscore001.dialogs.DialogRequestAddingScore;
import com.example.admin.uscore001.dialogs.MakePenaltyDialog;
import com.example.admin.uscore001.dialogs.ShowRequestDialog;
import com.example.admin.uscore001.fragments.AskForScoreDialogFragment;
import com.example.admin.uscore001.fragments.RulesBottomSheetFragment;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.models.User;
import com.example.admin.uscore001.util.GlideApp;
import com.example.admin.uscore001.util.SocailLinksAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Главное активити
 */

public class dashboard_activity extends AppCompatActivity implements
                                        View.OnClickListener,
                                        ActionBar.OnNavigationListener{

    private static final String TAG = "dashboard_activity";

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference student$db = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");

    // Виджеты
    TextView username, requestNumber, limitScoreView;
    CardView myProfileCardView, topScoresCardView, rulesCardView, recentCardView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView notification_alarm;
    Toolbar toolbar;

    // Переменные
    static int pickedLang = 0;
    String limitScore;
    int requestCounter = 0;
    String[] socialLinks = {"VK", "WHATS UP", "TWEETER"};
    Menu menu;
    private String intentMessageDecoded;
    private String studentID;
    private String scoreString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocal();
        setContentView(R.layout.activity_dashboard);
        init();
        initToolbar();
        initActionBarDrawerToggle();
        loadKfuPictureIntoNavigationDrawerHeader();

        setCurrentUserData();

    }

    public void setCurrentUserData(){
        if(Settings.getStatus().equals(Settings.TEACHER_STATUS)){
            limitScoreView.setVisibility(View.INVISIBLE);
            requestNumber.setOnClickListener(this);
            getTeacherClass(Settings.getLogin());
        }
        else if (Settings.getStatus().equals(Settings.STUDENT_STATUS)){
            getStudentClass(Settings.getLogin());
            requestNumber.setText("");
            notification_alarm.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Инизиализация виджетов
     */

    private void init(){
        requestNumber = findViewById(R.id.requestNumber);
        notification_alarm = findViewById(R.id.notification_alarm);
        limitScoreView = findViewById(R.id.limitScore);
        navigationView = findViewById(R.id.navigationView);

        username = findViewById(R.id.username);
        username.setText(currentUser.getEmail());

        myProfileCardView = findViewById(R.id.myProfileCardView);
        myProfileCardView.setOnClickListener(this);

        topScoresCardView = findViewById(R.id.topScores);
        topScoresCardView.setOnClickListener(this);

        recentCardView = findViewById(R.id.recentCardView);
        recentCardView.setOnClickListener(this);

        rulesCardView = findViewById(R.id.rulesCardView);
        rulesCardView.setOnClickListener(this);
    }

    /**
     * Инизиализация Toolbar
     */

    public void initToolbar(){
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        SocailLinksAdapter adapter = new SocailLinksAdapter(this, android.R.layout.simple_spinner_item, socialLinks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setListNavigationCallbacks(adapter, this);
    }

    /**
     * Инизиализация ActionBarDrawerToggle
     */

    public void initActionBarDrawerToggle(){
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.open_dialog,
                R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     * Загрузка логотипа IT-Лицей КФУ в NavigationDrawerHeader
     */

    public void loadKfuPictureIntoNavigationDrawerHeader(){
        try {
            ImageView view = navigationView.getHeaderView(0).findViewById(R.id.imageView);
            GlideApp.with(this).load(R.drawable.itl).centerCrop().into(view);
        }catch (Exception e){
            Log.d(TAG, "NavigationDrawerImage: " + e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        if(Settings.getStatus().equals(Settings.STUDENT_STATUS)) {           // Ученик
            menu.findItem(R.id.scanQRCODE).setVisible(false);
            menu.findItem(R.id.askForScore).setVisible(false);
            menu.findItem(R.id.makePenalty).setVisible(false);
            menu.findItem(R.id.studentRegisterRequests).setVisible(false);
        }else if(Settings.getStatus().equals(Settings.TEACHER_STATUS)){      // Учитель
            menu.findItem(R.id.askForScore).setVisible(false);
            menu.findItem(R.id.generateQERCODE).setVisible(false);
            menu.findItem(R.id.makeRequest).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signOut:{
                // Выход с аккаунта
                User.exit();
                Intent intent = new Intent(dashboard_activity.this, login_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.askForScore:{
                AskForScoreDialogFragment askForScoreDialogFragment = new AskForScoreDialogFragment();
                askForScoreDialogFragment.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.makeRequest:{
                if(Integer.parseInt(limitScore) != 0) {
                    DialogRequestAddingScore dialogRequestAddingScore = new DialogRequestAddingScore();
                    dialogRequestAddingScore.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                }else{
                    checkSpendLimitScoreDateAndCurrentDate(menu);
                }
                break;
            }
            case R.id.generateQERCODE:{
                if(Integer.parseInt(limitScore) != 0) {
                    Intent intent = new Intent(dashboard_activity.this, QRCODE_activity.class);
                    startActivity(intent);
                }else{
                    checkSpendLimitScoreDateAndCurrentDate(menu);
                }
                break;
            }
            case R.id.scanQRCODE:{
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt(getResources().getString(R.string.scanning_process));
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();
                break;
            }
            case R.id.changeLang:{
                showChangeLangDialog();
                break;
            }
            case R.id.makePenalty:{
                MakePenaltyDialog dialog = new MakePenaltyDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.studentRegisterRequests:{
                Intent intent = new Intent(dashboard_activity.this, StudentRegisterRequestActivity.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    /**
     * Показать окно с выбором языка
     */

    public void showChangeLangDialog() {
        final String[] languages = {"Русский", "English", "中文", "Татар"};
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(dashboard_activity.this);
        alertDialog.setTitle(R.string.changeLang);
       alertDialog.setSingleChoiceItems(languages, pickedLang, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int position) {
               if(position == 0){
                   setLocal("ru");
                   pickedLang = 0;
                   recreate();
               }
               if(position == 1){
                   setLocal("en");
                   pickedLang = 1;
                   recreate();
               }
               if(position == 2){
                   setLocal("zh");
                   pickedLang = 2;
                   recreate();
               }
               if(position == 3){
                   setLocal("tt");
                   pickedLang = 3;
                   recreate();
               }
               dialog.dismiss();
           }
       });
       AlertDialog dialog = alertDialog.create();
       dialog.show();
    }

    /**
     * Установка языка
     * @param lang          // язык
     */

    public void setLocal(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
            getBaseContext().getResources().getDisplayMetrics());
        // shared data to shared preference
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    /**
     * Загрузка языка
     */

    public void loadLocal(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocal(language);
    }

    /**
     * Приобретение учительского класса со всеми данными о учителе
     * @param teacherLogin          Логин учителя
     */

    public void getTeacherClass(String teacherLogin){
        Teacher.getTeacherClass(mGetTeacherClassCallback, teacherLogin);
    }

    /**
     * Callback, вызываемый после получения учительсого класса
     */

    Callback mGetTeacherClassCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            Teacher teacher = (Teacher) data;
            String groupID = teacher.getGroupID();
            String email = teacher.getResponsible_email();
            String image_path = teacher.getImage_path();
            String positionID = teacher.getPositionID();
            String subjectID = teacher.getSubjectID();
            String statusID = teacher.getStatusID();
            String teacherRequestID = teacher.getRequestID();
            String teacherID = teacher.getId();
            String teacherFirstName = teacher.getFirstName();
            String teacherSecondName = teacher.getSecondName();
            String teacherLastName = teacher.getLastName();
            Log.d(TAG, "teacher statusID: " + statusID);
            if (image_path.isEmpty()) {
                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
            }
            SharedPreferences sharedPreferences = getSharedPreferences(Teacher.TEACHER_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Teacher.GROUP_ID, groupID);
            editor.putString(Teacher.EMAIL, email);
            editor.putString(Teacher.IMAGE_PATH, image_path);
            editor.putString(Teacher.POSITION_ID, positionID);
            editor.putString(Teacher.SUBJECT_ID, subjectID);
            editor.putString(Teacher.STATUS_ID, statusID);
            editor.putString(Teacher.TEACHER_REQUEST_ID, teacherRequestID);
            editor.putString(Teacher.TEACHER_ID, teacherID);
            editor.putString(Teacher.FIRST_NAME, teacherFirstName);
            editor.putString(Teacher.SECOND_NAME, teacherSecondName);
            editor.putString(Teacher.LAST_NAME, teacherLastName);

            /*
                --------------------------------------------------------------
                |    Проверка Данных                                         |
                --------------------------------------------------------------
             */

            Log.d(TAG, "teacherData:" +
                "firstName: " + sharedPreferences.getString(Teacher.FIRST_NAME, "") + "\n" +
                "secondName: " + sharedPreferences.getString(Teacher.SECOND_NAME, "") + "\n" +
                "lastName: " + sharedPreferences.getString(Teacher.LAST_NAME, "") + "\n" +
                "id:" + sharedPreferences.getString(Teacher.TEACHER_ID, "") + "\n" +
                "requestID: " + sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "") + "\n" +
                "statusID : " + sharedPreferences.getString(Teacher.STATUS_ID, "") + "\n" +
                "subjectID: " + sharedPreferences.getString(Teacher.SUBJECT_ID, "") + "\n" +
                "positionID: " + sharedPreferences.getString(Teacher.POSITION_ID, "") + "\n" +
                "imagePath : " + sharedPreferences.getString(Teacher.IMAGE_PATH, "") + "\n" +
                "email: " + sharedPreferences.getString(Teacher.EMAIL, "") + "\n" +
                "groupID: " + sharedPreferences.getString(Teacher.GROUP_ID, "") + "\n");

            editor.apply();

        }
    };

    public void getTeacherRequestsNumber(String teacherRequestID){
        requestCounter = 0;
        requests$DB
            .document(teacherRequestID)
            .collection("STUDENTS")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    requestCounter = 0;
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        documentSnapshot
                            .getReference()
                            .collection("REQUESTS")
                            .whereEqualTo("answered", false)
                            .whereEqualTo("canceled", false)
                            /*
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "onComplete: successful");
                                        requestCounter += task.getResult().getDocuments().size();
                                        Log.d(TAG, "onComplete: " + requestCounter);
                                        requestNumber.setText(getResources().getString(R.string.my_requests) + " " + Integer.toString(requestCounter));
                                    }else{
                                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                    }
                                }
                            });
                            */

                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    requestCounter += queryDocumentSnapshots.getDocuments().size();
                                    Log.d(TAG, "requestCounter: " + requestCounter);
                                }
                            });
                        Log.d(TAG, "student request amount of answered canceled being false: " + requestCounter);
                    }
                    requestNumber.setText(getResources().getString(R.string.my_requests) + " " + Integer.toString(requestCounter));
//                    changeNotificationAlarmImage();
                }
            });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.myProfileCardView:{
                if(Settings.getStatus().equals(getString(R.string.studentStatusValue))) {
                    Intent intent = new Intent(dashboard_activity.this, StudentProfile_activity2.class);
                    startActivity(intent);
                }else if(Settings.getStatus().equals(getString(R.string.teacherStatusValue))){
                    Intent intent = new Intent(dashboard_activity.this, TeacherProfile.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.topScores:{
                Intent intent = new Intent(dashboard_activity.this, TopScore_acivity.class);
                startActivity(intent);
                break;
            }
            case R.id.requestNumber:{
                ShowRequestDialog dialog = new ShowRequestDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.recentCardView:{
                Intent intent = new Intent(this, RecentActionsPage.class);
                startActivity(intent);
                break;
            }
            case R.id.rulesCardView:{
                Toast.makeText(this, "Это функция будет добвлена в скором времени", Toast.LENGTH_SHORT).show();
//                RulesBottomSheetFragment rulesBottomSheetFragment = new RulesBottomSheetFragment();
//                rulesBottomSheetFragment.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(data == null || requestCode == RESULT_CANCELED){
            Toast.makeText(this, getResources().getString(R.string.cancelScanProccess), Toast.LENGTH_SHORT).show();
        }
        else{
            final AlertDialog alertDialog = new AlertDialog.Builder(dashboard_activity.this).create();
            String intentMessage = result.getContents();
            try {
                intentMessageDecoded = URLDecoder.decode(intentMessage, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
//                                      wqrwrwmflawmASDs
//                                      Очки: 200
//                                      ФИО: Аникин Кирилл
//                                      Группа: 10-6

                // Очки
                String messageWithoutSpaces = intentMessageDecoded.replace(" ", "");
                int indexOfI = messageWithoutSpaces.indexOf("и");
                int indexOfF = messageWithoutSpaces.indexOf("Ф");
                Log.d(TAG, "index of i: " + indexOfI);
                Log.d(TAG, "index of F: " + indexOfF);
                scoreString = messageWithoutSpaces.substring(indexOfI+1, indexOfF);
                Log.d(TAG, "scoreString: " + scoreString.substring(1));


                // ID Ученика
                int indexOfO = messageWithoutSpaces.indexOf("О");
                Log.d(TAG, "index of O: " + indexOfO);
                studentID = messageWithoutSpaces.substring(0, indexOfO);
                Log.d(TAG, "studentID: " + studentID);

                // Очки ученика на данный момент
                int indexOfA = messageWithoutSpaces.lastIndexOf("а");
                String currentUserScore = messageWithoutSpaces.substring(indexOfA+2);
                Log.d(TAG, "currentUserScore: " + currentUserScore);


            }catch (Exception e){
                Log.d(TAG, "matching failed die to: " + e.getMessage());
                Log.d(TAG, "message: " + intentMessageDecoded);
            }

            alertDialog.setMessage(intentMessageDecoded.replace(studentID,""));
            alertDialog.setTitle(getResources().getString(R.string.confirm));
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addScoreToStudent(scoreString.substring(1).trim(), studentID);
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   alertDialog.dismiss();
                   Toast.makeText(dashboard_activity.this, getResources().getString(R.string.addingProccessCanceled)+"", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Добавление очков к ученику
     */

    public void addScoreToStudent(String requestedScore, String studentID){
        Teacher.addScoreToStudent(mAddScoreToStudent, requestedScore, studentID);
    }

    Callback mAddScoreToStudent = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String message = (String) data;
            Toast.makeText(dashboard_activity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Рейтинг ученика в своей группе
     * @param id                 // id Группы
     */

    public void rateStudentInGroup(String id){
        Student.loadGroupStudentsByGroupID(id, mRateStudentInGroupCallback);
    }

    Callback mRateStudentInGroupCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String rateStudentInGroup = params[0];
            SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Student.RATE_IN_GROUP, rateStudentInGroup);
            Log.d(TAG, "rateStudentInGroup: " + rateStudentInGroup);
            editor.apply();
        }
    };

    /**
     * Рейтинг ученика в своей школе
     */

    public void rateStudentInSchool(){
        Student.loadAllStudents(mLoadAllStudents);
    }

    /**
     * Получение всех принятых запросов на добавление очков
     * @param studentID      ID Ученика
     */

    public void getStudentConfirmedRequestsAmount(String studentID){
        Student.getConfirmedRequests(mGetStudentConfirmedRequestsAmount, studentID);
    }

    /**
     * Callback, вызываемый после получения всех принятых запроов на добавление очков
     */

    Callback mGetStudentConfirmedRequestsAmount = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<RequestAddingScore> confirmedRequests = (ArrayList) data;
            int studentConfirmedRequestsAmount = confirmedRequests.size();
            SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Student.CONFIRMED_REQUESTS_AMOUNT, studentConfirmedRequestsAmount);
            Log.d(TAG, "studentConfirmedRequestsAmount: " + studentConfirmedRequestsAmount);
            editor.apply();
        }
    };

    /**
     * Получение всех отклоненных запросов на добавление очков
     * @param studentID      // ID Ученика
     */

    public void getStudentDeniedRequestsAmount(String studentID){
        Student.getDeniedRequests(mGetStudentDeniedRequestsAmount, studentID);
    }

    /**
     * Callback, вызываемый после получения всех отклоненных запроов на добавление очков
     */

    Callback mGetStudentDeniedRequestsAmount = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<RequestAddingScore> deniedRequests = (ArrayList) data;
            int studentDeniedRequestsAmount = deniedRequests.size();
            SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Student.DENIED_REQUESTS_AMOUNT, studentDeniedRequestsAmount);
            Log.d(TAG, "studentDeniedRequestsAmount: " + studentDeniedRequestsAmount);
            editor.apply();
        }
    };

    /**
     * Приобретение класса ученика со всеми данными
     */

    public void getStudentClass(String studentLogin){
        Student.getStudentClass(mGetStudentClassCallback, studentLogin);
    }

    /**
     * Callback, вызываемый после получения класса ученика
     */

    Callback mGetStudentClassCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            Student student = (Student) data;
            String groupID = student.getGroupID();
            String firstName = student.getFirstName();
            String secondName = student.getSecondName();
            String image_path = student.getImage_path();
            if(image_path.isEmpty()){
                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
            }
            String limitScore = student.getLimitScore();
            String studentID = student.getId();
            String statusID = student.getStatusID();
            int scoreValue = student.getScore();
            String teacherID = student.getTeacherID();
            SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Student.EMAIL, Settings.getLogin());
            editor.putString(Student.GROUP_ID, groupID);
            editor.putString(Student.FIRST_NAME, firstName);
            editor.putString(Student.SECOND_NAME, secondName);
            editor.putString(Student.IMAGE_PATH, image_path);
            editor.putString(Student.LIMIT_SCORE, limitScore);
            editor.putString(Student.ID, studentID);
            editor.putString(Student.STATUS_ID, statusID);
            editor.putInt(Student.SCORE, scoreValue);
            editor.putString(Student.TEACHER_ID, teacherID);
            editor.apply();

            limitScoreView.setText("Осталось");

            if(Integer.parseInt(limitScore) == 0){
                limitScoreView.setText("В данный момент ты не можешь добавлять быллы");
            }else{
                String leftText = limitScoreView.getText().toString();
                String result = leftText + ": " + limitScore + " осталось";
                limitScoreView.setText(result);
            }

            getStudentConfirmedRequestsAmount(studentID);
            getStudentDeniedRequestsAmount(studentID);

            rateStudentInGroup(groupID);
            rateStudentInSchool();

             /*
                --------------------------------------------------------------
                |    Проверка Данных                                          |
                --------------------------------------------------------------
             */

            Log.d(TAG, "studentData:" +
                "email: " + sharedPreferences.getString(Student.EMAIL, "") + "\n" +
                "groupID: " +sharedPreferences.getString(Student.GROUP_ID, "") + "\n" +
                "firstName: " + sharedPreferences.getString(Student.FIRST_NAME, "") + "\n" +
                "secondName: " + sharedPreferences.getString(Student.SECOND_NAME, "") + "\n" +
                "lastName: " + sharedPreferences.getString(Student.LAST_NAME, "") + "\n" +
                "image_path: " + sharedPreferences.getString(Student.IMAGE_PATH, "") + "\n" +
                "limitScore: " + sharedPreferences.getString(Student.LIMIT_SCORE, "") + "\n" +
                "id: " + sharedPreferences.getString(Student.ID, "") + "\n" +
                "statusID: " + sharedPreferences.getString(Student.STATUS_ID, "") + "\n" +
                "score: " + sharedPreferences.getInt(Student.SCORE, 0) + "\n" +
                "teacherID: " + sharedPreferences.getString(Student.TEACHER_ID, "") + "\n");

        }
    };

    /**
     * Проверка на то что прошло заданное время после того как ученик потратил все свои очки на день
     * @param menu      Главное меню dashboard_activity
     */

    private void checkSpendLimitScoreDateAndCurrentDate(Menu menu){
        SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
        student$db.document(sharedPreferences.getString(Student.ID, "")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Student student = documentSnapshot.toObject(Student.class);
                // дата, когда студен потратил все свои очки
                Date studentSpendLimitScoreDate = student.getSpendLimitScoreDate().toDate();
                Log.d(TAG, "studentSpendLimitScoreDate: " + studentSpendLimitScoreDate.toString());
                // дата в данный момент по Москве
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
                Date currentDate = calendar.getTime();
                Log.d(TAG, "currentDate: " + currentDate.toString());
                // сравниваем эти две даты
                long difference = currentDate.getTime() - studentSpendLimitScoreDate.getTime();
                long seconds = difference / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                Log.d(TAG, "difference in second: " + seconds);
                Log.d(TAG, "difference in minutes: " + minutes);
                Log.d(TAG, "difference in hours: " + hours);
                Log.d(TAG, "difference in days: " + days);
                if(minutes >= 1){
                    if(Integer.parseInt(limitScore) == 0) {
                        student$db.document(sharedPreferences.getString(Student.ID, "")).update("limitScore", "15");
                    }
                }else{
                    Toast.makeText(dashboard_activity.this, "На сегодня лимит исчерпан. Приходите завтра", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Callback, после получения всех учеников школы
     */

    Callback mLoadAllStudents = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String rateStudentInSchool = params[0];
            SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Student.RATE_IN_SCHOOL, rateStudentInSchool);
            Log.d(TAG, "rateStudentInSchool: " + rateStudentInSchool);
            editor.apply();
        }
    };

}
