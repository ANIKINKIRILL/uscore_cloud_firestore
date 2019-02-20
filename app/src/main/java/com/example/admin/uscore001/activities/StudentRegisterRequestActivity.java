package com.example.admin.uscore001.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.fragments.AllRegisterRequests;
import com.example.admin.uscore001.fragments.ConfirmedRegisterRequests;
import com.example.admin.uscore001.fragments.DeniedRegisterRequests;
import com.example.admin.uscore001.util.SectionPagerAdapter;


/**
 * Активити с запросами на регистрацию ученика
 */

public class StudentRegisterRequestActivity extends AppCompatActivity {

    private static final String TAG = "StudentRegisterRequestA";

    // widgets
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_register_request_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Запросы на регистрацию");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));

        init();
    }

    private void init(){
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragement(new AllRegisterRequests(), "Все запросы");
        adapter.addFragement(new ConfirmedRegisterRequests(), "Принятые");
        adapter.addFragement(new DeniedRegisterRequests(), "Отклоненные");
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:{
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.startblue_transparent));
                    break;
                }
                case 1:{
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.addedColor));
                    break;
                }
                case 2:{
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.canceledColor));
                    break;
                }
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

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
}
