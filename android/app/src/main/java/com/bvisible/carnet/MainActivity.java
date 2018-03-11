package com.bvisible.carnet;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bvisible.carnet.controllers.BikeGraphDatabase;
import com.bvisible.carnet.controllers.BikeGraphQueryNear;
import com.bvisible.carnet.controllers.BluetoothController;
import com.bvisible.carnet.controllers.TPGraphDatabase;
import com.bvisible.carnet.controllers.TPGraphQueryNearTP;
import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.RouteTime;
import com.bvisible.carnet.models.Stop;
import com.bvisible.carnet.models.StopNextRoutes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncResponse, BluetoothController.ReadReceived, BluetoothController.BluetoothStatus {

    private static final String GRAPH_DATABASE_NAME = "imdb.gdb";
    //permissions
    private static final int PERMISSION_REQUEST_WRITE_FILE = 1;
    public static String TAG = "MainActivity";
    private final String SparkseeLicense = "NWNRP-J7NZ0-7159N-FJG09";
    TPGraphQueryNearTP asyncTaskTP;
    BikeGraphQueryNear asyncTaskBikes;
    TPGraphQueryNearTP asyncTask;

    private TextView mTextMessage;
    private BluetoothController.ReadReceived readReceivedCallback = this;
    private BluetoothController.BluetoothStatus bluetoothStatusCallback = this;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setUpElements();

        asyncTaskTP = new TPGraphQueryNearTP(getApplicationContext());
        asyncTaskBikes = new BikeGraphQueryNear(getApplicationContext());
        asyncTaskTP.delegate = this;
        asyncTaskBikes.delegate = this;

        requestFileAccessPermission();
    }

    private void setUpElements() {
        mTextMessage = findViewById(R.id.message);
    }

    private void requestFileAccessPermission() {
        final int permissions = 5;
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                boolean allPermissionsChecked = report.getGrantedPermissionResponses().size() == permissions;

                if (allPermissionsChecked) {
                    startBluetooth();
                    openGDB();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    private void openGDB() {
        TPGraphDatabase tpGraphDB = new TPGraphDatabase(getApplicationContext());
        BikeGraphDatabase bikeGraphDB = new BikeGraphDatabase(getApplicationContext());

        try {
            tpGraphDB.loadDatabase();
            bikeGraphDB.loadDatabase();
            asyncTaskTP.queryGraph(tpGraphDB, 41.388693, 2.112126);
            asyncTaskBikes.queryGraph(bikeGraphDB, 41.388693, 2.112126);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tpGraphDB.closeDatabase();
    }

    @Override
    public void processFinish(String typeAsync) {
        Log.e(TAG, "SEFINI " + typeAsync);
        if (typeAsync.equals("STOPS")) {
            ArrayList<StopNextRoutes> nextRoutes = getNextRoutes();

            Log.e(TAG, getNextRoutes().size() + " ");

            String text = "";
            for (StopNextRoutes stopNextRoutes : nextRoutes) {
                text += stopNextRoutes.getStopname() + "\n";
                for (RouteTime routeTime : stopNextRoutes.getRoutes()) {
                    Calendar now = Calendar.getInstance();
                    int hour = now.get(Calendar.HOUR);
                    int minute = now.get(Calendar.MINUTE);
                    Date dateNow = parseDate(hour + ":" + minute);

                    long different = dateNow.getTime() - routeTime.getDate().getTime();
                    int elapsedHours = (int) different / (1000*60*60);
                    if (elapsedHours >= 0 && elapsedHours < 1 && dateNow.before(routeTime.getDate())) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        text += "  " + routeTime.getName() + " " + sdf.format(routeTime.getDate()) + "\n";
                        Log.e(TAG, elapsedHours + "");
                    }
                    else {
                        Log.e(TAG, "dd");
                        Log.e(TAG, elapsedHours + "");
                    }
                }
            }
            mTextMessage.setText(text);
        } else if (typeAsync.equals("BIKES")) {
            ArrayList<BikeLane> bikelanes = asyncTaskBikes.getBikes();
            Log.e(TAG, bikelanes.toString());
        }
    }

    private ArrayList<StopNextRoutes> getNextRoutes() {
        ArrayList<Stop> stops = asyncTaskTP.getRoutes();
        ArrayList<StopNextRoutes> stopNextRoutesArray = new ArrayList<>();

        for (Stop stop : stops) {
            StopNextRoutes stopNextRoutes = new StopNextRoutes();
            stopNextRoutes.setStopid(stop.getId());
            stopNextRoutes.setStopname(stop.getName());
            ArrayList<RouteTime> routetimes = new ArrayList<>();
            for (Route route : stop.getRoutes()) {
                for (String time : route.getTimetable()) {
                    RouteTime routeTime = new RouteTime();
                    routeTime.setName(route.getShortname());
                    routeTime.setDate(parseDate(time));
                    routetimes.add(routeTime);
                }
            }
            Collections.sort(routetimes);
            stopNextRoutes.setRoutes(routetimes);
            stopNextRoutesArray.add(stopNextRoutes);
            Log.e(TAG, stopNextRoutes.toString());
        }

        return stopNextRoutesArray;
    }

    private Date parseDate(String date) {
        String inputFormat = "HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private void startBluetooth() {
        BluetoothController.getInstance(this).startService();
        BluetoothController.getInstance(this).setCallbacks(this, this);
    }

    private void stopBluetooth() {
        BluetoothController.getInstance(this).stopService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBluetooth();
    }

    @Override
    public void onBluetoothChanged(int bluetoothStatus) {
        switch (bluetoothStatus) {
            case BluetoothController.BLUETOOTH_CONNECTING:
                Log.e(TAG, "BLUETOOTH_CONNECTING");

                break;
            case BluetoothController.BLUETOOTH_CONNECTED:
                Log.e(TAG, "BLUETOOTH_CONNECTED");
                BluetoothController.getInstance(this).sendData("Message from the phone");

                break;
            case BluetoothController.BLUETOOTH_READY:
                Log.e(TAG, "BLUETOOTH_READY");

                break;
            case BluetoothController.BLUETOOTH_DISCONNECTED:
                Log.e(TAG, "BLUETOOTH_DISCONNECTED");

                break;
            case BluetoothController.BLUETOOTH_FAILED:
                Log.e(TAG, "BLUETOOTH_FAILED");

                break;
        }
    }

    @Override
    public void onReadReceived(String valueReceived) {
        Log.e(TAG, "ReadReceived: " + valueReceived);
    }
}
