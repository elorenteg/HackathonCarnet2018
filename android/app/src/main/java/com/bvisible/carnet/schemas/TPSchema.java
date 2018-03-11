package com.bvisible.carnet.schemas;

import com.sparsity.sparksee.gdb.Graph;
import com.sparsity.sparksee.gdb.Type;

public class TPSchema {

    private int stopType        = Type.InvalidType;
    private int routeType       = Type.InvalidType;
    private int timeType        = Type.InvalidType;
    private int arrivaltimeType = Type.InvalidType;
    private int connectStopsType    = Type.InvalidType;
    private int connectRouteType    = Type.InvalidType;
    private int connectstopTimeType     = Type.InvalidType;
    private int connectrouteTimeType    = Type.InvalidType;

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

    private int arrivaltimeIdType   = Type.InvalidType;
    private int arrivaltimeTimeType = Type.InvalidType;

    private int timeStopType    = Type.InvalidType;
    private int timeRouteType   = Type.InvalidType;
    private int timeTimeType    = Type.InvalidType;


    public TPSchema(Graph graph) {

        stopType = graph.findType("stop");
        routeType = graph.findType("route");
        connectStopsType = graph.findType("connectstops");
        connectRouteType = graph.findType("connectroute");
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

        arrivaltimeType = graph.findType("arrivaltime");
        connectstopTimeType = graph.findType("connectstoptime");
        connectrouteTimeType = graph.findType("connectroutetime");
        arrivaltimeIdType = graph.findAttribute(arrivaltimeType,"id");
        arrivaltimeTimeType = graph.findAttribute(arrivaltimeType,"time");

        timeType = graph.findType("time");
        timeStopType = graph.findAttribute(timeType,"stopid");
        timeRouteType = graph.findAttribute(timeType,"routid");
        timeTimeType = graph.findAttribute(timeType,"time");

    }

    public int getStopType() {
        return stopType;
    }

    public int getRouteType() {
        return routeType;
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

    public int getConnectStopsType() {
        return connectStopsType;
    }

    public int getConnectRouteType() {
        return connectRouteType;
    }

    public int getArrivaltimeType() {
        return arrivaltimeType;
    }

    public int getConnectstopTimeType() {
        return connectstopTimeType;
    }

    public int getConnectrouteTimeType() {
        return connectrouteTimeType;
    }

    public int getArrivaltimeIdType() {
        return arrivaltimeIdType;
    }

    public int getArrivaltimeTimeType() {
        return arrivaltimeTimeType;
    }

    public int getTimeType() {
        return timeType;
    }

    public int getTimeStopType() {
        return timeStopType;
    }

    public int getTimeRouteType() {
        return timeRouteType;
    }

    public int getTimeTimeType() {
        return timeTimeType;
    }
}
