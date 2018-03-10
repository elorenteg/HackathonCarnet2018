package com.bvisible.carnet;

import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Type;

public class Schema {

    private int stopType        = Type.InvalidType;
    private int routeType       = Type.InvalidType;
    private int connectsType    = Type.InvalidType;
    private int hasStopType     = Type.InvalidType;

    private int stopIdType      = Type.InvalidType;
    private int stopNameType    = Type.InvalidType;
    private int stopLatType     = Type.InvalidType;
    private int stopLonType     = Type.InvalidType;

    private int routeIdType         = Type.InvalidType;
    private int routeShortNameType  = Type.InvalidType;
    private int routeLongNameType   = Type.InvalidType;
    private int routeUrlType        = Type.InvalidType;
    private int routeTypeType       = Type.InvalidType;


    public Schema(Graph graph) {

        stopType = graph.findType("stop");
        routeType = graph.findType("route");
        connectsType = graph.findType("connects");
        hasStopType = graph.findType("has_stop");

        stopIdType = graph.findAttribute(stopType,"id");
        stopNameType = graph.findAttribute(stopType,"name");
        stopLatType = graph.findAttribute(stopType,"lat");
        stopLonType = graph.findAttribute(stopType,"lon");

        routeIdType = graph.findAttribute(routeType,"id");
        routeShortNameType = graph.findAttribute(routeType,"short_name");
        routeLongNameType = graph.findAttribute(routeType,"long_name");
        routeUrlType = graph.findAttribute(routeType,"url");
        routeTypeType = graph.findAttribute(routeType,"type");

    }

    public int getStopType() {
        return stopType;
    }

    public int getRouteType() {
        return routeType;
    }

    public int getConnectsType() {
        return connectsType;
    }

    public int getHasStopType() {
        return hasStopType;
    }

    public int getStopIdType() {
        return stopIdType;
    }

    public int getStopNameType() {
        return stopNameType;
    }

    public int getStopLatType() {
        return stopLatType;
    }

    public int getStopLonType() {
        return stopLonType;
    }

    public int getRouteIdType() {
        return routeIdType;
    }

    public int getRouteShortNameType() {
        return routeShortNameType;
    }

    public int getRouteLongNameType() {
        return routeLongNameType;
    }

    public int getRouteUrlType() {
        return routeUrlType;
    }

    public int getRouteTypeType() {
        return routeTypeType;
    }
}
