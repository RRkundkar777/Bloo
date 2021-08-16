package com.dataserve.bluetoothtrail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    private static final int BLUETOOTH_PERMISSION_CODE = 100;
    private static final int BLUETOOTH_ADMIN_PERMISSION_CODE = 101;

    TextView mStatusBlueTv,mPairedTv;
    ImageView mBlueTv;
    Button mOnBtn,mOffBtn,mDiscoverBtn,mPairedBtn;

    BluetoothAdapter mBlueAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairedTv);
        mBlueTv = findViewById(R.id.bluetoothTv);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);

        // Adaptor
        mBlueAdaptor = BluetoothAdapter.getDefaultAdapter();

        // Bluetooth
        if(mBlueAdaptor == null) {
            mStatusBlueTv.setText("Bluetooth unavailable");
        }
        else{
            mStatusBlueTv.setText("Bluetooth is available");
        }

        // Set Image according to status
        if(mBlueAdaptor.isEnabled()){
            mBlueTv.setImageResource(R.drawable.ic_action_on);
        }
        else{
            mBlueTv.setImageResource(R.drawable.ic_action_off);
        }

        mOnBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!mBlueAdaptor.isEnabled()) {
                    showToast("Turning On Bluetooth ...");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
                else{
                    showToast("Bluetooth is already on");
                }
            }
        });

        mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkPermission(Manifest.permission.BLUETOOTH_ADMIN,BLUETOOTH_ADMIN_PERMISSION_CODE);
                if(!mBlueAdaptor.isDiscovering()){
                    showToast("Making Your Device Discoverable");
                    Intent intent = new Intent (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent,REQUEST_DISCOVER_BT);
                }
            }
        });

        mOffBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                    if(mBlueAdaptor.isEnabled()){
                        mPairedTv.setText("Paired Devices");
                        Set<BluetoothDevice> devices = mBlueAdaptor.getBondedDevices();
                        for(BluetoothDevice device: devices){
                            mPairedTv.append("\nDevice" + device.getName() + "," + device);
                        }
                    }
                    else{
                        showToast("Turn on bluetooth");
                    }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(Manifest.permission.BLUETOOTH,BLUETOOTH_PERMISSION_CODE);
        checkPermission(Manifest.permission.BLUETOOTH_ADMIN,BLUETOOTH_ADMIN_PERMISSION_CODE);
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

    public void checkPermission(String permission,int requestCode){
//        if(ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED){
//            // Requesting the permission
//            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
//        }
//        else{
//            showToast("Permission Already Granted");
//        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == BLUETOOTH_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Bluetooth Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == BLUETOOTH_ADMIN_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Bluetooth Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}