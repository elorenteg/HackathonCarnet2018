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

import com.bvisible.carnet.controllers.TPGraphDatabase;
import com.bvisible.carnet.controllers.TPGraphQueryNearStops;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

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
        TPGraphQueryNearStops tpGraphQueryStops = new TPGraphQueryNearStops(getApplicationContext());

        try {
            tpGraphDB.loadDatabase();
            tpGraphQueryStops.queryGraph(tpGraphDB, 41.388693, 2.112126);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tpGraphDB.closeDatabase();
    }
}
