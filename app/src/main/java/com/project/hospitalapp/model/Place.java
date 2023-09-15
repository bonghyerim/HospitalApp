package com.project.hospitalapp.model;



import java.io.Serializable;

public class Place implements Serializable {

    public String name;
    public String vicinity;

    public Geometry geometry;

    public String place_id;

    public OpeningHours opening_hours;

    private float distance;


    public class Geometry implements Serializable {

        public Location location;

        public class Location implements Serializable {
            public double lat;
            public double lng;
        }
    }

    public  class OpeningHours implements Serializable{

        public boolean open_now;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}