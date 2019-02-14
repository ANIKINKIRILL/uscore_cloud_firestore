package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.util.OnImageClickListener;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.dialogs.ImageDialog;
import com.example.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentProfile_activity2 extends AppCompatActivity implements View.OnClickListener, OnImageClickListener {

    private static final String TAG = "StProfile_activity2";

    // widgets
    CircleImageView userAvatar;
    ImageView backArraw;
    Button addCommentButton;
    TextView userStatus;
    TextView email, username, group, score, countAddedCanceledScore;
    TextView rateInSchool, rateInGroup;
    ProgressBar progressBar;
    TextView showAllComments;

    // vars
    byte[] mUploadBytes;
    ArrayList<Student> students = new ArrayList<>();
    Student currentStudentClass;
    String scoreValue, image_pathValue, usernameValue;
    String currentStudentGroup;
    String studentGroup, studentUsername;
    String currentStudentRateInGroup;
    String currentStudentRateInSchool;
    int confirmedRequestsNumber;
    int deniedRequestsNumber;
    private String currentStudentID;

    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mRef = storage.getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference student$db = firebaseFirestore.collection("STUDENTS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile2);
        init();
    }

    private void init(){
        rateInSchool = findViewById(R.id.rateInSchool);
        rateInGroup = findViewById(R.id.rateInGroup);

        loadSharedPreferences();

        rateInGroup.setText(currentStudentRateInGroup);
        rateInSchool.setText(currentStudentRateInSchool);

        email = findViewById(R.id.emailAddress);
        username = findViewById(R.id.username);
        group = findViewById(R.id.group);
        score = findViewById(R.id.score);
        progressBar = findViewById(R.id.progressBar);
        addCommentButton = findViewById(R.id.addCommentButton);
        countAddedCanceledScore = findViewById(R.id.countAddedCanceledScore);

        countAddedCanceledScore.setText(confirmedRequestsNumber+"/"+deniedRequestsNumber);

        userAvatar = findViewById(R.id.imageView);

        Activity activity = getParent();

        if(!user.getEmail().contains("teacher")) {
            getUserImage();
            setCurrentUserInfo(currentStudentID);
        }

        backArraw = findViewById(R.id.back);
        userAvatar.setOnClickListener(this);
        backArraw.setOnClickListener(this);

        addCommentButton.setOnClickListener(this);

        userStatus = findViewById(R.id.status);

        showAllComments = findViewById(R.id.showAllComments);
        showAllComments.setOnClickListener(this);

        if(!user.getEmail().contains("teacher")){
            userStatus.setText(R.string.statusStudent);
        }else {
            userStatus.setText(R.string.statusTeacher);
        }
    }

    public void setCurrentUserInfo(String studentID){
        student$db.document(studentID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                Student student = documentSnapshot.toObject(Student.class);
                String currentUserUsername = student.getFirstName() + " " + student.getSecondName();
                String currentUserGroup = studentGroup;
                String currentUserScore = student.getScore();
                email.setText(user.getEmail());
                username.setText(currentUserUsername);
                group.setText(currentUserGroup);
                if (currentUserScore.equals("")) {
                    score.setText("0");
                } else {
                    score.setText(currentUserScore);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView:{
                ImageDialog dialog = new ImageDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.back: {
                finish();
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

    public void loadSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StudentProfile_activity2.this);
        studentGroup = sharedPreferences.getString(getString(R.string.groupName), "");
        studentUsername = sharedPreferences.getString(getString(R.string.currentStudentUsername), "");
        currentStudentRateInGroup = sharedPreferences.getString(getString(R.string.currentStudentRateInGroup), "not found");
        currentStudentRateInSchool = sharedPreferences.getString(getString(R.string.currentStudentRateInSchool), "not found");
        confirmedRequestsNumber = sharedPreferences.getInt(getString(R.string.currentStudentConfirmedRequests), 0);
        deniedRequestsNumber = sharedPreferences.getInt(getString(R.string.currentStudentDeniedRequests), 0);
        currentStudentID = sharedPreferences.getString(getString(R.string.currentStudentID), "");
        Log.d(TAG, "loadSharedPreferences: currentStudentID: " + currentStudentID);
    }

    public void getUserImage(){
//        Query query = mDatabaseRef.child(studentGroup).orderByChild("email").equalTo(user.getEmail());
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot student : dataSnapshot.getChildren()){
//                    image_pathValue = student.getValue(Student.class).getImage_path();
//                    try {
//                        Glide.with(StudentProfile_activity2.this).load(image_pathValue).into(userAvatar);
//                    }catch (Exception e){
//                        Log.d(TAG, "onDataChange: " + e.getMessage());
//                    }
//                }
//                if(image_pathValue.isEmpty()){
//                    try {
//                        Glide.with(StudentProfile_activity2.this).load("https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png").into(userAvatar);
//                    }catch (Exception e){
//                        Log.d(TAG, "onDataChange: " + e.getMessage());
//                    }
//                }
//                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        student$db.whereEqualTo("email", user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    Student student = documentSnapshot.toObject(Student.class);
                    image_pathValue = student.getImage_path();
                    try {
                        Glide.with(StudentProfile_activity2.this).load(image_pathValue).into(userAvatar);
                    }catch (Exception e1){
                        Log.d(TAG, "onDataChange: " + e1.getMessage());
                    }
                }
                if(image_pathValue.isEmpty()){
                    try {
                        Glide.with(StudentProfile_activity2.this).load("https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png").into(userAvatar);
                    }catch (Exception e1){
                        Log.d(TAG, "onDataChange: " + e1.getMessage());
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void getBitmapPath(Bitmap bitmap) {
        userAvatar.setImageBitmap(bitmap);
        uploadBitmap(bitmap);
    }

    @Override
    public void getUriPath(Uri uri) {
        Glide.with(StudentProfile_activity2.this).load(uri).into(userAvatar);
        uploadUri(uri);
    }

    public void uploadBitmap(Bitmap bitmap){
        BackGroundResize resize = new BackGroundResize(bitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    public void uploadUri(Uri uri){
        BackGroundResize resize = new BackGroundResize(null);
        resize.execute(uri);
    }

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
            Toast.makeText(StudentProfile_activity2.this, "Compressing image...", Toast.LENGTH_SHORT).show();
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

    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public void executeUploadTask(){
        Toast.makeText(this, "Uploading image", Toast.LENGTH_SHORT).show();
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
