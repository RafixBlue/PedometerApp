package com.example.tm_projekt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class WeatherActivity extends AppCompatActivity {

    public static final String PREFERENCES = "preferences";
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String LANGUAGE = "language";
    public static final String TIME = "time";

    String LANGUAGE_TYPE = "polish";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather2);

        LoadPreferences();
        Language_Change();

    }

    public void LoadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE, "english");
    }

    public void Weather_Click(View view) {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        TextView TV_TEMP = findViewById(R.id.TV_TEMP);
        TextView TV_HUMI = findViewById(R.id.TV_HUMI);
        TextView TV_PRES = findViewById(R.id.TV_PRES);
        TextView TV_TIME = findViewById(R.id.TV_TIME);

        TV_TEMP.setText(sharedPref.getString(TEMPERATURE, "0"));
        TV_HUMI.setText(sharedPref.getString(HUMIDITY, "0"));
        TV_PRES.setText(sharedPref.getString(PRESSURE, "0"));
        TV_TIME.setText(sharedPref.getString(TIME, "0"));

    }

    public void Language_Change() {

        TextView temp = findViewById(R.id.textView10);
        TextView humi = findViewById(R.id.textView11);
        TextView press = findViewById(R.id.textView12);
        TextView time = findViewById(R.id.textView14);
        Button refresh = findViewById(R.id.button_refresh);

        if (LANGUAGE_TYPE.equals("polish")) {
            temp.setText("Temperature(C)");
            humi.setText("Humidity(%)");
            press.setText("Pressure(hPa)");
            time.setText("Measurement Time:");
            refresh.setText("Refresh");
        }
        if (LANGUAGE_TYPE.equals("english")) {
            temp.setText("Temperatura(C)");
            humi.setText("Wilgotność(%)");
            press.setText("Ciśnienie(hPa)");
            time.setText("Pomiar Wykonano:");
            refresh.setText("Odśwież");
        }

    }

}