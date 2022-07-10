package com.example.khughes.machewidget;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.khughes.machewidget.CarStatus.CarStatus;
import com.example.khughes.machewidget.OTAStatus.FuseResponse;
import com.example.khughes.machewidget.OTAStatus.FuseResponseList;
import com.example.khughes.machewidget.OTAStatus.LanguageText;
import com.example.khughes.machewidget.OTAStatus.OTAStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

@Entity(tableName = "vehicle_info")
public class VehicleInfo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String VIN;

    private String userId;

    private String nickname;

    private long lastRefreshTime;

    private long lastUpdateTime;

    @ColumnInfo(defaultValue = "0")
    @NonNull
    private long lastOTATime;

    private String lastLVBStatus;

    private String lastTPMSStatus;

    @ColumnInfo(defaultValue = "''")
    @NonNull
    private String lastChargeStatus;

    private double lastDTE;

    private double lastFuelLevel;

    private boolean supportsOTA;

    @ColumnInfo(defaultValue = "0")
    @NonNull
    private long initialForcedRefreshTime;

    @ColumnInfo(defaultValue = "0")
    @NonNull
    private long lastForcedRefreshTime;

    @ColumnInfo(defaultValue = "0")
    @NonNull
    private long forcedRefreshCount;

    @ColumnInfo(defaultValue = "1")
    @NonNull
    private Boolean enabled;

    @ColumnInfo(defaultValue = "0xffffffff")
    @NonNull
    private int colorValue;

    public VehicleInfo() {
        id = 0; // for new database entries, this will generate a new id
        initialForcedRefreshTime = 0;
        lastRefreshTime = 0;
        lastUpdateTime = 0;
        lastOTATime = 0;
        lastLVBStatus = "STATUS_GOOD";
        lastTPMSStatus = "Normal";
        lastDTE = 0.0;
        lastFuelLevel = 0.0;
        supportsOTA = true;
        enabled = true;
        lastChargeStatus = "''";
        lastForcedRefreshTime = 0;
        forcedRefreshCount = 0;
        colorValue = 0xffffffff;
    }

    @Embedded(prefix = "car_")
    private CarStatus carStatus;

    @Embedded(prefix = "ota_")
    private FuseResponseList responseList;

    @Embedded(prefix = "ota_")
    private Object error;

    private String otaAlertStatus;

    @Embedded(prefix = "ota_")
    private Object updatePendingState;

    @Embedded(prefix = "ota_")
    private LanguageText languageText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLastLVBStatus() {
        return lastLVBStatus;
    }

    public void setLastLVBStatus(String lastLVBStatus) {
        this.lastLVBStatus = lastLVBStatus;
    }

    public String getLastTPMSStatus() {
        return lastTPMSStatus;
    }

    public void setLastTPMSStatus(String lastTPMSStatus) {
        this.lastTPMSStatus = lastTPMSStatus;
    }

    public double getLastDTE() {
        return lastDTE;
    }

    public void setLastDTE(double lastDTE) {
        this.lastDTE = lastDTE;
    }

    public double getLastFuelLevel() {
        return lastFuelLevel;
    }

    public void setLastFuelLevel(double lastFuelLevel) {
        this.lastFuelLevel = lastFuelLevel;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setLastUpdateTime() {
        this.lastUpdateTime = LocalDateTime.now(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public boolean isSupportsOTA() {
        return supportsOTA;
    }

    public void setSupportsOTA(boolean supportsOTA) {
        this.supportsOTA = supportsOTA;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getLastChargeStatus() {
        return lastChargeStatus;
    }

    public void setLastChargeStatus(String lastChargeStatus) { this.lastChargeStatus = lastChargeStatus; }

    public long getLastOTATime() { return lastOTATime; }

    public void setLastOTATime(long lastOTATime) { this.lastOTATime = lastOTATime; }

    public long getLastForcedRefreshTime() {  return lastForcedRefreshTime; }

    public void setLastForcedRefreshTime(long lastForcedRefreshTime) { this.lastForcedRefreshTime = lastForcedRefreshTime; }

    public long getForcedRefreshCount() { return forcedRefreshCount; }

    public void setForcedRefreshCount(long forcedRefreshCount) { this.forcedRefreshCount = forcedRefreshCount; }

    public long getInitialForcedRefreshTime() { return initialForcedRefreshTime; }

    public void setInitialForcedRefreshTime(long initialForcedRefreshTime) { this.initialForcedRefreshTime = initialForcedRefreshTime; }

    public int getColorValue() {
        return colorValue;
    }

    public void setColorValue(int colorValue) {
        this.colorValue = colorValue;
    }

    @Entity
    public class CarStatusInfo {
        @PrimaryKey(autoGenerate = true)
        private int id;

        private String VIN;

        @Embedded
        private CarStatus carStatus;

        public int getId() { return id; }

        public void setId(int id) { this.id = id; }

        @NonNull
        public String getVIN() {
            return VIN;
        }

        public void setVIN(@NonNull String VIN) {
            this.VIN = VIN;
        }

        public CarStatus getCarStatus() {
            return carStatus;
        }

        public void setCarStatus(CarStatus carStatus) {
            this.carStatus = carStatus;
        }

    }
}
