package com.bvisible.carnet.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.bvisible.carnet.Algorithms;
import com.bvisible.carnet.Schema;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TPGraphQueryStops  extends AsyncTask<Void, Void, String> {

    public static String TAG = "TPGraphQueryStops";
    public static Context mContext;

    private static TPGraphDatabase tpGraphDB;

    public TPGraphQueryStops(Context context) {
        mContext = context;
    }

    public void queryGraph(TPGraphDatabase tpGraphDB) throws IOException {
        this.tpGraphDB = tpGraphDB;
        execute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            Database database = tpGraphDB.getDatabase();
            Graph graph = tpGraphDB.getGraph();
            Session session = tpGraphDB.getSession();
            Schema schema = tpGraphDB.getSchema();

            Objects castStops = graph.select(schema.getStopType());
            ObjectsIterator itstops = castStops.iterator();
            while (itstops.hasNext())
            {
                long stopOid = itstops.next();
                Value stopnamevalue = new Value();
                Value stoplatvalue = new Value();
                Value stoplngvalue = new Value();
                graph.getAttribute(stopOid, schema.getStopNameType(), stopnamevalue);
                graph.getAttribute(stopOid, schema.getStopLatType(), stoplatvalue);
                graph.getAttribute(stopOid, schema.getStopLonType(), stoplngvalue);
                Log.e(TAG, stopnamevalue.getString() + " // " + stoplatvalue.getString() + "-" + stoplngvalue.getString());
            }
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
        }

        return "";
    }

    protected void onPostExecute(String values) {
        Log.e(TAG, "hola");
    }
}