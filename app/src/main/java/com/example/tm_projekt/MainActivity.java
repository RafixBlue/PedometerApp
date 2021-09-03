package com.example.tm_projekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

public class MainActivity extends AppCompatActivity {

    //Todo: Fix design
    //Todo: Night Mode
    //Todo: Languages

    ///////////////////////// variables \\\\\\\\\\\\\\\\\\\\\\\\
    String LANGUAGE_TYPE = "polish";

    public static final String PREFERENCES = "preferences";
    public static final String LANGUAGE = "language";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadPreferences();
        ChangeLanguageButton();

        GraphView graph = (GraphView) findViewById(R.id.graph);

    }

    ////////////////////// Buttons Events \\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void onClick_Language_Change(View view) {
        ChangeLanguageButton();
    }

    private void ChangeLanguageButton()
    {
        Button button_change_language = findViewById(R.id.button_change_language);

        if(!LANGUAGE_TYPE.equals("english"))
        {
            button_change_language.setBackground(getDrawable(R.drawable.british_flag));
            savePreferences(LANGUAGE_TYPE);
            LANGUAGE_TYPE = "english";
            Language_Change();
            return;
        }

        if(!LANGUAGE_TYPE.equals("polish"))
        {
            button_change_language.setBackground(getDrawable(R.drawable.polish_flag));
            savePreferences(LANGUAGE_TYPE);
            LANGUAGE_TYPE = "polish";
            Language_Change();
            return;
        }
    }

    public void onClick_Open_Activity_Goals(View view) {
        Intent intent = new Intent(MainActivity.this,GoalsActivity.class);
        startActivity(intent);
    }

    public void onClick_Open_Activity_Bluetooth(View view) {
        Intent intent = new Intent(MainActivity.this,BluetoothActivity.class);
        startActivity(intent);
    }

    public void onClick_Open_Activity_Weather(View view) {
        Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
        startActivity(intent);
    }

    public void onClick_Open_Activity_Graph(View view) {
        Intent intent = new Intent(MainActivity.this,CalendarActivity.class);
        startActivity(intent);
    }


    ///////////////////// Shared Preferences \\\\\\\\\\\\\\\\\\\\
    public void savePreferences(String language)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(LANGUAGE,language);
        editor.commit();
    }

    public void LoadPreferences()
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE,"english");
    }

    /////////////////////// Language Change \\\\\\\\\\\\\\\\\\\\\\\\\
    public void Language_Change()
    {

        Button button_goals = findViewById(R.id.button_goals);
        Button button_charts = findViewById(R.id.button_charts);
        Button button_weather = findViewById(R.id.button_weather);
        Button button_bluetooth = findViewById(R.id.button_bluetooth);

        if(LANGUAGE_TYPE.equals("polish"))
        {
            button_goals.setText("Twoje Cele");
            button_charts.setText("Wykresy");
            button_weather.setText("Warunki Atmosferyczne");
            button_bluetooth.setText("Bluetooth");
        }
        if(LANGUAGE_TYPE.equals("english"))
        {
            button_goals.setText("Your Goals");
            button_charts.setText("Charts");
            button_weather.setText("Weather");
            button_bluetooth.setText("Bluetooth");
        }

    }


}