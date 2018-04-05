package com.hanssen.lab4;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;


public class messageAdapter extends BaseAdapter {

    private String username;
    private ArrayList<HashMap<String, String>> messages = null;
    private Context ctx;

    public messageAdapter(Context c, String user,  ArrayList<HashMap<String, String>> msglist) {
        ctx = c; username = user; messages = msglist;
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
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (messages.get(position).get("user").equals(username)) ? 0 : 1;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        int type = getItemViewType(position);

        // Inflate the layout according to the view type
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (type == 0) {
            v = inflater.inflate(R.layout.umessage, parent, false);
        }
        else {
            v = inflater.inflate(R.layout.message, parent, false);
        }

        HashMap<String, String> msg = messages.get(position);

        TextView nameView;
        TextView msgView;

        if (type == 0) {
            nameView = v.findViewById(R.id.uuser);
            msgView  = v.findViewById(R.id.umessage);
        } else {
            nameView = v.findViewById(R.id.user);
            msgView  = v.findViewById(R.id.message);
        }

        nameView.setText(msg.get("user"));
        msgView.setText(msg.get("message"));

        return v;
    }
}
