package com.bvisible.carnet.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bvisible.carnet.AsyncResponse;
import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.schemas.BikeSchema;
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

public class BikeGraphQueryNear extends AsyncTask<Void, Void, String> {
    public AsyncResponse delegate = null;

    private static String TAG = "BikeGraphQuery";
    private static Context mContext;

    private static double lat;
    private static double lng;

    private Objects nearBikelanes;
    private ArrayList<BikeLane> bikelanes;


    private static BikeGraphDatabase bikeGraphDB;

    public BikeGraphQueryNear(Context context) {
        mContext = context;
    }

    public void queryGraph(BikeGraphDatabase bikeGraphDB, double lat, double lng) throws IOException {
        this.bikeGraphDB = bikeGraphDB;
        this.lat = lat;
        this.lng = lng;
        execute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Database database = bikeGraphDB.getDatabase();
            Graph graph = bikeGraphDB.getGraph();
            Session session = bikeGraphDB.getSession();
            BikeSchema BikeSchema = bikeGraphDB.getSchema();

            nearBikelanes = null;
            bikelanes = new ArrayList<>();

            loadBikelanes(graph, BikeSchema);
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
        }

        return "";
    }

    private void loadBikelanes(Graph graph, BikeSchema BikeSchema) {
        double diffLat = 0.005;
        double diffLng = 0.005;

        double minLat = lat-diffLat;
        double maxLat = lat+diffLat;
        double minLng = lng-diffLng;
        double maxLng = lng+diffLng;

        Log.e(TAG, "Bikelanes between lat[" + minLat + "," + maxLat + "] lng[" + minLng + "," + maxLng + "]");

        Value value1 = new Value();
        Value value2 = new Value();

        Objects castNearBikelanesLat1 = graph.select(BikeSchema.getLaneLat1Type(), Condition.Between,
                value1.setString(String.valueOf(minLat)), value2.setString(String.valueOf(maxLat)));
        Objects castNEarBikelanesLng1 = graph.select(BikeSchema.getLaneLon1Type(), Condition.Between,
                value1.setString(String.valueOf(minLng)), value2.setString(String.valueOf(maxLng)));
        Objects castNearBikelanesLat2 = graph.select(BikeSchema.getLaneLat2Type(), Condition.Between,
                value1.setString(String.valueOf(minLat)), value2.setString(String.valueOf(maxLat)));
        Objects castNEarBikelanesLng2 = graph.select(BikeSchema.getLaneLon2Type(), Condition.Between,
                value1.setString(String.valueOf(minLng)), value2.setString(String.valueOf(maxLng)));
        Objects nearBikelanes1 = Objects.combineIntersection(castNearBikelanesLat1, castNEarBikelanesLng1);
        Objects nearBikelanes2 = Objects.combineIntersection(castNearBikelanesLat2, castNEarBikelanesLng2);
        nearBikelanes = Objects.combineUnion(nearBikelanes1, nearBikelanes2);

        ObjectsIterator itbikelanes = nearBikelanes.iterator();
        while (itbikelanes.hasNext()) {
            long bikelaneOid = itbikelanes.next();
            BikeLane bikelane = getBikelane(graph, BikeSchema, bikelaneOid);
            bikelanes.add(bikelane);
            //Log.e(TAG, bikelane.toString());
        }
    }

    private BikeLane getBikelane(Graph graph, BikeSchema BikeSchema, long bikelaneOid) {
        Value laneidvalue = new Value();
        Value lanenamevalue = new Value();
        Value lanelat1value = new Value();
        Value lanelng1value = new Value();
        Value lanelat2value = new Value();
        Value lanelng2value = new Value();
        graph.getAttribute(bikelaneOid, BikeSchema.getLaneIdType(), laneidvalue);
        graph.getAttribute(bikelaneOid, BikeSchema.getLaneNameType(), lanenamevalue);
        graph.getAttribute(bikelaneOid, BikeSchema.getLaneLat1Type(), lanelat1value);
        graph.getAttribute(bikelaneOid, BikeSchema.getLaneLon1Type(), lanelng1value);
        graph.getAttribute(bikelaneOid, BikeSchema.getLaneLat2Type(), lanelat2value);
        graph.getAttribute(bikelaneOid, BikeSchema.getLaneLon2Type(), lanelng2value);

        BikeLane bikelane = new BikeLane();
        bikelane.setId(laneidvalue.getString());
        bikelane.setName(lanenamevalue.getString());
        bikelane.setLat1(lanelat1value.getString());
        bikelane.setLng1(lanelng1value.getString());
        bikelane.setLat2(lanelat2value.getString());
        bikelane.setLng2(lanelng2value.getString());

        //Log.e(TAG, "Stop-" + stopidvalue.toString() + "-" + stopnamevalue.toString());

        return bikelane;
    }

    protected void onPostExecute(String string) {
        Log.e(TAG, "SEFINI");
        delegate.processFinish("BIKES");
    }

    public ArrayList<BikeLane> getBikes() {
        return bikelanes;
    }
}