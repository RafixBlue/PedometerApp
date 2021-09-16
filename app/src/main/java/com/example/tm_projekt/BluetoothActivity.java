package com.example.tm_projekt;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
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
    public static final String PREFERENCES = "preferences";
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String LANGUAGE = "language";
    public static final String TIME = "time";
    public static final String GOAL = "goal";
    private final static int CONNECTING_STATUS = 1;
    private final static int MESSAGE_READ = 2;
    /////////////////////////bluetooth łączenie zmienne////////////////////////
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    public static Handler handler;
    public static Handler Timer = new Handler(Looper.getMainLooper());

    public static boolean send_AVG = false;
    public static boolean conection_avg = false;
    static List<String> spinner_bluetooth_name = new ArrayList<String>(); //zmianna przechowująca listę nazw sparowanych urządzeń
    static List<String> spinner_bluetooth_adress = new ArrayList<String>();//zmianna przechowująca listę adresów sparowanych urządzeń
    static String text3 = "test";
    BluetoothAdapter my_Bluetooth_Adapter = BluetoothAdapter.getDefaultAdapter();//adapter bluetooth
    boolean previous_connection_status = false;
    String LANGUAGE_TYPE = "polish";
    Database db;
    int CHOOSEN_GOAL = 0;
    private String deviceName = null; //nazwa wybranego urządzenia
    private String deviceAddress; //adres wybranego urządzenia
    private final Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {

            SimpleDateFormat formatter = new SimpleDateFormat("mm");

            String time = formatter.format(new Date());//like 2020_12_14.txt

            if (send_AVG == false && conection_avg == true) {
                Cursor res = db.getdata();
                res.moveToFirst();
                String avg = res.getString(res.getColumnIndex("AVG_MAX"));
                try {
                    connectedThread.write("2" + avg);
                } catch (Exception e) {
                    Log.e(TAG, "not sent", e);
                }
                send_AVG = true;
                conection_avg = false;
            }

            Timer.postDelayed(this, 60000);

            if (previous_connection_status == true) {
                if (connectedThread.Connection_Status() != previous_connection_status) {
                    TextView tv_connection = findViewById(R.id.Text_View_Connect);
                    if(LANGUAGE_TYPE.equals("polish"))
                    {
                        tv_connection.setText("Lost connection " + deviceName);
                    }
                    if(LANGUAGE_TYPE.equals("english"))
                    {
                        tv_connection.setText("Utracono połączenie " + deviceName);
                    }
                }
                previous_connection_status = connectedThread.Connection_Status();
            }

            if (time.equals("00") || time.equals("15") || time.equals("30") || time.equals("45")) {
                try {
                    connectedThread.write("3");
                } catch (Exception e) {
                }
            }
            if (!time.equals("00") || !time.equals("15") || !time.equals("30") || !time.equals("45")) {
                try {
                    connectedThread.write("4");
                } catch (Exception e) {
                    Log.e(TAG, "doesnt work", e);
                }
            }

        }
    };

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
        db = new Database(this);
        db.insert_AVG("User", "0");
        try {
            db.insert("07", "01", "2021");
        } catch (Exception e) {
        }

        if (!my_Bluetooth_Adapter.isEnabled()) {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent, BLUETOOTH_REQ_CODE);
        }

        Set<BluetoothDevice> paired_Devices = my_Bluetooth_Adapter.getBondedDevices();

        Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        for (BluetoothDevice bt : paired_Devices) {
            spinner_bluetooth_adress.add(bt.getAddress());
            spinner_bluetooth_name.add(bt.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinner_bluetooth_name);

        spin.setAdapter(adapter);


        mToastRunnable.run();


        ////////////////////handler bluetooth////////////////////////////////
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {//Obiekt odpowiedzialny za odczytywanie wiadomosci przekazywanych przez watki. Odbiera informacje o statusie połaczenia(czy udało sie je uzyskac czy nie) i wyswietla ja w odpowiednim textboxie. Ta metoda równiez odczytuje wiadomosci zawierajace wyniki pomiarów kroków i kalibracji, po odczytaniu zapisuje je w bazie danych.
                switch (msg.what) {
                    case CONNECTING_STATUS:

                        switch (msg.arg1) {
                            case 1:
                                if (LANGUAGE_TYPE.equals("polish")) {
                                    tv_connection.setText("Connected to " + deviceName);
                                }
                                if (LANGUAGE_TYPE.equals("english")) {
                                    tv_connection.setText("Połączono z " + deviceName);
                                }

                                conection_avg = true;
                                break;
                            case -1:
                                if (LANGUAGE_TYPE.equals("polish")) {
                                    tv_connection.setText("Failed to connect");
                                }
                                if (LANGUAGE_TYPE.equals("english")) {
                                    tv_connection.setText("Nie udało się uzyskać połączenia");
                                }
                                break;
                        }
                        break;

                    case MESSAGE_READ:

                        String arduinoMsg = msg.obj.toString();
                        String sarduinoMsg = "000000000";
                        sarduinoMsg = arduinoMsg;

                        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        if (sarduinoMsg.length() >= 3) {

                            if (sarduinoMsg.startsWith("AVG")) {
                                //zapisz do bazy average 3do6 liczby
                                if (LANGUAGE_TYPE.equals("polish")) {
                                    tv_calibration.setText("Calibration compleated");
                                }
                                if (LANGUAGE_TYPE.equals("english")) {
                                    tv_calibration.setText("Zakończono kalibrację");
                                }


                                db.replace_AVG(sarduinoMsg.substring(3, 6));
                            }

                            if (sarduinoMsg.startsWith("STE")) {
                                Date now = new Date();

                                String day_time_now = new SimpleDateFormat("dd").format(now);                                            //SimpleDateFormat fday = new SimpleDateFormat("dd"); String day_time_now = fday.format(now);
                                String month_time_now = new SimpleDateFormat("MM").format(now);                                          //SimpleDateFormat fmonth = new SimpleDateFormat("MM"); String month_time_now = fmonth.format(now);
                                String year_time_now = new SimpleDateFormat("yyyy").format(now);                                         //SimpleDateFormat fyear = new SimpleDateFormat("yyyy"); String year_time_now = fyear.format(now);
                                String minutes_time_now = new SimpleDateFormat("mm").format(now);                                        //SimpleDateFormat fmin = new SimpleDateFormat("mm"); String minutes_time_now = fmin.format(now);
                                String hours_time_now = new SimpleDateFormat("HH").format(now);                                          //SimpleDateFormat fh = new SimpleDateFormat("HH"); String hours_time_now = fh.format(now);

                                try {
                                    db.insert(day_time_now, month_time_now, year_time_now);
                                } catch (Exception e) {
                                }

                                minutes_time_now = round_quarter_minute(Integer.parseInt(minutes_time_now));



                                db.replace(day_time_now, month_time_now, year_time_now, minutes_time_now, hours_time_now, sarduinoMsg.substring(3));



                                Cursor res = db.get_daysteps(new SimpleDateFormat("yyyy_MM_dd").format(now));
                                res.moveToFirst();
                                String steps = res.getString(res.getColumnIndex("Steps_Day"));


                                int isteps = Integer.parseInt(steps);

                                db.replace_kroki(sarduinoMsg.substring(3));
                                Cursor res2 = db.get_steps();
                                res2.moveToFirst();
                                int steps2 = res2.getInt(res2.getColumnIndex("kroki"));


                                if (!steps.equals("null0")) {
                                    isteps = isteps + steps2;
                                } else {
                                    isteps = steps2;
                                }


                                db.replace_day(day_time_now, month_time_now, year_time_now, minutes_time_now, hours_time_now, String.valueOf(isteps), String.valueOf(CHOOSEN_GOAL));

                                updateNotification(String.valueOf(isteps));

                            }

                            if (sarduinoMsg.startsWith("TEM")) {

                                editor.putString(TEMPERATURE, sarduinoMsg.substring(3));
                                editor.commit();
                            }

                            if (sarduinoMsg.startsWith("HUM")) {

                                editor.putString(HUMIDITY, sarduinoMsg.substring(3));
                                editor.commit();
                            }

                            if (sarduinoMsg.startsWith("PRE")) {

                                SimpleDateFormat form = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                                editor.putString(PRESSURE, sarduinoMsg.substring(3));
                                editor.putString(TIME, form.format(new Date()));
                                editor.commit();
                            }
                        }
                        break;
                }
            }
        };

    }

    String round_quarter_minute(int min) {
        if (min < 15) {
            return "00";
        }
        if (min < 30 && min >= 15) {
            return "15";
        }
        if (min < 45 && min >= 30) {
            return "30";
        }
        if (min >= 45) {
            return "45";
        }

        return "error";
    }

    private void updateNotification(String steps_day) {
        String text = "Progress:";
        if (LANGUAGE_TYPE.equals("polish")) {
            text = "Progress:";
        }
        if (LANGUAGE_TYPE.equals("english")) {
            text = "Wykonano:";
        }

        Notification notification = new NotificationCompat.Builder(this, "ChannelID")
                .setContentTitle(text)
                .setContentText(steps_day + "/" + CHOOSEN_GOAL)
                .setSmallIcon(R.mipmap.ic_launcher).build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }

    public void startRepeating(View v) {//Zatrzymuje metode run.
        mToastRunnable.run();
    }

    public void stopRepeating(View v) {
        Timer.removeCallbacks(mToastRunnable);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        deviceAddress = spinner_bluetooth_adress.get(position);
        deviceName = spinner_bluetooth_name.get(position);
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void RefreshList_Click(View view) {
        Set<BluetoothDevice> paired_Devices = my_Bluetooth_Adapter.getBondedDevices();
        Spinner spin = findViewById(R.id.spinner);
        spin.setAdapter(null);
        spin.setOnItemSelectedListener(this);
        spinner_bluetooth_name.clear();
        spinner_bluetooth_adress.clear();
        for (BluetoothDevice bt : paired_Devices) {
            spinner_bluetooth_adress.add(bt.getAddress());
            spinner_bluetooth_name.add(bt.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinner_bluetooth_name);
        spin.setAdapter(adapter);

    }

    public void Calibration_Click(View view) {

        TextView tv = findViewById(R.id.kalibracja_textview);
        try {
            connectedThread.write("1");
            if (LANGUAGE_TYPE.equals("polish")) {
                tv.setText("Calibration Started");
            }
            if (LANGUAGE_TYPE.equals("english")) {
                tv.setText("Rozpoczeto kalibracje!");
            }

        } catch (Exception e) {
            if (LANGUAGE_TYPE.equals("polish")) {
                tv.setText("Calibration Error");
            }
            if (LANGUAGE_TYPE.equals("english")) {
                tv.setText("Błąd kalibracji");
            }
        }
    }

    public void Connect_Click(View view) {
        if (!my_Bluetooth_Adapter.isEnabled()) {
            TextView TV_Status_Connect = findViewById(R.id.Text_View_Connect);
            TV_Status_Connect.setText("Włącz Bluetooth");
        } else {
            TextView TV_Status_Connect = findViewById(R.id.Text_View_Connect);
            TV_Status_Connect.setText("Łączenie bluetooth");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }
    }

    public void Language_Change() {

        Button calibration = findViewById(R.id.button_Kalibracja);
        Button connect = findViewById(R.id.button_connect_bluetooth);
        Button refresh = findViewById(R.id.button_refresh_b);
        TextView tv1 = findViewById(R.id.textView_Refresh_info);
        TextView tv2 = findViewById(R.id.textView2);
        TextView tv3 = findViewById(R.id.textView3);
        TextView tv4 = findViewById(R.id.textView4);

        if (LANGUAGE_TYPE.equals("polish")) {
            calibration.setText("Calibration");
            connect.setText("Connect");
            refresh.setText("Refresh List");
            tv1.setText("Refresh list of paired devices");
            tv2.setText("Choose the device you want to connect to");
            tv3.setText("Connect to picked device");
            tv4.setText("Calibrate your device");
        }
        if (LANGUAGE_TYPE.equals("english")) {
            calibration.setText("Kalibracja");
            connect.setText("Połącz");
            refresh.setText("Odśwież Listę");
            tv1.setText("Odświerz listę sparowanych urzadzeń");
            tv2.setText("Wybierz urządzenie z listy, z którym chcesz się połączyć");
            tv3.setText("Połącz się z wybranym urzadzeniem");
            tv4.setText("Przeprowadź kalibrację urzadzenia");
        }

    }

    public void LoadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LANGUAGE_TYPE = sharedPref.getString(LANGUAGE, "english");
        CHOOSEN_GOAL = sharedPref.getInt(GOAL, 100000);
    }

    public void cancel(View view) {
        try {
        connectedThread.cancel();
    } finally{}
    }
    //@Override
    //public void onBackPressed() {}

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            while (true) {
                try {

                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n') {
                        readMessage = new String(buffer, 0, bytes);
                        Log.e("Arduino Message", readMessage);
                        text3 = readMessage;
                        handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
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
                Log.e("Send Error", "X", e);
            }
        }

        public void cancel() { //Metoda zamykajaca socket połaczenia.
            try {
                mmSocket.close();
            } catch (IOException e) {
            }

        }

        public boolean Connection_Status() {
            return mmSocket.isConnected();
        }
    }

    ////////////////////////////Bluetooth łączenie//////////////////////////////////////////////
    public class CreateConnectThread extends Thread {


        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address)
        {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();                                                                           //uzyskanie Uuid urzadzenia
            try {
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);                                                     //utworzenie socketa

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run()
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
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