package com.it_score.admin.uscore001.activities;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.fragments.AllRegisterRequests;
import com.it_score.admin.uscore001.fragments.ConfirmedRegisterRequests;
import com.it_score.admin.uscore001.fragments.DeniedRegisterRequests;
import com.it_score.admin.uscore001.util.SectionPagerAdapter;


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
        actionBar.setTitle("Регистарция ученика");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.info:{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog
                .setMessage("Как учитель, Вы можете принимать или отклонять заявки на регистрацию учеников в ВАШ КЛАСС. Если Вы приняли заявку, то ученик будет добавлен в Ваш класс, иначе он не будет добавлен в систему USCORE. Добавляйте ученка, если он действительно из Вашего Класса");
                alertDialog.setTitle("Подробная информация");
                alertDialog.setPositiveButton("Спасибо", positivieButtonOnClickListener);
                alertDialog.show();
                break;
            }
        }
        return true;
    }


    DialogInterface.OnClickListener positivieButtonOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

}
