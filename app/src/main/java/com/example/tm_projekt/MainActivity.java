package com.example.tm_projekt;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    ///////////////////////// variables \\\\\\\\\\\\\\\\\\\\\\\\
    int LANGUAGE_TYPE = 1;

    public static final String PREFERENCES = "preferences";
    public static final String LANGUAGE = "language";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadPreferences();
        ChangeLanguageButton();
    }

    ////////////////////// Buttons Events \\\\\\\\\\\\\\\\\\\\\\\\\\\
    public void onClick_Language_Change(View view) {
        ChangeLanguageButton();
    }

    private void ChangeLanguageButton()
    {
        Button button_change_language = findViewById(R.id.button_change_language);

        if(LANGUAGE_TYPE == 1)
        {
            button_change_language.setBackground(getDrawable(R.drawable.british_flag));
            savePreferences(LANGUAGE_TYPE);
            LANGUAGE_TYPE = 2;
            return;
        }

        if(LANGUAGE_TYPE == 2)
        {
            button_change_language.setBackground(getDrawable(R.drawable.polish_flag));
            savePreferences(LANGUAGE_TYPE);
            LANGUAGE_TYPE = 1;
            return;
        }
    }

    ///////////////////// Shared Preferences \\\\\\\\\\\\\\\\\\\\
    private void savePreferences(int language)
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(LANGUAGE,language);
        editor.commit();
    }

    private void LoadPreferences()
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getInt(LANGUAGE,2);
    }

}