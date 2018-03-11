package com.bvisible.carnet;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.bvisible.carnet.controllers.BluetoothController;
import com.bvisible.carnet.controllers.NearSitesController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity implements BluetoothController.ReadReceived, BluetoothController.BluetoothStatus {

    private static final String GRAPH_DATABASE_NAME = "imdb.gdb";
    //permissions
    private static final int PERMISSION_REQUEST_WRITE_FILE = 1;
    public static String TAG = "MainActivity";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final String SparkseeLicense = "NWNRP-J7NZ0-7159N-FJG09";

    private BluetoothController.ReadReceived readReceivedCallback = this;
    private BluetoothController.BluetoothStatus bluetoothStatusCallback = this;
    private Location lastLocation;

    private AsyncResponse asyncResponse;

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
                    selectedFragment = BikeLanesFragment.newInstance();
                    selectedTag = BikeLanesFragment.TAG;
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = PublicTransportFragment.newInstance();
                    selectedTag = PublicTransportFragment.TAG;
                    break;
            }

            if (selectedFragment != null) {
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
                    startBluetooth();
                    getLocation();
                    NearSitesController.getInstance().openGDB(getApplicationContext());
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    public void audioRecognison() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }

    private void startBluetooth() {
        BluetoothController.getInstance(this).startService();
        BluetoothController.getInstance(this).setCallbacks(this, this);
    }

    private void stopBluetooth() {
        BluetoothController.getInstance(this).stopService();
    }

    private void getLocation() {
        SmartLocation.with(getApplicationContext()).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.e(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                        lastLocation = location;
                    }
                });
    }

    public boolean existsLastLocation() {
        return lastLocation != null;
    }

    public Location getLastLocation() {
        return lastLocation;
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
                BluetoothController.getInstance(this)
                        .sendData(BluetoothController.SEND_PALO_HELLO, "Message from the phone");

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
        if (valueReceived.equals("BOTON_APRETADO")) {

        } else if (valueReceived.equals("SHOCK_AGITADO")) {
            audioRecognison();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.e(TAG, result.get(0));

                    MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                    if (mainFragment != null) {
                        mainFragment.newVoiceMessage(result.get(0));
                    }
                }
                break;
            }
        }
    }
}
