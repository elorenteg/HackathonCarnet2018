package com.bvisible.carnet.controllers;

import android.content.Context;
import android.util.Log;

import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.RouteTime;
import com.bvisible.carnet.models.Stop;
import com.bvisible.carnet.models.StopNextRoutes;
import com.bvisible.carnet.utils.DateUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class NearSitesController {
    private static NearSitesController instance;

    public static String TAG = "NearSitesController";

    private TPGraphQueryNearTP asyncTaskTP;
    private BikeGraphQueryNear asyncTaskBikes;
    private TPGraphDatabase tpGraphDB;
    private BikeGraphDatabase bikeGraphDB;

    private double latitude = -1;
    private double longitude = -1;

    public NearSitesController() {
    }

    public static NearSitesController getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private static synchronized void createInstance() {
        if (instance == null) {
            instance = new NearSitesController();
        }
    }

    public void init(TPGraphQueryNearTP asyncTaskTP, BikeGraphQueryNear asyncTaskBikes) {
        this.asyncTaskTP = asyncTaskTP;
        this.asyncTaskBikes = asyncTaskBikes;
    }

    public void openGDB(Context context) {
        tpGraphDB = new TPGraphDatabase(context);
        bikeGraphDB = new BikeGraphDatabase(context);

        try {
            tpGraphDB.loadDatabase();
            bikeGraphDB.loadDatabase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //tpGraphDB.closeDatabase();
    }

    public void queryTPGraph(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
        try {
            asyncTaskTP.queryGraph(tpGraphDB, lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queryBikesGraph(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
        try {
            asyncTaskBikes.queryGraph(bikeGraphDB, lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TPGraphQueryNearTP getAsyncTaskTP() {
        return asyncTaskTP;
    }

    public BikeGraphQueryNear getAsyncTaskBikes() {
        return asyncTaskBikes;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
