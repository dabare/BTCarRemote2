package com.madhava.btcarremote2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class PairedDevices extends AppCompatActivity {


    private ListView pairedDeviceList;
    private BluetoothAdapter myBluetooth;
    private Set<BluetoothDevice> pairedDevices;
    private Button showDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_devices);

        pairedDeviceList = (ListView)findViewById(R.id.pairedDevicesLST);
        showDevices = (Button)findViewById(R.id.showDevicesBTN);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        }else {
            if(myBluetooth.isEnabled()){

            }else{
                Intent turnButton = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnButton,1);
            }
        }

        showDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPairedDeviceList();
            }
        });
    }


    private void setPairedDeviceList(){
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size()>0){
            for(BluetoothDevice bt: pairedDevices){
                list.add(bt.getName()+"\n"+bt.getAddress());
            }
        }else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,list);
        pairedDeviceList.setAdapter(adapter);
        pairedDeviceList.setOnItemClickListener(myListClickListner);
        myBluetooth.cancelDiscovery();
    }


    private AdapterView.OnItemClickListener myListClickListner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info = ((TextView)view).getText().toString();
            String address = info.substring(info.length()-17);
            Intent in = new Intent(PairedDevices.this,Remote.class);
            in.putExtra("address", address);
            startActivity(in);
        }
    };

    void hideSystemBars(){
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
