package com.example.khughes.machewidget;


import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.OTAStatus.FuseResponse;
import com.example.khughes.machewidget.OTAStatus.FuseResponseList;
import com.example.khughes.machewidget.OTAStatus.LanguageText;
import com.example.khughes.machewidget.OTAStatus.LatestStatus;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "vehicle_info")
public class VehicleInfo {

    @PrimaryKey
    @NonNull
    private String VIN;

    private String userId;

    @Embedded
    private CarStatus carStatus;

    @Embedded
    private FuseResponseList responseList;

    @Embedded
    private Object error;

    private String otaAlertStatus;

    @Embedded
    private Object updatePendingState;

    @Embedded
    private LanguageText languageText;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public CarStatus getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(CarStatus carStatus) {
        this.carStatus = carStatus;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public String getOtaAlertStatus() {
        return otaAlertStatus;
    }

    public void setOtaAlertStatus(String otaAlertStatus) {
        this.otaAlertStatus = otaAlertStatus;
    }

    public Object getUpdatePendingState() {
        return updatePendingState;
    }

    public void setUpdatePendingState(Object updatePendingState) {
        this.updatePendingState = updatePendingState;
    }

    public LanguageText getLanguageText() {
        return languageText;
    }

    public void setLanguageText(LanguageText languageText) {
        this.languageText = languageText;
    }

    public FuseResponseList getResponseList() {
        return responseList;
    }

    public void setResponseList(FuseResponseList responseList) {
        this.responseList = responseList;
    }

    public void fromOTAStatus(OTAStatus status) {
        this.responseList = status.getFuseResponse().getFuseResponseList().get(0);
        this.error = status.getError();
        this.otaAlertStatus = status.getOtaAlertStatus();
        this.updatePendingState = status.getUpdatePendingState();
        this.languageText = status.getFuseResponse().getLanguageText();
    }

    public OTAStatus toOTAStatus() {
        OTAStatus status = new OTAStatus();

        ArrayList<FuseResponseList> list = new ArrayList<>();
        list.add(this.responseList);
        FuseResponse tmp = new FuseResponse();
        tmp.setFuseResponseList(list);
        status.setFuseResponse(tmp);
        status.setError(this.error);
        status.setOtaAlertStatus(this.otaAlertStatus);
        status.setUpdatePendingState(this.updatePendingState);
        status.getFuseResponse().setLanguageText(this.languageText);
        return status;
    }

}
