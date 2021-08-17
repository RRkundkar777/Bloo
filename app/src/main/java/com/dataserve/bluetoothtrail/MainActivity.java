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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    // Bluetooth Permissions
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    // UI Elements
    TextView mStatusBlueTv,mPairedTv;
    ImageView mBlueTv;
    Button mOnBtn,mOffBtn,mDiscoverBtn,mPairedBtn;

    // Bluetooth Access
    BluetoothAdapter mBlueAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Elements
        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairedTv);
        mBlueTv = findViewById(R.id.bluetoothTv);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);

        // Adaptor
        mBlueAdaptor = BluetoothAdapter.getDefaultAdapter();

        // Bluetooth Status
        if(mBlueAdaptor == null) {
            mStatusBlueTv.setText("Bluetooth unavailable");
        }
        else{
            mStatusBlueTv.setText("Bluetooth available");
        }

        // Set Image according to status
        if(mBlueAdaptor.isEnabled()){
            mBlueTv.setImageResource(R.drawable.ic_action_on);
        }
        else{
            mBlueTv.setImageResource(R.drawable.ic_action_unavailable);
        }

        // Button On Event
        mOnBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Permission Grant
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    checkPermission();
                }
                // If bluetooth is off --> Turn it on
                if(!mBlueAdaptor.isEnabled()) {
                    showToast("Turning On Bluetooth ...");
                    mBlueAdaptor.enable();
                    mBlueTv.setImageResource(R.drawable.ic_action_on);
                }
                else{
                    showToast("Bluetooth is already on");
                }
            }
        });

        // Button Discover Event
        mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Permission Grant
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    checkPermission();
                }
                // If not discoverable --> Set to Discoverable
                if(!mBlueAdaptor.isDiscovering()){
                    showToast("Making Your Device Discoverable");
                    Intent intent = new Intent (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent,REQUEST_DISCOVER_BT);
                    // Set icon
                    mBlueTv.setImageResource(R.drawable.ic_action_discoverable);
                }
                else{
                    showToast("Device is already set to discoverable");
                }
            }
        });

        mOffBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Permission Grant
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        checkPermission();
                    }
                    // If bluetooth is enabled --> disable it
                    if(mBlueAdaptor.isEnabled()) {
                        mBlueAdaptor.disable();
                        showToast("Turning Off Bluetooth");
                        mBlueTv.setImageResource(R.drawable.ic_action_off);
                    }
                    else{
                        showToast("Bluetooth is already off");
                    }
            }
        });

        mPairedBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    // Permission Grant
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        checkPermission();
                    }

                    // Check if bluetooth is on
                    if(mBlueAdaptor.isEnabled()){

                        if(mPairedTv.getText() == "U") {
                            // Update the paired devices list
                            mPairedTv.setText("Paired Devices");
                            Set<BluetoothDevice> devices = mBlueAdaptor.getBondedDevices();

                            // List the devices
                            for (BluetoothDevice device : devices) {
                                mPairedTv.append("\nDevice: " +
                                        device.getName() + "    " + "(" + device + ")" );
                            }
//                            mPairedBtn.setText("Hide Paired Devices");
                        }
                        else{
                            mPairedTv.setText("U");
                            showToast("Python");
//                            mPairedBtn.setText("Show Paired Devices");
                        }
                    }
                    else{
                        showToast("Turn on bluetooth");
                    }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    mBlueTv.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth On");
                }
                else{
                    showToast("Couldn't On Bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode, data);
    }


    protected void checkPermission(){
        // Check if Permission is granted
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
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
                    this,Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
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
                                        Manifest.permission.CAMERA,
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
                                Manifest.permission.CAMERA,
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CODE:{
                // When request is cancelled, the results array are empty
                // Permissions Status
                boolean status = true;

                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        status = false;
                        break;
                    }

                }
                if((grantResults.length > 0) && status){
                    // Permissions are granted
                    //Toast.makeText(getApplicationContext(),"Permissions granted.",
                      //      Toast.LENGTH_SHORT).show();

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
}