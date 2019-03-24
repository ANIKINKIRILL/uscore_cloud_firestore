package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import com.example.admin.uscore001.Settings;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_page);
        init();
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
        actionBar.setTitle("QR CODE");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.generateButton:{
                String scoreValue = score.getText().toString();
                if(!scoreValue.trim().isEmpty()) {
                    if (Integer.parseInt(currentLimitScore) < Integer.parseInt(scoreValue)) {
                        try {
                            Toast.makeText(getApplicationContext(), "Ваш лимит меньше, чем запрашиваемые очки", Toast.LENGTH_SHORT).show();
                            YoYo.with(Techniques.Shake).repeat(0).duration(1000).playOn(score);
                        } catch (Exception e1) {
                            Log.d(TAG, "onEvent: " + e1.getMessage());
                        }
                    }else if (Integer.parseInt(currentLimitScore) >= Integer.parseInt(scoreValue)){
                        try {
                            generateQRCODE();
                        } catch (UnsupportedEncodingException | WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(score);
                    score.setError("Введите очки");
                }
                break;
            }
        }
    }

    /**
     * Сгенирировать QRCODE
     */

    private void generateQRCODE() throws UnsupportedEncodingException, WriterException {
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
        Teacher.decreaseStudentLimitScore(null, Integer.parseInt(score.getText().toString()), currentStudentID);
    }

}
