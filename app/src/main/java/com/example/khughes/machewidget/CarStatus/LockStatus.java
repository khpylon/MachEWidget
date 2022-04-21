package com.example.khughes.machewidget.CarStatus;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LockStatus {

    @SerializedName("value")
    @Expose
    @ColumnInfo(name = "lockstatus_value")
    private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
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