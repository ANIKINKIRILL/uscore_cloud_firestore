package com.it_score.admin.uscore001.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.models.LimitObject;
import com.it_score.admin.uscore001.models.Student;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Активити с генерацией QRCODE
 */

public class QRCODE_activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QRCODE_activity";

    // Виджеты
    ImageView qrcode_image;
    EditText score;
    Button generateButton;


    // Переменные
    String currentStudentID;
    private String currentLimitScore;
    private String studentFIO;
    private String studentGroupName;
    private int studentScore;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference QR_CODE_LIMIT = firebaseFirestore.collection("QR_CODE_LIMIT");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_page);
        init();
        getQRCODE_limit();
        initActionBar();
        getStudentData();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        qrcode_image = findViewById(R.id.qrcode_image);
        score = findViewById(R.id.score);
        generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(this);

        score.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyBoard(v);
                }
            }
        });
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR-код");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    /**
     * Получить лимит за qr code
     */

    private void getQRCODE_limit(){
        generateButton.setEnabled(false);
        ProgressDialog progressDialog = new ProgressDialog(QRCODE_activity.this);
        progressDialog.setMessage("Загрузка...");
        QR_CODE_LIMIT.document("TY9MpQFh4xVkOLpJif28").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                LimitObject limitObject = documentSnapshot.toObject(LimitObject.class);
                String limit = limitObject.getLimit();
                SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Student.LIMIT_QRCODE_NUMBER, limit);
                editor.apply();
                score.setHint("Максимум: " + limit);
                generateButton.setEnabled(true);
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Данные ученика
     */

    private void getStudentData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
        currentStudentID = sharedPreferences.getString(Student.ID, "");
        currentLimitScore = sharedPreferences.getString(Student.LIMIT_SCORE, "");
        studentFIO = sharedPreferences.getString(Student.FIRST_NAME, "") + " " + sharedPreferences.getString(Student.SECOND_NAME, "");
        studentGroupName = Settings.getGroupName();
        studentScore = sharedPreferences.getInt(Student.SCORE, 0);
    }

    /**
     * Скрытие клавиатуры при нажатии на пустое место
     * @param view  view
     */

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
            case R.id.info:{
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.generateButton:{
                SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
                String qrcode_limit = sharedPreferences.getString(Student.LIMIT_QRCODE_NUMBER, "50");
                String scoreValue = score.getText().toString();
                if(!scoreValue.trim().isEmpty() && Integer.parseInt(scoreValue) <= Integer.parseInt(qrcode_limit)) {
                    try {
                        generateQRCODE();
                    } catch (UnsupportedEncodingException | WriterException e) {
                        e.printStackTrace();
                    }
                }else{
                    YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(score);
                    score.setError(String.format("Ошибка, не ввели очки или запрос больше, чем на %s баллов", qrcode_limit));
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    /**
     * Сгенирировать QRCODE
     */

    private void generateQRCODE() throws UnsupportedEncodingException, WriterException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String addedDate = simpleDateFormat.format(new Date());
        String message = URLEncoder.encode(
                currentStudentID + "\n" +
                        "Очки: " + score.getText().toString() + "\n" +
                        "ФИО: " + studentFIO + "\n" +
                        "Группа: " + studentGroupName + "\n" +
                        "Баллы ученика: " + studentScore, "UTF-8");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        bitMatrix = multiFormatWriter.encode(
            message,
            BarcodeFormat.QR_CODE,
            300, 300
        );
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        qrcode_image.setImageBitmap(bitmap);
    }

}
