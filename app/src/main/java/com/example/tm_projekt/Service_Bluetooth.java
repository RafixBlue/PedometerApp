package com.example.tm_projekt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Service_Bluetooth extends Service {

    public static final String PREFERENCES = "preferences";
    public static final String GOAL = "goal";
    public static final String LANGUAGE = "language";
    int CHOOSEN_GOAL = 0;
    String LANGUAGE_TYPE = "polish";
    Database db;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LoadPreferences();
        createNotificationChannel();

        Intent intent1 = new Intent(this, GoalsActivity.class);

        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, intent1, 0);

        Date now = new Date();
        db = new Database(this);
        Cursor res = db.get_daysteps(new SimpleDateFormat("yyyy_MM_dd").format(now));
        res.moveToNext();

        String notification_text = "0/";

        if (!res.getString(res.getColumnIndex("Steps_Day")).equals("null0")) {
            IntBuffer buffer = IntBuffer.allocate(1);
            buffer.put(res.getInt(res.getColumnIndex("Steps_Day")));
            notification_text = buffer.get(0) + "/";
        }

        Notification notification = new NotificationCompat.Builder(this, "ChannelID")
                .setContentTitle("Progress: ")
                .setContentText((notification_text + CHOOSEN_GOAL))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingintent).build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);

        //updateNotification();

        startForeground(1, notification);

        return super.onStartCommand(intent, flags, startId);

    }

    private void updateNotification() {
        Date now = new Date();
        db = new Database(this);
        Cursor res = db.get_daysteps(new SimpleDateFormat("yyyy_MM_dd").format(now));
        res.moveToNext();
        IntBuffer buffer = IntBuffer.allocate(1);
        buffer.put(res.getInt(res.getColumnIndex("Steps_Day")));
        String notification_text = buffer.get(0) + "/";

        Notification notification = new NotificationCompat.Builder(this, "ChannelID")
                .setContentTitle("Progress:")
                .setContentText(notification_text + CHOOSEN_GOAL)
                .setSmallIcon(R.mipmap.ic_launcher).build();
        //.setContentIntent(pendingintent).build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }

    private void createNotificationChannel() {
        //check if os is oreo or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("ChannelID", "Foreground notification", NotificationManager.IMPORTANCE_DEFAULT);
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
    public void LoadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE, "english");
        CHOOSEN_GOAL = sharedPref.getInt(GOAL, 100000);
    }

}
