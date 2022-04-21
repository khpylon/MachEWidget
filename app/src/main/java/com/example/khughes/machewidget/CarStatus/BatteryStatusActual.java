package com.example.khughes.machewidget.CarStatus;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class BatteryStatusActual {

    @SerializedName("value")
    @Expose
    @ColumnInfo(name = "batterystatusactual_value")
    private Integer value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}

