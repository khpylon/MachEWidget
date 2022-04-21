package com.example.khughes.machewidget.OTAStatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LatestStatus {

    @SerializedName("aggregateStatus")
    @Expose
    private String aggregateStatus;
    @SerializedName("detailedStatus")
    @Expose
    private String detailedStatus;
    @SerializedName("dateTimestamp")
    @Expose
    private String dateTimestamp;

    public LatestStatus() {}

    public String getAggregateStatus() {
        return aggregateStatus;
    }

    public void setAggregateStatus(String aggregateStatus) {
        this.aggregateStatus = aggregateStatus;
    }

    public String getDetailedStatus() {
        return detailedStatus;
    }

    public void setDetailedStatus(String detailedStatus) {
        this.detailedStatus = detailedStatus;
    }

    public String getDateTimestamp() {
        return dateTimestamp;
    }

    public void setDateTimestamp(String dateTimestamp) {
        this.dateTimestamp = dateTimestamp;
    }

}

