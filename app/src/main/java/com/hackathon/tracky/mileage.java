package com.hackathon.tracky;

public  class mileage
{
    double mileage;

    public mileage(double mileage) {
        this.mileage = mileage;
    }

    public mileage() {
        this.mileage = 1.0;

    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }
}