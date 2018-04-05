package com.hanssen.lab4;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends AppCompatActivity {

    String username;
    ArrayList<HashMap<String, String>> messages = new ArrayList<>();
    DatabaseReference DBMessages, DBUsers;
    SharedPreferences sharedPref = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }


    private void setup() {
        if (!isMyServiceRunning()){
            startService(new Intent(this, update.class));
        }

        sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);
        validateUser();

        DBMessages = FirebaseDatabase.getInstance().getReference("messages");
        DBUsers    = FirebaseDatabase.getInstance().getReference("users");

        addListenerOnSendButton();
        addListenerOnDatabase();

        updateListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.users:
                startActivity(new Intent(Main.this, Users.class));
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void addListenerOnSendButton() {
        Button button = findViewById(R.id.sendbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText input = findViewById(R.id.msgInput);
                String text = input.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(Main.this, "Invalid message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(text);
                    input.setText("");
                }
            }
        });
    }

    private void addListenerOnDatabase() {
        DBMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                HashMap<String, String> m = (HashMap<String, String>) map.get("message");

                String user = m.get("username");
                String text = m.get("message");

                if (!text.isEmpty() && !user.isEmpty()) {
                    parseMessages(new Message(user, text));
                    updateListView();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database", "Failed to read value.", databaseError.toException());
            }
        });
    }


    private void validateUser() {
        String name = sharedPref.getString("username", "");

        Log.e("PREF", name);

        if (name.isEmpty()) {
            startActivity(new Intent(Main.this, Login.class));
        } else {
            username = name;
        }
    }


    private void updateListView() {
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(new messageAdapter(this, username, messages));
    }


    private void parseMessages(Message msg) {
        if (isMuted(msg.username)) return;

        HashMap<String, String> map = new HashMap<>();

        map.put("user", msg.username);
        map.put("message", msg.message);

        messages.add(map);
    }

    private boolean isMuted(String user) {
        return sharedPref.getBoolean("MUTE"+user, false);
    }


    private void sendMessage(String text) {
        DBMessages.child(hash()).child("message").setValue(new Message(username, text));
    }


    private String hash() {
        return UUID.randomUUID().toString().replace("-", "");
    }



    private void logout() {
        DBUsers.child(username).removeValue();

        SharedPreferences sharedPref = getSharedPreferences("preferences",0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.remove("username");
        prefEditor.remove("muted");
        prefEditor.apply();

        startActivity(new Intent(Main.this, Login.class));
    }




    private boolean isMyServiceRunning() {
        // Checks if the background service is running.
        ActivityManager manager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (update.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }




}