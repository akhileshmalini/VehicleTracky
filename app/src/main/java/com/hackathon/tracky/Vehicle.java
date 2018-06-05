package com.hackathon.tracky;

public class Vehicle {

    String vehicleName,vehicleNumber, vehicleImage;

    public Vehicle(String vehicleName, String vehicleNumber, String vehicleImage) {
        this.vehicleName = vehicleName;
        this.vehicleNumber = vehicleNumber;
        this.vehicleImage = vehicleImage;
    }

    public Vehicle() {
        this.vehicleName = "";
        this.vehicleNumber = "";
        this.vehicleImage = "";
    }


    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }
}
