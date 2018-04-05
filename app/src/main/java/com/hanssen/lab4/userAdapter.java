package com.hanssen.lab4;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;


public class userAdapter extends BaseAdapter {

    private ArrayList<String> users;
    private Context ctx;
    SharedPreferences sharedPref = null;

    public userAdapter(Context c, ArrayList<String> listOfUserNames) {
        ctx = c; users = listOfUserNames;
        sharedPref = ctx.getSharedPreferences("preferences", 0);
    }

    @Override
    public View getItem(int id) {
        return null;
    }

    @Override
    public long getItemId(int id) {
        return 0;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        // Inflate the layout according to the view type
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.user, parent, false);

        final String user = users.get(position);

        Switch muted = v.findViewById(R.id.mute);
        muted.setChecked(sharedPref.getBoolean("MUTE"+user, false));

        muted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putBoolean("MUTE" + user, isChecked);
                prefEditor.apply();
            }
        });


        TextView username = v.findViewById(R.id.username);
        username.setText(user);

        return v;
    }
}
