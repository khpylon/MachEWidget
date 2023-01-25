package com.example.khughes.machewidget.CarStatus;

import androidx.room.Embedded;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CarStatus {

    // simpler getters go here
    public Double getOdometer() {
        try {
            return getVehiclestatus().getOdometer().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Double getHVBFillLevel() {
        try {
            return getVehiclestatus().getBatteryFillLevel().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Double getElVehDTE() {
        try {
            return getVehiclestatus().getElVehDTE().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Double getDistanceToEmpty() {
        try {
            return getVehiclestatus().getFuel().getDistanceToEmpty();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Double getFuelLevel() {
        try {
            return getVehiclestatus().getFuel().getFuelLevel();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Integer getLVBVoltage() {
        try {
            return getVehiclestatus().getBattery().getBatteryStatusActual().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLVBStatus() {
        try {
            return getVehiclestatus().getBattery().getBatteryHealth().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getFrunk() {
        try {
            return getVehiclestatus().getDoorStatus().getHoodDoor().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getTailgate() {
        try {
            return getVehiclestatus().getDoorStatus().getTailgateDoor().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getDriverDoor() {
        try {
            return getVehiclestatus().getDoorStatus().getDriverDoor().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getPassengerDoor() {
        try {
            return getVehiclestatus().getDoorStatus().getPassengerDoor().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftRearDoor() {
        try {
            return getVehiclestatus().getDoorStatus().getLeftRearDoor().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightRearDoor() {
        try {
            return getVehiclestatus().getDoorStatus().getRightRearDoor().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getDriverWindow() {
        try {
            return getVehiclestatus().getWindowPosition().getDriverWindowPosition().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getPassengerWindow() {
        try {
            return getVehiclestatus().getWindowPosition().getPassWindowPosition().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftRearWindow() {
        try {
            return getVehiclestatus().getWindowPosition().getRearDriverWindowPos().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightRearWindow() {
        try {
            return getVehiclestatus().getWindowPosition().getRearPassWindowPos().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftFrontTireStatus() {
        try {
            return getVehiclestatus().getTpms().getLeftFrontTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightFrontTireStatus() {
        try {
            return getVehiclestatus().getTpms().getRightFrontTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftRearTireStatus() {
        try {
            return getVehiclestatus().getTpms().getOuterLeftRearTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightRearTireStatus() {
        try {
            return getVehiclestatus().getTpms().getOuterRightRearTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftFrontTirePressure() {
        try {
            return getVehiclestatus().getTpms().getLeftFrontTirePressure().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightFrontTirePressure() {
        try {
            return getVehiclestatus().getTpms().getRightFrontTirePressure().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftRearTirePressure() {
        try {
            return getVehiclestatus().getTpms().getOuterLeftRearTirePressure().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightRearTirePressure() {
        try {
            return getVehiclestatus().getTpms().getOuterRightRearTirePressure().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getIgnition() {
        try {
            return getVehiclestatus().getIgnitionStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLock() {
        try {
            return getVehiclestatus().getLockStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Boolean getRemoteStartStatus() {
        try {
            return getVehiclestatus().getRemoteStartStatus().getValue() == 1;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getAlarm() {
        try {
            return getVehiclestatus().getAlarm().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Boolean getDeepSleep() {
        try {
            return getVehiclestatus().getDeepSleepInProgress().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Boolean getPlugStatus() {
        try {
            return getVehiclestatus().getPlugStatus().getValue() == 1;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public String getChargingStatus() {
        try {
            return getVehiclestatus().getChargingStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getChargingEndTime() {
        try {
            return getVehiclestatus().getChargeEndTime().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLatitude() {
        try {
            return getVehiclestatus().getGps().getLatitude();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLongitude() {
        try {
            return getVehiclestatus().getGps().getLongitude();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLastRefresh() {
        try {
            String lastRefresh = getVehiclestatus().getLastRefresh();
            if (lastRefresh.contains("01-01-2018")) {
                lastRefresh = getVehiclestatus().getLastModifiedDate();
            }
            return lastRefresh;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static final int PROPULSION_UNKNOWN = 0;
    private static final int PROPULSION_ELECTRIC = 1;
    private static final int PROPULSION_PHEV = 2;
    private static final int PROPULSION_ICE_OR_HYBRID = 3;

    public int getPropulsion() {
        if (getVehiclestatus() != null) {
            if (getVehiclestatus().getFuel() == null) {
                return PROPULSION_ELECTRIC;
            } else if (getVehiclestatus().getBatteryFillLevel() == null) {
                return PROPULSION_ICE_OR_HYBRID;
            } else {
                return PROPULSION_PHEV;
            }
        }
        return PROPULSION_UNKNOWN;
    }

    public boolean isPropulsionElectric (int method) {
        return method == PROPULSION_ELECTRIC;
    }

    public boolean isPropulsionPHEV (int method) {
        return method == PROPULSION_PHEV;
    }

    public boolean isPropulsionICEOrHybrid (int method) {
        return method == PROPULSION_ICE_OR_HYBRID;
    }

//    @Generated("jsonschema2pojo")
//    public class BattTracLoSocDDsply {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class BatteryChargeStatus {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//    }
//
//    @Generated("jsonschema2pojo")
//    public class BatteryPerfStatus {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }


//    @Generated("jsonschema2pojo")
//    public class BatteryTracLowChargeThreshold {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class CcsSettings {
//
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//        @SerializedName("location")
//        @Expose
//        private Integer location;
//        @SerializedName("vehicleConnectivity")
//        @Expose
//        private Integer vehicleConnectivity;
//        @SerializedName("vehicleData")
//        @Expose
//        private Integer vehicleData;
//        @SerializedName("drivingCharacteristics")
//        @Expose
//        private Integer drivingCharacteristics;
//        @SerializedName("contacts")
//        @Expose
//        private Integer contacts;
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//        public Integer getLocation() {
//            return location;
//        }
//
//        public void setLocation(Integer location) {
//            this.location = location;
//        }
//
//        public Integer getVehicleConnectivity() {
//            return vehicleConnectivity;
//        }
//
//        public void setVehicleConnectivity(Integer vehicleConnectivity) {
//            this.vehicleConnectivity = vehicleConnectivity;
//        }
//
//        public Integer getVehicleData() {
//            return vehicleData;
//        }
//
//        public void setVehicleData(Integer vehicleData) {
//            this.vehicleData = vehicleData;
//        }
//
//        public Integer getDrivingCharacteristics() {
//            return drivingCharacteristics;
//        }
//
//        public void setDrivingCharacteristics(Integer drivingCharacteristics) {
//            this.drivingCharacteristics = drivingCharacteristics;
//        }
//
//        public Integer getContacts() {
//            return contacts;
//        }
//
//        public void setContacts(Integer contacts) {
//            this.contacts = contacts;
//        }
//
//    }
//
//    @Generated("jsonschema2pojo")
//    public class ChargeStartTime {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }
//
//    @Generated("jsonschema2pojo")
//    public class DcFastChargeData {
//
//        @SerializedName("fstChrgBulkTEst")
//        @Expose
//        private FstChrgBulkTEst fstChrgBulkTEst;
//        @SerializedName("fstChrgCmpltTEst")
//        @Expose
//        private FstChrgCmpltTEst fstChrgCmpltTEst;
//
//        public FstChrgBulkTEst getFstChrgBulkTEst() {
//            return fstChrgBulkTEst;
//        }
//
//        public void setFstChrgBulkTEst(FstChrgBulkTEst fstChrgBulkTEst) {
//            this.fstChrgBulkTEst = fstChrgBulkTEst;
//        }
//
//        public FstChrgCmpltTEst getFstChrgCmpltTEst() {
//            return fstChrgCmpltTEst;
//        }
//
//        public void setFstChrgCmpltTEst(FstChrgCmpltTEst fstChrgCmpltTEst) {
//            this.fstChrgCmpltTEst = fstChrgCmpltTEst;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class DualRearWheel {
//
//        @SerializedName("value")
//        @Expose
//        private Integer value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public Integer getValue() {
//            return value;
//        }
//
//        public void setValue(Integer value) {
//            this.value = value;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class FirmwareUpgInProgress {
//
//        @SerializedName("value")
//        @Expose
//        private Boolean value;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public Boolean getValue() {
//            return value;
//        }
//
//        public void setValue(Boolean value) {
//            this.value = value;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class FstChrgBulkTEst {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class FstChrgCmpltTEst {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class HybridModeStatus {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class InnerLeftRearTirePressure {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class InnerLeftRearTireStatus {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class InnerRightRearTirePressure {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }
//
//    @Generated("jsonschema2pojo")
//    public class InnerRightRearTireStatus {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class InnerTailgateDoor {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }
//
//    @Generated("jsonschema2pojo")
//    public class Oil {
//
//        @SerializedName("oilLife")
//        @Expose
//        private String oilLife;
//        @SerializedName("oilLifeActual")
//        @Expose
//        private Integer oilLifeActual;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getOilLife() {
//            return oilLife;
//        }
//
//        public void setOilLife(String oilLife) {
//            this.oilLife = oilLife;
//        }
//
//        public Integer getOilLifeActual() {
//            return oilLifeActual;
//        }
//
//        public void setOilLifeActual(Integer oilLifeActual) {
//            this.oilLifeActual = oilLifeActual;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class OutandAbout {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class PreCondStatusDsply {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class PrmtAlarmEvent {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class RecommendedFrontTirePressure {
//
//        @SerializedName("value")
//        @Expose
//        private Integer value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public Integer getValue() {
//            return value;
//        }
//
//        public void setValue(Integer value) {
//            this.value = value;
//        }
//
//    }
//
//    @Generated("jsonschema2pojo")
//    public class RecommendedRearTirePressure {
//
//        @SerializedName("value")
//        @Expose
//        private Integer value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public Integer getValue() {
//            return value;
//        }
//
//        public void setValue(Integer value) {
//            this.value = value;
//        }
//
//    }
//
//    @Generated("jsonschema2pojo")
//    public class TirePressure {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class TirePressureByLocation {
//
//        @SerializedName("value")
//        @Expose
//        private Integer value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public Integer getValue() {
//            return value;
//        }
//
//        public void setValue(Integer value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class TirePressureSystemStatus {
//
//        @SerializedName("value")
//        @Expose
//        private String value;
//        @SerializedName("status")
//        @Expose
//        private String status;
//        @SerializedName("timestamp")
//        @Expose
//        private String timestamp;
//
//        public String getValue() {
//            return value;
//        }
//
//        public void setValue(String value) {
//            this.value = value;
//        }
//
//        public String getStatus() {
//            return status;
//        }
//
//        public void setStatus(String status) {
//            this.status = status;
//        }
//
//        public String getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(String timestamp) {
//            this.timestamp = timestamp;
//        }
//
//    }


    @SerializedName("vehiclestatus")
    @Expose
    @Embedded
    private Vehiclestatus vehiclestatus;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("status")
    @Expose
    private Integer status;

    public Vehiclestatus getVehiclestatus() {
        return vehiclestatus;
    }

    public void setVehiclestatus(Vehiclestatus vehiclestatus) {
        this.vehiclestatus = vehiclestatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getStatus () { return status; }
    public void setStatus(Integer status) {
        this.status = status;
    }

}
