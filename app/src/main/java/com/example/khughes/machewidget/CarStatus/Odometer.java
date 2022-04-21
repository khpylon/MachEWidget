package com.example.khughes.machewidget.CarStatus;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Odometer {

    @SerializedName("value")
    @Expose
    @ColumnInfo(name = "odometer_value")
    private Double value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}

