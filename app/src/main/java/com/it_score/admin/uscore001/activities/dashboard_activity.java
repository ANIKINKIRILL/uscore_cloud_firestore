package com.it_score.admin.uscore001.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.dialogs.ChangePasswordDialog;
import com.it_score.admin.uscore001.dialogs.ShowRequestDialog;
import com.it_score.admin.uscore001.fragments.RulesBottomSheetFragment;
import com.it_score.admin.uscore001.models.Admin;
import com.it_score.admin.uscore001.models.RequestAddingScore;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.GlideApp;
import com.it_score.admin.uscore001.util.SocailLinksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Главное активити
 */

public class dashboard_activity extends AppCompatActivity implements
                                        View.OnClickListener,
                                        ActionBar.OnNavigationListener, android.support.v7.app.ActionBar.OnNavigationListener,
        NavigationView.OnNavigationItemSelectedListener{

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
    CircleImageView userAvatar;
    android.support.v7.widget.Toolbar toolbar;

    // Переменные
    static int pickedLang = 0;
    String limitScore;
    int requestCounter = 0;
    String[] socialLinks = {"VK", "WHATS UP", "TWEETER"};
    Menu menu;
    private String intentMessageDecoded;
    private String studentID;
    private String scoreString;
    private String currentUserScore;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";
    public static final String ADMIN_STATUS = "26gmBm7N0oUVupLktAg6";

    static FirebaseFirestore firebaseFirestore2 = FirebaseFirestore.getInstance();
    static CollectionReference STUDENTS$DB = firebaseFirestore2.collection("STUDENTS$DB");

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
        if(Settings.getStatus().equals(Settings.TEACHER_STATUS) || Settings.getStatus().equals(Settings.TEACHER_HELPER_STATUS)){
            limitScoreView.setVisibility(View.INVISIBLE);
            requestNumber.setOnClickListener(this);
            getTeacherClass(Settings.getLogin());
        }
        else if (Settings.getStatus().equals(Settings.STUDENT_STATUS)){
            getStudentClass(Settings.getLogin());
            requestNumber.setText("");
            notification_alarm.setVisibility(View.INVISIBLE);
        }
        if(Settings.getStatus().trim().equals(ADMIN_STATUS)){
            getAdminClass(Settings.getLogin());
            limitScoreView.setVisibility(View.INVISIBLE);
            requestNumber.setText("Администратор");
            notification_alarm.setVisibility(View.INVISIBLE);
            TextView recentCardViewViewTitle = recentCardView.findViewById(R.id.recentTitle);
            ImageView recentCardViewImage = recentCardView.findViewById(R.id.recentCardViewImage);
            recentCardViewViewTitle.setText("Панель Администратора");
            recentCardViewImage.setVisibility(View.GONE);
        }
    }

    /**
     * Инизиализация виджетов
     */

    private void init(){
        userAvatar = findViewById(R.id.userAvatar);
        requestNumber = findViewById(R.id.requestNumber);
        notification_alarm = findViewById(R.id.notification_alarm);
        limitScoreView = findViewById(R.id.limitScore);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(this);

        username = findViewById(R.id.username);
        // username.setText(currentUser.getEmail());

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
            GlideApp.with(this).load(R.drawable.itl).fitCenter().into(view);
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
            menu.findItem(R.id.makePenalty).setVisible(false);
            menu.findItem(R.id.studentRegisterRequests).setVisible(false);
            menu.findItem(R.id.changeLang).setVisible(false);
        }else if(Settings.getStatus().equals(Settings.TEACHER_STATUS)){      // Учитель
            menu.findItem(R.id.generateQERCODE).setVisible(false);
            menu.findItem(R.id.makeRequest).setVisible(false);
            menu.findItem(R.id.testChangePassword).setVisible(false);
            menu.findItem(R.id.changeLang).setVisible(false);
        }
        if(Settings.getStatus().trim().equals(ADMIN_STATUS)){         // Админ
            menu.findItem(R.id.scanQRCODE).setVisible(false);
            menu.findItem(R.id.generateQERCODE).setVisible(false);
            menu.findItem(R.id.makeRequest).setVisible(false);
            menu.findItem(R.id.makePenalty).setVisible(false);
            menu.findItem(R.id.studentRegisterRequests).setVisible(false);
            menu.findItem(R.id.changeLang).setVisible(false);
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
            case R.id.makeRequest:{
                if(Integer.parseInt(limitScore) != 0) {
                    /*
                    DialogRequestAddingScore dialogRequestAddingScore = new DialogRequestAddingScore();
                    dialogRequestAddingScore.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                    */
                    Intent intent = new Intent(this, RequestAddingScoreActivity.class);
                    startActivity(intent);
                }else{
                    checkSpendLimitScoreDateAndCurrentDate(menu);
                }
                break;
            }
            case R.id.generateQERCODE:{
                if(Integer.parseInt(limitScore) != 0) {
                    Intent intent = new Intent(dashboard_activity.this, QRCODE_activity.class);
                    startActivity(intent);
                }
                else{
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
                /*
                MakePenaltyDialog dialog = new MakePenaltyDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                */

                Intent intent = new Intent(this, MakePenaltyActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.studentRegisterRequests:{
                Intent intent = new Intent(dashboard_activity.this, StudentRegisterRequestActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.testChangePassword:{
                ChangePasswordDialog dialog = new ChangePasswordDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
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
            }else{
                GlideApp.with(dashboard_activity.this).load(image_path).centerCrop().into(userAvatar);
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

            SharedPreferences sharedPreferencesSettings = getSharedPreferences(Settings.SETTINGS, MODE_PRIVATE);
            SharedPreferences.Editor editorSettings = sharedPreferencesSettings.edit();
            editorSettings.putString(Settings.USER_ID, teacherID);
            editorSettings.apply();

            username.setText(teacherSecondName + "." + teacherFirstName.substring(0,1)+ "." + teacherLastName.substring(0,1));

            User.getUserGroupName(mGetUserGroupNameCallback, groupID);

//            getTeacherRequestsNumber(teacherRequestID);

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

    /**
     * Поулчить класс адимина
     * @param adminLogin        логин админа
     */

    public void getAdminClass(String adminLogin){
        User.getAdminClass(mGetAdminClassCallback, adminLogin);
    }

    private Callback mGetAdminClassCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            Admin admin = (Admin) data;
            String id = admin.getId();
            String firstName = admin.getFirstName();
            String secondName = admin.getSecondName();
            String lastName = admin.getLastName();
            String image_path = admin.getImage_path();
            String positionID = admin.getPositionID();
            String responsible_email = admin.getResponsible_email();
            String statusID = admin.getStatusID();
            int roomNumber = admin.getRoomNumber();
            if (image_path.isEmpty()) {
                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
            }else{
                GlideApp.with(dashboard_activity.this).load(image_path).centerCrop().into(userAvatar);
            }

            SharedPreferences sharedPreferencesSettings = getSharedPreferences(Settings.SETTINGS, MODE_PRIVATE);
            SharedPreferences.Editor editorSettings = sharedPreferencesSettings.edit();
            editorSettings.putString(Settings.USER_ID, id);
            editorSettings.apply();

            Admin.setAdminIdSharedPreference(id);
            Admin.setFirstNameSharedPreference(firstName);
            Admin.setSecondNameSharedPreference(secondName);
            Admin.setLastNameSharedPreference(lastName);
            Admin.setImage_pathSharedPreference(image_path);
            Admin.setPositionIDSharedPreference(positionID);
            Admin.setResponsible_emailSharedPreference(responsible_email);
            Admin.setStatusIDSharedPreference(statusID);
            Admin.setAdminRoomNumber(roomNumber);

            username.setText(secondName + "." + firstName.substring(0,1)+ "." + lastName.substring(0,1));

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
//                    requestNumber.setText(getResources().getString(R.string.my_requests) + " " + Integer.toString(requestCounter));
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
                if(Settings.getStatus().equals(getString(R.string.adminStatusValue))){
                    Intent intent = new Intent(dashboard_activity.this, AdminProfileActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.topScores:{
                Intent intent = new Intent(dashboard_activity.this, TopScore_Activity.class);
                startActivity(intent);
                break;
            }
            case R.id.requestNumber:{
                ShowRequestDialog dialog = new ShowRequestDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.recentCardView:{
                if(Settings.getStatus().equals(getString(R.string.adminStatusValue))){          // Админ
                    Intent intent = new Intent(this, AdminSectionActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(this, RecentActionsPage.class);   // Учитель или Ученик
                    startActivity(intent);
                }
                break;
            }
            case R.id.rulesCardView:{
                RulesBottomSheetFragment rulesBottomSheetFragment = new RulesBottomSheetFragment();
                rulesBottomSheetFragment.show(getSupportFragmentManager(), getString(R.string.open_dialog));
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
                Log.d(TAG, "intentMessageDecoded: " + intentMessageDecoded);
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
                currentUserScore = messageWithoutSpaces.substring(indexOfA+2);
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
                    counter1 = 0;
                    try {
                        String scoreToAdd = scoreString.substring(1).trim();
                        String studentId = studentID.trim();
                        String studentScore = currentUserScore;
                        Teacher.addScoreToStudent(mAddScoreToStudent, scoreToAdd, studentId, Integer.parseInt(studentScore));
                    }catch (Exception e){
                        Log.d(TAG, "qr code scanning error die to: " + e.getMessage());
                    }
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

    int counter1 = 0;
    Callback mAddScoreToStudent = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            if(counter1 == 0) {
                String message = (String) data;
                Toast.makeText(dashboard_activity.this, message, Toast.LENGTH_SHORT).show();
                counter1 = 1;
            }
        }
    };

    /**
     * Рейтинг ученика в своей группе
     * @param groupID         id Группы
     * @param studentID       id Ученика
     */

    public void rateStudentInGroup(String groupID, String studentID){
        Student.loadGroupStudentsByGroupID(groupID, studentID, mRateStudentInGroupCallback);
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
     * @param studentID         id ученика
     */

    public void rateStudentInSchool(String studentID){
        Student.loadAllStudents(mLoadAllStudents, studentID);
    }

    /**
     * Получение всех принятых запросов на добавление очков
     * @param studentID      ID Ученика
     */

    public void getStudentConfirmedRequestsAmount(String studentID){
        Student.getConfirmedRequests(mGetStudentConfirmedRequestsAmount, studentID);
    }

    /**
     * Callback, который вернется после асинхронного получения назвния группы с Серевера
     */

    Callback mGetUserGroupNameCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String groupName = (String) data;
            Log.d(TAG, "groupName: " + groupName);
            Log.d(TAG, "settings statusID: " + Settings.getStatus());
            SharedPreferences sharedPreferences = getSharedPreferences(Settings.SETTINGS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Settings.GROUP_NAME, groupName);
            editor.apply();
        }
    };

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
            String id = student.getId();
            String groupID = student.getGroupID();
            String firstName = student.getFirstName();
            String secondName = student.getSecondName();
            String image_path = student.getImage_path();
            // оборачиваем в try catch чтобы не было краша когда актитвити уже убито,
            // но мы все еще пытаемся загрузить фото
            try {
                if (image_path.isEmpty()) {
                    image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                } else {
                    GlideApp.with(dashboard_activity.this).load(image_path).centerCrop().into(userAvatar);
                }
            }catch (Exception e){
                e.getMessage();
            }
            limitScore = student.getLimitScore();
            String studentID = student.getId();
            String statusID = student.getStatusID();
            int scoreValue = student.getScore();
            String teacherID = student.getTeacherID();
            boolean change_password = student.isChange_password();
            if(!change_password){
                ChangePasswordDialog dialog = new ChangePasswordDialog();
                dialog.show(getSupportFragmentManager(), "open_dialog");
            }
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
            editor.putBoolean(Student.CHANGE_PASSWORD, change_password);
            editor.apply();

            SharedPreferences sharedPreferencesSettings = getSharedPreferences(Settings.SETTINGS, MODE_PRIVATE);
            SharedPreferences.Editor editorSettings = sharedPreferencesSettings.edit();
            editorSettings.putString(Settings.USER_ID, id);
            editorSettings.apply();

            username.setText("Привет, " + firstName);

            limitScoreView.setText("Осталось");

            if(Integer.parseInt(limitScore) == 0){
                limitScoreView.setText("В данный момент ты не можешь добавлять баллы");
            }else{
                String leftText = limitScoreView.getText().toString();
                String result = leftText + ": " + limitScore + " очков";
                limitScoreView.setText(result);
            }

            getStudentConfirmedRequestsAmount(studentID);
            getStudentDeniedRequestsAmount(studentID);

            rateStudentInGroup(groupID, studentID);
            rateStudentInSchool(studentID);
            
            User.getUserGroupName(mGetUserGroupNameCallback, groupID);

             /*
                --------------------------------------------------------------
                |    Проверка Данных                                            |
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
                if(hours >= 24){
                    if(Integer.parseInt(limitScore) == 0) {
                        student$db.document(sharedPreferences.getString(Student.ID, "")).update("limitScore", "50");
                    }
                }else{
                    Toast.makeText(dashboard_activity.this, "На сегодня лимит исчерпан. Осталось " + (24-hours) + " часа", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.my_profile:{
                Intent intent = new Intent(this, StudentProfile_activity2.class);
                startActivity(intent);
                break;
            }
            case R.id.actions:{
                Intent intent = new Intent(this, RecentActionsPage.class);
                startActivity(intent);
                break;
            }
            case R.id.vkPage:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://vk.com/itlkpfu"));
                startActivity(intent);
                break;
            }
            case R.id.youtube:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCwTgHIPCI3xgzJp6NTa_V1g"));
                startActivity(intent);
                break;
            }
            case R.id.instagram:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.instagram.com/itlkpfu/"));
                startActivity(intent);
                break;
            }
            case R.id.developer:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://vk.com/k.anikin2013"));
                startActivity(intent);
                break;
            }
            case R.id.rateApp:{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.it_score.admin.uscore001"));
                startActivity(intent);
                break;
            }
            case R.id.exit:{
                User.exit();
                Intent intent = new Intent(dashboard_activity.this, login_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.tech_help:{
                Intent intent = new Intent(
                    Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", "ky.anikin@mail.ru", null)
                );
                startActivity(Intent.createChooser(intent, "Использовать"));
                break;
            }
        }
        return true;
    }
}
