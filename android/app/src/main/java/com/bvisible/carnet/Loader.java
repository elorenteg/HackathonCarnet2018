package com.bvisible.carnet;

import com.sparsity.sparksee.gdb.*;
import com.sparsity.sparksee.script.ScriptParser;

import java.util.ArrayList;

public class Loader {

    public static void load() throws Exception {

        ScriptParser scriptParser = new ScriptParser();
        scriptParser.parse("transport.schema",true,"");
        scriptParser.parse("transport.load",true,"");

        Sparksee sparksee = new Sparksee(new SparkseeConfig());
        Database database = sparksee.open("transport.gdb",true);
        Session session = database.newSession();

        Schema schema = new Schema(session.getGraph());

        ArrayList<Integer> route = Algorithms.findRoute(session, schema, 0,29 );

        Graph graph = session.getGraph();
        for( Integer id : route ) {
            Objects objects = graph.select(schema.getStopIdType(), Condition.Equal, (new Value()).setInteger(id));
            Value value = new Value();
            graph.getAttribute(objects.any(), schema.getStopNameType(),value);
            System.out.println(value.getString());
            objects.close();
        }

        session.close();
        database.close();
        sparksee.close();
    }

}
