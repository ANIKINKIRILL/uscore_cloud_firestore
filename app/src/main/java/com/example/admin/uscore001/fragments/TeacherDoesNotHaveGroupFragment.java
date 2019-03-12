package com.example.admin.uscore001.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.uscore001.R;

/**
 * Фрагмент с сообщением, что у учителя нет класса
 */

public class TeacherDoesNotHaveGroupFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_does_not_have_group, container, false);
        return view;
    }
}
