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

            ArrayList<Integer> route = Algorithms.findRoute(session, schema, 0,29 );

            for( Integer id : route ) {
                Objects objects = graph.select(schema.getStopIdType(), Condition.Equal, (new Value()).setInteger(id));
                Value value = new Value();
                graph.getAttribute(objects.any(), schema.getStopNameType(),value);
                Log.e(TAG, value.getString());
                objects.close();
            }

            //session.close();
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
        }

        return "";
    }

    protected void onPostExecute(String values) {
        Log.e(TAG, "hola");
    }
}