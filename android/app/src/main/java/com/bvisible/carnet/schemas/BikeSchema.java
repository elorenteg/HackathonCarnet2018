package com.bvisible.carnet.schemas;

import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Type;

public class BikeSchema {
    private int laneType            = Type.InvalidType;

    private int laneIdType          = Type.InvalidType;
    private int laneNameType        = Type.InvalidType;
    private int laneLat1Type        = Type.InvalidType;
    private int laneLon1Type        = Type.InvalidType;
    private int laneLat2Type        = Type.InvalidType;
    private int laneLon2Type        = Type.InvalidType;


    public BikeSchema(Graph graph) {
        laneType = graph.findType("lane");

        laneIdType = graph.findAttribute(laneType,"id");
        laneNameType = graph.findAttribute(laneType,"name");
        laneLat1Type = graph.findAttribute(laneType,"lat1");
        laneLon1Type = graph.findAttribute(laneType,"lng1");
        laneLat2Type = graph.findAttribute(laneType,"lat2");
        laneLon2Type = graph.findAttribute(laneType,"lng2");
    }

    public int getLaneType() {
        return laneType;
    }

    public int getLaneIdType() {
        return laneIdType;
    }

    public int getLaneNameType() {
        return laneNameType;
    }

    public int getLaneLat1Type() {
        return laneLat1Type;
    }

    public int getLaneLon1Type() {
        return laneLon1Type;
    }

    public int getLaneLat2Type() {
        return laneLat2Type;
    }

    public int getLaneLon2Type() {
        return laneLon2Type;
    }
}
