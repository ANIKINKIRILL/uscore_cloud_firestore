package com.example.admin.uscore001.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.AsyncTaskDataArgument;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.dialogs.ImageDialog;
import com.example.admin.uscore001.dialogs.TeacherSettingsDialog;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.GlideApp;
import com.example.admin.uscore001.util.OnImageClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherProfile extends AppCompatActivity implements View.OnClickListener, OnImageClickListener{

    private static final String TAG = "TeacherProfile";

    // widgets
    CircleImageView imageView;
    TextView usernameView, status, emailAddress, positionView, subjectView, countAddedScore;
    Button profileSettings;
    LinearLayout showAllComments;

    // vars
    String email;
    int addedTimes = 0;
    byte[] mUploadBytes;
    private String requestID;
    private String lastName;
    private String firstName;
    private String teacherImagePath;
    private String positionID;
    private String subjectID;
    private String teacherID;

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mRef = storage.getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference teachers$db = firebaseFirestore.collection("TEACHERS$DB");
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_profile_layout);
        getTeacherData();
        init();
        initActionBar();
        getSubjectByID(subjectID);
        getPositionByID(positionID);
        setTeacherData(teacherImagePath, firstName, lastName, email, requestID);
        setTeacherAddedTimes(requestID);
    }

    /**
     * Извлечение данных учителя из SharedPreferences
     */

    private void getTeacherData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Teacher.TEACHER_DATA, MODE_PRIVATE);
        teacherID = sharedPreferences.getString(Teacher.TEACHER_ID, "");
        teacherImagePath = sharedPreferences.getString(Teacher.IMAGE_PATH, "");
        firstName = sharedPreferences.getString(Teacher.FIRST_NAME, "");
        lastName = sharedPreferences.getString(Teacher.LAST_NAME, "");
        email = sharedPreferences.getString(Teacher.EMAIL, "");
        requestID = sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "");
        subjectID = sharedPreferences.getString(Teacher.SUBJECT_ID, "");
        positionID = sharedPreferences.getString(Teacher.POSITION_ID, "");
    }

    /**
     * Инициализация
     */

    private void init(){
        imageView = findViewById(R.id.imageView);
        countAddedScore = findViewById(R.id.countAddedScore);
        usernameView = findViewById(R.id.username);
        status = findViewById(R.id.status);
        emailAddress = findViewById(R.id.emailAddress);
        positionView = findViewById(R.id.position);
        subjectView = findViewById(R.id.subject);
        profileSettings = findViewById(R.id.profileSettings);
        showAllComments = findViewById(R.id.showAllComments);

        showAllComments.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:{
                ImageDialog dialog = new ImageDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.profileSettings:{
                TeacherSettingsDialog dialog = new TeacherSettingsDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.showAllComments:{
                Snackbar.make(v, "Эта функция пока не доступна", Snackbar.LENGTH_LONG).show();
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

    /**
     * Предмет учителя по ID
     * @param subjectID     Предмет
     */

    private void getSubjectByID(String subjectID){
        Teacher.getSubjectValueByID(mGetSubjectByIDCallback, subjectID);
    }

    /**
     * Callback, после асинхронного получения Предмета учителя с Сервера
     */

    Callback mGetSubjectByIDCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            SharedPreferences sharedPreferences = getSharedPreferences(Teacher.TEACHER_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String callbackResult = (String) data;
            editor.putString(Teacher.SUBJECT_DATA, callbackResult);
            subjectView.setText(callbackResult);
        }
    };

    /**
     * Позиция учителя по ID
     * @param positionID     Позиция
     */

    private void getPositionByID(String positionID){
        Teacher.getPositionValueByID(mGetPositionByIDCallback, positionID);
    }

    /**
     * Callback, после асинхронного получения Позиции учителя с Сервера
     */

    Callback mGetPositionByIDCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            SharedPreferences sharedPreferences = getSharedPreferences(Teacher.TEACHER_DATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String callbackResult = (String) data;
            editor.putString(Teacher.POSITION_DATA, callbackResult);
            positionView.setText(callbackResult);
        }
    };



    /**
     * Загрузка данных учителя в виджеты
     *
     * @param teacherImagePath  Аватарка
     * @param firstName         Имя
     * @param lastName          Отчество
     * @param email             Почта
     * @param teacherRequestID  requestID
     */

    public void setTeacherData(String teacherImagePath, String firstName, String lastName, String email, String teacherRequestID){
        GlideApp.with(this).load(teacherImagePath).centerCrop().into(imageView);
        usernameView.setText(String.format("%s %s", firstName, lastName));
        status.setText("Учитель");
        emailAddress.setText(email);
    }

    /**
     * Сколько раз учитель принял заявку на добавление очков ученику
     * @param teacherRequestID  requestID учителя
     */

    private void setTeacherAddedTimes(String teacherRequestID){
        addedTimes = 0;
        requests$DB
            .document(teacherRequestID)
            .collection("STUDENTS")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        documentSnapshot
                            .getReference()
                            .collection("REQUESTS")
                            .whereEqualTo("answered", true)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        addedTimes += task.getResult().getDocuments().size();
                                        countAddedScore.setText(Integer.toString(addedTimes));
                                    }
                                }
                            });
                    }
                }
            });
    }

    /**
     * Метод интрефеса OnImageClickListener
     * @param bitmap    фото с камеры
     */

    @Override
    public void getBitmapPath(Bitmap bitmap) {
        GlideApp.with(this).load(bitmap).centerCrop().into(imageView);
        uploadBitmap(bitmap);
    }

    /**
     * Метод интрефеса OnImageClickListener
     * @param uri       картинка с галереии
     */

    @Override
    public void getUriPath(Uri uri) {
        Glide.with(TeacherProfile.this).load(uri).into(imageView);
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
            Log.d(TAG, "onPreExecute: " + "Compressing image...");
            Toast.makeText(TeacherProfile.this, "Сжимаем фото...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            if(bitmap == null){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(TeacherProfile.this.getContentResolver(), uris[0]);
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
                        teachers$db.document(teacherID).update("image_path", uri.toString());
                    }
                });
            }
        });
    }

}
