package com.example.tm_projekt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public Database(Context context) {
        super(context, "BazaDanych.db", null, 1);
    }//Konstruktor klasy tworzy instancje bazy danych w plikach wewnetrznych telefonu.

    @Override
    public void onCreate(SQLiteDatabase db) {//Metoda wykonywana przy pierwszym wywołaniu klasy. Tworzy w bazie tabele Walk i Parameters.

        db.execSQL("create Table Parameters(IDAVG TEXT primary key, AVG_MAX TEXT)");
        db.execSQL("create Table Walk(Date TEXT primary key, Day TEXT, Month TEXT,Year TEXT,s00_00 TEXT,s00_15 TEXT,s00_30 TEXT,s00_45 TEXT, s01_00 TEXT, s01_15 TEXT, s01_30 TEXT, s01_45 TEXT,s02_00 TEXT,s02_15 TEXT,s02_30 TEXT,s02_45 TEXT,s03_00 TEXT,s03_15 TEXT,s03_30 TEXT,s03_45 TEXT,s04_00 TEXT,s04_15 TEXT,s04_30 TEXT,s04_45 TEXT,s05_00 TEXT,s05_15 TEXT,s05_30 TEXT,s05_45 TEXT,s06_00 TEXT,s06_15 TEXT,s06_30 TEXT,s06_45 TEXT,s07_00 TEXT,s07_15 TEXT,s07_30 TEXT,s07_45 TEXT,s08_00 TEXT,s08_15 TEXT,s08_30 TEXT,s08_45 TEXT,s09_00 TEXT,s09_15 TEXT,s09_30 TEXT,s09_45 TEXT,s10_00 TEXT,s10_15 TEXT,s10_30 TEXT,s10_45 TEXT,s11_00 TEXT,s11_15 TEXT,s11_30 TEXT,s11_45 TEXT,s12_00 TEXT,s12_15 TEXT,s12_30 TEXT,s12_45 TEXT,s13_00 TEXT,s13_15 TEXT,s13_30 TEXT,s13_45 TEXT,s14_00 TEXT,s14_15 TEXT,s14_30 TEXT,s14_45 TEXT,s15_00 TEXT,s15_15 TEXT,s15_30 TEXT,s15_45 TEXT,s16_00 TEXT,s16_15 TEXT,s16_30 TEXT,s16_45 TEXT,s17_00 TEXT,s17_15 TEXT,s17_30 TEXT,s17_45 TEXT,s18_00 TEXT,s18_15 TEXT,s18_30 TEXT,s18_45 TEXT,s19_00 TEXT,s19_15 TEXT,s19_30 TEXT,s19_45 TEXT,s20_00 TEXT,s20_15 TEXT,s20_30 TEXT,s20_45 TEXT,s21_00 TEXT,s21_15 TEXT,s21_30 TEXT,s21_45 TEXT,s22_00 TEXT,s22_15 TEXT,s22_30 TEXT,s22_45 TEXT,s23_00 TEXT,s23_15 TEXT,s23_30 TEXT,s23_45 TEXT)");
        db.execSQL("create Table Weather(ID TEXT primary key, Temperature TEXT,Humidity TEXT,Pressure TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop Table if exists Parameters");
    }

    public boolean insert(String day,String month,String year)//Tworzy w tabeli Walk zapis z danego dnia wypełniajac pola data, dzien, miesiac, rok. Pozostałe 96 rekordów odpowiadajacych ilosci kroków w danym kwadransie wypełnia zerami.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues xd = new ContentValues();
        String Date = year+"_"+month+"_"+day;
        int iyear = Integer.parseInt(year);
        iyear = iyear-2000;
        String syear= Integer.toString(iyear);
        xd.put("Date",Date);
        xd.put("Day",day);
        xd.put("Month",month);
        xd.put("Year",syear);
        for(int h=0;h < 24; h++)
        {
            for(int m=0;m < 4;m++)
            {
                String sh = Integer.toString(h);
                String sm = Integer.toString(m*15);
                if(h<10) {sh="0"+sh;}
                if(m==0) {sm="0"+sm;}
                xd.put("s"+sh+"_"+sm,"0");
            }
        }
        //
        long resoult = db.insert("Walk",null, xd);

        if(resoult==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean insert_AVG(String ID, String AVG)//Tworzy w tabeli Parameters wpis z kalibracji. Wpisuje nazwe uzytkownika oraz przypisuje wartosc zero w miejsce wartosci kalibracji.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues xd = new ContentValues();
        xd.put("IDAVG",ID);
        xd.put("AVG_MAX",AVG);
        long resoult = db.insert("Parameters",null, xd);

        if(resoult==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    public boolean replace(String day,String month,String year,String minutes,String hours,String steps)//Wpisuje do stworzonego wczesniej wpisu w tabeli Walk wartosc z ostatniego otrzymanego pomiaru kroków. Miejsce w tabeli, do którego wpisywana jest wartosc zalezy od godziny otrzymania pomiaru.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues xd = new ContentValues();
        String Date = year+"_"+month+"_"+day;
        String time = "s"+hours+"_"+minutes;
        xd.put("Date",Date);
        xd.put(time,steps);

        long resoult = db.update("Walk", xd, "Date = ?", new String[]{Date});
        if(resoult==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean replace_AVG(String AVG)//Aktualizuje w bazie wynik kalibracji poprzez zastapienie starego nowym.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues xd = new ContentValues();
        xd.put("IDAVG","User");
        xd.put("AVG_MAX",AVG);
        long resoult = db.update("Parameters", xd, "IDAVG = ?", new String[]{"User"});
        if(resoult==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getdata()//Odczytuje wartosc kalibracji(AVG MAX) zapisanej w tabeli Parameters i wpisuje ja do zmiennej.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select AVG_MAX from Parameters",null);
        return cursor;
    }

    public Cursor getdatafile(String date)//Tworzy liste na podstawie danych zamieszczonych w tabeli Walk. Lista zawiera pomiary z wybranego dnia.
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Walk Where Date =?  Group by Date",new String[]{date});
        return cursor;
    }

}
