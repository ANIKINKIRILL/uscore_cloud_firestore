package com.example.admin.uscore001.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.dialogs.ImageDialog;
import com.example.admin.uscore001.dialogs.TeacherSettingsDialog;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.OnImageClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherProfile extends AppCompatActivity implements View.OnClickListener, OnImageClickListener{

    // widgets
    ImageView back;
    CircleImageView imageView;
    TextView usernameView, status, emailAddress, positionView, subjectView, countAddedScore;
    Button profileSettings;

    // vars
    String email;
    String fullname;
    String image_path;
    String position;
    String subject;
    int addedTimes = 0;
    byte[] mUploadBytes;

    // Firebase
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://fir-01-ff46b.firebaseio.com/");
    private DatabaseReference databaseReference = firebaseDatabase.getReference("RequestsAddingScore");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("Teachers");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mRef = storage.getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_profile_layout);

        imageView = findViewById(R.id.imageView);
        countAddedScore = findViewById(R.id.countAddedScore);
        usernameView = findViewById(R.id.username);
        status = findViewById(R.id.status);
        emailAddress = findViewById(R.id.emailAddress);
        positionView = findViewById(R.id.position);
        subjectView = findViewById(R.id.subject);
        profileSettings = findViewById(R.id.profileSettings);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = sharedPreferences.getString(getString(R.string.intentTeacherEmail), "");
        fullname = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");
        image_path = sharedPreferences.getString(getString(R.string.intentTeacherImage_path), "");
        position = sharedPreferences.getString(getString(R.string.intentTeacherPosition), "");
        subject = sharedPreferences.getString(getString(R.string.intentTeacherSubject), "");

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        imageView.setOnClickListener(this);
        profileSettings.setOnClickListener(this);

        setTeacherInfo();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                finish();
                break;
            }
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
        }
    }

    public void setTeacherInfo(){
        Glide.with(TeacherProfile.this).load(image_path).into(imageView);
        usernameView.setText(fullname);
        status.setText(getResources().getString(R.string.statusTeacher));
        emailAddress.setText(email);
        positionView.setText(position);
        subjectView.setText(subject);
        getTeacherAddedTimes(fullname);
    }

    private void getTeacherAddedTimes(String teacherName){
        databaseReference.child(teacherName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot student : dataSnapshot.getChildren()){
                    for(DataSnapshot studentRequest : student.getChildren()){
                        if(studentRequest.getValue(RequestAddingScore.class).isAnswer()){
                           addedTimes++;
                        }
                    }
                }
                countAddedScore.setText(Integer.toString(addedTimes));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getBitmapPath(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        uploadBitmap(bitmap);
    }

    @Override
    public void getUriPath(Uri uri) {
        Glide.with(TeacherProfile.this).load(uri).into(imageView);
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
            Toast.makeText(TeacherProfile.this, "Compressing image...", Toast.LENGTH_SHORT).show();
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
                final Uri downloadUri = taskSnapshot.getDownloadUrl();
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot teacher : dataSnapshot.getChildren()){
                            if(teacher.getValue(Teacher.class).getEmail().equals(user.getEmail())){
                                teacher.getRef().child("image_path").setValue(downloadUri.toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(TeacherProfile.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeacherProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
