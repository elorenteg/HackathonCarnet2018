package com.bvisible.carnet.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bvisible.carnet.Algorithms;
import com.bvisible.carnet.R;
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
import com.sparsity.sparksee.script.ScriptParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TPGraph extends AsyncTask<Void, List<String>, List<String>> {

    public static String TAG = "TPGraph";
    public static Context mContext;

    // Sparksee
    private final String SparkseeLicense = "NWNRP-J7NZ0-7159N-FJG09";
    private static final String GRAPH_DATABASE_NAME = "transport.gdb";
    private Sparksee sparksee;
    private Database database;
    private Session session;
    private Graph graph;

    public void loadGraph(Context context) throws IOException {
        mContext = context;

        sparksee = null;
        database = null;
        session = null;
        graph = null;

        execute();
    }

    public boolean isGraphLoaded() {
        return graph != null;
    }

    public Graph getGraph() {
        return graph;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        try {
            SparkseeConfig cfg = new SparkseeConfig();
            cfg.setLicense(SparkseeLicense);
            //cfg.setCacheMaxSize(15);

            File sdcard = Environment.getExternalStorageDirectory();
            cfg.setLogFile(sdcard.getPath() + "/sparksee.log");
            cfg.setLogLevel(LogLevel.Debug);
            sparksee = new Sparksee(cfg);

            File file = new File(sdcard, GRAPH_DATABASE_NAME);
            database = sparksee.open(file.getPath(), true);
            session = database.newSession();

            Schema schema = new Schema(session.getGraph());

            ArrayList<Integer> route = Algorithms.findRoute(session, schema, 0,29 );

            graph = session.getGraph();
            for( Integer id : route ) {
                Objects objects = graph.select(schema.getStopIdType(), Condition.Equal, (new Value()).setInteger(id));
                Value value = new Value();
                graph.getAttribute(objects.any(), schema.getStopNameType(),value);
                //Log.e(TAG, value.getString());
                objects.close();
            }

            session.close();
            database.close();
            sparksee.close();
        } catch (Exception e) {
            Log.e(TAG, "error ", e);
        }

        return null;
    }

    protected void onPostExecute(List<String> values) {
        Log.e(TAG, "onPost");
    }
}
