package com.example.tm_projekt;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GoalsActivity extends AppCompatActivity {

    String LANGUAGE_TYPE = "no";

    public static final String PREFERENCES = "preferences";
    public static final String LANGUAGE = "language";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        LoadPreferences();
        Language_Change();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    ///////////////////// Shared Preferences \\\\\\\\\\\\\\\\\\\\
    public void LoadPreferences()
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE,"english");
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
}