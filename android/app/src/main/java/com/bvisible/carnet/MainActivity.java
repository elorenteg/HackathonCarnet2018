package com.bvisible.carnet;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.bvisible.carnet.controllers.BikeGraphDatabase;
import com.bvisible.carnet.controllers.BikeGraphQueryNear;
import com.bvisible.carnet.controllers.BluetoothController;
import com.bvisible.carnet.controllers.NearSitesController;
import com.bvisible.carnet.controllers.TPGraphDatabase;
import com.bvisible.carnet.controllers.TPGraphQueryNearTP;
import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.RouteTime;
import com.bvisible.carnet.models.Stop;
import com.bvisible.carnet.models.StopNextRoutes;
import com.bvisible.carnet.utils.DateUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncResponse, BluetoothController.ReadReceived, BluetoothController.BluetoothStatus {

    private static final String GRAPH_DATABASE_NAME = "imdb.gdb";
    //permissions
    private static final int PERMISSION_REQUEST_WRITE_FILE = 1;
    public static String TAG = "MainActivity";
    private final String SparkseeLicense = "NWNRP-J7NZ0-7159N-FJG09";
    private TPGraphQueryNearTP asyncTaskTP;
    private BikeGraphQueryNear asyncTaskBikes;

    private Fragment currentFragment = null;
    private String currentFragmentTAG = null;

    private BluetoothController.ReadReceived readReceivedCallback = this;
    private BluetoothController.BluetoothStatus bluetoothStatusCallback = this;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            String selectedTag = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = MainFragment.newInstance();
                    selectedTag = MainFragment.TAG;
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = SecondaryFragment.newInstance();
                    selectedTag = SecondaryFragment.TAG;
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = ThirdFragment.newInstance();
                    selectedTag = ThirdFragment.TAG;
                    break;
            }

            if (selectedFragment != null) {
                currentFragment = selectedFragment;
                currentFragmentTAG = selectedTag;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, selectedTag).commit();
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

        navigation.setSelectedItemId(R.id.navigation_home);

        asyncTaskTP = new TPGraphQueryNearTP(getApplicationContext());
        asyncTaskBikes = new BikeGraphQueryNear(getApplicationContext());
        asyncTaskTP.delegate = this;
        asyncTaskBikes.delegate = this;

        NearSitesController.getInstance().init(asyncTaskTP, asyncTaskBikes);

        requestFileAccessPermission();
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
                    //startBluetooth();
                    NearSitesController.getInstance().openGDB(getApplicationContext());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    @Override
    public void processFinish(String typeAsync) {
        Log.e(TAG, "SEFINI " + typeAsync);
        String text = "";
        boolean update = false;
        boolean updateStops = false;
        boolean updateBikes = false;
        if (typeAsync.equals("STOPS")) {
            update = true;
            updateStops = true;
            text = NearSitesController.getInstance().getTPtext();
            Log.e(TAG, text);
        } else if (typeAsync.equals("BIKES")) {
            update = true;
            updateBikes = true;
            text = NearSitesController.getInstance().getBikesText();
            Log.e(TAG, text);
        }

        if (update) {
            if (currentFragmentTAG == SecondaryFragment.TAG && updateBikes) {
                SecondaryFragment secondaryFragment = (SecondaryFragment) getSupportFragmentManager().findFragmentByTag(currentFragmentTAG);
                if (secondaryFragment != null) {
                    secondaryFragment.updateInfo(text);
                }
            }
            else if (currentFragmentTAG == ThirdFragment.TAG && updateStops) {
                ThirdFragment thirdFragment = (ThirdFragment) getSupportFragmentManager().findFragmentByTag(currentFragmentTAG);
                if (thirdFragment != null) {
                    thirdFragment.updateInfo(text);
                }
            }
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
