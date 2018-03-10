package com.bvisible.carnet.models;

public class Route {
    private int id;
    private String shortname;
    private String longname;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getLongname() {
        return longname;
    }

    public void setLongname(String longname) {
        this.longname = longname;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", shortname='" + shortname + '\'' +
                ", longname='" + longname + '\'' +
                ", type=" + type +
                '}';
    }
}
