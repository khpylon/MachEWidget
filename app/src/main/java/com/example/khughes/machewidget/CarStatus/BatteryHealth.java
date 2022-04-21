package com.example.khughes.machewidget.CarStatus;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class BatteryHealth {

    @SerializedName("value")
    @Expose
    @ColumnInfo(name = "batteryhealth_value")
    private String value;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}