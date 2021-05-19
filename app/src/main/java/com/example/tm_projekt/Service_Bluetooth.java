package com.example.tm_projekt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class Service_Bluetooth extends Service {

    int CHOOSEN_GOAL = 0;

    public static final String PREFERENCES = "preferences";
    public static final String GOAL = "goal";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LoadPreferences();
        createNotificationChannel();

        Intent intent1 = new Intent(this,GoalsActivity.class);

        PendingIntent pendingintent = PendingIntent.getActivity(this,0,intent1,0);

        Notification notification = new NotificationCompat.Builder(this,"ChannelID")
                .setContentTitle("Progress:")
                .setContentText(String.valueOf(CHOOSEN_GOAL))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingintent).build();

        startForeground(1,notification);

        return super.onStartCommand(intent, flags, startId);



    }

    private void createNotificationChannel() {
        //check if os is oreo or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel("ChannelID","Foreground notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    ///////////////////// Shared Preferences \\\\\\\\\\\\\\\\\\\\
    public void LoadPreferences()
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        CHOOSEN_GOAL = sharedPref.getInt(GOAL,100000);
    }
}
