package com.example.admin.uscore001.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.util.BottomSheetViewPagerAdapter;

public class RulesBottomSheetFragment extends BottomSheetDialogFragment {

    // widgets
    private ViewPager viewPager;
    private TabLayout tabLayout;

    // vars
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
        adapter = new BottomSheetViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PromotionBottomSheetFragment(), "Поощрения");
        adapter.addFragment(new PromotionBottomSheetFragment(), "Общие наказания");
        adapter.addFragment(new PromotionBottomSheetFragment(), "Учебные наказания");
        adapter.addFragment(new PromotionBottomSheetFragment(), "Нарушение дресс-кода");
        adapter.addFragment(new PromotionBottomSheetFragment(), "Правонарушения");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
