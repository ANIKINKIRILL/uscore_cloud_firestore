package com.it_score.admin.uscore001.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.util.BottomSheetViewPagerAdapter;

/**
 * Всплывающий снизу фрагмент с правилами наказаний и поощрений
 */

public class RulesBottomSheetFragment extends BottomSheetDialogFragment{

    // Виджеты
    private ViewPager viewPager;
    private TabLayout tabLayout;

    // Переменные
    BottomSheetViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_rules_layout, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(baseOnTabSelectedListener);
        adapter = new BottomSheetViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PromotionBottomSheetFragment(), "Поощрения");
        adapter.addFragment(new PenaltyBottomSheetFragment(), "Общие наказания");
        /*
        adapter.addFragment(new PromotionBottomSheetFragment(), "Учебные наказания");
        adapter.addFragment(new PromotionBottomSheetFragment(), "Нарушение дресс-кода");
        adapter.addFragment(new PromotionBottomSheetFragment(), "Правонарушения");
        */
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    TabLayout.BaseOnTabSelectedListener baseOnTabSelectedListener = new TabLayout.BaseOnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()){
                case 0:{
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.addedColor));
                    break;
                }
                case 1:{
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

}
