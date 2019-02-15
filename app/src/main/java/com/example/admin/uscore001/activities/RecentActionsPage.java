package com.example.admin.uscore001.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.dialogs.FilterRequestsDialog;
import com.example.admin.uscore001.fragments.AllRequestsFragment;
import com.example.admin.uscore001.fragments.NegativeRequestsFragment;
import com.example.admin.uscore001.fragments.PositiveRequestsFragment;
import com.example.admin.uscore001.util.GlideApp;
import com.example.admin.uscore001.util.GlideAppModule;
import com.example.admin.uscore001.util.RequestsSectionPageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentActionsPage extends AppCompatActivity implements View.OnClickListener{

    // widgets
    ViewPager viewPager;
    TabLayout tabs;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView studentImageView, backArrow;
    CircleImageView filterMenu;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_actions_page);

        viewPager = findViewById(R.id.viewpager_container);
        tabs = findViewById(R.id.tabs);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        studentImageView = findViewById(R.id.studentImageView);
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(this);
        filterMenu = findViewById(R.id.filterMenu);
        filterMenu.setOnClickListener(this);

        if(currentUser.getEmail().contains("teacher")){
            filterMenu.setVisibility(View.INVISIBLE);
        }

        setViewPagerWithAdapter();

        if(!currentUser.getEmail().contains("teacher")) { // is a STUDENT
            setUpCurrentUserInfo();
        }else{                                            // is a TEACHER
            setUpCurrentTeacherInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backArrow:{
                finish();
                break;
            }
            case R.id.filterMenu:{
                FilterRequestsDialog dialog = new FilterRequestsDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.open_dialog));
                break;
            }
        }
    }

    public void setViewPagerWithAdapter(){
        RequestsSectionPageAdapter adapter = new RequestsSectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllRequestsFragment(), getResources().getString(R.string.all_tab));
        adapter.addFragment(new PositiveRequestsFragment(), getResources().getString(R.string.positive_tab));
        adapter.addFragment(new NegativeRequestsFragment(), getResources().getString(R.string.negative_tab));
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }

    public void setUpCurrentUserInfo(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String currentUsername = sharedPreferences.getString(getString(R.string.currentStudentUsername), "");
        String currentStudentImage = sharedPreferences.getString(getString(R.string.intentSenderImage), "");
        if(currentStudentImage.isEmpty()){
            GlideApp
                    .with(RecentActionsPage.this)
                    .load("https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png")
                    .centerCrop()
                    .into(studentImageView);

        }else {
            GlideApp
                    .with(RecentActionsPage.this)
                    .load(currentStudentImage)
                    .centerCrop()
                    .into(studentImageView);
        }
        collapsingToolbarLayout.setTitle(currentUsername);
    }

    public void setUpCurrentTeacherInfo(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String teacherImage = sharedPreferences.getString(getString(R.string.intentTeacherImage_path), "");
        String teacherFullName = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");

        if(teacherImage.isEmpty()){
            GlideApp
                    .with(RecentActionsPage.this)
                    .load("https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png")
                    .centerCrop()
                    .into(studentImageView);
        }else {
            GlideApp
                    .with(RecentActionsPage.this)
                    .load(teacherImage)
                    .centerCrop()
                    .into(studentImageView);
        }

        collapsingToolbarLayout.setTitle(teacherFullName);

    }

}
