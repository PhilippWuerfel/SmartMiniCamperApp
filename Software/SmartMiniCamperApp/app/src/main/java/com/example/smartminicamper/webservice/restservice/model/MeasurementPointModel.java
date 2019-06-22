package com.example.smartminicamper.webservice.restservice.model;

import java.util.Date;

public class MeasurementPointModel {

    String id;
    double batteryVoltage;
    double powerUsage;
    double powerCharge;
    String date;
    //Date date;

    public MeasurementPointModel(String id, double batteryVoltage, double powerUsage, double powerCharge, String date) {
        this.id = id;
        this.batteryVoltage = batteryVoltage;
        this.powerUsage = powerUsage;
        this.powerCharge = powerCharge;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public void setPowerUsage(double powerUsage) {
        this.powerUsage = powerUsage;
    }

    public double getPowerCharge() {
        return powerCharge;
    }

    public void setPowerCharge(double powerCharge) {
        this.powerCharge = powerCharge;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
