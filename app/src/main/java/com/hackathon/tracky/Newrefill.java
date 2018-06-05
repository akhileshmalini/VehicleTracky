package com.hackathon.tracky;

class Newrefill
{
    public double petrol_before_refuel, petrol_after_refuel, distance, latitude,longitude;
    String timestamp;


    public Newrefill(double petrol_before_refuel, double petrol_after_refuel, double distance, double latitude, double longitude, String timestamp) {
        this.petrol_before_refuel = petrol_before_refuel;
        this.petrol_after_refuel = petrol_after_refuel;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }


    public Newrefill() {
        this.petrol_before_refuel = 1.0;
        this.petrol_after_refuel = 1.0;
        this.distance = 1.0;
        this.latitude = 1.0;
        this.longitude = 1.0;
        this.timestamp = "";
    }


    public double getPetrol_before_refuel() {
        return petrol_before_refuel;
    }

    public void setPetrol_before_refuel(double petrol_before_refuel) {
        this.petrol_before_refuel = petrol_before_refuel;
    }

    public double getPetrol_after_refuel() {
        return petrol_after_refuel;
    }

    public void setPetrol_after_refuel(double petrol_after_refuel) {
        this.petrol_after_refuel = petrol_after_refuel;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
