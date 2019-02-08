package com.example.admin.uscore001.util;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ArrayList<String> framentTitles = new ArrayList<>();

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragement(Fragment fragment, String title){
        fragmentArrayList.add(fragment);
        framentTitles.add(title);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentArrayList.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return framentTitles.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
}
