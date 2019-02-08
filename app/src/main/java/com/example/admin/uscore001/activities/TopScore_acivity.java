package com.example.admin.uscore001.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.fragments.EntireSchoolTopScoreFragment;
import com.example.admin.uscore001.fragments.MyGroupTopScoreFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TopScore_acivity extends AppCompatActivity {

    // widgets
    FrameLayout frameLayout;
    ImageView backArraw;
    BottomNavigationView bottomNavigationView;
    MaterialSearchView materialSearchView;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topscore);

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        backArraw = findViewById(R.id.back);
        backArraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        frameLayout = findViewById(R.id.fragment_container);
        bottomNavigationView = findViewById(R.id.bottomNavView);

        if(!currentUser.getEmail().contains("teacher")) {
            doFragmentTransaction(new MyGroupTopScoreFragment());
        }else if(currentUser.getEmail().contains("teacher")){
            doFragmentTransaction(new EntireSchoolTopScoreFragment());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.myGroup: {
                        if(!currentUser.getEmail().contains("teacher")) {
                            doFragmentTransaction(new MyGroupTopScoreFragment());
                        }
                        break;
                    }
                    case R.id.entireSchool: {
                        doFragmentTransaction(new EntireSchoolTopScoreFragment());
                        break;
                    }

                }
                return true;
            }
        });

        materialSearchView = findViewById(R.id.materialSearchView);

    }

    public void doFragmentTransaction(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_score_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(searchItem);
        return true;
    }
}
