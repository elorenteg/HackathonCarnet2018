package com.bvisible.carnet.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bvisible.carnet.AsyncResponse;
import com.bvisible.carnet.models.RouteTime;
import com.bvisible.carnet.models.StopNextRoutes;
import com.bvisible.carnet.schemas.TPSchema;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.Stop;
import com.bvisible.carnet.utils.DateUtils;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.Database;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Session;
import com.sparsity.sparksee.gdb.Value;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class TPGraphQueryNearTP  extends AsyncTask<Void, Void, String> {
    public AsyncResponse delegate = null;

    private static String TAG = "TPGraphQueryStops";
    private static Context mContext;

    private static double lat;
    private static double lng;

    Objects nearStops = null;
    Objects nearRoutes = null;
    private ArrayList<Stop> stops;
    private ArrayList<Route> routes;


    private static TPGraphDatabase tpGraphDB;

    public TPGraphQueryNearTP(Context context) {
        mContext = context;
    }

    public void queryGraph(TPGraphDatabase tpGraphDB, double lat, double lng) throws IOException {
        this.tpGraphDB = tpGraphDB;
        this.lat = lat;
        this.lng = lng;
        execute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Database database = tpGraphDB.getDatabase();
            Graph graph = tpGraphDB.getGraph();
            Session session = tpGraphDB.getSession();
            TPSchema TPSchema = tpGraphDB.getSchema();

            nearStops = null;
            nearRoutes = null;
            stops = new ArrayList<>();

            loadNearStops(graph, TPSchema);
            loadNearRoutes(graph, TPSchema);
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
        }

        return "";
    }

    private void loadNearStops(Graph graph, TPSchema TPSchema) {
        double diffLat = 0.0015;
        double diffLng = 0.0015;

        double minLat = lat-diffLat;
        double maxLat = lat+diffLat;
        double minLng = lng-diffLng;
        double maxLng = lng+diffLng;

        Log.e(TAG, "Stops between lat[" + minLat + "," + maxLat + "] lng[" + minLng + "," + maxLng + "]");

        Value value1 = new Value();
        Value value2 = new Value();

        Objects castNearStopsLat = graph.select(TPSchema.getStopLatType(), Condition.Between,
                value1.setString(String.valueOf(minLat)), value2.setString(String.valueOf(maxLat)));
        Objects castNEarStopsLng = graph.select(TPSchema.getStopLonType(), Condition.Between,
                value1.setString(String.valueOf(minLng)), value2.setString(String.valueOf(maxLng)));
        nearStops = Objects.combineIntersection(castNearStopsLat, castNEarStopsLng);
    }

    private void loadNearRoutes(Graph graph, TPSchema TPSchema) {
        if (nearStops != null) {
            Objects routes = null;

            ObjectsIterator itstops = nearStops.iterator();
            while (itstops.hasNext()) {
                long stopOid = itstops.next();
                Stop stop = getStop(graph, TPSchema, stopOid);
                ArrayList<Route> stoproutes = new ArrayList<>();

                Objects routesFromStop = graph.neighbors(stopOid, TPSchema.getConnectRouteType(), EdgesDirection.Ingoing);
                ObjectsIterator itroutes = routesFromStop.iterator();
                while (itroutes.hasNext())
                {
                    long routeOid = itroutes.next();
                    Route route = getRoute(graph, TPSchema, routeOid);
                    stoproutes.add(route);
                }
                stop.setRoutes(stoproutes);
                Log.e(TAG, stop.toString());
                stops.add(stop);
            }
        }
    }

    private Stop getStop(Graph graph, TPSchema TPSchema, long stopOid) {
        Value stopidvalue = new Value();
        Value stopnamevalue = new Value();
        Value stoplatvalue = new Value();
        Value stoplngvalue = new Value();
        graph.getAttribute(stopOid, TPSchema.getStopIdType(), stopidvalue);
        graph.getAttribute(stopOid, TPSchema.getStopNameType(), stopnamevalue);
        graph.getAttribute(stopOid, TPSchema.getStopLatType(), stoplatvalue);
        graph.getAttribute(stopOid, TPSchema.getStopLonType(), stoplngvalue);

        Stop stop = new Stop();
        stop.setId(stopidvalue.getInteger());
        stop.setName(stopnamevalue.getString());
        stop.setLat(stoplatvalue.getString());
        stop.setLng(stoplngvalue.getString());

        //Log.e(TAG, "Stop-" + stopidvalue.toString() + "-" + stopnamevalue.toString());

        return stop;
    }

    private Route getRoute(Graph graph, TPSchema TPSchema, long routeOid) {
        Value routeidvalue = new Value();
        Value routeshortnamevalue = new Value();
        Value routelongnamevalue = new Value();
        Value routetypevalue = new Value();
        graph.getAttribute(routeOid, TPSchema.getRouteIdType(), routeidvalue);
        graph.getAttribute(routeOid, TPSchema.getRouteShortNameType(), routeshortnamevalue);
        graph.getAttribute(routeOid, TPSchema.getRouteLongNameType(), routelongnamevalue);
        graph.getAttribute(routeOid, TPSchema.getRouteTypeType(), routetypevalue);

        ArrayList<String> timetable = getTimetable();

        Route route = new Route();
        route.setId(routeidvalue.getInteger());
        route.setShortname(routeshortnamevalue.getString());
        route.setLongname(routelongnamevalue.getString());
        route.setId(routeidvalue.getInteger());
        route.setTimetable(timetable);

        //Log.e(TAG, "---Route-" + routeidvalue.toString() + "-" + routeshortnamevalue.toString());

        return route;
    }

    ArrayList<String> getTimetable() {
        ArrayList<String> timetable = new ArrayList<>();
        int min = 5;
        int max = 15;
        int freq = 5 + (int)(Math.random() * ((15 - 5) + 1));

        int minHour = 0;
        int maxHour = 24;
        int actHour = minHour;
        int actMins = 0;
        int N = (maxHour-minHour)*60/freq;
        for (int i = 0; i < N; ++i) {
            String hour = String.format("%02d", actHour) + ":" + String.format("%02d", actMins);
            timetable.add(hour);

            actMins += freq;
            if (actMins >= 60) {
                actMins -= 60;
                actHour++;
            }
        }

        return timetable;
    }

    protected void onPostExecute(String string) {
        Log.e(TAG, "SEFINI");
        delegate.processFinish("STOPS");
    }

    public ArrayList<Stop> getRoutes() {
        return stops;
    }

    public ArrayList<StopNextRoutes> getNextRoutes() {
        ArrayList<Stop> stops = getRoutes();
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
}