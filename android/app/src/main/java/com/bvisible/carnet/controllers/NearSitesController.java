package com.bvisible.carnet.controllers;

import android.content.Context;
import android.util.Log;

import com.bvisible.carnet.AsyncResponse;
import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.RouteTime;
import com.bvisible.carnet.models.Stop;
import com.bvisible.carnet.models.StopNextRoutes;
import com.bvisible.carnet.utils.DateUtils;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class NearSitesController {
    public static final String TAG = NearSitesController.class.getSimpleName();
    private static NearSitesController instance;
    private final TPGraphQueryNearTP tpGraphQueryNearTP;
    private final BikeGraphQueryNear bikeGraphQueryNear;
    private TPGraphDatabase tpGraphDB;
    private BikeGraphDatabase bikeGraphDB;

    private double latitude = -1;
    private double longitude = -1;

    public NearSitesController() {
        this.tpGraphQueryNearTP = new TPGraphQueryNearTP();
        this.bikeGraphQueryNear = new BikeGraphQueryNear();
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

    public void openGDB(Context mContext) {
        tpGraphDB = new TPGraphDatabase(mContext);
        bikeGraphDB = new BikeGraphDatabase(mContext);

        try {
            tpGraphDB.loadDatabase();
            bikeGraphDB.loadDatabase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //tpGraphDB.closeDatabase();
    }

    public void queryTPGraph(double lat, double lng, AsyncResponse responseCallback) {
        this.latitude = lat;
        this.longitude = lng;
        tpGraphQueryNearTP.queryGraph(tpGraphDB, lat, lng, responseCallback);
    }

    public void queryBikesGraph(double lat, double lng, AsyncResponse responseCallback) {
        this.latitude = lat;
        this.longitude = lng;
        bikeGraphQueryNear.queryGraph(bikeGraphDB, lat, lng, responseCallback);
    }

    private ArrayList<StopNextRoutes> getNextRoutes() {
        ArrayList<Stop> stops = tpGraphQueryNearTP.getRoutes();
        ArrayList<StopNextRoutes> stopNextRoutesArray = new ArrayList<>();

        for (Stop stop : stops) {
            StopNextRoutes stopNextRoutes = new StopNextRoutes();
            stopNextRoutes.setStopid(stop.getId());
            stopNextRoutes.setStopname(stop.getName());
            ArrayList<RouteTime> routeTimes = new ArrayList<>();
            for (Route route : stop.getRoutes()) {
                for (String time : route.getTimetable()) {
                    RouteTime routeTime = new RouteTime();
                    routeTime.setName(route.getShortname());
                    routeTime.setDate(DateUtils.parseDate(time));
                    routeTimes.add(routeTime);
                }
            }
            Collections.sort(routeTimes);
            stopNextRoutes.setRoutes(routeTimes);
            stopNextRoutesArray.add(stopNextRoutes);
            Log.e(TAG, stopNextRoutes.toString());
        }

        return stopNextRoutesArray;
    }

    public String getTPtext() {
        ArrayList<StopNextRoutes> nextRoutes = getNextRoutes();

        Log.e(TAG, getNextRoutes().size() + " ");

        String text = "";
        for (StopNextRoutes stopNextRoutes : nextRoutes) {
            text += stopNextRoutes.getStopname() + "\n";
            for (RouteTime routeTime : stopNextRoutes.getRoutes()) {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR);
                int minute = now.get(Calendar.MINUTE);
                Date dateNow = DateUtils.parseDate(hour + ":" + minute);

                long different = dateNow.getTime() - routeTime.getDate().getTime();
                int elapsedHours = (int) different / (1000 * 60 * 60);
                if (elapsedHours >= 0 && elapsedHours < 1 && dateNow.before(routeTime.getDate())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    text += "  " + routeTime.getName() + " " + sdf.format(routeTime.getDate()) + "\n";
                    //Log.e(TAG, elapsedHours + "");
                }
            }
        }

        return text;
    }

    public String getBikesText() {
        ArrayList<BikeLane> bikelanes = bikeGraphQueryNear.getBikes();

        String text = "";
        for (BikeLane bikelane : bikelanes) {
            text += bikelane.getName() +
                    "[" + bikelane.getLat1() + "," + bikelane.getLng1() + "] - " +
                    "[" + bikelane.getLat2() + "," + bikelane.getLng2() + "]" +
                    "\n";
        }

        return text;
    }

    public TPGraphQueryNearTP getAsyncTaskTP() {
        return tpGraphQueryNearTP;
    }

    public BikeGraphQueryNear getAsyncTaskBikes() {
        return bikeGraphQueryNear;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
