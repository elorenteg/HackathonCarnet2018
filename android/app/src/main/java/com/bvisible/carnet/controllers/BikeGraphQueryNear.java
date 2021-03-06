package com.bvisible.carnet.controllers;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bvisible.carnet.AsyncResponse;
import com.bvisible.carnet.models.BikeLane;
import com.bvisible.carnet.models.StopNextRoutes;
import com.bvisible.carnet.schemas.BikeSchema;
import com.bvisible.carnet.utils.Point;
import com.bvisible.carnet.utils.PointUtils;
import com.sparsity.sparksee.gdb.Condition;
import com.sparsity.sparksee.gdb.Database;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Objects;
import com.sparsity.sparksee.gdb.ObjectsIterator;
import com.sparsity.sparksee.gdb.Session;
import com.sparsity.sparksee.gdb.Value;

import java.util.ArrayList;
import java.util.Collections;

public class BikeGraphQueryNear {
    public static final String TAG = BikeGraphQueryNear.class.getSimpleName();
    private Objects nearBikelanes;
    private ArrayList<BikeLane> bikelanes;

    public BikeGraphQueryNear() {
    }

    public void queryGraph(BikeGraphDatabase bikeGraphDB, double lat, double lng, AsyncResponse responseCallback) {
        new BikeGraphQueryNearTask(bikeGraphDB, lat, lng, responseCallback).execute();
    }

    public ArrayList<BikeLane> getBikes() {
        return bikelanes;
    }

    public class BikeGraphQueryNearTask extends AsyncTask<Void, Void, String> {
        private BikeGraphDatabase bikeGraphDatabase;
        private double lat;
        private double lng;
        private AsyncResponse responseCallback;

        public BikeGraphQueryNearTask(BikeGraphDatabase bikeGraphDatabase, double lat, double lng, AsyncResponse responseCallback) {
            super();
            this.bikeGraphDatabase = bikeGraphDatabase;
            this.lat = lat;
            this.lng = lng;
            this.responseCallback = responseCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Database database = bikeGraphDatabase.getDatabase();
                Graph graph = bikeGraphDatabase.getGraph();
                Session session = bikeGraphDatabase.getSession();
                BikeSchema BikeSchema = bikeGraphDatabase.getSchema();

                nearBikelanes = null;
                bikelanes = new ArrayList<>();

                loadBikelanes(graph, BikeSchema);
            } catch (Exception e) {
                Log.e(TAG, "error ", e);
            }

            return "";
        }

        private BikeLane getBikelane(Graph graph, BikeSchema BikeSchema, long bikelaneOid) {
            Value laneidvalue = new Value();
            Value lanecidvalue = new Value();
            Value lanenamevalue = new Value();
            Value lanelat1value = new Value();
            Value lanelng1value = new Value();
            Value lanelat2value = new Value();
            Value lanelng2value = new Value();
            graph.getAttribute(bikelaneOid, BikeSchema.getLaneIdType(), laneidvalue);
            graph.getAttribute(bikelaneOid, BikeSchema.getLaneCidType(), lanecidvalue);
            graph.getAttribute(bikelaneOid, BikeSchema.getLaneNameType(), lanenamevalue);
            graph.getAttribute(bikelaneOid, BikeSchema.getLaneLat1Type(), lanelat1value);
            graph.getAttribute(bikelaneOid, BikeSchema.getLaneLon1Type(), lanelng1value);
            graph.getAttribute(bikelaneOid, BikeSchema.getLaneLat2Type(), lanelat2value);
            graph.getAttribute(bikelaneOid, BikeSchema.getLaneLon2Type(), lanelng2value);

            BikeLane bikelane = new BikeLane();
            bikelane.setId(laneidvalue.getString());
            bikelane.setCid(lanecidvalue.getString());
            bikelane.setName(lanenamevalue.getString());
            bikelane.setLat1(lanelat1value.getDouble());
            bikelane.setLng1(lanelng1value.getDouble());
            bikelane.setLat2(lanelat2value.getDouble());
            bikelane.setLng2(lanelng2value.getDouble());

            Point p = new Point(lat, lng);
            Point pA = new Point(bikelane.getLat1(), bikelane.getLng1());
            Point pB = new Point(bikelane.getLat2(), bikelane.getLng2());
            double distance = PointUtils.pointToLineDistance(pA, pB, p);
            bikelane.setDistance(distance);

            //Log.e(TAG, "Stop-" + stopidvalue.toString() + "-" + stopnamevalue.toString());

            return bikelane;
        }


        protected void onPostExecute(String string) {
            Log.e(TAG, "SEFINI");
            responseCallback.processFinish("BIKES");
        }

        private void loadBikelanes(Graph graph, BikeSchema BikeSchema) {
            double diffLat = 0.05;
            double diffLng = 0.05;

            double minLat = lat - diffLat;
            double maxLat = lat + diffLat;
            double minLng = lng - diffLng;
            double maxLng = lng + diffLng;

            Log.e(TAG, "Bikelanes between lat[" + minLat + "," + maxLat + "] lng[" + minLng + "," + maxLng + "]");

            Value value1 = new Value();
            Value value2 = new Value();

            Objects castNearBikelanesLat1 = graph.select(BikeSchema.getLaneLat1Type(), Condition.Between,
                    value1.setDouble(minLat), value2.setDouble(maxLat));
            Objects castNEarBikelanesLng1 = graph.select(BikeSchema.getLaneLon1Type(), Condition.Between,
                    value1.setDouble(minLng), value2.setDouble(maxLng));
            Objects castNearBikelanesLat2 = graph.select(BikeSchema.getLaneLat2Type(), Condition.Between,
                    value1.setDouble(minLat), value2.setDouble(maxLat));
            Objects castNEarBikelanesLng2 = graph.select(BikeSchema.getLaneLon2Type(), Condition.Between,
                    value1.setDouble(minLng), value2.setDouble(maxLng));
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
            Collections.sort(bikelanes);
        }
    }
}