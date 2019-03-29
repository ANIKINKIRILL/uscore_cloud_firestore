package com.it_score.admin.uscore001.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Option;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.RulesListViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Фрагмент с поощрениями
 */

public class PromotionBottomSheetFragment extends Fragment {

    private static final String TAG = "PromotionBottomSheetFra";

    // Виджеты
    private ListView listView;

    // Переменные
    private RulesListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.promotion_fragment, container, false);
        init(view);
        loadAllOptions();
        return view;
    }

    /**
     * Инициализация виджетоы
     * @param view      окошко фрагмента
     */

    private void init(View view){
        listView = view.findViewById(R.id.listView);
    }

    /**
     * Загрузить поощрения
     */

    private void loadAllOptions(){
        User.getAllEncouragementsList(mGetAllEncouragementsListCallback);
    }

    private Callback mGetAllEncouragementsListCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Option> options = (ArrayList) data;
            adapter = new RulesListViewAdapter(options, getContext());
            listView.setAdapter(adapter);
        }
    };


}
