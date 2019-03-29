package com.it_score.admin.uscore001.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Option;
import com.it_score.admin.uscore001.models.Penalty;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.RulesListViewAdapter;

import java.util.ArrayList;

/**
 * Фрагмент с наказаниями
 */

public class PenaltyBottomSheetFragment extends Fragment {

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
        loadAllPenalties();
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
     * Загрузить наказания
     */

    private void loadAllPenalties(){
        User.getAllPenaltiesList(mGetAllPenaltiesListCallback);
    }

    private Callback mGetAllPenaltiesListCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Option> penalties = (ArrayList) data;
            adapter = new RulesListViewAdapter(penalties, getContext());
            listView.setAdapter(adapter);
        }
    };


}
