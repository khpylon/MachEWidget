package com.example.khughes.machewidget.CarStatus;

import androidx.room.Embedded;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Battery {

    @SerializedName("batteryHealth")
    @Expose
    @Embedded
    private BatteryHealth batteryHealth;
    @SerializedName("batteryStatusActual")
    @Expose
    @Embedded
    private BatteryStatusActual batteryStatusActual;

    public BatteryHealth getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(BatteryHealth batteryHealth) {
        this.batteryHealth = batteryHealth;
    }

    public BatteryStatusActual getBatteryStatusActual() {
        return batteryStatusActual;
    }

    public void setBatteryStatusActual(BatteryStatusActual batteryStatusActual) {
        this.batteryStatusActual = batteryStatusActual;
    }

}
