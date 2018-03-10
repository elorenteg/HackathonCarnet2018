package com.bvisible.carnet.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bvisible.carnet.Schema;
import com.bvisible.carnet.models.Route;
import com.bvisible.carnet.models.Stop;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.Database;
import com.sparsity.sparksee.gdb.EdgesDirection;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Session;
import com.sparsity.sparksee.gdb.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TPGraphQueryNearTP  extends AsyncTask<Void, Void, String> {

    private static String TAG = "TPGraphQueryStops";
    private static Context mContext;

    private static double lat;
    private static double lng;

    Objects nearStops = null;
    Objects nearRoutes = null;
    private List<Stop> stops;
    private List<Route> routes;


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
            Schema schema = tpGraphDB.getSchema();

            nearStops = null;
            nearRoutes = null;

            loadNearStops(graph, schema);
            loadNearRoutes(graph, schema);
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
        }

        return "";
    }

    private void loadNearStops(Graph graph, Schema schema) {
        stops = new ArrayList<>();

        double diffLat = 0.0015;
        double diffLng = 0.0015;

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
        nearStops = Objects.combineIntersection(castNearStopsLat, castNEarStopsLng);
        /*
        ObjectsIterator itstops = nearStops.iterator();
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
        */
    }

    private void loadNearRoutes(Graph graph, Schema schema) {
        if (nearStops != null) {
            Objects routes = null;

            ObjectsIterator itstops = nearStops.iterator();
            while (itstops.hasNext()) {
                long stopOid = itstops.next();
                Value stopidvalue = new Value();
                Value stopnamevalue = new Value();
                graph.getAttribute(stopOid, schema.getStopIdType(), stopidvalue);
                graph.getAttribute(stopOid, schema.getStopNameType(), stopnamevalue);
                Log.e(TAG, "Stop-" + stopidvalue.toString() + "-" + stopnamevalue.toString());

                Objects routesFromStop = graph.neighbors(stopOid, schema.getConnectRouteType(), EdgesDirection.Ingoing);
                ObjectsIterator itroutes = routesFromStop.iterator();
                while (itroutes.hasNext())
                {
                    long routeOid = itroutes.next();
                    Value routeidvalue = new Value();
                    Value routeshortnamevalue = new Value();
                    graph.getAttribute(routeOid, schema.getRouteIdType(), routeidvalue);
                    graph.getAttribute(routeOid, schema.getRouteShortNameType(), routeshortnamevalue);
                    Log.e(TAG, "---Route-" + routeidvalue.toString() + "-" + routeshortnamevalue.toString());
                }
            }

            /*
            Objects routesFromNearStops = graph.neighbors(nearStops, schema.getConnectRouteType(), EdgesDirection.Ingoing);
            ObjectsIterator itroutes = routesFromNearStops.iterator();
            while (itroutes.hasNext())
            {
                long routeOid = itroutes.next();
                Value routeidvalue = new Value();
                graph.getAttribute(routeOid, schema.getRouteIdType(), routeidvalue);
                Log.e(TAG, "Route-" + routeidvalue.toString());
            }
            */
        }
    }

    protected void onPostExecute(String values) {
        Log.e(TAG, "hola");
    }
}