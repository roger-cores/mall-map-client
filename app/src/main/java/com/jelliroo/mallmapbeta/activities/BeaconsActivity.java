package com.jelliroo.mallmapbeta.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.adapters.BeaconRecyclerAdapter;
import com.jelliroo.mallmapbeta.bean.Beacon;
import com.jelliroo.mallmapbeta.bean.ScanResult;
import com.jelliroo.mallmapbeta.endpoints.BeaconEndPoint;
import com.jelliroo.mallmapbeta.vholders.BeaconViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BeaconsActivity extends AppCompatActivity {

    private WifiManager mWifiManager;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver mWifiScanReceiver, mBluetoothScanReceiver;
    private Spinner beaconSpinner;
    private Switch mSwitch;
    private EditText nameEditText;
    private RecyclerView beaconRecyclerView;
    private BeaconRecyclerAdapter beaconAdapter;
    private WifiListAdapter adapter1;
    private ScanResult scanResult1;
    private String selectedBSSID1;

    private Retrofit retrofit;
    private String TAG = "BeaconActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        beaconSpinner = (Spinner) findViewById(R.id.beacon_spinner);
        mSwitch = (Switch) findViewById(R.id.beacon_type_switch);
        nameEditText = (EditText) findViewById(R.id.beacon_name);
        beaconRecyclerView = (RecyclerView) findViewById(R.id.beacon_recycler);
        beaconAdapter = new BeaconRecyclerAdapter(new LinkedHashMap<String, Beacon>());
        beaconRecyclerView.setAdapter(beaconAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        beaconRecyclerView.setLayoutManager(mLayoutManager);
        beaconRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(beaconRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        beaconRecyclerView.addItemDecoration(dividerItemDecoration);


        refreshData();

        adapter1 = new WifiListAdapter(this, R.layout.beacon_item);
        beaconSpinner.setAdapter(adapter1);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                BeaconEndPoint beaconEndPoint = retrofit.create(BeaconEndPoint.class);
                final String name = ((BeaconViewHolder) viewHolder).beaconName.getText().toString();
                final String mac = ((BeaconViewHolder) viewHolder).beaconRssiSsid.getText().toString();
                Call<Object> callForDeleteBeacon = beaconEndPoint.deleteBeacon(name);
                callForDeleteBeacon.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(response.code() == 201){
                            beaconAdapter.removeBeacon(mac);
                            beaconAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Delete Failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        t.printStackTrace();
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(beaconRecyclerView);



        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    adapter1.clear();
                    adapter1.notifyDataSetChanged();
                    try {
                        unregisterReceiver(mWifiScanReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    registerReceiver(mBluetoothScanReceiver, filter);

                    boolean response = bluetoothAdapter.startDiscovery();
                    Log.d("response", response + "");
                } else {
                    adapter1.clear();
                    adapter1.notifyDataSetChanged();
                    try {
                        unregisterReceiver(mBluetoothScanReceiver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

                    registerReceiver(mWifiScanReceiver, filter);
                    mWifiManager.startScan();
                }
            }
        });

        beaconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scanResult1 = adapter1.getWifiEntitiesList().get(position);
                selectedBSSID1 = scanResult1.getBSSID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //scanResult1 = null;
            }
        });

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
                    ScanResult scanResult = new ScanResult(deviceName, deviceHardwareAddress, 100 + rssi);
                    adapter1.addItem(deviceHardwareAddress, scanResult);

                    if(scanResult1 != null)
                        scanResult1 = adapter1.getItemById(scanResult1.getBSSID());
                    adapter1.notifyDataSetChanged();
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    bluetoothAdapter.startDiscovery();
                }

            }
        };

        mWifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    List<android.net.wifi.ScanResult> mScanResults = mWifiManager.getScanResults();
                    //scanResults.clear();
                    adapter1.resetStrengths();
                    for(android.net.wifi.ScanResult scanResult : mScanResults){
                        Log.d("scan result", scanResult.SSID);
                        if(scanResult.SSID.equals("")) continue;
                        int level = WifiManager.calculateSignalLevel(mWifiManager.getConnectionInfo().getRssi(), scanResult.level);

                        //scanResults.put(scanResult.BSSID, new com.jelliroo.mallmap.entities.ScanResult(scanResult.SSID, scanResult.BSSID, level + 100));
                        adapter1.addItem(scanResult.BSSID, new ScanResult(scanResult.SSID, scanResult.BSSID, level + 100));

                    }


                    if(scanResult1 != null)
                        scanResult1 = adapter1.getItemById(scanResult1.getBSSID());
                    adapter1.notifyDataSetChanged();
                    mWifiManager.startScan();

                }
            }
        };

        if(mSwitch.isChecked()){
            adapter1.clear();
            adapter1.notifyDataSetChanged();
            try {
                unregisterReceiver(mWifiScanReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mBluetoothScanReceiver, filter);

            boolean response = bluetoothAdapter.startDiscovery();
            Log.d("response", response + "");
        } else {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            registerReceiver(mWifiScanReceiver, filter);
            mWifiManager.startScan();
        }




    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mWifiScanReceiver);
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            unregisterReceiver(mBluetoothScanReceiver);
        } catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }


    class WifiListAdapter extends ArrayAdapter<ScanResult> {

        HashMap<String, ScanResult> wifiEntities = new LinkedHashMap<>();

        public WifiListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.beacon_item, null);
            }
            ScanResult scanResult = new ArrayList<>(wifiEntities.values()).get(position);

            TextView name, strength;
            name = (TextView) convertView.findViewById(R.id.beacon_name);
            strength = (TextView) convertView.findViewById(R.id.beacon_rssi);

            name.setText(scanResult.getSSID());
            strength.setText(scanResult.getStrength() + "");

            return convertView;
        }


        public void addItem(String BSSID, ScanResult scanResult){
            wifiEntities.put(BSSID, scanResult);
        }

        public ScanResult getItemById(String BSSID){
            return wifiEntities.get(BSSID);
        }

        @Override
        public void clear() {
            if(wifiEntities != null) wifiEntities.clear();
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.beacon_item, null);
            }
            ScanResult scanResult = new ArrayList<>(wifiEntities.values()).get(position);

            TextView name, strength;
            name = (TextView) convertView.findViewById(R.id.beacon_name);
            strength = (TextView) convertView.findViewById(R.id.beacon_rssi);

            name.setText(scanResult.getSSID());
            strength.setText(scanResult.getStrength() + "");
            return convertView;
        }


        public HashMap<String, ScanResult> getWifiEntities() {
            return wifiEntities;
        }

        public List<ScanResult> getWifiEntitiesList(){
            return new ArrayList<>(wifiEntities.values());
        }

        public void resetStrengths(){
            List<ScanResult> scanResults = getWifiEntitiesList();
            for(ScanResult scanResult : scanResults){
                scanResult.setStrength(0);
            }
        }

        @Nullable
        @Override
        public ScanResult getItem(int position) {
            if(wifiEntities != null){
                return new ArrayList<>(wifiEntities.values()).get(position);
            } else return null;
        }

        @Override
        public int getCount() {
            if(wifiEntities != null){
                return wifiEntities.size();
            } else return 0;
        }
    }

    public void refreshData(){
        BeaconEndPoint beaconEndPoint = retrofit.create(BeaconEndPoint.class);
        Call<List<Beacon>> callForGetAllBeacons = beaconEndPoint.getAllBeacons();
        callForGetAllBeacons.enqueue(new Callback<List<Beacon>>() {
            @Override
            public void onResponse(Call<List<Beacon>> call, Response<List<Beacon>> response) {
                if(response.code() == 200){
                    List<Beacon> beacons = response.body();
                    beaconAdapter.clear();
                    for(Beacon beacon : beacons){
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

    public void save(View view){
        if(scanResult1 == null || nameEditText.getText().toString().trim().equals("")){
            return;
        }

        BeaconEndPoint beaconEndPoint = retrofit.create(BeaconEndPoint.class);
        Call<Beacon> callForCreateBeacon = beaconEndPoint.createBeacon(new Beacon(nameEditText.getText().toString(), scanResult1.getBSSID()));
        callForCreateBeacon.enqueue(new Callback<Beacon>() {
            @Override
            public void onResponse(Call<Beacon> call, Response<Beacon> response) {
                if(response.code() == 201){
                    Beacon beacon = response.body();
                    beaconAdapter.addBeacon(beacon);
                    beaconAdapter.notifyDataSetChanged();

                    InputMethodManager imm = (InputMethodManager) BeaconsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (null != BeaconsActivity.this.getCurrentFocus())
                        imm.hideSoftInputFromWindow(BeaconsActivity.this.getCurrentFocus()
                                .getApplicationWindowToken(), 0);

                } else {
                    Log.d(TAG, "Creation Failed");
                }
            }

            @Override
            public void onFailure(Call<Beacon> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
