package com.it_score.admin.uscore001.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.fragments.EntireSchoolTopScoreFragment;
import com.it_score.admin.uscore001.fragments.MyGroupTopScoreFragment;
import com.it_score.admin.uscore001.fragments.TeacherDoesNotHaveGroupFragment;
import com.it_score.admin.uscore001.models.Teacher;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Set;

/**
 * Активити с рейтингом учеников
 */

public class TopScore_Activity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "TopScore_Activity";

    // Виджеты
    FrameLayout frameLayout;
    ImageView backArraw;
    BottomNavigationView bottomNavigationView;
    MaterialSearchView materialSearchView;

    // Переменные
    private String teacherGroupID;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";
    public static final String ADMIN_STATUS = "26gmBm7N0oUVupLktAg6";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topscore);
        initActionBar();
        init();

        if(getUserStatus().equals(TEACHER_STATUS)){            // Пользователь = Учитель
            if(doesTeacherHasGroup()){      // У учителя есть класс
                setLaunchFragment();
            }else{                          // У учителя нет класса
                doFragmentTransaction(new TeacherDoesNotHaveGroupFragment());
            }
        }

        if(getUserStatus().equals(STUDENT_STATUS)){            // Пользователь = Ученик
            setLaunchFragment();
        }

        if(getUserStatus().equals(ADMIN_STATUS)){              // Пользователь = Администратор
            doFragmentTransaction(new EntireSchoolTopScoreFragment());
            bottomNavigationView.getMenu().findItem(R.id.myGroup).setVisible(false);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    /**
     * Инициализация ActionBar
     */

    private void initActionBar(){
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        backArraw = findViewById(R.id.back);
        frameLayout = findViewById(R.id.fragment_container);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        materialSearchView = findViewById(R.id.materialSearchView);
        backArraw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.back:{
              finish();
              break;
          }
      }
    }

    /**
     * Установка самого первого Fragment, который будет запускаться при запуске Активити
     */

    public void setLaunchFragment(){
        doFragmentTransaction(new MyGroupTopScoreFragment());
    }

    /**
     * Есть ли у учителя класс
     * @return  Если есть класс -> true      Если нет класса -> false
     */

    private boolean doesTeacherHasGroup(){
        SharedPreferences sharedPreferences = getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
        teacherGroupID = sharedPreferences.getString(Teacher.GROUP_ID, "");
        if(teacherGroupID.trim().isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * Получить статус пользователя
     * @return      статус пользователя
     */

    private String getUserStatus(){
        return Settings.getStatus();
    }

    /**
     * Замена fragment_container на fragment
     * @param fragment      Фрагмент на который будет заменен fragment_container
     */

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
        materialSearchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    MaterialSearchView.OnQueryTextListener onQueryTextListener = new MaterialSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(Settings.getStatus().equals(ADMIN_STATUS)){
                try {
                    EntireSchoolTopScoreFragment.adapter.getFilter().filter(newText);
                }catch (Exception e){
                    Log.d(TAG, "onQueryTextChange: " + e.getMessage());
                }
            }else {
                MyGroupTopScoreFragment.adapter.getFilter().filter(newText);
                try {
                    EntireSchoolTopScoreFragment.adapter.getFilter().filter(newText);
                } catch (Exception e) {
                    Log.d(TAG, "onQueryTextChange: " + e.getMessage());
                }
            }
            return true;
        }
    };

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.myGroup: {
                    doFragmentTransaction(new MyGroupTopScoreFragment());
                    break;
                }
                case R.id.entireSchool: {
                    doFragmentTransaction(new EntireSchoolTopScoreFragment());
                    break;
                }

            }
            return true;
        }
    };

}
