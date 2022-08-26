package com.example.khughes.machewidget;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.khughes.machewidget.OTAStatus.FuseResponse;
import com.example.khughes.machewidget.OTAStatus.FuseResponseList;
import com.example.khughes.machewidget.OTAStatus.LanguageText;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;

import java.util.ArrayList;

@Entity(tableName = "ota_info")
public class OTAInfo {

    public OTAInfo(String VIN) {
        this.VIN = VIN;
        id = 0;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String VIN;

    @Embedded(prefix = "ota_")
    private FuseResponseList responseList;

    @Embedded(prefix = "ota_")
    private LanguageText languageText;

    private String ota_AlertStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public FuseResponseList getResponseList() {
        return responseList;
    }

    public void setResponseList(FuseResponseList responseList) {
        this.responseList = responseList;
    }

    public String getOtaAlertStatus() {
        return ota_AlertStatus;
    }

    public void setOtaAlertStatus(String otaAlertStatus) {
        this.ota_AlertStatus = otaAlertStatus;
    }

    public String getOta_AlertStatus() { return ota_AlertStatus; }

    public LanguageText getLanguageText() {
        return languageText;
    }

    public void setLanguageText(LanguageText languageText) {
        this.languageText = languageText;
    }

    public void setOta_AlertStatus(String ota_AlertStatus) {
        this.ota_AlertStatus = ota_AlertStatus;
    }

    public void fromOTAStatus(OTAStatus status) {
        this.responseList = status.getFuseResponse().getFuseResponseList().get(0);
        this.ota_AlertStatus = status.getOtaAlertStatus();
        this.languageText = status.getFuseResponse().getLanguageText();
    }

    public OTAStatus toOTAStatus() {
        OTAStatus status = new OTAStatus();

        ArrayList<FuseResponseList> list = new ArrayList<>();
        list.add(this.responseList);
        FuseResponse tmp = new FuseResponse();
        tmp.setFuseResponseList(list);
        status.setFuseResponse(tmp);
        status.setOtaAlertStatus(this.ota_AlertStatus);
        status.getFuseResponse().setLanguageText(this.languageText);
        return status;
    }

}
