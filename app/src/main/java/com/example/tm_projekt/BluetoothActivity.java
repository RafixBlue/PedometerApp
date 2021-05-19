package com.example.tm_projekt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int BLUETOOTH_REQ_CODE = 1;
    static List<String> spinner_bluetooth_name = new ArrayList<String>(); //zmianna przechowująca listę nazw sparowanych urządzeń
    static List<String> spinner_bluetooth_adress = new ArrayList<String>();//zmianna przechowująca listę adresów sparowanych urządzeń

    BluetoothAdapter my_Bluetooth_Adapter = BluetoothAdapter.getDefaultAdapter();//adapter bluetooth
    private String deviceName = null; //nazwa wybranego urządzenia
    private String deviceAddress; //adres wybranego urządzenia


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

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
    }


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

    /*public void Connect_Click(View view) {//Łaczy telefon z wybranym w liscie urzadzeniem.
        if(!my_Bluetooth_Adapter.isEnabled())
        {
            TextView TV_Status_Connect = findViewById(R.id.Text_View_Connect);
            TV_Status_Connect.setText("Włącz Bluetooth");
        }
        else
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }
    }*/

}