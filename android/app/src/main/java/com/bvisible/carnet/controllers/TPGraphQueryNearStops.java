package com.bvisible.carnet.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.bvisible.carnet.Algorithms;
import com.bvisible.carnet.Schema;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.Stop;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.Database;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.LogLevel;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Session;
import com.sparsity.sparksee.gdb.Sparksee;
import com.sparsity.sparksee.gdb.SparkseeConfig;
import com.sparsity.sparksee.gdb.Value;
import com.sparsity.sparksee.gdb.ValueList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TPGraphQueryNearStops  extends AsyncTask<Void, Void, String> {

    private static String TAG = "TPGraphQueryStops";
    private static Context mContext;

    private static double lat;
    private static double lng;

    private List<Stop> stops;
    private List<Route> routes;


    private static TPGraphDatabase tpGraphDB;

    public TPGraphQueryNearStops(Context context) {
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
            Schema schema = tpGraphDB.getSchema();

            loadNearStops(graph, schema);
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
        }

        return "";
    }

    private void loadNearStops(Graph graph, Schema schema) {
        stops = new ArrayList<>();

        double diffLat = 0.002;
        double diffLng = 0.002;

        double minLat = lat-diffLat;
        double maxLat = lat+diffLat;
        double minLng = lng-diffLng;
        double maxLng = lng+diffLng;

        Log.e(TAG, "Stops between lat[" + minLat + "," + maxLat + "] lng[" + minLng + "," + maxLng + "]");

        Value value1 = new Value();
        Value value2 = new Value();

        Objects castNearStopsLat = graph.select(schema.getStopLatType(), Condition.Between,
                value1.setString(String.valueOf(minLat)), value2.setString(String.valueOf(maxLat)));
        Objects castNEarStopsLng = graph.select(schema.getStopLonType(), Condition.Between,
                value1.setString(String.valueOf(minLng)), value2.setString(String.valueOf(maxLng)));
        Objects castNearStops = Objects.combineIntersection(castNearStopsLat, castNEarStopsLng);
        ObjectsIterator itstops = castNearStops.iterator();
        while (itstops.hasNext())
        {
            long stopOid = itstops.next();
            Value stopidvalue = new Value();
            Value stopnamevalue = new Value();
            Value stoplatvalue = new Value();
            Value stoplngvalue = new Value();
            graph.getAttribute(stopOid, schema.getStopIdType(), stopidvalue);
            graph.getAttribute(stopOid, schema.getStopNameType(), stopnamevalue);
            graph.getAttribute(stopOid, schema.getStopLatType(), stoplatvalue);
            graph.getAttribute(stopOid, schema.getStopLonType(), stoplngvalue);

            Stop stop = new Stop();
            stop.setId(stopidvalue.getInteger());
            stop.setName(stopnamevalue.getString());
            stop.setLat(stoplatvalue.getString());
            stop.setLng(stoplngvalue.getString());
            stops.add(stop);
            Log.e(TAG, stop.toString());
        }
    }



    protected void onPostExecute(String values) {
        Log.e(TAG, "hola");
    }
}