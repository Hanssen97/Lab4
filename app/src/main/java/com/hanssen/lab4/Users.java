package com.hanssen.lab4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Users extends AppCompatActivity {

    DatabaseReference   DBUsers;
    ArrayList<String>   users  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        setup();
    }

    private void setup() {
        DBUsers = FirebaseDatabase.getInstance().getReference("users");

        getDBUsers();
    }

    private void getDBUsers() {
        DBUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                Iterable<DataSnapshot> usersSnapshot = s.getChildren();
                HashMap<String, String> map = new HashMap<>();;

                for (DataSnapshot user : usersSnapshot) {
                    users.add(user.getKey());
                }

                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError e) {
                Log.e("DB ERROR", e.getMessage());
            }
        });
    }

    private void updateListView() {
        ListView listView = findViewById(R.id.userlist);
        listView.setAdapter(new userAdapter(this, users));
    }


    @Override
    public void onBackPressed() {
        // Since I am using a Store object I force activity (A1) when back button is pressed.
        startActivity(new Intent(Users.this, Main.class));
    }
}
