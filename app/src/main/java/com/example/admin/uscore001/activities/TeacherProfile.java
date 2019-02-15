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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.dialogs.ImageDialog;
import com.example.admin.uscore001.dialogs.TeacherSettingsDialog;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Teacher;
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
    ImageView back;
    CircleImageView imageView;
    TextView usernameView, status, emailAddress, positionView, subjectView, countAddedScore;
    Button profileSettings;
    LinearLayout showAllComments;

    // vars
    String email;
    String fullname;
    String image_path;
    String positionID;
    String subjectID;
    int addedTimes = 0;
    byte[] mUploadBytes;
    private String subject;
    private String position;
    private String teacherRequestID;
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
    CollectionReference subjects$DB = firebaseFirestore.collection("SUBJECTS$DB");
    CollectionReference positions$DB = firebaseFirestore.collection("POSITIONS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_profile_layout);
        init();
        setTeacherInfo();
    }

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = sharedPreferences.getString(getString(R.string.intentTeacherEmail), "");
        fullname = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");
        image_path = sharedPreferences.getString(getString(R.string.intentTeacherImage_path), "");
        positionID = sharedPreferences.getString(getString(R.string.intentTeacherPosition), "");
        subjectID = sharedPreferences.getString(getString(R.string.intentTeacherSubject), "");
        teacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");
        teacherID = sharedPreferences.getString("teacherID", "");

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        imageView.setOnClickListener(this);
        profileSettings.setOnClickListener(this);
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
            case R.id.showAllComments:{
                Snackbar.make(v, "Эта функция пока не доступна", Snackbar.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void getSubjectPositionByID(String subjectID, String positionID){
        subjects$DB.document(subjectID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                subject = documentSnapshot.getString("name");
                subjectView.setText(subject);
            }
        });
        positions$DB.document(positionID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                position = documentSnapshot.getString("name");
                positionView.setText(position);
            }
        });
    }

    public void setTeacherInfo(){
        getSubjectPositionByID(subjectID, positionID);
        Glide.with(TeacherProfile.this).load(image_path).into(imageView);
        usernameView.setText(fullname);
        status.setText(getResources().getString(R.string.statusTeacher));
        emailAddress.setText(email);
        getTeacherAddedTimes(teacherRequestID);
    }

    private void getTeacherAddedTimes(String teacherRequestID){
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
            Log.d(TAG, "onPreExecute: " + "Compressing image...");
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
                Task<Uri> uriTask = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        student$db.document(currentStudentID).update("image_path", uri.toString());
                        teachers$db.document(teacherID).update("image_path", uri.toString());
                    }
                });
            }
        });
    }

}
