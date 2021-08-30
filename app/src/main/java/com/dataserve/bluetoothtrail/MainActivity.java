package com.dataserve.bluetoothtrail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    // Bluetooth Permissions
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private static final UUID MY_UUID = UUID.fromString(
            "68da1326-08f6-11ec-9a03-0242ac130003");

    // UI Elements
    TextView mStatusBlueTv;
    ImageView mBlueTv;
    Button mOnBtn,mOffBtn,mRegisterBtn;

    // Bluetooth Adaptor
    BluetoothAdapter mBlueAdaptor;

    // Bluetooth Device
    String infinitePair = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Elements
        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mBlueTv = findViewById(R.id.bluetoothTv);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mRegisterBtn = findViewById(R.id.registerBtn);

        // Adaptor
        mBlueAdaptor = BluetoothAdapter.getDefaultAdapter();

        // Activity
        AppCompatActivity mActivity = this;

        // Check if Hardware supports bluetooth
        if(mBlueAdaptor == null) {
            mStatusBlueTv.setText("Bluetooth unavailable");
            mBlueTv.setImageResource(R.drawable.ic_action_unavailable);
        }
        else{
            mStatusBlueTv.setText("Bluetooth available");
        }

        // Set Image
        mBlueTv.setImageResource(R.drawable.ic_action_off);

        // BroadcastReceiver which keeps bluetooth On
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Action
                String action = intent.getAction();

                // Bluetooth State changed
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    // Bluetooth is Off --> Turn it on
                    if (mBlueAdaptor.getState() == BluetoothAdapter.STATE_OFF) {
                        // The user bluetooth is already disabled.
                        mBlueAdaptor.enable();

                        if(infinitePair != null) {
                            Set<BluetoothDevice> pairedDevices =
                                    mBlueAdaptor.getBondedDevices();
                            for (BluetoothDevice device : pairedDevices) {
                                if (device.getName() == infinitePair) {
                                    connectPairedDevice(device);
                                    break;
                                }
                            }
                            showToast(infinitePair);
                        }
                        return;
                    }
                }
            }
        };

        // On Button
        mOnBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Permission Grant
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    checkPermission();
                }
                showToast("Turning On Bloo");
                mBlueAdaptor.enable();

                // Registering Broadcaster
                mActivity.registerReceiver(mReceiver,
                        new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
                mBlueTv.setImageResource(R.drawable.ic_action_on);
            }
        });

        // Off Button
        mOffBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    // Permission Grant
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        checkPermission();
                    }
                    showToast("Turning Off Bloo");
                    mActivity.unregisterReceiver(mReceiver);
                    mBlueTv.setImageResource(R.drawable.ic_action_off);
                    mBlueAdaptor.disable();
            }
        });

        // Register Btn
        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Permission Grant
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    checkPermission();
                }

                // Enable Bluetooth
                if(!mBlueAdaptor.isEnabled()){
                    showToast("Turn On Bluetooth");
                    return;
                }

                // Get Device List
                Set <BluetoothDevice> pairedDevices = mBlueAdaptor.getBondedDevices();
                List <String> deviceList = new ArrayList<String>();
                for(BluetoothDevice device : pairedDevices){
                    deviceList.add(device.getName());
                }
                // Final Device Names
                CharSequence[] deviceNames = deviceList.toArray(new CharSequence
                        [deviceList.size()]);


                // Register Device
                AlertDialog.Builder register = new AlertDialog.Builder(mActivity);
                register.setTitle("Register Bluetooth Device");

                // Set Choices
                register.setSingleChoiceItems(deviceNames,0,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("Selected " + deviceNames[which]);
                                infinitePair = deviceNames[which].toString();
                                // Set Device Name to Button
                                mRegisterBtn.setText(infinitePair);
                            }
                        });

                // Set OK Button
                register.setPositiveButton("Register",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("Device Registered Successfully");
                    }
                });

                // Set Cancel Button
                register.setNegativeButton("Cancel", null);

                // Execute
                register.show();
            }
        });

    }

    protected void checkPermission(){
        // Check if Permission is granted
        if(ContextCompat.checkSelfPermission(
                this,Manifest.permission.BLUETOOTH)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.BLUETOOTH_ADMIN)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)

                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.BLUETOOTH)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.BLUETOOTH_ADMIN)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app may not work correctly without"
                        + "the requested permissions"
                        );
                builder.setTitle("Permissions Required");

                // Event listener on OK button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.BLUETOOTH_ADMIN,
                                        Manifest.permission.BLUETOOTH,
                                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });

                // Set cancel button
                builder.setNeutralButton("Cancel",null);
                // Show Dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }else {
            // Do something, when permissions are already granted
            // Toast.makeText(getApplicationContext(),"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    // Decide what happens when RequestPermissions is run
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        boolean status = true;
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CODE:{
                // When request is cancelled, the results array are empty
                // Permissions Status
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        status = false;
                        break;
                    }
                }
                
                if((grantResults.length > 0) && status){
                    // Permissions are granted
                    Toast.makeText(getApplicationContext(),"Permissions granted.",
                            Toast.LENGTH_SHORT).show();

                }else {
                    // Permissions are denied
                    Toast.makeText(getApplicationContext(),"Permissions denied.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    // Function to show Toast
    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void connectPairedDevice(BluetoothDevice device){
        if(device.getBondState() == device.BOND_BONDED){
            Log.d(TAG,device.getName());
            BluetoothSocket mSocket = null;

            try {
                mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);

            } catch(IOException err){
                Log.d(TAG,"Socket not Created");
                showToast("Socket not Created");
                err.printStackTrace();
            }
            try{
                mSocket.connect();
            }catch (IOException err){
                try{
                    mSocket.close();
                    Log.d(TAG,"Cannot Connect");
                    showToast("Cannot Connect");
                }catch(IOException err1){
                    Log.d(TAG,"Socket not closed");
                    showToast("Socket not closed");
                    err1.printStackTrace();
                }
                err.printStackTrace();
            }
        }
    }
}