package com.bvisible.carnet.controllers;

import android.content.Context;
import android.os.Environment;

import com.bvisible.carnet.schemas.BikeSchema;
import com.sparsity.sparksee.gdb.Database;
import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.LogLevel;
import com.sparsity.sparksee.gdb.Session;
import com.sparsity.sparksee.gdb.Sparksee;
import com.sparsity.sparksee.gdb.SparkseeConfig;

import java.io.File;
import java.io.FileNotFoundException;

public class BikeGraphDatabase {
    public static final String TAG = BikeGraphDatabase.class.getSimpleName();
    private static final String GRAPH_DATABASE_NAME = "bici.gdb";
    public static Context mContext;
    // Sparksee
    private final String SparkseeLicense = "NWNRP-J7NZ0-7159N-FJG09";
    private Sparksee sparksee = null;
    private Database database = null;
    private Session session = null;
    private BikeSchema schema = null;
    private Graph graph = null;

    private boolean isGraphLoaded = false;

    public BikeGraphDatabase(Context context) {
        mContext = context;
    }

    public void loadDatabase() throws FileNotFoundException {
        SparkseeConfig cfg = new SparkseeConfig();
        cfg.setLicense(SparkseeLicense);

        File sdcard = Environment.getExternalStorageDirectory();
        cfg.setLogFile(sdcard.getPath() + "/sparksee.log");
        cfg.setLogLevel(LogLevel.Debug);
        sparksee = new Sparksee(cfg);

        File file = new File(sdcard, GRAPH_DATABASE_NAME);
        database = sparksee.open(file.getPath(), true);

        session = database.newSession();
        schema = new BikeSchema(session.getGraph());
        graph = session.getGraph();
    }

    public void closeDatabase() {
        if (session != null) session.close();
        if (database != null) database.close();
        if (sparksee != null) sparksee.close();
    }

    public boolean isDatabaseAvailable() {
        return (session != null) && (database != null) && (sparksee != null) && (schema != null) && (graph != null);
    }

    public Graph getGraph() {
        return graph;
    }

    public Database getDatabase() {
        return database;
    }

    public Session getSession() {
        return session;
    }

    public BikeSchema getSchema() {
        return schema;
    }
}
