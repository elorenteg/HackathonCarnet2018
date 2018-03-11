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
        try {
            asyncTaskTP.queryGraph(tpGraphDB, lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queryBikesGraph(double lat, double lng) {
        try {
            asyncTaskBikes.queryGraph(bikeGraphDB, lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
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
                    routeTime.setDate(DateUtils.parseDate(time));
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
        ArrayList<BikeLane> bikelanes = asyncTaskBikes.getBikes();

        String text = "";
        for (BikeLane bikelane : bikelanes) {
            text += bikelane.getName() +
                    "[" + bikelane.getLat1() + "," + bikelane.getLng1() + "] - " +
                    "[" + bikelane.getLat2() + "," + bikelane.getLng2() + "]" +
                    "\n";
        }

        return text;
    }
}