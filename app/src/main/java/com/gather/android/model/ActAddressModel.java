package com.gather.android.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Christain on 2015/3/27.
 */
public class ActAddressModel implements Serializable {

    private int id;//地点ID
    private double lon;
    private double lat;
    private String addr_city;
    private String addr_area;
    private String addr_road;
    private String addr_num;
    private String addr_route;
    private String addr_name;
    private String time;
    private ArrayList<ActCarLocationModel> parking_spaces;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddr_city() {
        if (addr_city != null) {
            return addr_city;
        }
        return "";
    }

    public void setAddr_city(String addr_city) {
        this.addr_city = addr_city;
    }

    public String getAddr_area() {
        if (addr_area != null) {
            return addr_area;
        }
        return "";
    }

    public void setAddr_area(String addr_area) {
        this.addr_area = addr_area;
    }

    public String getAddr_road() {
        if (addr_road != null) {
            return addr_road;
        }
        return "";
    }

    public void setAddr_road(String addr_road) {
        this.addr_road = addr_road;
    }

    public String getAddr_num() {
        if (addr_num != null) {
            return addr_num;
        }
        return "";
    }

    public void setAddr_num(String addr_num) {
        this.addr_num = addr_num;
    }

    public String getAddr_route() {
        if (addr_route != null) {
            return addr_route;
        }
        return "";
    }

    public void setAddr_route(String addr_route) {
        this.addr_route = addr_route;
    }

    public String getAddr_name() {
        if (addr_name != null) {
            return addr_name;
        }
        return "";
    }

    public void setAddr_name(String addr_name) {
        this.addr_name = addr_name;
    }

    public String getTime() {
        if (time != null) {
            return time;
        }
        return "";
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<ActCarLocationModel> getParking_spaces() {
        return parking_spaces;
    }

    public void setParking_spaces(ArrayList<ActCarLocationModel> parking_spaces) {
        this.parking_spaces = parking_spaces;
    }
}
