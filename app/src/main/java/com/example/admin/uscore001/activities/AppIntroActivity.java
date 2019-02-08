package com.example.admin.uscore001.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.admin.uscore001.fragments.AppIntroSlide1;
import com.example.admin.uscore001.fragments.AppIntroSlide2;
import com.example.admin.uscore001.fragments.AppIntroSlide3;
import com.example.admin.uscore001.fragments.AppIntroSlide4;
import com.example.admin.uscore001.fragments.AppIntroSlide5;
import com.github.paolorotolo.appintro.AppIntro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AppIntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new AppIntroSlide1());
        addSlide(new AppIntroSlide2());
        addSlide(new AppIntroSlide3());
        addSlide(new AppIntroSlide4());
        addSlide(new AppIntroSlide5());

        setDepthAnimation();

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(FirebaseAuth.getInstance().getCurrentUser() != null){   // already logged in
//            Intent intent = new Intent("android.intent.action.LOGIN");
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent("android.intent.action.LOGIN");
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent("android.intent.action.LOGIN");
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
