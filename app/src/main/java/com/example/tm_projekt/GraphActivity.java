package com.example.tm_projekt;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.BigInteger;
import java.nio.IntBuffer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GraphActivity extends AppCompatActivity {

    Database db;

    String fileName;
    String file_day;
    String file_month;
    String file_year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat fd = new SimpleDateFormat("dd");
        SimpleDateFormat fm = new SimpleDateFormat("MM");
        SimpleDateFormat fy = new SimpleDateFormat("yyyy");
        Date now = new Date();
        fileName = formatter.format(now);//like 2020_12_14.txt
        file_day = fd.format(now);;
        file_month = fm.format(now);;
        file_year = fy.format(now);;

        GraphView graph = (GraphView) findViewById(R.id.graph);

        //db.insert_AVG("User","0");
        db=new Database(this);
        try {
            db.insert(file_day,file_month,file_year);
            db.replace(file_day,file_month,file_year,"00","00","22");
            db.replace(file_day,file_month,file_year,"15","00","23");
            db.replace(file_day,file_month,file_year,"30","00","435");
            db.replace(file_day,file_month,file_year,"45","00","765");
            db.replace(file_day,file_month,file_year,"00","01","422");
            db.replace(file_day,file_month,file_year,"00","02","322");
            db.replace(file_day,file_month,file_year,"00","03","242");
            db.replace(file_day,file_month,file_year,"00","04","242");
            db.replace(file_day,file_month,file_year,"00","05","242");
            db.replace(file_day,file_month,file_year,"00","06","242");
            db.replace(file_day,file_month,file_year,"00","07","242");
            db.replace(file_day,file_month,file_year,"00","10","242");
            db.replace(file_day,file_month,file_year,"00","12","242");
            db.replace(file_day,file_month,file_year,"00","14","242");
            db.replace(file_day,file_month,file_year,"00","16","242");
            db.replace(file_day,file_month,file_year,"00","18","242");
            db.replace(file_day,file_month,file_year,"00","20","242");
            db.replace(file_day,file_month,file_year,"00","22","242");
            db.replace(file_day,file_month,file_year,"00","23","242");



        }catch (Exception e){}

        IntBuffer buffer = IntBuffer.allocate(100);
        Cursor res=db.getdatafile(fileName);

        res.moveToNext();

        DataPoint[] points = new DataPoint[96];
        int i = 0;

        SimpleDateFormat X = new SimpleDateFormat("HH:mm");

        long xvalue = 1625011200000L;

        for(int h=0;h < 24; h++)
        {
            for(int m=0;m < 4;m++)
            {
                String sh = Integer.toString(h);
                String sm = Integer.toString(m*15);
                if(h<10) {sh="0"+sh;}
                if(m==0) {sm="0"+sm;}

                buffer.put(res.getInt(res.getColumnIndex("s"+sh+"_"+sm)));

                Date d = new Date(xvalue);

                points[i] = new DataPoint(d, buffer.get(i));

                xvalue = xvalue + 900000;

                i++;

            }
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(500);

        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(9000000);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

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



        //SimpleDateFormat X = new SimpleDateFormat("HH:mm");
        //int date = 1624924800;
        //String Xf = X.format(date);

    }
}