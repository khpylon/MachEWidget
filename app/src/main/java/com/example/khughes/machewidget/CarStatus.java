package com.example.khughes.machewidget;

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

    public String getTrunk() {
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
            return getVehiclestatus().getTPMS().getLeftFrontTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightFrontTireStatus() {
        try {
            return getVehiclestatus().getTPMS().getRightFrontTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftRearTireStatus() {
        try {
            return getVehiclestatus().getTPMS().getOuterLeftRearTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightRearTireStatus() {
        try {
            return getVehiclestatus().getTPMS().getOuterRightRearTireStatus().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftFrontTirePressure() {
        try {
            return getVehiclestatus().getTPMS().getLeftFrontTirePressure().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightFrontTirePressure() {
        try {
            return getVehiclestatus().getTPMS().getRightFrontTirePressure().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getLeftRearTirePressure() {
        try {
            return getVehiclestatus().getTPMS().getOuterLeftRearTirePressure().getValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getRightRearTirePressure() {
        try {
            return getVehiclestatus().getTPMS().getOuterRightRearTirePressure().getValue();
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

    @Generated("jsonschema2pojo")
    public class Alarm {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class BattTracLoSocDDsply {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class Battery {

        @SerializedName("batteryHealth")
        @Expose
        private BatteryHealth batteryHealth;
        @SerializedName("batteryStatusActual")
        @Expose
        private BatteryStatusActual batteryStatusActual;

        public BatteryHealth getBatteryHealth() {
            return batteryHealth;
        }

        public void setBatteryHealth(BatteryHealth batteryHealth) {
            this.batteryHealth = batteryHealth;
        }

        public BatteryStatusActual getBatteryStatusActual() {
            return batteryStatusActual;
        }

        public void setBatteryStatusActual(BatteryStatusActual batteryStatusActual) {
            this.batteryStatusActual = batteryStatusActual;
        }

    }

    @Generated("jsonschema2pojo")
    public class BatteryChargeStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class BatteryFillLevel {

        @SerializedName("value")
        @Expose
        private Double value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class BatteryHealth {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class BatteryPerfStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class BatteryStatusActual {

        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class BatteryTracLowChargeThreshold {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class CcsSettings {

        @SerializedName("timestamp")
        @Expose
        private String timestamp;
        @SerializedName("location")
        @Expose
        private Integer location;
        @SerializedName("vehicleConnectivity")
        @Expose
        private Integer vehicleConnectivity;
        @SerializedName("vehicleData")
        @Expose
        private Integer vehicleData;
        @SerializedName("drivingCharacteristics")
        @Expose
        private Integer drivingCharacteristics;
        @SerializedName("contacts")
        @Expose
        private Integer contacts;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public Integer getLocation() {
            return location;
        }

        public void setLocation(Integer location) {
            this.location = location;
        }

        public Integer getVehicleConnectivity() {
            return vehicleConnectivity;
        }

        public void setVehicleConnectivity(Integer vehicleConnectivity) {
            this.vehicleConnectivity = vehicleConnectivity;
        }

        public Integer getVehicleData() {
            return vehicleData;
        }

        public void setVehicleData(Integer vehicleData) {
            this.vehicleData = vehicleData;
        }

        public Integer getDrivingCharacteristics() {
            return drivingCharacteristics;
        }

        public void setDrivingCharacteristics(Integer drivingCharacteristics) {
            this.drivingCharacteristics = drivingCharacteristics;
        }

        public Integer getContacts() {
            return contacts;
        }

        public void setContacts(Integer contacts) {
            this.contacts = contacts;
        }

    }

    @Generated("jsonschema2pojo")
    public class ChargeEndTime {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class ChargeStartTime {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class ChargingStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class DcFastChargeData {

        @SerializedName("fstChrgBulkTEst")
        @Expose
        private FstChrgBulkTEst fstChrgBulkTEst;
        @SerializedName("fstChrgCmpltTEst")
        @Expose
        private FstChrgCmpltTEst fstChrgCmpltTEst;

        public FstChrgBulkTEst getFstChrgBulkTEst() {
            return fstChrgBulkTEst;
        }

        public void setFstChrgBulkTEst(FstChrgBulkTEst fstChrgBulkTEst) {
            this.fstChrgBulkTEst = fstChrgBulkTEst;
        }

        public FstChrgCmpltTEst getFstChrgCmpltTEst() {
            return fstChrgCmpltTEst;
        }

        public void setFstChrgCmpltTEst(FstChrgCmpltTEst fstChrgCmpltTEst) {
            this.fstChrgCmpltTEst = fstChrgCmpltTEst;
        }

    }

    @Generated("jsonschema2pojo")
    public class DeepSleepInProgress {

        @SerializedName("value")
        @Expose
        private Boolean value;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class DoorStatus {

        @SerializedName("rightRearDoor")
        @Expose
        private RightRearDoor rightRearDoor;
        @SerializedName("leftRearDoor")
        @Expose
        private LeftRearDoor leftRearDoor;
        @SerializedName("driverDoor")
        @Expose
        private DriverDoor driverDoor;
        @SerializedName("passengerDoor")
        @Expose
        private PassengerDoor passengerDoor;
        @SerializedName("hoodDoor")
        @Expose
        private HoodDoor hoodDoor;
        @SerializedName("tailgateDoor")
        @Expose
        private TailgateDoor tailgateDoor;
        @SerializedName("innerTailgateDoor")
        @Expose
        private InnerTailgateDoor innerTailgateDoor;

        public RightRearDoor getRightRearDoor() {
            return rightRearDoor;
        }

        public void setRightRearDoor(RightRearDoor rightRearDoor) {
            this.rightRearDoor = rightRearDoor;
        }

        public LeftRearDoor getLeftRearDoor() {
            return leftRearDoor;
        }

        public void setLeftRearDoor(LeftRearDoor leftRearDoor) {
            this.leftRearDoor = leftRearDoor;
        }

        public DriverDoor getDriverDoor() {
            return driverDoor;
        }

        public void setDriverDoor(DriverDoor driverDoor) {
            this.driverDoor = driverDoor;
        }

        public PassengerDoor getPassengerDoor() {
            return passengerDoor;
        }

        public void setPassengerDoor(PassengerDoor passengerDoor) {
            this.passengerDoor = passengerDoor;
        }

        public HoodDoor getHoodDoor() {
            return hoodDoor;
        }

        public void setHoodDoor(HoodDoor hoodDoor) {
            this.hoodDoor = hoodDoor;
        }

        public TailgateDoor getTailgateDoor() {
            return tailgateDoor;
        }

        public void setTailgateDoor(TailgateDoor tailgateDoor) {
            this.tailgateDoor = tailgateDoor;
        }

        public InnerTailgateDoor getInnerTailgateDoor() {
            return innerTailgateDoor;
        }

        public void setInnerTailgateDoor(InnerTailgateDoor innerTailgateDoor) {
            this.innerTailgateDoor = innerTailgateDoor;
        }

    }

    @Generated("jsonschema2pojo")
    public class DriverDoor {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class DriverWindowPosition {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class DualRearWheel {

        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class ElVehDTE {

        @SerializedName("value")
        @Expose
        private Double value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class FirmwareUpgInProgress {

        @SerializedName("value")
        @Expose
        private Boolean value;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class FstChrgBulkTEst {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class FstChrgCmpltTEst {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class Gps {

        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        @SerializedName("gpsState")
        @Expose
        private String gpsState;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getGpsState() {
            return gpsState;
        }

        public void setGpsState(String gpsState) {
            this.gpsState = gpsState;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class HoodDoor {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class HybridModeStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class IgnitionStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class InnerLeftRearTirePressure {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class InnerLeftRearTireStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class InnerRightRearTirePressure {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class InnerRightRearTireStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class InnerTailgateDoor {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class LeftFrontTirePressure {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class LeftFrontTireStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class LeftRearDoor {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class LockStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class Odometer {

        @SerializedName("value")
        @Expose
        private Double value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class Oil {

        @SerializedName("oilLife")
        @Expose
        private String oilLife;
        @SerializedName("oilLifeActual")
        @Expose
        private Integer oilLifeActual;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getOilLife() {
            return oilLife;
        }

        public void setOilLife(String oilLife) {
            this.oilLife = oilLife;
        }

        public Integer getOilLifeActual() {
            return oilLifeActual;
        }

        public void setOilLifeActual(Integer oilLifeActual) {
            this.oilLifeActual = oilLifeActual;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class OutandAbout {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class OuterLeftRearTirePressure {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class OuterLeftRearTireStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class OuterRightRearTirePressure {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class OuterRightRearTireStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class PassWindowPosition {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class PassengerDoor {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class PlugStatus {

        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class PreCondStatusDsply {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class PrmtAlarmEvent {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RearDriverWindowPos {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RearPassWindowPos {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RecommendedFrontTirePressure {

        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RecommendedRearTirePressure {

        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RemoteStart {

        @SerializedName("remoteStartDuration")
        @Expose
        private Integer remoteStartDuration;
        @SerializedName("remoteStartTime")
        @Expose
        private Integer remoteStartTime;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getRemoteStartDuration() {
            return remoteStartDuration;
        }

        public void setRemoteStartDuration(Integer remoteStartDuration) {
            this.remoteStartDuration = remoteStartDuration;
        }

        public Integer getRemoteStartTime() {
            return remoteStartTime;
        }

        public void setRemoteStartTime(Integer remoteStartTime) {
            this.remoteStartTime = remoteStartTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RemoteStartStatus {

        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RightFrontTirePressure {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RightFrontTireStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class RightRearDoor {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class TailgateDoor {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class TirePressure {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class TirePressureByLocation {

        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class TirePressureSystemStatus {

        @SerializedName("value")
        @Expose
        private String value;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("timestamp")
        @Expose
        private String timestamp;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

    }

    @Generated("jsonschema2pojo")
    public class TPMS {

        @SerializedName("tirePressureByLocation")
        @Expose
        private TirePressureByLocation tirePressureByLocation;
        @SerializedName("tirePressureSystemStatus")
        @Expose
        private TirePressureSystemStatus tirePressureSystemStatus;
        @SerializedName("dualRearWheel")
        @Expose
        private DualRearWheel dualRearWheel;
        @SerializedName("leftFrontTireStatus")
        @Expose
        private LeftFrontTireStatus leftFrontTireStatus;
        @SerializedName("leftFrontTirePressure")
        @Expose
        private LeftFrontTirePressure leftFrontTirePressure;
        @SerializedName("rightFrontTireStatus")
        @Expose
        private RightFrontTireStatus rightFrontTireStatus;
        @SerializedName("rightFrontTirePressure")
        @Expose
        private RightFrontTirePressure rightFrontTirePressure;
        @SerializedName("outerLeftRearTireStatus")
        @Expose
        private OuterLeftRearTireStatus outerLeftRearTireStatus;
        @SerializedName("outerLeftRearTirePressure")
        @Expose
        private OuterLeftRearTirePressure outerLeftRearTirePressure;
        @SerializedName("outerRightRearTireStatus")
        @Expose
        private OuterRightRearTireStatus outerRightRearTireStatus;
        @SerializedName("outerRightRearTirePressure")
        @Expose
        private OuterRightRearTirePressure outerRightRearTirePressure;
        @SerializedName("innerLeftRearTireStatus")
        @Expose
        private InnerLeftRearTireStatus innerLeftRearTireStatus;
        @SerializedName("innerLeftRearTirePressure")
        @Expose
        private InnerLeftRearTirePressure innerLeftRearTirePressure;
        @SerializedName("innerRightRearTireStatus")
        @Expose
        private InnerRightRearTireStatus innerRightRearTireStatus;
        @SerializedName("innerRightRearTirePressure")
        @Expose
        private InnerRightRearTirePressure innerRightRearTirePressure;
        @SerializedName("recommendedFrontTirePressure")
        @Expose
        private RecommendedFrontTirePressure recommendedFrontTirePressure;
        @SerializedName("recommendedRearTirePressure")
        @Expose
        private RecommendedRearTirePressure recommendedRearTirePressure;

        public TirePressureByLocation getTirePressureByLocation() {
            return tirePressureByLocation;
        }

        public void setTirePressureByLocation(TirePressureByLocation tirePressureByLocation) {
            this.tirePressureByLocation = tirePressureByLocation;
        }

        public TirePressureSystemStatus getTirePressureSystemStatus() {
            return tirePressureSystemStatus;
        }

        public void setTirePressureSystemStatus(TirePressureSystemStatus tirePressureSystemStatus) {
            this.tirePressureSystemStatus = tirePressureSystemStatus;
        }

        public DualRearWheel getDualRearWheel() {
            return dualRearWheel;
        }

        public void setDualRearWheel(DualRearWheel dualRearWheel) {
            this.dualRearWheel = dualRearWheel;
        }

        public LeftFrontTireStatus getLeftFrontTireStatus() {
            return leftFrontTireStatus;
        }

        public void setLeftFrontTireStatus(LeftFrontTireStatus leftFrontTireStatus) {
            this.leftFrontTireStatus = leftFrontTireStatus;
        }

        public LeftFrontTirePressure getLeftFrontTirePressure() {
            return leftFrontTirePressure;
        }

        public void setLeftFrontTirePressure(LeftFrontTirePressure leftFrontTirePressure) {
            this.leftFrontTirePressure = leftFrontTirePressure;
        }

        public RightFrontTireStatus getRightFrontTireStatus() {
            return rightFrontTireStatus;
        }

        public void setRightFrontTireStatus(RightFrontTireStatus rightFrontTireStatus) {
            this.rightFrontTireStatus = rightFrontTireStatus;
        }

        public RightFrontTirePressure getRightFrontTirePressure() {
            return rightFrontTirePressure;
        }

        public void setRightFrontTirePressure(RightFrontTirePressure rightFrontTirePressure) {
            this.rightFrontTirePressure = rightFrontTirePressure;
        }

        public OuterLeftRearTireStatus getOuterLeftRearTireStatus() {
            return outerLeftRearTireStatus;
        }

        public void setOuterLeftRearTireStatus(OuterLeftRearTireStatus outerLeftRearTireStatus) {
            this.outerLeftRearTireStatus = outerLeftRearTireStatus;
        }

        public OuterLeftRearTirePressure getOuterLeftRearTirePressure() {
            return outerLeftRearTirePressure;
        }

        public void setOuterLeftRearTirePressure(OuterLeftRearTirePressure outerLeftRearTirePressure) {
            this.outerLeftRearTirePressure = outerLeftRearTirePressure;
        }

        public OuterRightRearTireStatus getOuterRightRearTireStatus() {
            return outerRightRearTireStatus;
        }

        public void setOuterRightRearTireStatus(OuterRightRearTireStatus outerRightRearTireStatus) {
            this.outerRightRearTireStatus = outerRightRearTireStatus;
        }

        public OuterRightRearTirePressure getOuterRightRearTirePressure() {
            return outerRightRearTirePressure;
        }

        public void setOuterRightRearTirePressure(OuterRightRearTirePressure outerRightRearTirePressure) {
            this.outerRightRearTirePressure = outerRightRearTirePressure;
        }

        public InnerLeftRearTireStatus getInnerLeftRearTireStatus() {
            return innerLeftRearTireStatus;
        }

        public void setInnerLeftRearTireStatus(InnerLeftRearTireStatus innerLeftRearTireStatus) {
            this.innerLeftRearTireStatus = innerLeftRearTireStatus;
        }

        public InnerLeftRearTirePressure getInnerLeftRearTirePressure() {
            return innerLeftRearTirePressure;
        }

        public void setInnerLeftRearTirePressure(InnerLeftRearTirePressure innerLeftRearTirePressure) {
            this.innerLeftRearTirePressure = innerLeftRearTirePressure;
        }

        public InnerRightRearTireStatus getInnerRightRearTireStatus() {
            return innerRightRearTireStatus;
        }

        public void setInnerRightRearTireStatus(InnerRightRearTireStatus innerRightRearTireStatus) {
            this.innerRightRearTireStatus = innerRightRearTireStatus;
        }

        public InnerRightRearTirePressure getInnerRightRearTirePressure() {
            return innerRightRearTirePressure;
        }

        public void setInnerRightRearTirePressure(InnerRightRearTirePressure innerRightRearTirePressure) {
            this.innerRightRearTirePressure = innerRightRearTirePressure;
        }

        public RecommendedFrontTirePressure getRecommendedFrontTirePressure() {
            return recommendedFrontTirePressure;
        }

        public void setRecommendedFrontTirePressure(RecommendedFrontTirePressure recommendedFrontTirePressure) {
            this.recommendedFrontTirePressure = recommendedFrontTirePressure;
        }

        public RecommendedRearTirePressure getRecommendedRearTirePressure() {
            return recommendedRearTirePressure;
        }

        public void setRecommendedRearTirePressure(RecommendedRearTirePressure recommendedRearTirePressure) {
            this.recommendedRearTirePressure = recommendedRearTirePressure;
        }

    }

    @Generated("jsonschema2pojo")
    public class Vehiclestatus {

        @SerializedName("vin")
        @Expose
        private String vin;
        @SerializedName("lockStatus")
        @Expose
        private LockStatus lockStatus;
        @SerializedName("alarm")
        @Expose
        private Alarm alarm;
        @SerializedName("PrmtAlarmEvent")
        @Expose
        private PrmtAlarmEvent prmtAlarmEvent;
        @SerializedName("odometer")
        @Expose
        private Odometer odometer;
        @SerializedName("fuel")
        @Expose
        private Object fuel;
        @SerializedName("gps")
        @Expose
        private Gps gps;
        @SerializedName("remoteStart")
        @Expose
        private RemoteStart remoteStart;
        @SerializedName("remoteStartStatus")
        @Expose
        private RemoteStartStatus remoteStartStatus;
        @SerializedName("battery")
        @Expose
        private Battery battery;
        @SerializedName("oil")
        @Expose
        private Oil oil;
        @SerializedName("tirePressure")
        @Expose
        private TirePressure tirePressure;
        @SerializedName("authorization")
        @Expose
        private String authorization;
        @SerializedName("TPMS")
        @Expose
        private TPMS tpms;
        @SerializedName("firmwareUpgInProgress")
        @Expose
        private FirmwareUpgInProgress firmwareUpgInProgress;
        @SerializedName("deepSleepInProgress")
        @Expose
        private DeepSleepInProgress deepSleepInProgress;
        @SerializedName("ccsSettings")
        @Expose
        private CcsSettings ccsSettings;
        @SerializedName("lastRefresh")
        @Expose
        private String lastRefresh;
        @SerializedName("lastModifiedDate")
        @Expose
        private String lastModifiedDate;
        @SerializedName("serverTime")
        @Expose
        private String serverTime;
        @SerializedName("batteryFillLevel")
        @Expose
        private BatteryFillLevel batteryFillLevel;
        @SerializedName("elVehDTE")
        @Expose
        private ElVehDTE elVehDTE;
        @SerializedName("hybridModeStatus")
        @Expose
        private HybridModeStatus hybridModeStatus;
        @SerializedName("chargingStatus")
        @Expose
        private ChargingStatus chargingStatus;
        @SerializedName("plugStatus")
        @Expose
        private PlugStatus plugStatus;
        @SerializedName("chargeStartTime")
        @Expose
        private ChargeStartTime chargeStartTime;
        @SerializedName("chargeEndTime")
        @Expose
        private ChargeEndTime chargeEndTime;
        @SerializedName("preCondStatusDsply")
        @Expose
        private PreCondStatusDsply preCondStatusDsply;
        @SerializedName("chargerPowertype")
        @Expose
        private Object chargerPowertype;
        @SerializedName("batteryPerfStatus")
        @Expose
        private BatteryPerfStatus batteryPerfStatus;
        @SerializedName("outandAbout")
        @Expose
        private OutandAbout outandAbout;
        @SerializedName("batteryChargeStatus")
        @Expose
        private BatteryChargeStatus batteryChargeStatus;
        @SerializedName("dcFastChargeData")
        @Expose
        private DcFastChargeData dcFastChargeData;
        @SerializedName("windowPosition")
        @Expose
        private WindowPosition windowPosition;
        @SerializedName("doorStatus")
        @Expose
        private DoorStatus doorStatus;
        @SerializedName("ignitionStatus")
        @Expose
        private IgnitionStatus ignitionStatus;
        @SerializedName("batteryTracLowChargeThreshold")
        @Expose
        private BatteryTracLowChargeThreshold batteryTracLowChargeThreshold;
        @SerializedName("battTracLoSocDDsply")
        @Expose
        private BattTracLoSocDDsply battTracLoSocDDsply;
        @SerializedName("dieselSystemStatus")
        @Expose
        private Object dieselSystemStatus;

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public LockStatus getLockStatus() {
            return lockStatus;
        }

        public void setLockStatus(LockStatus lockStatus) {
            this.lockStatus = lockStatus;
        }

        public Alarm getAlarm() {
            return alarm;
        }

        public void setAlarm(Alarm alarm) {
            this.alarm = alarm;
        }

        public PrmtAlarmEvent getPrmtAlarmEvent() {
            return prmtAlarmEvent;
        }

        public void setPrmtAlarmEvent(PrmtAlarmEvent prmtAlarmEvent) {
            this.prmtAlarmEvent = prmtAlarmEvent;
        }

        public Odometer getOdometer() {
            return odometer;
        }

        public void setOdometer(Odometer odometer) {
            this.odometer = odometer;
        }

        public Object getFuel() {
            return fuel;
        }

        public void setFuel(Object fuel) {
            this.fuel = fuel;
        }

        public Gps getGps() {
            return gps;
        }

        public void setGps(Gps gps) {
            this.gps = gps;
        }

        public RemoteStart getRemoteStart() {
            return remoteStart;
        }

        public void setRemoteStart(RemoteStart remoteStart) {
            this.remoteStart = remoteStart;
        }

        public RemoteStartStatus getRemoteStartStatus() {
            return remoteStartStatus;
        }

        public void setRemoteStartStatus(RemoteStartStatus remoteStartStatus) {
            this.remoteStartStatus = remoteStartStatus;
        }

        public Battery getBattery() {
            return battery;
        }

        public void setBattery(Battery battery) {
            this.battery = battery;
        }

        public Oil getOil() {
            return oil;
        }

        public void setOil(Oil oil) {
            this.oil = oil;
        }

        public TirePressure getTirePressure() {
            return tirePressure;
        }

        public void setTirePressure(TirePressure tirePressure) {
            this.tirePressure = tirePressure;
        }

        public String getAuthorization() {
            return authorization;
        }

        public void setAuthorization(String authorization) {
            this.authorization = authorization;
        }

        public TPMS getTPMS() {
            return tpms;
        }

        public void setTpms(TPMS tpms) {
            this.tpms = tpms;
        }

        public FirmwareUpgInProgress getFirmwareUpgInProgress() {
            return firmwareUpgInProgress;
        }

        public void setFirmwareUpgInProgress(FirmwareUpgInProgress firmwareUpgInProgress) {
            this.firmwareUpgInProgress = firmwareUpgInProgress;
        }

        public DeepSleepInProgress getDeepSleepInProgress() {
            return deepSleepInProgress;
        }

        public void setDeepSleepInProgress(DeepSleepInProgress deepSleepInProgress) {
            this.deepSleepInProgress = deepSleepInProgress;
        }

        public CcsSettings getCcsSettings() {
            return ccsSettings;
        }

        public void setCcsSettings(CcsSettings ccsSettings) {
            this.ccsSettings = ccsSettings;
        }

        public String getLastRefresh() {
            return lastRefresh;
        }

        public void setLastRefresh(String lastRefresh) {
            this.lastRefresh = lastRefresh;
        }

        public String getLastModifiedDate() {
            return lastModifiedDate;
        }

        public void setLastModifiedDate(String lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
        }

        public String getServerTime() {
            return serverTime;
        }

        public void setServerTime(String serverTime) {
            this.serverTime = serverTime;
        }

        public BatteryFillLevel getBatteryFillLevel() {
            return batteryFillLevel;
        }

        public void setBatteryFillLevel(BatteryFillLevel batteryFillLevel) {
            this.batteryFillLevel = batteryFillLevel;
        }

        public ElVehDTE getElVehDTE() {
            return elVehDTE;
        }

        public void setElVehDTE(ElVehDTE elVehDTE) {
            this.elVehDTE = elVehDTE;
        }

        public HybridModeStatus getHybridModeStatus() {
            return hybridModeStatus;
        }

        public void setHybridModeStatus(HybridModeStatus hybridModeStatus) {
            this.hybridModeStatus = hybridModeStatus;
        }

        public ChargingStatus getChargingStatus() {
            return chargingStatus;
        }

        public void setChargingStatus(ChargingStatus chargingStatus) {
            this.chargingStatus = chargingStatus;
        }

        public PlugStatus getPlugStatus() {
            return plugStatus;
        }

        public void setPlugStatus(PlugStatus plugStatus) {
            this.plugStatus = plugStatus;
        }

        public ChargeStartTime getChargeStartTime() {
            return chargeStartTime;
        }

        public void setChargeStartTime(ChargeStartTime chargeStartTime) {
            this.chargeStartTime = chargeStartTime;
        }

        public ChargeEndTime getChargeEndTime() {
            return chargeEndTime;
        }

        public void setChargeEndTime(ChargeEndTime chargeEndTime) {
            this.chargeEndTime = chargeEndTime;
        }

        public PreCondStatusDsply getPreCondStatusDsply() {
            return preCondStatusDsply;
        }

        public void setPreCondStatusDsply(PreCondStatusDsply preCondStatusDsply) {
            this.preCondStatusDsply = preCondStatusDsply;
        }

        public Object getChargerPowertype() {
            return chargerPowertype;
        }

        public void setChargerPowertype(Object chargerPowertype) {
            this.chargerPowertype = chargerPowertype;
        }

        public BatteryPerfStatus getBatteryPerfStatus() {
            return batteryPerfStatus;
        }

        public void setBatteryPerfStatus(BatteryPerfStatus batteryPerfStatus) {
            this.batteryPerfStatus = batteryPerfStatus;
        }

        public OutandAbout getOutandAbout() {
            return outandAbout;
        }

        public void setOutandAbout(OutandAbout outandAbout) {
            this.outandAbout = outandAbout;
        }

        public BatteryChargeStatus getBatteryChargeStatus() {
            return batteryChargeStatus;
        }

        public void setBatteryChargeStatus(BatteryChargeStatus batteryChargeStatus) {
            this.batteryChargeStatus = batteryChargeStatus;
        }

        public DcFastChargeData getDcFastChargeData() {
            return dcFastChargeData;
        }

        public void setDcFastChargeData(DcFastChargeData dcFastChargeData) {
            this.dcFastChargeData = dcFastChargeData;
        }

        public WindowPosition getWindowPosition() {
            return windowPosition;
        }

        public void setWindowPosition(WindowPosition windowPosition) {
            this.windowPosition = windowPosition;
        }

        public DoorStatus getDoorStatus() {
            return doorStatus;
        }

        public void setDoorStatus(DoorStatus doorStatus) {
            this.doorStatus = doorStatus;
        }

        public IgnitionStatus getIgnitionStatus() {
            return ignitionStatus;
        }

        public void setIgnitionStatus(IgnitionStatus ignitionStatus) {
            this.ignitionStatus = ignitionStatus;
        }

        public BatteryTracLowChargeThreshold getBatteryTracLowChargeThreshold() {
            return batteryTracLowChargeThreshold;
        }

        public void setBatteryTracLowChargeThreshold(BatteryTracLowChargeThreshold batteryTracLowChargeThreshold) {
            this.batteryTracLowChargeThreshold = batteryTracLowChargeThreshold;
        }

        public BattTracLoSocDDsply getBattTracLoSocDDsply() {
            return battTracLoSocDDsply;
        }

        public void setBattTracLoSocDDsply(BattTracLoSocDDsply battTracLoSocDDsply) {
            this.battTracLoSocDDsply = battTracLoSocDDsply;
        }

        public Object getDieselSystemStatus() {
            return dieselSystemStatus;
        }

        public void setDieselSystemStatus(Object dieselSystemStatus) {
            this.dieselSystemStatus = dieselSystemStatus;
        }

    }

    @Generated("jsonschema2pojo")
    public class WindowPosition {

        @SerializedName("driverWindowPosition")
        @Expose
        private DriverWindowPosition driverWindowPosition;
        @SerializedName("passWindowPosition")
        @Expose
        private PassWindowPosition passWindowPosition;
        @SerializedName("rearDriverWindowPos")
        @Expose
        private RearDriverWindowPos rearDriverWindowPos;
        @SerializedName("rearPassWindowPos")
        @Expose
        private RearPassWindowPos rearPassWindowPos;

        public DriverWindowPosition getDriverWindowPosition() {
            return driverWindowPosition;
        }

        public void setDriverWindowPosition(DriverWindowPosition driverWindowPosition) {
            this.driverWindowPosition = driverWindowPosition;
        }

        public PassWindowPosition getPassWindowPosition() {
            return passWindowPosition;
        }

        public void setPassWindowPosition(PassWindowPosition passWindowPosition) {
            this.passWindowPosition = passWindowPosition;
        }

        public RearDriverWindowPos getRearDriverWindowPos() {
            return rearDriverWindowPos;
        }

        public void setRearDriverWindowPos(RearDriverWindowPos rearDriverWindowPos) {
            this.rearDriverWindowPos = rearDriverWindowPos;
        }

        public RearPassWindowPos getRearPassWindowPos() {
            return rearPassWindowPos;
        }

        public void setRearPassWindowPos(RearPassWindowPos rearPassWindowPos) {
            this.rearPassWindowPos = rearPassWindowPos;
        }

    }

    @SerializedName("vehiclestatus")
    @Expose
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
