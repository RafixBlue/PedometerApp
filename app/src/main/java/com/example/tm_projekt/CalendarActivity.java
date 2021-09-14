package com.example.tm_projekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    public static final String PREFERENCES = "preferences";
    public static final String LANGUAGE = "language";
    String LANGUAGE_TYPE = "polish";
    String date_full;
    String date_day;
    String date_month;
    String date_year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        LoadPreferences();

        if (LANGUAGE_TYPE.equals("polish")) {
            Button butt = findViewById(R.id.button4);
            Button butt2 = findViewById(R.id.button3);
            TextView tv = findViewById(R.id.textview_info);
            tv.setText("Pick between monthly or day only graph");
            butt.setText("Chart Day");
            butt2.setText("Chart month");
        }

        if (LANGUAGE_TYPE.equals("english")) {
            Button butt = findViewById(R.id.button4);
            Button butt2 = findViewById(R.id.button3);
            TextView tv = findViewById(R.id.textview_info);
            tv.setText("Wybierz pomiędzy wykresem z danego dnia lub całego miesiąca");
            butt.setText("Wykres Dnia");
            butt2.setText("Wykres Miesiąca");
        }

        date_full = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        date_day = new SimpleDateFormat("dd").format(new Date());
        date_month = new SimpleDateFormat("MM").format(new Date());
        date_year = new SimpleDateFormat("yyyy").format(new Date());

        CalendarView calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) { //Wykrywa zmiany w kalendarzu i wprowadza te zmiany do zmiennych wykorzystywanych przy tworzeniu pliku. Zmienne te reprezentuja dzien, miesiac i rok.
                month = month + 1;
                String sday = "" + dayOfMonth;
                String smonth = "" + month;

                if (month < 10) {
                    smonth = "0" + smonth;
                }
                if (dayOfMonth < 10) {
                    sday = "0" + sday;
                }

                date_full = year + "_" + smonth + "_" + sday;
                date_day = sday;
                date_month = smonth;
                date_year = Integer.toString(year);
            }
        });

    }


    public void onClick_Open_Activity_Graph_Day(View view) {

        Intent intent = new Intent(CalendarActivity.this, GraphActivity.class);
        intent.putExtra("date_full", date_full);
        intent.putExtra("date_day", date_day);
        intent.putExtra("date_month", date_month);
        intent.putExtra("date_year", date_year);
        intent.putExtra("month_or_day", "day");
        startActivity(intent);

    }

    public void onClick_Open_Activity_Graph_Month(View view) {

        Intent intent = new Intent(CalendarActivity.this, GraphActivity.class);
        intent.putExtra("date_full", date_full);
        intent.putExtra("date_day", date_day);
        intent.putExtra("date_month", date_month);
        intent.putExtra("date_year", date_year);
        intent.putExtra("month_or_day", "month");
        startActivity(intent);

    }

    public void LoadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE, "polish");
    }

}