package com.example.tm_projekt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int BLUETOOTH_REQ_CODE = 1;
    static List<String> spinner_bluetooth_name = new ArrayList<String>(); //zmianna przechowująca listę nazw sparowanych urządzeń
    static List<String> spinner_bluetooth_adress = new ArrayList<String>();//zmianna przechowująca listę adresów sparowanych urządzeń

    BluetoothAdapter my_Bluetooth_Adapter = BluetoothAdapter.getDefaultAdapter();//adapter bluetooth
    private String deviceName = null; //nazwa wybranego urządzenia
    private String deviceAddress; //adres wybranego urządzenia

    /////////////////////////bluetooth łączenie zmienne////////////////////////
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1;
    private final static int MESSAGE_READ = 2;
    static String text3 ="test";
    boolean test_connection = false;
    boolean test_connection2 = false;

    public static Handler handler;
    public static Handler Timer = new Handler(Looper.getMainLooper());

    public static boolean send_AVG = false;
    public static boolean conection_avg = false;

    String LANGUAGE_TYPE = "polish";

    public static final String PREFERENCES = "preferences";
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String LANGUAGE = "language";
    public static final String TIME = "time";

    Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        LoadPreferences();
        Language_Change();

        TextView tv_connection = findViewById(R.id.Text_View_Connect);
        TextView tv_calibration = findViewById(R.id.kalibracja_textview);

        spinner_bluetooth_name.clear();
        spinner_bluetooth_adress.clear();

        ////usunac///
        db=new Database(this);
        db.insert_AVG("User","0");
        try {
            db.insert("07","01","2021");
        }catch (Exception e){}

        if(!my_Bluetooth_Adapter.isEnabled())
        {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent,BLUETOOTH_REQ_CODE );
        }

        Set<BluetoothDevice> paired_Devices  = my_Bluetooth_Adapter.getBondedDevices();

        Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        for(BluetoothDevice bt : paired_Devices) { spinner_bluetooth_adress.add(bt.getAddress());spinner_bluetooth_name.add(bt.getName()); }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spinner_bluetooth_name);

        spin.setAdapter(adapter);

        mToastRunnable.run();

        ////////////////////handler bluetooth
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){//Obiekt odpowiedzialny za odczytywanie wiadomosci przekazywanych przez watki. Odbiera informacje o statusie połaczenia(czy udało sie je uzyskac czy nie) i wyswietla ja w odpowiednim textboxie. Ta metoda równiez odczytuje wiadomosci zawierajace wyniki pomiarów kroków i kalibracji, po odczytaniu zapisuje je w bazie danych.
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                tv_connection.setText("Połączono z " + deviceName);
                                conection_avg =true;
                                break;
                            case -1:
                                tv_connection.setText("Nie udało się uzyskać połączenia");
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString();
                        String sarduinoMsg = "000000000";
                        sarduinoMsg= arduinoMsg;
                        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if(sarduinoMsg.length()>=3)
                        {
                            if(sarduinoMsg.substring(0,3).equals("AVG"))
                            {
                                //zapisz do bazy average 3do6 liczby
                                tv_calibration.setText("Zakończono kalibrację");
                                db.replace_AVG(sarduinoMsg.substring(3,6));

                            }
                            else if(sarduinoMsg.substring(0,3).equals("STE"))
                            {
                                Date now = new Date();
                                SimpleDateFormat fday = new SimpleDateFormat("dd");
                                SimpleDateFormat fmonth = new SimpleDateFormat("MM");
                                SimpleDateFormat fyear = new SimpleDateFormat("yyyy");
                                SimpleDateFormat fmin = new SimpleDateFormat("mm");
                                SimpleDateFormat fh = new SimpleDateFormat("HH");
                                String d = fday.format(now);
                                String m = fmonth.format(now);
                                String y = fyear.format(now);
                                String mm = fmin.format(now);
                                String h = fh.format(now);
                                try{
                                    db.insert(d,m,y);
                                }
                                catch (Exception e){}
                                if(Integer.parseInt(mm) < 15) { mm="00"; }
                                else if(Integer.parseInt(mm) < 30 && Integer.parseInt(mm) >= 15) { mm="15"; }
                                else if(Integer.parseInt(mm) < 45 && Integer.parseInt(mm) >= 30) { mm="30"; }
                                else if(Integer.parseInt(mm) >= 45) { mm="45"; }
                                db.replace(d,m,y,mm,h,sarduinoMsg.substring(3));
                                test_connection = false;
                                test_connection2 = false;
                            }
                            else if(sarduinoMsg.substring(0,3).equals("TEM"))
                            {

                                editor.putString(TEMPERATURE,sarduinoMsg.substring(3));

                                editor.commit();

                            }
                            else if(sarduinoMsg.substring(0,3).equals("HUM"))
                            {
                                editor.putString(HUMIDITY,sarduinoMsg.substring(3));

                                editor.commit();
                            }
                            else if(sarduinoMsg.substring(0,3).equals("PRE"))
                            {
                                SimpleDateFormat form = new SimpleDateFormat("mm:HH dd/MM/yyyy");

                                editor.putString(PRESSURE,sarduinoMsg.substring(3));
                                editor.putString(TIME,form.format(new Date()));
                                editor.commit();
                            }
                        }
                        break;
                }
            }
        };
    }

    public void startRepeating(View v) {//Zatrzymuje metode run.
        mToastRunnable.run();
    }
    public void stopRepeating(View v) {
        Timer.removeCallbacks(mToastRunnable);
    }
    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {//Metoda nieustannie działajaca w tle aplikacji. Co pietnascie minut wysyła do Rejestratora prosbe o wysłanie wyniku pomiaru kroków jednoczesnie sprawdzajac czy urzadzenie odpowiedziało na wiadomosc. Jezeli nie odpowiedziało to w textboxie wyswietlany jest komunikat o utraconym połaczeniu. Metoda jest odpowiedzialna równiez za odczytanie z tabeli Parameters w bazie danych informacji o wyniku poprzedniej kalibracji i wysłanie jej do Rejestratora.

            SimpleDateFormat formatter = new SimpleDateFormat("mm");
            Date now = new Date();
            String time = formatter.format(now);//like 2020_12_14.txt



            if(test_connection == true)
            {
                if(test_connection2 == true)
                {
                    TextView tv_connection = findViewById(R.id.Text_View_Connect);
                    tv_connection.setText("Utracono połączenie " + deviceName);
                }
                test_connection2 = true;
            }

            if(send_AVG == false && conection_avg == true)
            {
                TextView tv = findViewById(R.id.kalibracja_textview);
                Cursor res=db.getdata();
                res.moveToFirst();
                String avg = res.getString(res.getColumnIndex("AVG_MAX"));
                if(avg.equals("0"))
                {
                    send_AVG = true;
                }
                else
                {
                    try{
                        connectedThread.write("2" + avg);
                    }catch(Exception e){ }
                    send_AVG = true;
                }
            }
            Timer.postDelayed(this, 60000);
            if(time.equals("00")||time.equals("15")||time.equals("30")||time.equals("45"))
            {
                try{
                    connectedThread.write("3");
                    test_connection = true;

                }catch(Exception e){ }
            }
            else
            {
                try{
                    connectedThread.write("4");
                }catch(Exception e){ }
            }

        }
    };


    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//Odczytuje nazwe i adres sparowanego urzadzenia wybranego z listy i zapisuje je do odpowiednich zmiennych.
        deviceAddress=spinner_bluetooth_adress.get(position);
        deviceName = spinner_bluetooth_name.get(position);
    }

    public void onNothingSelected(AdapterView<?> parent) { }

    public void RefreshList_Click(View view) {//Odswieza lub uzupełnia liste sparowanych urzadzen.
        Set<BluetoothDevice> paired_Devices  = my_Bluetooth_Adapter.getBondedDevices();
        Spinner spin = findViewById(R.id.spinner);
        spin.setAdapter(null);
        spin.setOnItemSelectedListener(this);
        spinner_bluetooth_name.clear();
        spinner_bluetooth_adress.clear();
        for(BluetoothDevice bt : paired_Devices) { spinner_bluetooth_adress.add(bt.getAddress());spinner_bluetooth_name.add(bt.getName()); }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spinner_bluetooth_name);
        spin.setAdapter(adapter);

    }

    public void Calibration_Click(View view) {//Przesyła wiadomosc do Rejestratora, która rozpoczyna kalibracje.

        TextView tv = findViewById(R.id.kalibracja_textview);
        try{
            connectedThread.write("1");
            tv.setText("Rozpoczeto kalibracje!");

        }catch(Exception e){ tv.setText("Błąd kalibracji"); }
    }

    public void Connect_Click(View view) {//Łaczy telefon z wybranym w liscie urzadzeniem.
        if(!my_Bluetooth_Adapter.isEnabled())
        {
            TextView TV_Status_Connect = findViewById(R.id.Text_View_Connect);
            TV_Status_Connect.setText("Włącz Bluetooth");
        }
        else
        {
            TextView TV_Status_Connect = findViewById(R.id.Text_View_Connect);
            TV_Status_Connect.setText("Łączenie bluetooth");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }
    }

    public void Language_Change() {

        Button calibration = findViewById(R.id.button_Kalibracja);
        Button connect = findViewById(R.id.button_connect_bluetooth);
        Button refresh = findViewById(R.id.button_refresh_b);

        if (LANGUAGE_TYPE.equals("polish")) {
            calibration.setText("Calibration");
            connect.setText("Connect");
            refresh.setText("Refresh List");
        }
        if (LANGUAGE_TYPE.equals("english")) {
            calibration.setText("Kalibracja");
            connect.setText("Połącz");
            refresh.setText("Odśwież Listę");
        }

    }

    public void LoadPreferences()
    {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE,"english");
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////Bluetooth łączenie//////////////////////////////////////////////
    public class CreateConnectThread extends Thread {


        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) //Konstruktor klasy tworzy obiekt BluetoothDevice reprezentujacy urzadzenie, z którym łaczy sie smartfon. Rozpoczyna równiez tworzenie socketa oraz pobiera UUID z BluetoothDevice. Tworzy socket i przypisuje go do zmiennej miedzyklasowej.
        {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();                                                                           //uzyskanie Uuid urzadzenia
            try{
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);                                                     //utworzenie socketa

            }
            catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() //Działajaca w tle programu metoda tworzy obiekt BluetoothAdapter w celu zakonczenia potencjalnego skanu, który mógłby spowodowac przerwanie połaczenia. Nastepnie rozpoczyna próbe połaczenia sie z Rejestratorem w wypadku niepowodzenia zamyka socket i wysyła wiadomosc o niepowodzeniu do głównej petli programu. W przeciwnym wypadku wysyła wiadomosc o powodzeniu operacji połaczenia i inicjalizuje obiekt klasy ConecctedThread
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try{
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            }
            catch (IOException connectException)
            {
                try{
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                }
                catch(IOException closeException)
                {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        public void cancel() {//Metoda zamykajaca socket połaczenia.
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

    }

    public static class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {//Konstruktor klasy odbiera obiekt socketa z klasy CreateConnectThread. Tworzy strumienie przesyłu danych
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() { //Działajaca w tle programu metoda wyczekuje na otrzymanie wiadomosci z modułu rejestratora. Po otrzymaniu wiadomosci odbiera ja i przypisuje do zmiennej typu String. Nastepnie wysyła ja do głównej klasy programu.
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            while (true) {
                try {

                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        text3=readMessage;
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(String input) { //Metoda odpowiedzialna za wysyłanie wiadomosci do rejestratora.
            byte[] bytes = input.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","X",e);
            }
        }

        public void cancel() { //Metoda zamykajaca socket połaczenia.
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
    /*@Override
    public void onBackPressed() { //Metoda konczaca połaczenie w wypadku zamkniecia aplikacji.
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }*/
}