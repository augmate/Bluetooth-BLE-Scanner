package com.augmate.ble_scanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    public static final String TAG = "BeaconScanner";
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getPackageManager() == null)
        {
            Toast.makeText(this, "Failed to get package manager! :(", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is NOT supported! :(", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "BLE is supported! :)", Toast.LENGTH_SHORT).show();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            finish();
        }

        Log.d(TAG, "Starting LE Scanner..");
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                if(device != null)
                    Log.d(TAG, "BT Device: " + device.getName() + " " + device.getAddress());
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "device" + device.getName() + " " + device.getAddress() + " " + rssi);
                            if(!mLeDevices.contains(device)) {
                                mLeDevices.add(device);
                                Log.d(TAG, "UNIQUE DEVICE -> device" + device.getName() + " " + device.getAddress() + " " + rssi);
                            }
                        }
                    });
                }
            };

    private void startActivityForResult(Intent enableBtIntent,Object REQUEST_ENABLE_BT2) {
        Log.d(TAG, "BT was Enabled:" + REQUEST_ENABLE_BT2);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "Stopping LE Scanner");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}
