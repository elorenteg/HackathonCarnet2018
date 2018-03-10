package com.bvisible.carnet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bvisible.carnet.controllers.BikeGraphDatabase;
import com.bvisible.carnet.controllers.BikeGraphQueryNear;
import com.bvisible.carnet.controllers.TPGraphDatabase;
import com.bvisible.carnet.controllers.TPGraphQueryNearTP;
import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.Stop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    public static String TAG = "MainActivity";
    TPGraphQueryNearTP asyncTaskTP;
    BikeGraphQueryNear asyncTaskBikes;

    //permissions
    private static final int PERMISSION_REQUEST_WRITE_FILE= 1;

    private final String SparkseeLicense = "NWNRP-J7NZ0-7159N-FJG09";
    private static final String GRAPH_DATABASE_NAME = "imdb.gdb";

    private TextView mTextMessage;

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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        asyncTaskTP = new TPGraphQueryNearTP(getApplicationContext());
        asyncTaskBikes = new BikeGraphQueryNear(getApplicationContext());
        asyncTaskTP.delegate = this;
        asyncTaskBikes.delegate = this;
        requestFileAccessPermission();
    }

    private void requestFileAccessPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"permission granted");
            openGDB();
        }
        else{
            Log.e(TAG,"requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_FILE);
        }
    }

    private void openGDB() {
        TPGraphDatabase tpGraphDB = new TPGraphDatabase(getApplicationContext());
        BikeGraphDatabase bikeGraphDB = new BikeGraphDatabase(getApplicationContext());

        try {
            tpGraphDB.loadDatabase();
            bikeGraphDB.loadDatabase();
            asyncTaskTP.queryGraph(tpGraphDB, 41.388693, 2.112126);
            asyncTaskBikes.queryGraph(bikeGraphDB, 41.388693, 2.112126);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tpGraphDB.closeDatabase();
    }

    @Override
    public void processFinish(String typeAsync) {
        Log.e(TAG, "SEFINI " + typeAsync);
        if (typeAsync.equals("STOPS")) {
            ArrayList<Stop> stops = asyncTaskTP.getRoutes();

            String text = "";
            for (Stop stop : stops) {
                text += stop.getName() + "\n";
                for (Route route : stop.getRoutes()) {
                    text += "  " + route.getShortname() + " " + route.getLongname() + "\n";
                }
            }
            mTextMessage.setText(text);
        }
        else if (typeAsync.equals("BIKES")) {
            ArrayList<BikeLane> bikelanes = asyncTaskBikes.getBikes();
            Log.e(TAG, bikelanes.toString());
        }
    }
}
