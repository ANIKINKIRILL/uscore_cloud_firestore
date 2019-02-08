package com.example.admin.uscore001.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.util.OnImageClickListener;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.dialogs.ImageDialog;
import com.example.admin.uscore001.fragments.FragmentGroup;
import com.example.admin.uscore001.fragments.FragmentProfile;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.util.SectionPagerAdapter;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class StudentProfile_activity extends AppCompatActivity implements View.OnClickListener, OnImageClickListener {

    // widgets
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView backArraw, userAvatar;
    TextView userStatus;

    // vars
    SectionPagerAdapter adapter;
    byte[] mUploadBytes;


    //Firebase STUFF
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("Students");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mRef = storage.getReference();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);

        userAvatar = findViewById(R.id.userAvatar);

        getUserImage();

        backArraw = findViewById(R.id.back);
        userAvatar.setOnClickListener(this);
        backArraw.setOnClickListener(this);
        viewPager = findViewById(R.id.viewpager_container);
        tabLayout = findViewById(R.id.tabs);
        adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragement(new FragmentProfile(), "PROFILE");
        adapter.addFragement(new FragmentGroup(), "GROUP");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        userStatus = findViewById(R.id.userStatus);

        if(!user.getEmail().contains("teacher")){
            userStatus.setText(R.string.statusStudent);
        }else{
            userStatus.setText(R.string.statusTeacher);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userAvatar:{
                ImageDialog dialog = new ImageDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
            case R.id.back:{
                finish();
                break;
            }
        }
    }

    public void getUserImage(){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            int counter = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot groups: dataSnapshot.getChildren()){
                    for(DataSnapshot student : groups.getChildren()){
                        if(student.getKey().equals(user.getEmail().replace(".", ""))){
                            String image_path = student.getValue(Student.class).getImage_path();
                            if(!image_path.trim().isEmpty()) {
                                ImageLoader.getInstance().displayImage(image_path, userAvatar);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        ImageLoader.getInstance().displayImage(uri.toString(), userAvatar);
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

    public class BackGroundResize extends AsyncTask<Uri, Integer, byte[]>{

        Bitmap bitmap;

        public BackGroundResize(Bitmap bitmap) {
            if(bitmap != null){
                this.bitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(StudentProfile_activity.this, "Compressing image...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            if(bitmap == null){
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(StudentProfile_activity.this.getContentResolver(), uris[0]);
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
                        for(DataSnapshot groups : dataSnapshot.getChildren()){
                            for(DataSnapshot student : groups.getChildren()){
                                if(student.getKey().equals(user.getEmail().replace(".",""))){
                                    student.getRef().child("image_path").setValue(downloadUri.toString());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(StudentProfile_activity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentProfile_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
