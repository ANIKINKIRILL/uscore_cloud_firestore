package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.content.Entity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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

    // vars
    String currentStudentUsername;
    String currentStudentGroup;
    boolean isDone = false;
    boolean islimitAndRequestScoreChecked = true;
    public static final int LIMIT_REQUEST_SCORE = 150;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_page);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        qrcode_image = findViewById(R.id.qrcode_image);
        score = findViewById(R.id.score);
        generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QRCODE_activity.this);
        currentStudentGroup = sharedPreferences.getString(getString(R.string.currentStudentGroup), "not found");
        currentStudentUsername = sharedPreferences.getString(getString(R.string.currentStudentUsername), "not found");


        score.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyBoard(v);
                }
            }
        });

    }

    public void hideKeyBoard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                finish();
                break;
            }
            case R.id.generateButton:{
                String scoreValue = score.getText().toString();
                try {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    BitMatrix bitMatrix = multiFormatWriter.encode(scoreValue + "| from: " + ", " + currentUser.getEmail() + "/" + currentStudentGroup, BarcodeFormat.QR_CODE,
                            300, 300);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qrcode_image.setImageBitmap(bitmap);
                    decreaseLimitScore(currentStudentGroup, currentUser.getEmail(), Integer.parseInt(scoreValue));
                }catch (Exception e){
                    Log.d(TAG, "onClick: " + e.getMessage());
                }
                break;
            }
        }
    }

    public void decreaseLimitScore(String group, String email, int requestedScoreValue){
            mRef.child(group).child(email.replace(".", ""))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!isDone) {
                                try {
                                    String limitScore = dataSnapshot.getValue(Student.class).getLimitScore();
                                    int limitScoreInteger = Integer.parseInt(limitScore);
                                    int result = limitScoreInteger - requestedScoreValue;
                                    if (result <= 0) {
                                        dataSnapshot.getRef().child("limitScore").setValue("0");
                                    } else {
                                        String resultString = Integer.toString(result);
                                        dataSnapshot.getRef().child("limitScore").setValue(resultString);
                                    }
                                    isDone = true;
                                }catch (Exception e){
                                    Log.d("decreaseLimitScore ", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

}
