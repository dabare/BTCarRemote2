package com.madhava.btcarremote2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Remote extends AppCompatActivity {

    private Button leftBTN,rightBTN,fwrdBTN,revBTN,disconBTN;
    private WebView cameraWEBVW;
    private SeekBar speedSKBR;
    private TextView speedTXTVW;

    private String add;
    private String  old="", leftDown = "c" , center = "l" , rightDown = "d" , forwardDown = "a" , reverseDown = "b" , stop = "k";


    private ProgressDialog progress;
    BluetoothAdapter myBluetooth;
    BluetoothSocket btSocket;
    private boolean isBTConnected = false;
    static  final UUID myUUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        hideSystemBars();

        add = getIntent().getExtras().getString("address");
        leftBTN = (Button)findViewById(R.id.leftBTN);
        rightBTN= (Button)findViewById(R.id.rightBTN);
        fwrdBTN = (Button)findViewById(R.id.forwardBTN);
        revBTN = (Button)findViewById(R.id.reverseBTN);
        disconBTN = (Button)findViewById(R.id.disconBTN);

        cameraWEBVW = (WebView) findViewById(R.id.cameraWEBVW);

        speedSKBR = (SeekBar) findViewById(R.id.speedSKBR);

        speedTXTVW = (TextView) findViewById(R.id.speedTXTVW);


        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e){

                msg("Error");


            }
        }
        new ConnectBT().execute();

        disconBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket!=null) //If the btSocket is busy
                {
                    try
                    {
                        btSocket.close(); //close connection
                    }
                    catch (IOException e){

                        msg("Error");


                    }
                }
                finish(); //return to the first layout
            }
        });

        fwrdBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendCommand(forwardDown);
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        sendCommand(stop);
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        revBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendCommand(reverseDown);
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        sendCommand(stop);
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        leftBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendCommand(leftDown);
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        sendCommand(center);
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        rightBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendCommand(rightDown);
                        // PRESSED
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        sendCommand(center);
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
    }

    void sendCommand(String str){
        hideSystemBars();
        if (btSocket!=null && (!old.equals(str)))
        {
            try
            {
                btSocket.getOutputStream().write(str.toString().getBytes());
                old = str;
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

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

    private class ConnectBT extends AsyncTask {

        private boolean ConnectSuccess = true;
        @Override
        protected Object doInBackground(Object[] objects) {
            connect();
            return null;
        }

        void connect(){
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice dispositive = myBluetooth.getRemoteDevice(add);
            try{
                if(btSocket==null||!isBTConnected){

                    btSocket = dispositive.createInsecureRfcommSocketToServiceRecord(myUUID);


                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                    ConnectSuccess = true;
                }
            }catch (IOException e){
                e.printStackTrace();
                try {
                    Method createMethod = dispositive.getClass().getMethod("createInsecureRfcommSocket", new Class[] { int.class });
                    btSocket = (BluetoothSocket)createMethod.invoke(dispositive, 1);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                    ConnectSuccess = true;
                }catch(Exception ex){
                    ConnectSuccess = false;

                    connect();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Remote.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (!ConnectSuccess){
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }else{
                msg("Connected.");
                isBTConnected = true;
            }
            progress.dismiss();
        }


    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
