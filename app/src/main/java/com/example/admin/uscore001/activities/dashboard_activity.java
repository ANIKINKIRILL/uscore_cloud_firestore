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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class dashboard_activity extends AppCompatActivity implements
                                        View.OnClickListener,
                                        ActionBar.OnNavigationListener{

    private static final String TAG = "dashboard_activity";

    // Firebase STUFF
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference("Students");
    DatabaseReference mRefTeacher = mDatabase.getReference("Teachers");
    DatabaseReference mRefTeacherRequest = mDatabase.getReference("RequestsAddingScore");

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference student$db = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");
    CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");

    // widgets
    TextView username, requestNumber, limitScoreView, timer;
    CardView myProfileCardView, topScoresCardView, rulesCardView, recentCardView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CircleImageView userImage;
    ImageView notification_alarm;

    // vars
    int counter = 0;
    static int pickedLang = 0;
    String currentUserGroupID;
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
    private String groupName;
    private String currentStudentID;
    private String statusID;
    private String intentMessageDecoded;
    private String studentID;
    private String scoreString;
    private String currentUserScore;

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
            Log.d(TAG, "onStart: " + TimeLeftInMillis);
            if (TimeLeftInMillis < 0) {
                TimeLeftInMillis = 0;
                timerRunning = false;
                updateCountDownText();
            } else if(TimeLeftInMillis > 0){
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
            findCurrentUserInfo(); // in here have rateStudentInGroup, putGroupNameToSharedPref function
            rateStudentInSchool();
//            confirmedAndDeniedRequestsNumber(currentUser.getEmail());
            requestNumber.setText("");
            notification_alarm.setVisibility(View.INVISIBLE);
        }else {                                                                     // is s Teacher
            isTeacher = true;
            requestNumber.setOnClickListener(this);
            getTeacherInfo(); // in here getTeacherRequestsNumber
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
        if(requestNumber.getText().toString().equals("0")){
            notification_alarm.setImageResource(R.drawable.ic_notifications_paused);
        }else if (!requestNumber.getText().toString().equals("0")){
            notification_alarm.setImageResource(R.drawable.ic_notifications_ring);
        }
    }

    private void putGroupNameByGroupIDToSharedPref(String groupID){
        groups$DB.document(groupID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                groupName = documentSnapshot.get("name").toString();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.groupName), groupName);
                editor.apply();
            }
        });
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
        teachers$DB.whereEqualTo("responsible_email", currentUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Teacher teacher = documentSnapshot.toObject(Teacher.class);
                    String email = teacher.getResponsible_email();
                    String fullname = teacher.getFirstName() + " " + teacher.getLastName();
                    String image_path = teacher.getImage_path();
                    String positionID = teacher.getPositionID();
                    String subjectID = teacher.getSubjectID();
                    String requestID = teacher.getRequestID();
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
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.intentTeacherEmail), email);
                    editor.putString(getString(R.string.intentTeacherFullname), fullname);
                    editor.putString(getString(R.string.intentTeacherImage_path), image_path);
                    editor.putString(getString(R.string.intentTeacherPosition), positionID);
                    editor.putString(getString(R.string.intentTeacherSubject), subjectID);
                    editor.putString("teacherID", teacherID);
                    editor.putString("intentTeacherRequestID", requestID);
                    editor.putString("teacherLastName", teacherLastName);
                    editor.putString("teacherSecondName", teacherSecondName);
                    editor.putString("teacherFirstName", teacherFirstName);
                    editor.putString(getString(R.string.teacherStatusID), statusID);
                    editor.apply();
//                    getTeacherRequestsNumber(teacherRequestID);
                }
            }
        });
    }

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
                    changeNotificationAlarmImage();
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

//    public void addScore(final int score, final String email, final String group){
//        // query request here
//        Query query = mRef.child(group).orderByChild("email").equalTo(email);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(counter != 1) {
//                    for (DataSnapshot selectedStudent : dataSnapshot.getChildren()) {
//                        String old_score = selectedStudent.getValue(Student.class).getScore();
//                        int old_score_int = Integer.parseInt(old_score);
//                        int result = old_score_int + score;
//                        String result_str = Integer.toString(result);
//                        selectedStudent.getRef().child("score").setValue(result_str);
//                        counter = 1;
//                        Toast.makeText(dashboard_activity.this, getResources().getString(R.string.added_to) + " " + email + "/" + group + "->" + score, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

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
            String intentMessage = result.getContents().toString();
            try {
                intentMessageDecoded = URLDecoder.decode(intentMessage, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
//                                      wqrwrwmflawmASDs
//             requestedScore           Очки: 200
//                                      ФИО: Аникин Кирилл
//                                      Группа: 10-6

                // score
                String messageWithoutSpaces = intentMessageDecoded.replace(" ", "");
                int indexOfI = messageWithoutSpaces.indexOf("и");
                int indexOfF = messageWithoutSpaces.indexOf("Ф");
                Log.d(TAG, "index of i: " + indexOfI);
                Log.d(TAG, "index of F: " + indexOfF);
                scoreString = messageWithoutSpaces.substring(indexOfI+1, indexOfF);
                Log.d(TAG, "scoreString: " + scoreString.substring(1));


                // student id
                int indexOfO = messageWithoutSpaces.indexOf("О");
                Log.d(TAG, "index of O: " + indexOfO);
                studentID = messageWithoutSpaces.substring(0, indexOfO);
                Log.d(TAG, "studentID: " + studentID);

                // student current score
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
                    addScore();
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

    public void addScore(){
        int result = Integer.parseInt(currentUserScore)
                + Integer.parseInt(scoreString.substring(1).trim());
        student$db.document(studentID.trim()).update("score", Integer.toString(result));
        Toast.makeText(this, "Вы успешно добавили очки", Toast.LENGTH_SHORT).show();
    }

    public void findCurrentUserInfo(){
        findCurrentUserInfoBackGroundTask task = new findCurrentUserInfoBackGroundTask(this);
        task.execute();
    }

    public void rateStudentInGroup(final String foundGroup){
        students.clear();
        student$db.whereEqualTo("groupID", foundGroup).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Student student = documentSnapshot.toObject(Student.class);
                    if(student.getEmail().equals(currentUser.getEmail())){
                        score = student.getScore();
                        currentStudentClass = new Student(
                                "",
                                student.getFirstName() + " " + student.getSecondName(),
                                "",
                                "",
                                score,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                        );
                    }else{
                        score = student.getScore();
                        Student studentClass = new Student(
                                "",
                                student.getFirstName() + " " + student.getSecondName(),
                                "",
                                "",
                                score,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                        );
                        students.add(studentClass);
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
        });
    }

    public void rateStudentInSchool(){
        rateStudentInSchoolBackGroundTask task = new rateStudentInSchoolBackGroundTask(this);
        task.execute();
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

    public void confirmedAndDeniedRequestsNumber(final String currentStudentID){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        mRefTeacherRequest.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot teacher : dataSnapshot.getChildren()){
//                    if(teacher.hasChild(currentUserEmail.replace(".", ""))){
//                        for(DataSnapshot currentUserRequests : teacher.child(currentUserEmail.replace(".","")).getChildren()){
//                            if(currentUserRequests.getValue(RequestAddingScore.class).isAnswer()){
//                                confirmedRequestsNumber++;
//                            }
//
//                            if(currentUserRequests.getValue(RequestAddingScore.class).isCancel()){
//                                deniedRequestsNumber++;
//                            }
//                        }
//                    }
//                }
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dashboard_activity.this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt(getString(R.string.currentStudentConfirmedRequests), confirmedRequestsNumber);
//                editor.putInt(getString(R.string.currentStudentDeniedRequests), deniedRequestsNumber);
//                editor.apply();
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        requests$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot teachersRequestsID : queryDocumentSnapshots.getDocuments()){
                    teachersRequestsID.getReference().collection("STUDENTS").document(currentStudentID).collection("REQUESTS")
                        .whereEqualTo("answered", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                confirmedRequestsNumber += task.getResult().getDocuments().size();
                                editor.putInt(getString(R.string.currentStudentConfirmedRequests), confirmedRequestsNumber);
                                editor.apply();
                                Log.d(TAG, "+: " + confirmedRequestsNumber);
                            }
                        });
                    teachersRequestsID.getReference().collection("STUDENTS").document(currentStudentID).collection("REQUESTS")
                        .whereEqualTo("canceled", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                deniedRequestsNumber += task.getResult().getDocuments().size();
                                editor.putInt(getString(R.string.currentStudentDeniedRequests), deniedRequestsNumber);
                                editor.apply();
                                Log.d(TAG, "-: " + deniedRequestsNumber);
                            }
                        });
                }
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
            activity.student$db.whereEqualTo("email", activity.currentUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Student student = documentSnapshot.toObject(Student.class);
                        activity.limitScoreView.setText(activity.getString(R.string.leftScore));
                        activity.currentUserGroupID = student.getGroupID();
                        activity.putGroupNameByGroupIDToSharedPref(activity.currentUserGroupID);
                        activity.currentUserUsername = student.getFirstName() + " " + student.getSecondName();
                        activity.senderImage = student.getImage_path();
                        activity.limitScore = student.getLimitScore();
                        activity.currentStudentID = student.getId();
                        Log.d(TAG, "currentStudentID: " + activity.currentStudentID);
                        activity.statusID = student.getStatusID();
                        Log.d(TAG, "student statusID: " + activity.statusID);
                        activity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                        SharedPreferences.Editor editor = activity.sharedPreferences.edit();
                        editor.putString(activity.getString(R.string.currentStudentGroupID), activity.currentUserGroupID);
                        editor.putString(activity.getString(R.string.currentStudentUsername), activity.currentUserUsername);
                        editor.putString(activity.getString(R.string.intentSenderImage), activity.senderImage);
                        editor.putString(activity.getString(R.string.intentLimitScore), activity.limitScore);
                        editor.putString(activity.getString(R.string.currentStudentID), activity.currentStudentID);
                        editor.putString(activity.myProfileCardView.getContext().getString(R.string.studentStatusID), activity.statusID);
                        editor.apply();
                    }
                    try{
                        activity.checkLimitScoreValue(activity.limitScore);
                    }catch (Exception e1){
                        Log.d(TAG, "onEvent: " + e1.getMessage());
                    }
                    String leftText = activity.limitScoreView.getText().toString();
                    String result = leftText + ": " + activity.limitScore + " " + activity.getString(R.string.leftPoints);
                    activity.limitScoreView.setText(result);
                    activity.rateStudentInGroup(activity.currentUserGroupID);
                    activity.confirmedAndDeniedRequestsNumber(activity.currentStudentID);
                }
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

            /*
                        START CLOUD FIRESTORE
             */
            activity.student$db.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot studentSnapshot : queryDocumentSnapshots.getDocuments()){
                        Student student = studentSnapshot.toObject(Student.class);
                        if(student.getEmail().equals(activity.currentUser.getEmail())){
                            activity.score = student.getScore();
                            activity.image_path = student.getImage_path();
                            activity.usernameValue = student.getFirstName() + " " + student.getSecondName();
                            if (activity.score.trim().isEmpty()) {
                                activity.score = "In process...";
                            }
                            if (activity.image_path.isEmpty()) {
                                activity.image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            activity.currentStudentClass = new Student(
                                    "",
                                    activity.usernameValue,
                                    "",
                                    activity.image_path,
                                    activity.score,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    ""
                            );
                        }else{
                            activity.score = student.getScore();
                            if (activity.score.trim().isEmpty()) {
                                activity.score = "In process...";
                            }
                            activity.image_path = student.getImage_path();
                            if (activity.image_path.isEmpty()) {
                                activity.image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                            }
                            activity.usernameValue = student.getFirstName() + " " + student.getSecondName();
                            Student new_student = new Student(
                                    "",
                                    activity.usernameValue,
                                    "",
                                    activity.image_path,
                                    activity.score,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    ""
                            );
                            activity.students2.add(new_student);
                        }
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
            });
            return null;
        }
    }

    public void checkLimitScoreValue(String limitScore){
        int limitScoreInteger = Integer.parseInt(limitScore);
        Log.d(TAG, "checkLimitScoreValue: " + limitScoreInteger);
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
        student$db.whereEqualTo("email", currentUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Student student = documentSnapshot.toObject(Student.class);
                    student.setLimitScore("5000");
                }
            }
        });
    }


}
