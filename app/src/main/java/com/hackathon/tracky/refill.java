package com.hackathon.tracky;

class refill
{
    public double petrol_before_refuel, petrol_after_refuel, distance;

    public refill(double distance, double petrol_before_refuel, double petrol_after_refuel)
    {
        this.petrol_before_refuel = petrol_before_refuel;
        this.petrol_after_refuel = petrol_after_refuel;
        this.distance = distance;
    }

    public refill() {
        this.petrol_before_refuel = 1;
        this.petrol_after_refuel = 1;
        this.distance = 1;
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
}
