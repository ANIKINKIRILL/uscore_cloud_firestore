package com.it_score.admin.uscore001.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.util.GlideApp;
import com.it_score.admin.uscore001.util.OnImageClickListener;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.dialogs.ImageDialog;
import com.it_score.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Активити 'Мой Профиль'
 */

public class StudentProfile_activity2 extends AppCompatActivity implements View.OnClickListener, OnImageClickListener {

    private static final String TAG = "StProfile_activity2";

    // Виджеты
    CircleImageView userAvatar;
    Button addCommentButton;
    TextView userStatus;
    TextView email, username, group, score, countAddedCanceledScore;
    TextView rateInSchool, rateInGroup;
    ProgressBar progressBar;
    TextView showAllComments;

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mRef = storage.getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference student$db = firebaseFirestore.collection("STUDENTS$DB");

    // Переменные
    private byte[] mUploadBytes;
    private String currentStudentID;
    private String imagePath;
    private String firstName;
    private String secondName;
    private String rateStudentInSchool;
    private String rateStudentInGroup;
    private int studentConfirmedRequestsAmount;
    private int studentDeniedRequestsAmount;
    private String statusID;
    private String studentEmail;
    private int studentScore;
    private String groupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile2);
        loadSharedPreferences();
        init();
        initActionBar();
    }

    /**
     * Инициализация
     */

    private void init(){
        rateInSchool = findViewById(R.id.rateInSchool);
        rateInGroup = findViewById(R.id.rateInGroup);
        email = findViewById(R.id.emailAddress);
        username = findViewById(R.id.username);
        group = findViewById(R.id.group);
        score = findViewById(R.id.score);
        progressBar = findViewById(R.id.progressBar);
        addCommentButton = findViewById(R.id.addCommentButton);
        countAddedCanceledScore = findViewById(R.id.countAddedCanceledScore);
        userAvatar = findViewById(R.id.imageView);
        userStatus = findViewById(R.id.status);
        showAllComments = findViewById(R.id.showAllComments);

        userAvatar.setOnClickListener(this);
        addCommentButton.setOnClickListener(this);
        showAllComments.setOnClickListener(this);

        GlideApp.with(this).load(imagePath).centerCrop().into(userAvatar);
        rateInGroup.setText(rateStudentInGroup);
        rateInSchool.setText(rateStudentInSchool);
        email.setText(studentEmail);
        username.setText(firstName + " " + secondName);
        group.setText(groupName + " (Группа)");
        score.setText(Integer.toString(studentScore) + " (Очков)");
        userStatus.setText("Ученик");
        countAddedCanceledScore.setText(studentConfirmedRequestsAmount+"/"+studentDeniedRequestsAmount);

    }

    /**
     * Инициализация ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Мой профиль");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_profile_activity_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:{
                ImageDialog dialog = new ImageDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.addCommentButton:{
                Intent intent = new Intent(StudentProfile_activity2.this, CommentsPage.class);
                intent.putExtra("to_whom_send_email", email.getText().toString());
                startActivity(intent);
                break;
            }
            case R.id.showAllComments:{
                Intent intent = new Intent(StudentProfile_activity2.this, CommentsPage.class);
                intent.putExtra("to_whom_send_email", email.getText().toString());
                startActivity(intent);
                break;
            }
        }
    }

    /**
     * Загрузка данных ученика
     */

    public void loadSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
        SharedPreferences sharedPreferencesSettings = getSharedPreferences(Settings.SETTINGS, MODE_PRIVATE);
        currentStudentID = sharedPreferences.getString(Student.ID, "");
        imagePath = sharedPreferences.getString(Student.IMAGE_PATH, "");
        firstName = sharedPreferences.getString(Student.FIRST_NAME, "");
        secondName = sharedPreferences.getString(Student.SECOND_NAME, "");
        rateStudentInGroup = sharedPreferences.getString(Student.RATE_IN_GROUP, "");
        rateStudentInSchool = sharedPreferences.getString(Student.RATE_IN_SCHOOL, "");
        studentConfirmedRequestsAmount = sharedPreferences.getInt(Student.CONFIRMED_REQUESTS_AMOUNT, 0);
        studentDeniedRequestsAmount = sharedPreferences.getInt(Student.DENIED_REQUESTS_AMOUNT, 0);
        statusID = sharedPreferences.getString(Student.STATUS_ID, "");
        studentEmail = sharedPreferences.getString(Student.EMAIL, "");
        studentScore = sharedPreferences.getInt(Student.SCORE, 0);
        groupName = sharedPreferencesSettings.getString(Settings.GROUP_NAME, "");
    }

    /**
     * Метод интрефеса OnImageClickListener
     * @param bitmap    фото с камеры
     */

    @Override
    public void getBitmapPath(Bitmap bitmap) {
        GlideApp.with(this).load(bitmap).centerCrop().into(userAvatar);
        uploadBitmap(bitmap);
    }

    /**
     * Метод интрефеса OnImageClickListener
     * @param uri       картинка с галереии
     */

    @Override
    public void getUriPath(Uri uri) {
        GlideApp.with(StudentProfile_activity2.this).load(uri).centerCrop().into(userAvatar);
        uploadUri(uri);
    }

    /**
     * Загрузка фото с камеры в бд
     * @param bitmap    фото с камеры
     */

    public void uploadBitmap(Bitmap bitmap){
        BackGroundResize resize = new BackGroundResize(bitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    /**
     * Загрузка картинки с галерии в бд
     * @param uri       картинка с галереии
     */

    public void uploadUri(Uri uri){
        BackGroundResize resize = new BackGroundResize(null);
        resize.execute(uri);
    }

    /**
     * Асинхронное сжатие фото
     */

    public class BackGroundResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap bitmap;

        public BackGroundResize(Bitmap bitmap) {
            if(bitmap != null){
                this.bitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(StudentProfile_activity2.this, "Сжимаем фото...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            if(bitmap == null){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(StudentProfile_activity2.this.getContentResolver(), uris[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            byte[] bytes = null;
            bytes = getBytesFromBitmap(bitmap, 100);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mUploadBytes = bytes;
            executeUploadTask();
        }
    }

    /**
     * Биты информации после сжатия фото
     *
     * @param bitmap        фото с камеры
     * @param quality       качество фото на которые было она сжато
     * @return              массив битов
     */

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    /**
     * Загрузка на БД
     */

    public void executeUploadTask(){
        Toast.makeText(this, "Загружаем фото...", Toast.LENGTH_SHORT).show();
        UploadTask uploadTask = mRef.child(user.getUid()).putBytes(mUploadBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        student$db.document(currentStudentID).update("image_path", uri.toString());
                    }
                });
            }
        });
    }
}
