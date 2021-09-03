package com.example.tm_projekt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

public class CalendarActivity extends AppCompatActivity {

    String fileName;
    String file_day;
    String file_month;
    String file_year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        CalendarView calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) { //Wykrywa zmiany w kalendarzu i wprowadza te zmiany do zmiennych wykorzystywanych przy tworzeniu pliku. Zmienne te reprezentuja dzien, miesiac i rok.
                month = month + 1;
                String sday = "" + dayOfMonth;
                String smonth = "" + month;
                if(month < 10)
                {
                    smonth = "0"+smonth;
                }
                if(dayOfMonth < 10)
                {
                    sday = "0" + sday;
                }
                fileName = year+"_"+smonth+"_"+sday;
                file_day = sday;
                file_month = smonth;
                file_year = Integer.toString(year);
            }
        });

    }

    public void onClick_Open_Activity_Graph(View view) {
        String fn = fileName;
        String fd = file_day;
        String fm = file_month;
        String fy = file_year;

        Intent intent = new Intent(CalendarActivity.this,GraphActivity.class);
        intent.putExtra("filename", fn);
        intent.putExtra("file_day", fd);
        intent.putExtra("file_month", fm);
        intent.putExtra("file_year", fy);
        startActivity(intent);

    }

}