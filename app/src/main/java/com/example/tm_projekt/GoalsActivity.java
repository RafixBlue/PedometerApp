package com.example.tm_projekt;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GoalsActivity extends AppCompatActivity {

    public static final String PREFERENCES = "preferences";
    public static final String LANGUAGE = "language";
    public static final String GOAL = "goal";
    String LANGUAGE_TYPE = "no";
    int CHOOSEN_GOAL = 0;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        LoadPreferences();
        Language_Change();

        Intent intent = new Intent(this, Service_Bluetooth.class);

        TextView goal = findViewById(R.id.TextNumber_Goal);
        //goal.setText(0);
        System.out.println("30");
    }

    ///////////////////// Shared Preferences \\\\\\\\\\\\\\\\\\\\
    public void LoadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE, "english");
        CHOOSEN_GOAL = sharedPref.getInt(GOAL, 100000);
    }

    public void savePreferences(int goal) {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(GOAL, goal);
        editor.commit();
    }


    //////// Language Change \\\\\\\\\
    public void Language_Change() {
        TextView tv = findViewById(R.id.Information_textview);


        if (LANGUAGE_TYPE.equals("polish")) {
            tv.setText("Pick your steps goal for today");
        }

        if (LANGUAGE_TYPE.equals("english")) {
            tv.setText("Wybierz swój cel na dziś");
        }

    }

    public void onClick_start_intent(View view) {

        TextView goal = findViewById(R.id.TextNumber_Goal);
        savePreferences(Integer.parseInt(goal.getText().toString()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, Service_Bluetooth.class));
        } else {
            startService(new Intent(this, Service_Bluetooth.class));
        }


    }

    public void onClick_stop_intent(View view) {
        stopService(new Intent(this, Service_Bluetooth.class));

    }

    public void onClick_back(View view) {
        finish();
    }

    public void onClick_test(View view) {
        updateNotification();
    }

    private void updateNotification() {
        Date now = new Date();
        db = new Database(this);
        Cursor res = db.get_daysteps(new SimpleDateFormat("yyyy_MM_dd").format(now));
        res.moveToNext();
        IntBuffer buffer = IntBuffer.allocate(1);
        buffer.put(res.getInt(res.getColumnIndex("Steps_Day")));
        String notification_text = buffer.get(0) + "/";

        String text = "Progress:";
        if(LANGUAGE_TYPE.equals("polish"))
        {
            text = "Progress:";
        }
        if(LANGUAGE_TYPE.equals("english"))
        {
            text = "Wykonano:";
        }
        Notification notification = new NotificationCompat.Builder(this, "ChannelID")
                .setContentTitle(text)
                .setContentText(notification_text + CHOOSEN_GOAL)
                .setSmallIcon(R.mipmap.ic_launcher).build();


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }
}