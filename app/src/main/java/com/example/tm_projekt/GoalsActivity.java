package com.example.tm_projekt;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GoalsActivity extends AppCompatActivity {

    String LANGUAGE_TYPE = "no";



    public static final String PREFERENCES = "preferences";
    public static final String LANGUAGE = "language";
    public static final String GOAL = "goal";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);


        LoadPreferences();
        Language_Change();

        Intent intent = new Intent(this,Service_Bluetooth.class);

        TextView goal = findViewById(R.id.TextNumber_Goal);
        //goal.setText(0);



    }



    ///////////////////// Shared Preferences \\\\\\\\\\\\\\\\\\\\
    public void LoadPreferences()
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE,"english");
    }

    public void savePreferences(int goal)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(GOAL,goal);
        editor.commit();
    }


    //////// Language Change \\\\\\\\\
    public void Language_Change()
    {
        TextView textView = findViewById(R.id.textView);

        if(LANGUAGE_TYPE.equals("polish"))
        {
            textView.setText("English");
        }

        if(LANGUAGE_TYPE.equals("english"))
        {
            textView.setText("Polski");
        }

    }

    public void onClick_start_intent(View view) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(new Intent(this,Service_Bluetooth.class));
        }
        else
        {
            startService(new Intent(this,Service_Bluetooth.class));
        }

    }

    public void onClick_stop_intent(View view) {

        stopService(new Intent(this,Service_Bluetooth.class));

    }

    public void onClick_back(View view) {
        finish();
    }

    public void onClick_aply_goal(View view) {
        TextView goal = findViewById(R.id.TextNumber_Goal);
        savePreferences(Integer.parseInt(goal.getText().toString()));
    }
}