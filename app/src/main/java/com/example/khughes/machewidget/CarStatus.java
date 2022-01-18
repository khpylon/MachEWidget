package com.example.khughes.machewidget;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class CarStatus {

    // simpler getters go here
    public Double getOdometer() { return getVehiclestatus().getOdometer().getValue(); }

    public class Alarm {
        private String value;
        private String status;
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

    public class BattTracLoSocDDsply {
        private String value;
        private String status;
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

    public class Battery {
        private BatteryHealth batteryHealth;
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

    public class BatteryChargeStatus {
        private String value;
        private String status;
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

    public class BatteryFillLevel {
        private Double value;
        private String status;
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

    public class BatteryHealth {
        private String value;
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

    public class BatteryPerfStatus {
        private String value;
        private String status;
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

    public class BatteryStatusActual {
        private Integer value;
        private String status;
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

    public class BatteryTracLowChargeThreshold {
        private String value;
        private String status;
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

    public class CcsSettings {
        private String timestamp;
        private Integer location;
        private Integer vehicleConnectivity;
        private Integer vehicleData;
        private Integer drivingCharacteristics;
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

    public class ChargeEndTime {
        private String value;
        private String status;
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

    public class ChargeStartTime {
        private String value;
        private String status;
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

    public class ChargingStatus {
        private String value;
        private String status;
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

    public class DcFastChargeData {
        private FstChrgBulkTEst fstChrgBulkTEst;
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

    public class DeepSleepInProgress {
        private Boolean value;
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

    public class DoorStatus {
        private RightRearDoor rightRearDoor;
        private LeftRearDoor leftRearDoor;
        private DriverDoor driverDoor;
        private PassengerDoor passengerDoor;
        private HoodDoor hoodDoor;
        private TailgateDoor tailgateDoor;
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

    public class DriverDoor {
        private String value;
        private String status;
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

    public class DriverWindowPosition {
        private String value;
        private String status;
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

    public class DualRearWheel {
        private Integer value;
        private String status;
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

    public class ElVehDTE {
        private Double value;
        private String status;
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

    public class FirmwareUpgInProgress {
        private Boolean value;
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

    public class FstChrgBulkTEst {
        private String value;
        private String status;
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

    public class FstChrgCmpltTEst {
        private String value;
        private String status;
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

    public class Gps {
        private String latitude;
        private String longitude;
        private String gpsState;
        private String status;
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

    public class HoodDoor {
        private String value;
        private String status;
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

    public class HybridModeStatus {
        private String value;
        private String status;
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

    public class IgnitionStatus {
        private String value;
        private String status;
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

    public class InnerLeftRearTirePressure {
        private String value;
        private String status;
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

    public class InnerLeftRearTireStatus {
        private String value;
        private String status;
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

    public class InnerRightRearTirePressure {
        private String value;
        private String status;
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

    public class InnerRightRearTireStatus {
        private String value;
        private String status;
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

    public class InnerTailgateDoor {
        private String value;
        private String status;
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

    public class LeftFrontTirePressure {
        private String value;
        private String status;
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

    public class LeftFrontTireStatus {
        private String value;
        private String status;
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

    public class LeftRearDoor {
        private String value;
        private String status;
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

    public class LockStatus {
        private String value;
        private String status;
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

    public class Odometer {
        private Double value;
        private String status;
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

    public class Oil {
        private String oilLife;
        private Integer oilLifeActual;
        private String status;
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

    public class OutandAbout {
        private String value;
        private String status;
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

    public class OuterLeftRearTirePressure {
        private String value;
        private String status;
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

    public class OuterLeftRearTireStatus {
        private String value;
        private String status;
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

    public class OuterRightRearTirePressure {
        private String value;
        private String status;
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

    public class OuterRightRearTireStatus {
        private String value;
        private String status;
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

    public class PassWindowPosition {
        private String value;
        private String status;
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

    public class PassengerDoor {
        private String value;
        private String status;
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

    public class PlugStatus {
        private Integer value;
        private String status;
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

    public class PreCondStatusDsply {
        private String value;
        private String status;
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

    public class PrmtAlarmEvent {
        private String value;
        private String status;
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

    public class RearDriverWindowPos {
        private String value;
        private String status;
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

    public class RearPassWindowPos {
        private String value;
        private String status;
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

    public class RecommendedFrontTirePressure {
        private Integer value;
        private String status;
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

    public class RecommendedRearTirePressure {
        private Integer value;
        private String status;
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

    public class RemoteStart {
        private Integer remoteStartDuration;
        private Integer remoteStartTime;
        private String status;
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

    public class RemoteStartStatus {
        private Integer value;
        private String status;
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

    public class RightFrontTirePressure {
        private String value;
        private String status;
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

    public class RightFrontTireStatus {
        private String value;
        private String status;
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

    public class RightRearDoor {
        private String value;
        private String status;
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

        private Vehiclestatus vehiclestatus;
        private String version;
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

    public class TPMS {
        private TirePressureByLocation tirePressureByLocation;
        private TirePressureSystemStatus tirePressureSystemStatus;
        private DualRearWheel dualRearWheel;
        private LeftFrontTireStatus leftFrontTireStatus;
        private LeftFrontTirePressure leftFrontTirePressure;
        private RightFrontTireStatus rightFrontTireStatus;
        private RightFrontTirePressure rightFrontTirePressure;
        private OuterLeftRearTireStatus outerLeftRearTireStatus;
        private OuterLeftRearTirePressure outerLeftRearTirePressure;
        private OuterRightRearTireStatus outerRightRearTireStatus;
        private OuterRightRearTirePressure outerRightRearTirePressure;
        private InnerLeftRearTireStatus innerLeftRearTireStatus;
        private InnerLeftRearTirePressure innerLeftRearTirePressure;
        private InnerRightRearTireStatus innerRightRearTireStatus;
        private InnerRightRearTirePressure innerRightRearTirePressure;
        private RecommendedFrontTirePressure recommendedFrontTirePressure;
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

    public class TailgateDoor {
        private String value;
        private String status;
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

    public class TirePressure {
        private String value;
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

    public class TirePressureByLocation {
        private Integer value;
        private String status;
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

    public class TirePressureSystemStatus {
        private String value;
        private String status;
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

    public class Vehiclestatus {
        private String vin;
        private LockStatus lockStatus;
        private Alarm alarm;
        private PrmtAlarmEvent prmtAlarmEvent;
        private Odometer odometer;
        private Object fuel;
        private Gps gps;
        private RemoteStart remoteStart;
        private RemoteStartStatus remoteStartStatus;
        private Battery battery;
        private Oil oil;
        private TirePressure tirePressure;
        private String authorization;
        private TPMS tPMS;
        private FirmwareUpgInProgress firmwareUpgInProgress;
        private DeepSleepInProgress deepSleepInProgress;
        private CcsSettings ccsSettings;
        private String lastRefresh;
        private String lastModifiedDate;
        private String serverTime;
        private BatteryFillLevel batteryFillLevel;
        private ElVehDTE elVehDTE;
        private HybridModeStatus hybridModeStatus;
        private ChargingStatus chargingStatus;
        private PlugStatus plugStatus;
        private ChargeStartTime chargeStartTime;
        private ChargeEndTime chargeEndTime;
        private PreCondStatusDsply preCondStatusDsply;
        private Object chargerPowertype;
        private BatteryPerfStatus batteryPerfStatus;
        private OutandAbout outandAbout;
        private BatteryChargeStatus batteryChargeStatus;
        private DcFastChargeData dcFastChargeData;
        private WindowPosition windowPosition;
        private DoorStatus doorStatus;
        private IgnitionStatus ignitionStatus;
        private BatteryTracLowChargeThreshold batteryTracLowChargeThreshold;
        private BattTracLoSocDDsply battTracLoSocDDsply;
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
        private Odometer getOdometer() {
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
            return tPMS;
        }
        public void setTPMS(TPMS tPMS) {
            this.tPMS = tPMS;
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

    public class WindowPosition {
        private DriverWindowPosition driverWindowPosition;
        private PassWindowPosition passWindowPosition;
        private RearDriverWindowPos rearDriverWindowPos;
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
}


