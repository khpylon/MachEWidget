package com.example.khughes.machewidget.CarStatus;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class DeepSleepInProgress {

    @SerializedName("value")
    @Expose
    @ColumnInfo(name = "deepsleep_value")
    private Boolean value;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

}

