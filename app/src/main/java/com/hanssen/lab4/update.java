package com.hanssen.lab4;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class update extends Service {

    DatabaseReference database;


    private static final int    NEW_CONTENT_NOTIFICATION_ID         = 1;
    private static final String NEW_CONTENT_NOTIFICATION_CHANNEL_ID = "Chatapp";

    NotificationCompat.Builder nBuilder = null;
    NotificationManager nManager        = null;

    String username = "";


    public void onCreate() {
        getUserPreferences();

        // Notification setup
        nBuilder = new NotificationCompat.Builder(this, NEW_CONTENT_NOTIFICATION_CHANNEL_ID);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // For specific SDK versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NEW_CONTENT_NOTIFICATION_CHANNEL_ID, "Chatapp", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Chat channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.setVibrationPattern(new long[]{0, 300, 100, 100, 50, 300});
            notificationChannel.enableVibration(true);
            nManager.createNotificationChannel(notificationChannel);
        }

        //DB Setup
        database = FirebaseDatabase.getInstance().getReference("messages");
        addListenerOnDatabase();
    }


    private void getUserPreferences() {
        // Gets data from cache.
        SharedPreferences sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);

        username = sharedPref.getString("username", "");

    }


    private void addListenerOnDatabase() {
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                HashMap<String, String> m = (HashMap<String, String>) map.get("message");

                String user = m.get("username");
                String text = m.get("message");

                if (
                        !text.isEmpty() &&
                        !user.isEmpty() &&
                        !user.equals(username) &&
                        !isForeground("com.hanssen.lab4") &&
                        !username.isEmpty()
                ) {
                    notifyNewContent(user, text);
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

    private void notifyNewContent(String user, String text) {
        String message = "[" + user + "]: " + text;

        nBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Chatapp")
        .setContentText(message)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(user))
        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(PendingIntent.getActivity(this, 0,
                new Intent(this, Main.class), PendingIntent.FLAG_UPDATE_CURRENT));

        nManager.notify(NEW_CONTENT_NOTIFICATION_ID, nBuilder.build());
    }


    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        //Log.d("SERVICE", "Destroyed");
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
}
