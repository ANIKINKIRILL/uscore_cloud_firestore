package com.it_score.admin.uscore001.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Admin;
import com.it_score.admin.uscore001.models.AdminFunction;
import com.it_score.admin.uscore001.util.AdminFunctionsRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Панель Администратора с добавлением группы
 */

public class AdminSectionActivity extends AppCompatActivity{

    private static final String TAG = "AdminSectionActivity";

    // Виджеты
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_section_activity);
        init();
        initActionBar();
        populateAdminFunctions();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        recyclerView = findViewById(R.id.adminFunctions);
    }

    /**
     * Наполнить список функций админа
     */

    private void populateAdminFunctions(){
        Admin.getAdminFunctionsList(mGetAdminFunctionsCallback);
    }

    private Callback mGetAdminFunctionsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<AdminFunction> adminFunctions = (ArrayList) data;
            Log.d(TAG, "execute: " + adminFunctions.size());
            AdminFunctionsRecyclerViewAdapter adminFunctionsRecyclerViewAdapter = new AdminFunctionsRecyclerViewAdapter(adminFunctions);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adminFunctionsRecyclerViewAdapter);
        }
    };

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Панель Администратора");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

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
