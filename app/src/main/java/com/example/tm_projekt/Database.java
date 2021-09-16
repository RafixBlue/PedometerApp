package com.example.tm_projekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {



    public Database(Context context) {
        super(context, "BazaDanych5.db", null, 1);
    }//Konstruktor klasy tworzy instancje bazy danych w plikach wewnetrznych telefonu.

    @Override
    public void onCreate(SQLiteDatabase db) {//Metoda wykonywana przy pierwszym wywołaniu klasy. Tworzy w bazie tabele Walk i Parameters.

        db.execSQL("create Table Parameters(IDAVG TEXT primary key, AVG_MAX TEXT, kroki  TEXT)");
        db.execSQL("create Table Walk(Date TEXT primary key, Day TEXT, Month TEXT,Year TEXT,Steps_Day TEXT,Steps_Goal TEXT,s00_00 TEXT,s00_15 TEXT,s00_30 TEXT,s00_45 TEXT, s01_00 TEXT, s01_15 TEXT, s01_30 TEXT, s01_45 TEXT,s02_00 TEXT,s02_15 TEXT,s02_30 TEXT,s02_45 TEXT,s03_00 TEXT,s03_15 TEXT,s03_30 TEXT,s03_45 TEXT,s04_00 TEXT,s04_15 TEXT,s04_30 TEXT,s04_45 TEXT,s05_00 TEXT,s05_15 TEXT,s05_30 TEXT,s05_45 TEXT,s06_00 TEXT,s06_15 TEXT,s06_30 TEXT,s06_45 TEXT,s07_00 TEXT,s07_15 TEXT,s07_30 TEXT,s07_45 TEXT,s08_00 TEXT,s08_15 TEXT,s08_30 TEXT,s08_45 TEXT,s09_00 TEXT,s09_15 TEXT,s09_30 TEXT,s09_45 TEXT,s10_00 TEXT,s10_15 TEXT,s10_30 TEXT,s10_45 TEXT,s11_00 TEXT,s11_15 TEXT,s11_30 TEXT,s11_45 TEXT,s12_00 TEXT,s12_15 TEXT,s12_30 TEXT,s12_45 TEXT,s13_00 TEXT,s13_15 TEXT,s13_30 TEXT,s13_45 TEXT,s14_00 TEXT,s14_15 TEXT,s14_30 TEXT,s14_45 TEXT,s15_00 TEXT,s15_15 TEXT,s15_30 TEXT,s15_45 TEXT,s16_00 TEXT,s16_15 TEXT,s16_30 TEXT,s16_45 TEXT,s17_00 TEXT,s17_15 TEXT,s17_30 TEXT,s17_45 TEXT,s18_00 TEXT,s18_15 TEXT,s18_30 TEXT,s18_45 TEXT,s19_00 TEXT,s19_15 TEXT,s19_30 TEXT,s19_45 TEXT,s20_00 TEXT,s20_15 TEXT,s20_30 TEXT,s20_45 TEXT,s21_00 TEXT,s21_15 TEXT,s21_30 TEXT,s21_45 TEXT,s22_00 TEXT,s22_15 TEXT,s22_30 TEXT,s22_45 TEXT,s23_00 TEXT,s23_15 TEXT,s23_30 TEXT,s23_45 TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop Table if exists Parameters");
    }

    public boolean insert(String day, String month, String year)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues CV = new ContentValues();

        String Date = year + "_" + month + "_" + day;

        int iyear = Integer.parseInt(year);
        iyear = iyear - 2000;
        String syear = Integer.toString(iyear);

        CV.put("Date", Date);
        CV.put("Day", day);
        CV.put("Month", month);
        CV.put("Year", syear);
        CV.put("Steps_Day", "0");
        CV.put("Steps_Goal", "10000");

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
                CV.put("s" + sh + "_" + sm, "0");
            }
        }

        long resoult = db.insert("Walk", null, CV);

        return resoult != -1;
    }

    public boolean insert_AVG(String ID, String AVG)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues CV = new ContentValues();

        CV.put("IDAVG", ID);
        CV.put("AVG_MAX", AVG);

        long resoult = db.insert("Parameters", null, CV);

        return resoult != -1;
    }

    public boolean replace(String day, String month, String year, String minutes, String hours, String steps)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues CV = new ContentValues();

        String Date = year + "_" + month + "_" + day;
        String time = "s" + hours + "_" + minutes;

        CV.put("Date", Date);
        CV.put(time, steps);

        long resoult = db.update("Walk", CV, "Date = ?", new String[]{Date});

        return resoult != -1;
    }

    public boolean replace_AVG(String AVG)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues CV = new ContentValues();

        CV.put("IDAVG", "User");
        CV.put("AVG_MAX", AVG);

        long resoult = db.update("Parameters", CV, "IDAVG = ?", new String[]{"User"});

        return resoult != -1;
    }

    public boolean replace_kroki(String kroki)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues CV = new ContentValues();

        CV.put("IDAVG", "User");
        CV.put("kroki", kroki);

        long resoult = db.update("Parameters", CV, "IDAVG = ?", new String[]{"User"});

        return resoult != -1;
    }

    public boolean replace_day(String day, String month, String year, String minutes, String hours, String steps, String goal)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues CV = new ContentValues();
        String Date = year + "_" + month + "_" + day;

        CV.put("Date", Date);
        CV.put("Steps_Day", steps);
        CV.put("Steps_Goal", goal);

        long resoult = db.update("Walk", CV, "Date = ?", new String[]{Date});

        return resoult != -1;
    }

    public Cursor getdata()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select AVG_MAX from Parameters", null);
        return cursor;
    }

    public Cursor getdatafile(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Walk Where Date =?  Group by Date", new String[]{date});
        return cursor;
    }

    public Cursor get_daysteps(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select Steps_Day from Walk Where Date =?  Group by Date", new String[]{date});
        return cursor;
    }

    public Cursor get_goal(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select Steps_Goal from Walk Where Date =?  Group by Date", new String[]{date});
        return cursor;
    }

    public Cursor get_daysteps_month(String day, String month, String year)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select Steps_Day from Walk Where Day =? and Month =? and Year =?  Group by Date", new String[]{day, month, year});
        return cursor;
    }

    public Cursor get_steps()
    {
        String user = "User";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select kroki from Parameters Where IDAVG =?  Group by IDAVG", new String[]{user});
        return cursor;
    }

}
