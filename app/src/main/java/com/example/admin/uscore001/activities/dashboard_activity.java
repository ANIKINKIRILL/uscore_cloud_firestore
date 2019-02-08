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
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.dialogs.DialogRequestAddingScore;
import com.example.admin.uscore001.dialogs.MakePenaltyDialog;
import com.example.admin.uscore001.dialogs.ShowRequestDialog;
import com.example.admin.uscore001.fragments.AskForScoreDialogFragment;
import com.example.admin.uscore001.fragments.RulesBottomSheetFragment;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.GlideApp;
import com.example.admin.uscore001.util.SocailLinksAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class dashboard_activity extends AppCompatActivity implements View.OnClickListener,
                                        ActionBar.OnNavigationListener{

    private static final String TAG = "dashboard_activity";

    // Firebase STUFF
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference("Students");
    DatabaseReference mRefTeacher = mDatabase.getReference("Teachers");
    DatabaseReference mRefTeacherRequest = mDatabase.getReference("RequestsAddingScore");

    // widgets
    TextView username, requestNumber, limitScoreView, timer;
    CardView myProfileCardView, topScoresCardView, rulesCardView, recentCardView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CircleImageView userImage;
    ImageView notification_alarm;
//    de.hdodenhof.circleimageview.CircleImageView floatingActionButton;
//    de.hdodenhof.circleimageview.CircleImageView fabIconNew;

    // vars
    int counter = 0;
    static int pickedLang = 0;
    String currentUserGroup;
    String currentUserUsername;
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Student> students2= new ArrayList<>();
    String score;
    String image_path;
    String senderImage;
    String usernameValue;
    String limitScore;
    Student currentStudentClass;
    int currentStudentRateSchool;
    SharedPreferences sharedPreferences;
    boolean isTeacher = false;
    int requestCounter = 0;
    String[] socialLinks = {"VK", "WHATS UP", "TWEETER"};
    int confirmedRequestsNumber = 0;
    int deniedRequestsNumber = 0;
    Menu menu;
    private static final long START_TIME_IN_MILLIS = 30000; // 60 SECONDS
    private long TimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean timerRunning;
    private CountDownTimer countDownTimer;
    private ModalBottomSheetDialogFragment modalBottomSheetDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocal();
        setContentView(R.layout.activity_dashboard);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!currentUser.getEmail().contains("teacher")) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
            TimeLeftInMillis = sharedPreferences.getLong("timeLeftInMills", START_TIME_IN_MILLIS);
            timerRunning = sharedPreferences.getBoolean("timeRunning", false);
            updateCountDownText();

            if (TimeLeftInMillis < 0) {
                TimeLeftInMillis = 0;
                timerRunning = false;
                updateCountDownText();
            } else {
                startTimer();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!currentUser.getEmail().contains("teacher")) {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("timeLeftInMills", TimeLeftInMillis);
            editor.putBoolean("timeRunning", timerRunning);
            editor.apply();
        }
    }

    private void init(){
        requestNumber = findViewById(R.id.requestNumber);
        notification_alarm = findViewById(R.id.notification_alarm);
        limitScoreView = findViewById(R.id.limitScore);
        timer = findViewById(R.id.timer);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        SocailLinksAdapter adapter = new SocailLinksAdapter(this, android.R.layout.simple_spinner_item, socialLinks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setListNavigationCallbacks(adapter, this);

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.open_dialog,
                R.string.close_drawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if(!currentUser.getEmail().contains("teacher")) {                           // is a Student
            findCurrentUserInfo(); // in here have rateStudentInGroup function
            rateStudentInSchool();
            confirmedAndDeniedRequestsNumber(currentUser.getEmail());
            requestNumber.setText("");
            notification_alarm.setVisibility(View.INVISIBLE);
        }else {                                                                     // is s Teacher
            isTeacher = true;
            requestNumber.setOnClickListener(this);
            getTeacherInfo(); // in here getTeacherRequestsNumber
            changeNotificationAlarmImage();
            limitScoreView.setVisibility(View.INVISIBLE);

        }

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

//        floatingActionButton = findViewById(R.id.floatingButton);

//        buildSubMenu();
        try {
            ImageView view = navigationView.getHeaderView(0).findViewById(R.id.imageView);
            Log.d(TAG, "NavigationDrawerImage: " + navigationView.getHeaderCount());
            GlideApp.with(this).load(R.drawable.itl).centerCrop().into(view);
            Log.d(TAG, "NavigationDrawerImage: image is loaded");
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "NavigationDrawerImage: " + e.getMessage());
        }
    }

    public void changeNotificationAlarmImage(){
        if(requestCounter == 0){
            notification_alarm.setImageResource(R.drawable.ic_notifications_paused);
        }else if (requestCounter > 0){
            notification_alarm.setImageResource(R.drawable.ic_notifications_ring);
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
        if(!currentUser.getEmail().contains("teacher")) {           // is STUDENT
//            menu.findItem(R.id.addScore).setVisible(false);
            menu.findItem(R.id.scanQRCODE).setVisible(false);
            menu.findItem(R.id.askForScore).setVisible(false);
            menu.findItem(R.id.makePenalty).setVisible(false);
        }else {                                                     // is TEACHER
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
                mAuth.signOut();
                Intent intent = new Intent(dashboard_activity.this, login_activity.class);
                startActivity(intent);
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
                    Toast.makeText(getApplicationContext(), getString(R.string.enableAddScoreMessage), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.generateQERCODE:{
                if(Integer.parseInt(limitScore) != 0) {
                    Intent intent = new Intent(dashboard_activity.this, QRCODE_activity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.enableAddScoreMessage), Toast.LENGTH_SHORT).show();
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
        }
        return true;
    }

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

    public void loadLocal(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocal(language);
    }

    public void getTeacherInfo(){
        final String currentUserEmail = currentUser.getEmail();
        mRefTeacher.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot teacher : dataSnapshot.getChildren()){
                    if(teacher.getValue(Teacher.class).getEmail().equals(currentUserEmail)){
                        String email = teacher.getValue(Teacher.class).getEmail();
                        String fullname = teacher.getValue(Teacher.class).getFullname();
                        String image_path = teacher.getValue(Teacher.class).getImage_path();
                        String position = teacher.getValue(Teacher.class).getPosition();
                        String subject = teacher.getValue(Teacher.class).getSubject();

                        if(image_path.isEmpty()){
                            image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                        }

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.intentTeacherEmail), email);
                        editor.putString(getString(R.string.intentTeacherFullname), fullname);
                        editor.putString(getString(R.string.intentTeacherImage_path), image_path);
                        editor.putString(getString(R.string.intentTeacherPosition), position);
                        editor.putString(getString(R.string.intentTeacherSubject), subject);
                        editor.apply();
                        getTeacherRequestsNumber(fullname);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getTeacherRequestsNumber(String fullname){
        requestCounter = 0;
        mRefTeacherRequest.child(fullname)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    requestCounter = 0;
                    for(DataSnapshot student : dataSnapshot.getChildren()){
                        for(DataSnapshot studentRequests : student.getChildren()){
                            if(!studentRequests.getValue(RequestAddingScore.class).isAnswer()
                                    &&
                                !studentRequests.getValue(RequestAddingScore.class).isCancel()){
                                requestCounter++;
                            }
                        }
                    }

                    requestNumber.setText(getResources().getString(R.string.my_requests) + " " + Integer.toString(requestCounter));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.myProfileCardView:{
                if(!isTeacher) {
                    Intent intent = new Intent(dashboard_activity.this, StudentProfile_activity2.class);
                    startActivity(intent);
                }else if(isTeacher){
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
                RulesBottomSheetFragment rulesBottomSheetFragment = new RulesBottomSheetFragment();
                rulesBottomSheetFragment.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
        }
    }

    public void addScore(final int score, final String email, final String group){
        // query request here
        Query query = mRef.child(group).orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(counter != 1) {
                    for (DataSnapshot selectedStudent : dataSnapshot.getChildren()) {
                        String old_score = selectedStudent.getValue(Student.class).getScore();
                        int old_score_int = Integer.parseInt(old_score);
                        int result = old_score_int + score;
                        String result_str = Integer.toString(result);
                        selectedStudent.getRef().child("score").setValue(result_str);
                        counter = 1;
                        Toast.makeText(dashboard_activity.this, getResources().getString(R.string.added_to) + " " + email + "/" + group + "->" + score, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        counter = 0;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result == null){
            Toast.makeText(this, getResources().getString(R.string.cancelScanProccess), Toast.LENGTH_SHORT).show();
        }
        else{
            final AlertDialog alertDialog = new AlertDialog.Builder(dashboard_activity.this).create();
            alertDialog.setMessage(getResources().getString(R.string.requestScore) + result.getContents());
            alertDialog.setTitle(getResources().getString(R.string.confirm));
            alertDialog.setButton(Dialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(result.getContents() != null) {
                        int stop_moment = result.getContents().indexOf("|");
                        int start_moment_username = result.getContents().indexOf(":");
                        int end_moment_username = result.getContents().indexOf(",");
                        int start_moment_email = result.getContents().indexOf(",");
                        int end_moment_email = result.getContents().indexOf("/");
                        int start_moment_group = result.getContents().indexOf("/");
                        int end_moment_group = result.getContents().length();
                        String requestScore = result.getContents().substring(0, stop_moment);
                        String requestUsername = result.getContents().substring(start_moment_username, end_moment_username).replace(":", "").trim();
                        String requestEmail = result.getContents().substring(start_moment_email, end_moment_email).replace(",", "").trim();
                        String requestGroup = result.getContents().substring(start_moment_group, end_moment_group).replace("/", "").trim();
                        addScore(Integer.parseInt(requestScore), requestEmail, requestGroup);
//                    Toast.makeText(dashboard_activity.this, requestScore + getResources().getString(R.string.addedPerson) + requestUsername + " " + requestGroup, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(dashboard_activity.this, requestEmail, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }else{
                        alertDialog.dismiss();
                        Toast.makeText(dashboard_activity.this, "You canceled scanning", Toast.LENGTH_SHORT).show();
                    }
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

    public void findCurrentUserInfo(){
        findCurrentUserInfoBackGroundTask task = new findCurrentUserInfoBackGroundTask(this);
        task.execute();
        //        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot groups : dataSnapshot.getChildren()){
//                    for(DataSnapshot student : groups.getChildren()){
//                        if(student.getKey().equals(currentUser.getEmail().replace(".", ""))){
//                            currentUserGroup = student.getValue(Student.class).getGroup();
//                            currentUserUsername = student.getValue(Student.class).getUsername();
//                            senderImage = student.getValue(Student.class).getImage_path();
//                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString(getString(R.string.currentStudentGroup), currentUserGroup);
//                            editor.putString(getString(R.string.currentStudentUsername), currentUserUsername);
//                            editor.putString(getString(R.string.intentSenderImage), senderImage);
//                            editor.apply();
//                            rateStudentInGroup(currentUserGroup);
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
    }

    public void rateStudentInGroup(final String foundGroup){
        students.clear();
        mRef.child(foundGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot currentGroupMembers : dataSnapshot.getChildren()){
                    if(currentGroupMembers.getKey().equals(currentUser.getEmail().replace(".", ""))){
                        score = currentGroupMembers.getValue(Student.class).getScore();
                        currentStudentClass = new Student(score, "", "", "", "");
                    }else {
                        score = currentGroupMembers.getValue(Student.class).getScore();
                        Student student = new Student(score, usernameValue, image_path, "", "");
                        students.add(student);
                    }

                }
                students.add(currentStudentClass);
                bubbleSortStudents(students);
                Collections.reverse(students);
                int currentStudentRateGroup = students.indexOf(currentStudentClass)+1;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.currentStudentRateInGroup), Integer.toString(currentStudentRateGroup));
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void rateStudentInSchool(){
        rateStudentInSchoolBackGroundTask task = new rateStudentInSchoolBackGroundTask(this);
        task.execute();
        //        students2.clear();
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot groups : dataSnapshot.getChildren()){
//                    for(DataSnapshot student : groups.getChildren()) {
//                        if(student.getKey().equals(currentUser.getEmail().replace(".", ""))){
//                            score = student.getValue(Student.class).getScore();
//                            image_path = student.getValue(Student.class).getImage_path();
//                            usernameValue = student.getValue(Student.class).getUsername();
//                            if (score.trim().isEmpty()) {
//                                score = "In process...";
//                            }
//                            if (image_path.isEmpty()) {
//                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
//                            }
//                            currentStudentClass = new Student(score, usernameValue, image_path, "", "");
//                        }else {
//                            score = student.getValue(Student.class).getScore();
//                            if (score.trim().isEmpty()) {
//                                score = "In process...";
//                            }
//                            image_path = student.getValue(Student.class).getImage_path();
//                            if (image_path.isEmpty()) {
//                                image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
//                            }
//                            usernameValue = student.getValue(Student.class).getUsername();
//                            Student new_student = new Student(score, usernameValue, image_path, "", "");
//                            students2.add(new_student);
//
//                        }
//                    }
//                }
//                students2.add(currentStudentClass);
//                bubbleSortStudents(students2);
//                Collections.reverse(students2);
//                currentStudentRateSchool = students2.indexOf(currentStudentClass)+1;
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(getString(R.string.currentStudentRateInSchool), Integer.toString(currentStudentRateSchool));
//                editor.apply();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public void bubbleSortStudents(ArrayList<Student> students){
            int size = students.size();
            Student temp;
            for(int i = 0; i < size; i++){
                for(int j = 1; j < size; j++){
                    if(Integer.parseInt(students.get(j-1).getScore()) > Integer.parseInt(students.get(j).getScore())) {
                        temp = students.get(j-1);
                        students.set(j-1, students.get(j));
                        students.set(j, temp);
                    }
                }
            }
        }

    public void confirmedAndDeniedRequestsNumber(final String currentUserEmail){
        mRefTeacherRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot teacher : dataSnapshot.getChildren()){
                    if(teacher.hasChild(currentUserEmail.replace(".", ""))){
                        for(DataSnapshot currentUserRequests : teacher.child(currentUserEmail.replace(".","")).getChildren()){
                            if(currentUserRequests.getValue(RequestAddingScore.class).isAnswer()){
                                confirmedRequestsNumber++;
                            }

                            if(currentUserRequests.getValue(RequestAddingScore.class).isCancel()){
                                deniedRequestsNumber++;
                            }
                        }
                    }
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.currentStudentConfirmedRequests), confirmedRequestsNumber);
                editor.putInt(getString(R.string.currentStudentDeniedRequests), deniedRequestsNumber);
                editor.apply();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class findCurrentUserInfoBackGroundTask extends AsyncTask<Void, Void, Void>{

        // vars
        private WeakReference<dashboard_activity> dashboardActivityWeakReference;

        findCurrentUserInfoBackGroundTask(dashboard_activity activity){
            dashboardActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dashboard_activity activity = dashboardActivityWeakReference.get();
            if(activity == null || activity.isFinishing()){
                return;
            }
            Log.d(TAG, "onPreExecute: current user info is in the process of loading");
        }
        @Override
        protected Void doInBackground(Void... voids) {

            final dashboard_activity activity = dashboardActivityWeakReference.get();
            if(activity == null || activity.isFinishing()){
                return null;
            }

            activity.mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    activity.limitScoreView.setText(activity.getString(R.string.leftScore));
                    for(DataSnapshot groups : dataSnapshot.getChildren()){
                        for(DataSnapshot student : groups.getChildren()){
                            if(student.getKey().equals(activity.currentUser.getEmail().replace(".", ""))){
                                activity.currentUserGroup = student.getValue(Student.class).getGroup();
                                activity.currentUserUsername = student.getValue(Student.class).getUsername();
                                activity.senderImage = student.getValue(Student.class).getImage_path();
                                activity.limitScore = student.getValue(Student.class).getLimitScore();
                                try {
                                    activity.checkLimitScoreValue(activity.limitScore);
                                }catch (Exception e){
                                    Log.d(TAG, "onDataChange: " + e.getMessage());
                                }
                                String leftText = activity.limitScoreView.getText().toString();
                                String result = leftText + ": " + activity.limitScore + " " + activity.getString(R.string.leftPoints);
                                activity.limitScoreView.setText(result);
                                activity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                SharedPreferences.Editor editor = activity.sharedPreferences.edit();
                                editor.putString(activity.getString(R.string.currentStudentGroup), activity.currentUserGroup);
                                editor.putString(activity.getString(R.string.currentStudentUsername), activity.currentUserUsername);
                                editor.putString(activity.getString(R.string.intentSenderImage), activity.senderImage);
                                editor.putString(activity.getString(R.string.intentLimitScore), activity.limitScore);
                                editor.apply();
                                activity.rateStudentInGroup(activity.currentUserGroup);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dashboard_activity activity = dashboardActivityWeakReference.get();
            if(activity == null || activity.isFinishing()){
                return;
            }
            Log.d(TAG, "onPostExecute: current user info loading is finished");
        }
    }

    public static class rateStudentInSchoolBackGroundTask extends AsyncTask<Void, Void, Void>{

        // vars
        private WeakReference<dashboard_activity> dashboardActivityWeakReference;

        rateStudentInSchoolBackGroundTask(dashboard_activity activity){
            dashboardActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final dashboard_activity activity = dashboardActivityWeakReference.get();
            if(activity.isFinishing() || activity == null){
                return null;
            }

            activity.students2.clear();
            activity.mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot groups : dataSnapshot.getChildren()) {
                            for (DataSnapshot student : groups.getChildren()) {
                                if (student.getKey().equals(activity.currentUser.getEmail().replace(".", ""))) {
                                    activity.score = student.getValue(Student.class).getScore();
                                    activity.image_path = student.getValue(Student.class).getImage_path();
                                    activity.usernameValue = student.getValue(Student.class).getUsername();
                                    if (activity.score.trim().isEmpty()) {
                                        activity.score = "In process...";
                                    }
                                    if (activity.image_path.isEmpty()) {
                                        activity.image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                                    }
                                    activity.currentStudentClass = new Student(activity.score, activity.usernameValue, activity.image_path, "", "");
                                } else {
                                    activity.score = student.getValue(Student.class).getScore();
                                    if (activity.score.trim().isEmpty()) {
                                        activity.score = "In process...";
                                    }
                                    activity.image_path = student.getValue(Student.class).getImage_path();
                                    if (activity.image_path.isEmpty()) {
                                        activity.image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                                    }
                                    activity.usernameValue = student.getValue(Student.class).getUsername();
                                    Student new_student = new Student(activity.score, activity.usernameValue, activity.image_path, "", "");
                                    activity.students2.add(new_student);

                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    activity.students2.add(activity.currentStudentClass);
                    activity.bubbleSortStudents(activity.students2);
                    Collections.reverse(activity.students2);
                    activity.currentStudentRateSchool = activity.students2.indexOf(activity.currentStudentClass)+1;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(activity.getString(R.string.currentStudentRateInSchool), Integer.toString(activity.currentStudentRateSchool));
                    editor.apply();
                }
                @Override
                public void onCancelled(DatabaseError databaseError){

                }
            });
            return null;
        }
    }

    // buildSubMenu -> arcLayout at the bottom
    //    public void buildSubMenu(){
//        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
//        // Create an image view for each menu item
//        ImageView menuOption1 = new ImageView(this);
//        ImageView menuOption2 = new ImageView(this);
//        ImageView menuOption3 = new ImageView(this);
//        ImageView menuOption4 = new ImageView(this);
//
//        // Set the icon for each menu item
//        menuOption1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_settings));
//        menuOption2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_groups));
//        menuOption3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_alarm));
//        menuOption4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_contacts));
//
//        // Build the menu with default options: 90 degrees, 72dp radius.
//        // Set 4 default SubActionButtons
//        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
//                .addSubActionView(rLSubBuilder.setContentView(menuOption1).setLayoutParams(new FrameLayout.LayoutParams(150, 150)).build())
//                .addSubActionView(rLSubBuilder.setContentView(menuOption2).setLayoutParams(new FrameLayout.LayoutParams(150, 150)).build())
//                .addSubActionView(rLSubBuilder.setContentView(menuOption3).setLayoutParams(new FrameLayout.LayoutParams(150, 150)).build())
//                .addSubActionView(rLSubBuilder.setContentView(menuOption4).setLayoutParams(new FrameLayout.LayoutParams(150, 150)).build())
//                .attachTo(floatingActionButton)
//                //.setStartAngle(360)
//                .build();
//
//        // Listen for menu open and close events to animate the button content view
//        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
//            @Override
//            public void onMenuOpened(FloatingActionMenu menu) {
//                // Rotate the icon of rightLowerButton 45 degrees clockwise
////                fabIconNew.setRotation(0);
////                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
////                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
////                animation.start();
//            }
//
//            @Override
//            public void onMenuClosed(FloatingActionMenu menu) {
//                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
////                fabIconNew.setRotation(45);
////                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
////                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
////                animation.start();
//            }
//        });
//
//        // OnClickListeners for each menu item
//        menuOption1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Option 1", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        menuOption2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Option 2", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        menuOption3.setOnClickListener(new View.OnClickListener() {                                     // limitScore and CountDownTimer dialog
//            @Override
//            public void onClick(View v) {
//                // open dialog with info of your limitScore and otherwise leftTime
//                LimitScoreLeftTimeDialog dialog = new LimitScoreLeftTimeDialog();
//                dialog.show(getFragmentManager(), getString(R.string.open_dialog));
//            }
//        });
//
//        menuOption4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Option 4", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public void checkLimitScoreValue(String limitScore){
        int limitScoreInteger = Integer.parseInt(limitScore);
        if(limitScoreInteger == 0){
            menu.findItem(R.id.generateQERCODE).setEnabled(false);
            menu.findItem(R.id.makeRequest).setEnabled(false);
            limitScoreView.setVisibility(View.INVISIBLE);
            timer.setVisibility(View.VISIBLE);
            startTimer();
        }else{
            menu.findItem(R.id.generateQERCODE).setEnabled(true);
            menu.findItem(R.id.makeRequest).setEnabled(true);
            timer.setVisibility(View.INVISIBLE);
        }
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                TimeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                updateLimitScore();
                timer.setVisibility(View.INVISIBLE);
                limitScoreView.setVisibility(View.VISIBLE);
                TimeLeftInMillis = START_TIME_IN_MILLIS;

            }
        }.start();
        timerRunning = true;

    }

    private void updateCountDownText() {
        int minutes = (int) TimeLeftInMillis / 1000 / 60;
        int second = (int) TimeLeftInMillis / 1000 % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, second);
        timer.setText(timeLeftFormatted);
    }

    public void updateLimitScore(){
        mRef.child(currentUserGroup).child(currentUser.getEmail().replace(".", ""))
                .child("limitScore").setValue("5000");
    }


}
