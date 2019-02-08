package com.example.admin.uscore001.dialogs;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.uscore001.R;

public class LimitScoreLeftTimeDialog extends DialogFragment {

    // widgets
    TextView left;
    TextView time;

    // Firebase

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.limit_score_left_time_dialog, container, false);

        getDialog().setTitle("Лимит и время");

        left = view.findViewById(R.id.left);
        time = view.findViewById(R.id.time);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getDialog().getContext());
        String leftScore = sharedPreferences.getString(getString(R.string.intentLimitScore), "");

        left.append(" " + leftScore);

        return view;
    }
}
