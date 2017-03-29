package com.jelliroo.mallmapbeta.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.adapters.BeaconRecyclerAdapter;
import com.jelliroo.mallmapbeta.adapters.ClassRecyclerAdapter;
import com.jelliroo.mallmapbeta.bean.Beacon;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.RequestObject;
import com.jelliroo.mallmapbeta.bean.ScanResult;
import com.jelliroo.mallmapbeta.bean.TrainingSet;
import com.jelliroo.mallmapbeta.endpoints.BeaconEndPoint;
import com.jelliroo.mallmapbeta.endpoints.ClassEndPoint;
import com.jelliroo.mallmapbeta.endpoints.KNNEndPoint;
import com.jelliroo.mallmapbeta.enums.ClassType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrainingActivity extends AppCompatActivity {

    private WifiManager mWifiManager;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver mWifiScanReceiver, mBluetoothScanReceiver;
    private List<String> bluetoothMacs = new ArrayList<>();
    private RecyclerView recyclerView;
    private Spinner classSpinner;
    private ArrayAdapter<ClassRecord> classArrayAdapter;
    private Retrofit retrofit;
    private String TAG = "TrainingActivity";
    private BeaconRecyclerAdapter beaconAdapter;

    private TextView wifiUpdateT, bluetoothUpdateT;
    private int wifiUpdate = 0, bluetoothUpdate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.class_recycler_list);
        classSpinner = (Spinner) findViewById(R.id.class_spinner);
        wifiUpdateT = (TextView) findViewById(R.id.wifi_update);
        bluetoothUpdateT = (TextView) findViewById(R.id.bluetooth_update);

        classArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        classSpinner.setAdapter(classArrayAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        beaconAdapter = new BeaconRecyclerAdapter(new LinkedHashMap<String, Beacon>());
        recyclerView.setAdapter(beaconAdapter);

        mBluetoothScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    bluetoothMacs.add(deviceHardwareAddress);
                    Beacon beacon = beaconAdapter.getItem(deviceHardwareAddress);
                    if(beacon != null){
                        beacon.setStrength(rssi + 100);
                        beacon.setSsid(deviceName);
                        beacon.setType(Beacon.BLUETOOTH);
                    }


                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {


                    beaconAdapter.resetStrengths(Beacon.BLUETOOTH, bluetoothMacs);
                    bluetoothMacs.clear();

                    bluetoothUpdate++;
                    bluetoothUpdateT.setText("B : " + bluetoothUpdate);

                    bluetoothAdapter.startDiscovery();
                }

            }
        };

        mWifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    List<android.net.wifi.ScanResult> mScanResults = mWifiManager.getScanResults();

                    beaconAdapter.resetStrengths(Beacon.WIFI);

                    for(android.net.wifi.ScanResult scanResult : mScanResults){
                        Log.d("scan result", scanResult.SSID);
                        if(scanResult.SSID.equals("")) continue;
                        int level = WifiManager.calculateSignalLevel(mWifiManager.getConnectionInfo().getRssi(), scanResult.level);
                        int level2 = level + 100;
                        Beacon beacon = beaconAdapter.getItem(scanResult.BSSID);
                        if(beacon != null){
                            beacon.setStrength(level + 100);
                            beacon.setSsid(scanResult.SSID);
                            beacon.setType(Beacon.WIFI);
                        }
                    }

                    beaconAdapter.notifyDataSetChanged();
                    wifiUpdate++;
                    wifiUpdateT.setText("W : " + wifiUpdate);


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWifiManager.startScan();
                        }
                    }, 800);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothScanReceiver, filter);

        boolean response = bluetoothAdapter.startDiscovery();

        filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWifiScanReceiver, filter);

        mWifiManager.startScan();


        refresh();
        refreshBeacons();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        } else if(item.getItemId() == R.id.action_manage) {
            Intent intent = new Intent(this, ManageTrainingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh(){
        final ClassEndPoint classEndPoint = retrofit.create(ClassEndPoint.class);
        Call<List<ClassRecord>> callForGetAllClasses = classEndPoint.getAllClassRecordsByType(ClassType.CLASS.name());
                ;

        callForGetAllClasses.enqueue(new Callback<List<ClassRecord>>() {
            @Override
            public void onResponse(Call<List<ClassRecord>> call, Response<List<ClassRecord>> response) {
                if(response.code() == 200){
                    classArrayAdapter.clear();
                    classArrayAdapter.addAll(response.body());
                    classArrayAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Get all classes failed");
                }
            }

            @Override
            public void onFailure(Call<List<ClassRecord>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void refreshBeacons(){
        BeaconEndPoint beaconEndPoint = retrofit.create(BeaconEndPoint.class);
        Call<List<Beacon>> callForGetAllBeacons = beaconEndPoint.getAllBeacons();
        callForGetAllBeacons.enqueue(new Callback<List<Beacon>>() {
            @Override
            public void onResponse(Call<List<Beacon>> call, Response<List<Beacon>> response) {
                if(response.code() == 200){
                    for(Beacon beacon : response.body()){
                        beacon.setStrength(0);
                        beacon.setType(3);
                        beacon.setSsid("NO SERVICE");
                        beaconAdapter.addBeacon(beacon);
                    }
                    beaconAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Creation Failed");
                }
            }

            @Override
            public void onFailure(Call<List<Beacon>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(mBluetoothScanReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            unregisterReceiver(mWifiScanReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTraining(View view){
        ClassRecord classRecord = (ClassRecord) classSpinner.getSelectedItem();
        RequestObject requestObject = new RequestObject();
        requestObject.setClassRecord(classRecord);
        TrainingSet trainingSet = new TrainingSet();
        trainingSet.setBeacon1(0);
        trainingSet.setBeacon2(0);
        trainingSet.setBeacon3(0);
        trainingSet.setBeacon4(0);
        trainingSet.setBeacon5(0);

        LinkedHashMap<String, Beacon> beacons = beaconAdapter.getBeaconLinkedHashMap();
        for(Beacon beacon : beacons.values()){
            switch (beacon.getName()){
                case "beacon1":
                    trainingSet.setBeacon1(beacon.getStrength());
                    break;
                case "beacon2":
                    trainingSet.setBeacon2(beacon.getStrength());
                    break;
                case "beacon3":
                    trainingSet.setBeacon3(beacon.getStrength());
                    break;
                case "beacon4":
                    trainingSet.setBeacon4(beacon.getStrength());
                    break;
                case "beacon5":
                    trainingSet.setBeacon5(beacon.getStrength());
            }
        }

        requestObject.setTrainingSet(trainingSet);

        KNNEndPoint knnEndPoint = retrofit.create(KNNEndPoint.class);
        Call<Object> callForSendTraining = knnEndPoint.postTrainingData(requestObject);
        callForSendTraining.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.code() == 201){

                } else {
                    Log.d(TAG, "Post failed");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_training, menu);
        return true;
    }


}
