package com.example.tm_projekt;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.math.BigInteger;
import java.nio.IntBuffer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class GraphActivity extends AppCompatActivity {

    Database db;

    String month_or_day = "day";
    String fileName;
    String file_day;
    String file_month;
    String file_year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        fileName = getIntent().getStringExtra("filename");

        GraphView graph = (GraphView) findViewById(R.id.graph);

        ///////////////////filing database for testing////////////////////
        db = new Database(this);                                   //
        fill_database(db);                                              //
        //////////////////////////////////////////////////////////////////

        if (month_or_day == "day") {
            get_data_day();
            create_graph_day(get_data_day());
        }

        if (month_or_day == "month") {
            get_data_month();
            create_graph_month(get_data_month());
        }

    }


    DataPoint[] get_data_day() {
        db = new Database(this);
        IntBuffer buffer = IntBuffer.allocate(100);

        Cursor res = db.getdatafile(fileName);
        res.moveToNext();

        DataPoint[] points = new DataPoint[96];

        SimpleDateFormat X = new SimpleDateFormat("HH:mm");

        Date d;
        String string_h, string_m;
        int i = 0;

        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 4; m++) {
                string_h = Integer.toString(h);
                string_m = Integer.toString(m * 15);

                if (h < 10) { string_h = "0" + string_h; }
                if (m == 0) { string_m = "0" + string_m; }

                buffer.put(res.getInt(res.getColumnIndex("s" + string_h + "_" + string_m)));

                Calendar test = Calendar.getInstance(TimeZone.getDefault());
                test.set(2000, 1, 1, h, m * 15);
                d = test.getTime();

                points[i] = new DataPoint(d, buffer.get(i));
                i++;
            }
        }
        return points;
    }

    void create_graph_day(DataPoint[] points) {
        GraphView graph = (GraphView) findViewById(R.id.graph);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(500);

        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(9000000);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        series.setSpacing(50);

        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Format formatter = new SimpleDateFormat("HH:mm");
                    return formatter.format(new Date((long) value));
                }
                return super.formatLabel(value, isValueX);
            }
        });

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                long time_bar = ((long) (dataPoint.getX()));
                Date test = new Date(time_bar);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                String time_bar_time_format = formatter.format(test);

                //Todo: Dopracować języki
                Toast.makeText(getApplicationContext(), "Series1: On Data Point clicked: " + time_bar_time_format + " " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    DataPoint[] get_data_month()
    {
        int max_days_in_month = set_max_days();

        String date, temp_day;

        IntBuffer buffer = IntBuffer.allocate(max_days_in_month);
        DataPoint[] points = new DataPoint[max_days_in_month];
        points[0] = new DataPoint(0, buffer.get(0));

        for (int i = 1; i < max_days_in_month; i++) {

            temp_day = String.valueOf(i);
            if (i < 10) { temp_day = "0" + temp_day; }

            date = file_year + "_" + file_month + "_" + temp_day;
            Cursor res = db.getdatafile(date);
            res.moveToNext();

            try {
                buffer.put(res.getInt(res.getColumnIndex("Steps_Day")));
                points[i] = new DataPoint(i, buffer.get(i - 1));
            } catch (Exception No_data_for_that_day) {
                points[i] = new DataPoint(i, 10);
            }

            buffer.clear();
            buffer.position(0);
        }

        return points;
    }

    void create_graph_month(DataPoint[] points)
    {
        GraphView graph = (GraphView) findViewById(R.id.graph);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(20);

        graph.getGridLabelRenderer().setHumanRounding(true);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(31);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        series.setSpacing(50);

        graph.addSeries(series);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                long time_bar = ((long) (dataPoint.getX()));
                Date test = new Date(time_bar);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                String time_bar_time_format = formatter.format(test);

                //Todo: Dopracować 2 języki
                Toast.makeText(getApplicationContext(), "Series1: On Data Point clicked: " + dataPoint.getX() + " " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    int set_max_days()
    {
        int max_days_month=30;

        if(file_month.equals("2"))
        {
            max_days_month =29;
        }
        if(file_month.equals("1")||file_month.equals("3")||file_month.equals("5")||file_month.equals("7")||file_month.equals("8")||file_month.equals("10")||file_month.equals("12"))
        {
            max_days_month =31;
        }
        if(file_month.equals("4")||file_month.equals("6")||file_month.equals("9")||file_month.equals("11"))
        {
            max_days_month =30;
        }

        return max_days_month;
    }




    void fill_database(Database temp_db)
    {
        file_day = getIntent().getStringExtra("file_day");
        file_month = getIntent().getStringExtra("file_month");
        file_year = getIntent().getStringExtra("file_year");

        try {
            temp_db.insert(file_day,file_month,file_year);
            temp_db.replace_day(file_day,file_month,file_year,"00","00","69");
            temp_db.replace(file_day,file_month,file_year,"00","00","22");
            temp_db.replace(file_day,file_month,file_year,"15","00","23");
            temp_db.replace(file_day,file_month,file_year,"30","00","435");
            temp_db.replace(file_day,file_month,file_year,"45","00","765");
            temp_db.replace(file_day,file_month,file_year,"00","01","422");
            temp_db.replace(file_day,file_month,file_year,"00","02","322");
            temp_db.replace(file_day,file_month,file_year,"00","03","242");
            temp_db.replace(file_day,file_month,file_year,"00","04","242");
            temp_db.replace(file_day,file_month,file_year,"00","05","242");
            temp_db.replace(file_day,file_month,file_year,"00","06","242");
            temp_db.replace(file_day,file_month,file_year,"00","07","242");
            temp_db.replace(file_day,file_month,file_year,"00","10","242");
            temp_db.replace(file_day,file_month,file_year,"00","12","242");
            temp_db.replace(file_day,file_month,file_year,"00","14","242");
            temp_db.replace(file_day,file_month,file_year,"00","16","242");
            temp_db.replace(file_day,file_month,file_year,"00","18","242");
            temp_db.replace(file_day,file_month,file_year,"00","20","242");
            temp_db.replace(file_day,file_month,file_year,"00","22","242");
            temp_db.replace(file_day,file_month,file_year,"00","23","242");
        }catch (Exception e){}
    }
}



/*
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        fileName = getIntent().getStringExtra("filename");


        GraphView graph = (GraphView) findViewById(R.id.graph);

        ///////////////////filing database for testing////////////////////
        db = new Database(this);                                   //
        fill_database(db);                                              //
        //////////////////////////////////////////////////////////////////

        if (month_or_day == "day") {
            get_data_day();
            create_graph_day(get_data_day());
        }

        if (month_or_day == "month") {
            get_data_month();
            create_graph_month(get_data_month());
        }

        if (month_or_day == "xd") {
            IntBuffer buffer = IntBuffer.allocate(100);
            //IntBuffer buffer2 = IntBuffer.allocate(100);
            Cursor res = db.getdatafile(fileName);

            res.moveToNext();

            //buffer2.put(res.getInt(res.getColumnIndex("Steps_Day")));

            DataPoint[] points = new DataPoint[96];

            int i = 0;

            SimpleDateFormat X = new SimpleDateFormat("HH:mm");
            //X.setTimeZone(TimeZone.getDefault());
            Date d;


            long xvalue = 86400000;

            for (int h = 0; h < 24; h++) {
                for (int m = 0; m < 4; m++) {
                    String sh = Integer.toString(h);
                    String sm = Integer.toString(m * 15);
                    if (h < 10) {
                        sh = "0" + sh;
                    }
                    if (m == 0) {
                        sm = "0" + sm;
                    }
                    buffer.put(res.getInt(res.getColumnIndex("s" + sh + "_" + sm)));
                    //Date d = new Date(xvalue);
                    //d.setTime(0);
                    //Calendar test = Calendar.getInstance(TimeZone.getDefault());

                    Calendar test = Calendar.getInstance(TimeZone.getDefault());
                    test.set(2000, 1, 1, h, m * 15);
                    d = test.getTime();
                    points[i] = new DataPoint(d, buffer.get(i));

                    //xvalue = xvalue + 900000;

                    i++;

                }
            }

            //LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

            // set manual X bounds
            graph.getViewport().setYAxisBoundsManual(true);
            //graph.getViewport().setY
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(500);

            graph.getGridLabelRenderer().setHumanRounding(false);

            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(0);
            graph.getViewport().setMaxX(9000000);
            //graph.getViewport().
            // enable scaling and scrolling
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(true);

            series.setSpacing(50);


            graph.addSeries(series);

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        Format formatter = new SimpleDateFormat("HH:mm");
                        return formatter.format(new Date((long) value));
                    }
                    return super.formatLabel(value, isValueX);
                }
            });

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    long time_bar = ((long) (dataPoint.getX()));
                    Date test = new Date(time_bar);
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    String time_bar_time_format = formatter.format(test);

                    //Todo: Dopracować języki
                    Toast.makeText(getApplicationContext(), "Series1: On Data Point clicked: " + time_bar_time_format + " " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (month_or_day == "month") {
            int max_days_in_month = set_max_days();

            String date, temp_day;

            IntBuffer buffer = IntBuffer.allocate(max_days_in_month);
            DataPoint[] points = new DataPoint[max_days_in_month];
            points[0] = new DataPoint(0, buffer.get(0));

            for (int i = 1; i < max_days_in_month; i++) {
                temp_day = String.valueOf(i);
                if (i < 10) {
                    temp_day = "0" + String.valueOf(i);
                }

                date = file_year + "_" + file_month + "_" + temp_day;
                Cursor res = db.getdatafile(date);
                res.moveToNext();

                try {
                    buffer.put(res.getInt(res.getColumnIndex("Steps_Day")));
                    points[i] = new DataPoint(i, buffer.get(i - 1));
                } catch (Exception No_data_for_that_day) {
                    points[i] = new DataPoint(i, 10);
                }
                buffer.clear();
                buffer.position(0);
            }

            //LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

            // set manual X bounds
            graph.getViewport().setYAxisBoundsManual(true);
            //graph.getViewport().setY
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(20);

            graph.getGridLabelRenderer().setHumanRounding(true);

            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(1);
            graph.getViewport().setMaxX(31);
            //graph.getViewport().
            // enable scaling and scrolling
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(true);

            series.setSpacing(50);

            graph.addSeries(series);

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    long time_bar = ((long) (dataPoint.getX()));
                    Date test = new Date(time_bar);
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    String time_bar_time_format = formatter.format(test);

                    //Todo: Dopracować 2 języki
                    Toast.makeText(getApplicationContext(), "Series1: On Data Point clicked: " + dataPoint.getX() + " " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
 */