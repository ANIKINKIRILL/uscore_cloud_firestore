package com.example.admin.uscore001.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.admin.uscore001.R;


public class SocailLinksAdapter extends ArrayAdapter {

    String[] links;
    Context context;

    public SocailLinksAdapter(@NonNull Context context, int resource, @NonNull String[] links) {
        super(context, resource, links);
        this.context = context;
        this.links = links;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = View.inflate(context, R.layout.sociallinks_list_item, null);
        return view;
    }
}
