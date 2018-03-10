package com.bvisible.carnet;

import com.sparsity.sparksee.algorithms.SinglePairShortestPathBFS;
import com.sparsity.sparksee.gdb.*;

import java.util.ArrayList;

public class Algorithms {

    public static ArrayList<Integer> findRoute(Session session, Schema schema, int stopA, int stopB) {

        Graph graph = session.getGraph();

        Objects aux = graph.select(schema.getStopIdType(), Condition.Equal, (new Value()).setInteger(stopA));
        long stopAOid = aux.any();
        aux.close();
        aux = graph.select(schema.getStopIdType(), Condition.Equal, (new Value()).setInteger(stopB));
        long stopBOid = aux.any();
        aux.close();

        SinglePairShortestPathBFS shortestPath = new SinglePairShortestPathBFS(session,
                                                                               stopAOid,
                                                                               stopBOid);

        shortestPath.addNodeType(schema.getStopType());
        shortestPath.addEdgeType(schema.getConnectsType(), EdgesDirection.Any);
        shortestPath.run();

        OIDList list = shortestPath.getPathAsNodes();
        ArrayList<Integer> path = new ArrayList<Integer>();
        for( long stop : list) {
            Value value = new Value();
            graph.getAttribute(stop, schema.getStopIdType(), value);
            path.add(value.getInteger());
        }

        return path;
    }
}
