package com.example.gpstest;


import android.location.Location;

public class PointOfInterest {

    private String name;
    private double latitude;
    private double longitude;

    public PointOfInterest(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        String result = name + "\nLatitude: " + latitude + "\nLongitude: " + longitude;
        return result;
    }

    public double getDistance(Location location) {
//        double latDiff = location.getLatitude() - latitude;
//        double longDiff = location.getLongitude() - longitude;
//        double distance = latDiff * latDiff + longDiff * longDiff;
//        return distance;


        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(location.getLatitude() - latitude);
        double dLng = Math.toRadians(location.getLongitude() - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(location.getLatitude())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (double) (earthRadius * c);

        return dist;
    }

    public String distanceSummary(Location location) {
        return toString() + "\nDistance: " + getDistance(location) + " metres";
    }
}
