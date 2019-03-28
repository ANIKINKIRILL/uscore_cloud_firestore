package com.it_score.admin.uscore001.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.dialogs.FilterRequestsDialog;
import com.it_score.admin.uscore001.fragments.AllRequestsFragment;
import com.it_score.admin.uscore001.fragments.NegativeRequestsFragment;
import com.it_score.admin.uscore001.fragments.NewRequestsFragment;
import com.it_score.admin.uscore001.fragments.PenaltyFragment;
import com.it_score.admin.uscore001.fragments.PositiveRequestsFragment;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.util.GlideApp;
import com.it_score.admin.uscore001.util.RequestsSectionPageAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Активити с Недавними Дейсвимями
 */

public class RecentActionsPage extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "RecentActionsPage";

    // Виджеты
    ViewPager viewPager;
    TabLayout tabs;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ImageView studentImageView, backArrow;
    CircleImageView filterMenu;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_actions_page);
        init();
        setViewPagerWithAdapter();
        if(Settings.getStatus().equals(STUDENT_STATUS)){
            setCurrentStudentData();
        }

        if(Settings.getStatus().equals(TEACHER_STATUS)){
            setCurrentTeacherData();
        }

    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        viewPager = findViewById(R.id.viewpager_container);
        tabs = findViewById(R.id.tabs);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        studentImageView = findViewById(R.id.studentImageView);
        backArrow = findViewById(R.id.backArrow);
        filterMenu = findViewById(R.id.filterMenu);
        backArrow.setOnClickListener(this);
        tabs.setOnTabSelectedListener(baseOnTabSelectedListener);
        filterMenu.setOnClickListener(this);
        filterMenu.setEnabled(false);
    }

    TabLayout.BaseOnTabSelectedListener baseOnTabSelectedListener = new TabLayout.BaseOnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:{
                    tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.startblue_transparent));
                    Log.d(TAG, "setViewPagerWithAdapter: selected tab" + tabs.getSelectedTabPosition());
                    break;
                }
                case 1:{
                    tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.startblue_transparent));
                    Log.d(TAG, "setViewPagerWithAdapter: selected tab" + tabs.getSelectedTabPosition());
                    break;
                }
                case 2:{
                    tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.addedColor));
                    Log.d(TAG, "setViewPagerWithAdapter: selected tab" + tabs.getSelectedTabPosition());
                    break;
                }
                case 3:{
                    tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.canceledColor));
                    Log.d(TAG, "setViewPagerWithAdapter: selected tab" + tabs.getSelectedTabPosition());
                    break;
                }
                case 4:{
                    tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.recentCardViewUsername));
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

    /**
     * Установливаем фрагменты в адаптер
     */

    public void setViewPagerWithAdapter(){
        RequestsSectionPageAdapter adapter = new RequestsSectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllRequestsFragment(), getResources().getString(R.string.all_tab));
        adapter.addFragment(new NewRequestsFragment(), "Новые");
        adapter.addFragment(new PositiveRequestsFragment(), getResources().getString(R.string.positive_tab));
        adapter.addFragment(new NegativeRequestsFragment(), getResources().getString(R.string.negative_tab));
        adapter.addFragment(new PenaltyFragment(), "Штрафы");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * Устанавливаем данные ученика
     */

    public void setCurrentStudentData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Student.STUDENT_DATA, MODE_PRIVATE);
        String studentImage = sharedPreferences.getString(Student.IMAGE_PATH, "");
        String studentFirstName = sharedPreferences.getString(Student.FIRST_NAME, "");
        String studentSecondName = sharedPreferences.getString(Student.SECOND_NAME, "");
        if(studentImage.isEmpty()){
            GlideApp
                    .with(RecentActionsPage.this)
                    .load("https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png")
                    .centerCrop()
                    .into(studentImageView);

        }else {
            GlideApp
                    .with(RecentActionsPage.this)
                    .load(studentImage)
                    .centerCrop()
                    .into(studentImageView);
        }
        collapsingToolbarLayout.setTitle(String.format("%s %s", studentFirstName, studentSecondName));
    }

    /**
     * Устанавливаем
     */

    public void setCurrentTeacherData(){
        SharedPreferences sharedPreferences = getSharedPreferences(Teacher.TEACHER_DATA, MODE_PRIVATE);
        String teacherImage = sharedPreferences.getString(Teacher.IMAGE_PATH, "");
        String teacherFirstName = sharedPreferences.getString(Teacher.FIRST_NAME, "");
        String teacherLastName = sharedPreferences.getString(Teacher.LAST_NAME, "");
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

        collapsingToolbarLayout.setTitle(String.format("%s %s", teacherFirstName, teacherLastName));

    }

}
