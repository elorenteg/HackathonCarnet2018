package com.bvisible.carnet.models;

import java.util.ArrayList;

public class StopNextRoutes {
    private int stopid;
    private String stopname;
    private ArrayList<RouteTime> routes;

    public int getStopid() {
        return stopid;
    }

    public void setStopid(int stopid) {
        this.stopid = stopid;
    }

    public String getStopname() {
        return stopname;
    }

    public void setStopname(String stopname) {
        this.stopname = stopname;
    }

    public ArrayList<RouteTime> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<RouteTime> routes) {
        this.routes = routes;
    }

    @Override
    public String toString() {
        return "StopNextRoutes{" +
                "stopid=" + stopid +
                ", stopname='" + stopname + '\'' +
                ", routes=" + routes +
                '}';
    }
}
