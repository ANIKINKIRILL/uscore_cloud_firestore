package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.content.Entity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
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
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class QRCODE_activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QRCODE_activity";

    // widgets
    ImageView back, qrcode_image;
    EditText score;
    Button generateButton;
    TextView groupView;

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference("Students");

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference groups$db = firebaseFirestore.collection("GROUPS$DB");
    CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");

    // vars
    String currentStudentUsername;
    boolean isDone = false;
    boolean islimitAndRequestScoreChecked = true;
    public static final int LIMIT_REQUEST_SCORE = 150;
    String currentStudentID;
    private String currentStudentGroupName;
    private String currentScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR CODE");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));

        qrcode_image = findViewById(R.id.qrcode_image);
        score = findViewById(R.id.score);
        generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(this);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QRCODE_activity.this);
        currentStudentUsername = sharedPreferences.getString(getString(R.string.currentStudentUsername), "not found");
        currentStudentID = sharedPreferences.getString(getString(R.string.currentStudentID), "");
        currentStudentGroupName = sharedPreferences.getString(getString(R.string.groupName), "");

        getCurrentScore();

        score.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyBoard(v);
                }
            }
        });

    }

    private void getCurrentScore(){
        students$DB.document(currentStudentID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Student student = documentSnapshot.toObject(Student.class);
//                currentScore = student.getScore();
            }
        });
    }

    public void hideKeyBoard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            case R.id.generateButton:{
                String scoreValue = score.getText().toString();
                if(!scoreValue.trim().isEmpty()) {
                    students$DB.document(currentStudentID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            Student student = documentSnapshot.toObject(Student.class);
                            int currentLimitScore = Integer.parseInt(student.getLimitScore());

                            if (currentLimitScore < Integer.parseInt(scoreValue)) {
                                try {
                                    Toast.makeText(getApplicationContext(), "Ваш лимит меньше, чем запрашиваемые очки", Toast.LENGTH_SHORT).show();
                                    YoYo.with(Techniques.Shake).repeat(0).duration(1000).playOn(score);
                                } catch (Exception e1) {
                                    Log.d(TAG, "onEvent: " + e1.getMessage());
                                }
                            } else if (currentLimitScore >= Integer.parseInt(scoreValue)) {
                                try {
                                    String message = URLEncoder.encode(
                                            currentStudentID + "\n" +
                                                    "Очки: " + scoreValue + "\n" +
                                                    "ФИО: " + currentStudentUsername + "\n" +
                                                    "Группа: " + currentStudentGroupName + "\n" +
                                                    "Баллы ученика: " + currentScore, "UTF-8");

                                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                    BitMatrix bitMatrix = multiFormatWriter.encode(
                                            message,
                                            BarcodeFormat.QR_CODE,
                                            300, 300);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                    qrcode_image.setImageBitmap(bitmap);
                                    decreaseLimitScore(Integer.parseInt(scoreValue));
                                } catch (Exception e1) {
                                    Log.d(TAG, "onClick: " + e1.getMessage());
                                }
                            }
                        }
                    });
                }else{
                    YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(score);
                    score.setError("Введите очки");
                }
                break;
            }
        }
    }

    public void decreaseLimitScore(int requestedScoreValue){
        students$DB
                .document(currentStudentID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        Student student = documentSnapshot.toObject(Student.class);
                        if(!isDone) {
                            String limitScore = student.getLimitScore();
                            int limitScoreInteger = Integer.parseInt(limitScore);
                            int result = limitScoreInteger - requestedScoreValue;
                            String resultString = Integer.toString(result);
                            if(result <= 0){
                                resultString = "0";
                                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
                                Date currentDate = calendar.getTime();
                                Map<String, Date> map = new HashMap<>();
                                map.put("spendLimitScoreDate", currentDate);
                                students$DB.document(currentStudentID).set(map, SetOptions.merge());
                            }
                            students$DB.document(currentStudentID).update("limitScore", resultString);
                            Log.d(TAG, "decreaseLimitScore: " + resultString);
                            isDone = true;
                        }
                    }
                });
    }

}
