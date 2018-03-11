package com.bvisible.carnet.models;

import android.support.annotation.NonNull;

import java.util.Date;

public class RouteTime implements Comparable<RouteTime> {
    private String name;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RouteTime{" +
                "name='" + name + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public int compareTo(@NonNull RouteTime rt) {
        if (date.before(rt.getDate())) {
            return -1;
        }
        else return 1;
    }
}
