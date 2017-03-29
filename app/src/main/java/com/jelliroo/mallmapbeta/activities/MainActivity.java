package com.jelliroo.mallmapbeta.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.adapters.ProductRecyclerAdapter;
import com.jelliroo.mallmapbeta.bean.Beacon;
import com.jelliroo.mallmapbeta.bean.ClassRecord;
import com.jelliroo.mallmapbeta.bean.DrawPin;
import com.jelliroo.mallmapbeta.bean.MapPin;
import com.jelliroo.mallmapbeta.bean.Product;
import com.jelliroo.mallmapbeta.bean.RequestObject;
import com.jelliroo.mallmapbeta.bean.TrainingSet;
import com.jelliroo.mallmapbeta.customviews.PinView;
import com.jelliroo.mallmapbeta.endpoints.BeaconEndPoint;
import com.jelliroo.mallmapbeta.endpoints.ClassEndPoint;
import com.jelliroo.mallmapbeta.endpoints.KNNEndPoint;
import com.jelliroo.mallmapbeta.endpoints.ProductEndPoint;
import com.jelliroo.mallmapbeta.endpoints.RouteEndPoint;
import com.jelliroo.mallmapbeta.enums.ClassType;
import com.jelliroo.mallmapbeta.enums.PinType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private PinView imageView;
    private Retrofit retrofit;

    private LinkedHashMap<String, Beacon> beacons = new LinkedHashMap<>();
    private LinkedHashMap<String, ClassRecord> classes = new LinkedHashMap<>();
    private List<ClassRecord> rouote = new ArrayList<>();

    private WifiManager mWifiManager;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver mWifiScanReceiver, mBluetoothScanReceiver;
    private List<String> bluetoothMacs = new ArrayList<>();
    private String TAG = "MainActivity";
    private ClassRecord location;
    private AutoCompleteTextView gotoClassTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.auth_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        refreshBeacons();
        refreshClasses();

        gotoClassTextView = (AutoCompleteTextView) findViewById(R.id.goto_class);

        gotoClassTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(view.getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

                String destination = new ArrayList<>(classes.keySet()).get(position);
                if(location != null)
                    refreshPath(location.getLabel(), destination);
            }
        });



        gotoClassTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String destination = gotoClassTextView.getText().toString();
                if(location != null)
                    refreshPath(location.getLabel(), destination);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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

                    bluetoothMacs.add(deviceHardwareAddress);
                    Beacon beacon = beacons.get(deviceHardwareAddress);
                    if(beacon != null){
                        beacon.setStrength(rssi + 100);
                        beacon.setSsid(deviceName);
                        beacon.setType(Beacon.BLUETOOTH);
                    }

                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {


                    resetStrengths(Beacon.BLUETOOTH, bluetoothMacs);
                    bluetoothMacs.clear();

                    bluetoothAdapter.startDiscovery();
                }

            }
        };

        mWifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    List<android.net.wifi.ScanResult> mScanResults = mWifiManager.getScanResults();

                    resetStrengths(Beacon.WIFI);

                    for(android.net.wifi.ScanResult scanResult : mScanResults){
                        Log.d("scan result", scanResult.SSID);
                        if(scanResult.SSID.equals("")) continue;
                        int level = WifiManager.calculateSignalLevel(mWifiManager.getConnectionInfo().getRssi(), scanResult.level);
                        int level2 = level + 100;
                        Beacon beacon = beacons.get(scanResult.BSSID);
                        if(beacon != null){
                            beacon.setStrength(level + 100);
                            beacon.setSsid(scanResult.SSID);
                            beacon.setType(Beacon.WIFI);
                        }
                    }



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


        imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.floor_3r));
        imageView.setRouteEnabled(true);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    float x = event.getX();
                    float y = event.getY();

                    PointF point = imageView.viewToSourceCoord(x, y);
                    Log.d(TAG, point.toString());
                    int position = imageView.getPinPositionByPoint(point);
                    if(position!=-1) {
                        DrawPin drawPin = imageView.getDrawPinAt(position);


                        ProductEndPoint productEndPoint = retrofit.create(ProductEndPoint.class);
                        Call<List<Product>> callForProducts = productEndPoint.getAllLinksForClassLabel(drawPin.getClassName());
                        callForProducts.enqueue(new Callback<List<Product>>() {
                            @Override
                            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                                if(response.code() == 200){
                                    List<Product> products = response.body();

                                    View view = getLayoutInflater().inflate(R.layout.product_layout, null);
                                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.product_recycler);

                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                                            mLayoutManager.getOrientation());
                                    recyclerView.addItemDecoration(dividerItemDecoration);

                                    ProductRecyclerAdapter adapter = new ProductRecyclerAdapter();
                                    adapter.addAll(products);
                                    adapter.notifyDataSetChanged();

                                    recyclerView.setAdapter(adapter);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Products")
                                            .setView(view)
                                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                    builder.show();



                                } else {
                                    Log.d(TAG, "Load Products Failed");
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Product>> call, Throwable t) {
                                Log.d(TAG, t.getMessage());
                                t.printStackTrace();
                            }
                        });
                    }
                }



                return false;
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                classify(null);
            }
        }, 1000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_beacons:
                Intent intent = new Intent(this, BeaconsActivity.class);
                startActivity(intent);
                break;

            case R.id.action_classes:
                intent = new Intent(this, ClassesActivity.class);
                startActivity(intent);
                break;

            case R.id.action_training:
                intent = new Intent(this, TrainingActivity.class);
                startActivity(intent);
                break;

            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void classify(View view) {
        KNNEndPoint knnEndPoint = retrofit.create(KNNEndPoint.class);

        RequestObject requestObject = new RequestObject();
        TrainingSet trainingSet = new TrainingSet();
        trainingSet.setBeacon1(0);
        trainingSet.setBeacon2(0);
        trainingSet.setBeacon3(0);
        trainingSet.setBeacon4(0);
        trainingSet.setBeacon5(0);

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

        Call<ClassRecord> callForClassifyData = knnEndPoint.classifyData(requestObject, 3);
        callForClassifyData.enqueue(new Callback<ClassRecord>() {
            @Override
            public void onResponse(Call<ClassRecord> call, Response<ClassRecord> response) {
                if(response.code() == 200){
                    ClassRecord classRecord = response.body();
                    if(classRecord != null){
                        location = classRecord;
                        ArrayList<MapPin> mapPins = new ArrayList<MapPin>();
                        mapPins.add(new MapPin(classRecord.getX(), classRecord.getY(), 1, PinType.CURRENT_LOCATION, classRecord.getLabel(), classRecord.getClassType()));
                        imageView.setPins(mapPins);
                    } else {
                        Log.d(TAG, "Classify Failed");
                    }
                } else {
                    Log.d(TAG, "Classify Failed");
                }

//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        classify(null);
//                    }
//                }, 500);
            }

            @Override
            public void onFailure(Call<ClassRecord> call, Throwable t) {
                try {
                    Log.d(TAG, t.getMessage());
                    t.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        classify(null);
//                    }
//                }, 500);
            }
        });
    }

    public void resetStrengths(int type, List<String> bluetoothMacs){
        for(Beacon beacon : beacons.values()){
            if(beacon.getType() == null) continue;
            if(beacon.getType() == type && !bluetoothMacs.contains(beacon.getMac())){
                beacon.setStrength(0);
            }
        }
    }

    public void resetStrengths(int type) {
        for(Beacon beacon : beacons.values()){
            if(beacon.getType() == null) continue;
            if(beacon.getType() == type){
                beacon.setStrength(0);
            }
        }
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
                        beacons.put(beacon.getMac(), beacon);
                    }
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

    public void refreshClasses(){
        final ClassEndPoint classEndPoint = retrofit.create(ClassEndPoint.class);
        Call<List<ClassRecord>> callForGetAllClasses = classEndPoint.getAllClassRecords();;

        callForGetAllClasses.enqueue(new Callback<List<ClassRecord>>() {
            @Override
            public void onResponse(Call<List<ClassRecord>> call, Response<List<ClassRecord>> response) {
                if(response.code() == 200){
                    classes.clear();
                    for(ClassRecord classRecord : response.body()){
                        classes.put(classRecord.getLabel(), classRecord);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (MainActivity.this,android.R.layout.simple_list_item_1,new ArrayList<String>(classes.keySet()));
                    gotoClassTextView.setAdapter(adapter);
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

    public void refreshPath(String source, String destination){
        RouteEndPoint routeEndPoint = retrofit.create(RouteEndPoint.class);
        Call<List<String>> callForRoute = routeEndPoint.getShortestPath(source, destination);
        callForRoute.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.code() == 200){
                    rouote.clear();
                    for(String string : response.body()){
                        rouote.add(classes.get(string));
                    }

                    ArrayList<MapPin> mapPins = new ArrayList<MapPin>();
                    for(ClassRecord classRecord : rouote){
                        mapPins.add(new MapPin(classRecord.getX(), classRecord.getY(), 1, PinType.CLASS, classRecord.getLabel(), classRecord.getClassType()));
                    }
                    imageView.setPins(mapPins);
                } else {
                    Log.d(TAG, "Rout Path");
                }


            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });

    }
}
