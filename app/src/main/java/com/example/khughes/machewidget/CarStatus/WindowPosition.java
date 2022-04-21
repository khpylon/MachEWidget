package com.example.khughes.machewidget.CarStatus;

import androidx.room.Embedded;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class WindowPosition {

    @SerializedName("driverWindowPosition")
    @Expose
    @Embedded
    private DriverWindowPosition driverWindowPosition;
    @SerializedName("passWindowPosition")
    @Expose
    @Embedded
    private PassWindowPosition passWindowPosition;
    @SerializedName("rearDriverWindowPos")
    @Expose
    @Embedded
    private RearDriverWindowPos rearDriverWindowPos;
    @SerializedName("rearPassWindowPos")
    @Expose
    @Embedded
    private RearPassWindowPos rearPassWindowPos;

    public DriverWindowPosition getDriverWindowPosition() {
        return driverWindowPosition;
    }

    public void setDriverWindowPosition(DriverWindowPosition driverWindowPosition) {
        this.driverWindowPosition = driverWindowPosition;
    }

    public PassWindowPosition getPassWindowPosition() {
        return passWindowPosition;
    }

    public void setPassWindowPosition(PassWindowPosition passWindowPosition) {
        this.passWindowPosition = passWindowPosition;
    }

    public RearDriverWindowPos getRearDriverWindowPos() {
        return rearDriverWindowPos;
    }

    public void setRearDriverWindowPos(RearDriverWindowPos rearDriverWindowPos) {
        this.rearDriverWindowPos = rearDriverWindowPos;
    }

    public RearPassWindowPos getRearPassWindowPos() {
        return rearPassWindowPos;
    }

    public void setRearPassWindowPos(RearPassWindowPos rearPassWindowPos) {
        this.rearPassWindowPos = rearPassWindowPos;
    }

}

