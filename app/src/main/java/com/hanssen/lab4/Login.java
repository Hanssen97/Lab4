package com.hanssen.lab4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class Login extends AppCompatActivity {

    DatabaseReference database;
    DataSnapshot      snapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setup();
    }

    private void setup() {
        database = FirebaseDatabase.getInstance().getReference("users");
        getDBSnapshot();
        addListenerOnPreferencesButton();
    }

    private void getDBSnapshot() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot s) {
                snapshot = s;
            }

            @Override
            public void onCancelled(DatabaseError e) {
                Log.e("DB ERROR", e.getMessage());
            }
        });
    }

    private void addListenerOnPreferencesButton() {
        // Starts Main activity when button is pressed.
        Button button = findViewById(R.id.enter);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);
                String username = input.getText().toString();

                if (validateUsername(username)) {
                    saveUsername(username);
                    addUserToDB(username);
                    startActivity(new Intent(Login.this, Main.class));
                }
            }
        });
    }

    private void saveUsername(String username) {
        // Saves username to cache.
        SharedPreferences sharedPref = getSharedPreferences("preferences",0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("username", username);
        prefEditor.apply();
    }

    private void addUserToDB(String username) {
        database.child(username).setValue("");
    }

    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            Toast.makeText(Login.this, "username cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (snapshot.child(username).exists()) {
            Toast.makeText(Login.this, "username already taken", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String hash() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
