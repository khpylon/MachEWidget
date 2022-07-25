package com.example.khughes.machewidget.OTAStatus;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
@Entity(tableName = "ota_status")
public class TappsResponse {

    @SerializedName("vin")
    @Expose
    private String vin;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("vehicleInhibitStatus")
    @Expose
    private Object vehicleInhibitStatus;
    @SerializedName("lifeCycleModeStatus")
    @Expose
    private LifeCycleModeStatus lifeCycleModeStatus;
    @SerializedName("asuActivationSchedule")
    @Expose
    private AsuActivationSchedule asuActivationSchedule;
    @SerializedName("asuSettingsStatus")
    @Expose
    private Object asuSettingsStatus;
    @SerializedName("version")
    @Expose
    private String version;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getVehicleInhibitStatus() {
        return vehicleInhibitStatus;
    }

    public void setVehicleInhibitStatus(Object vehicleInhibitStatus) {
        this.vehicleInhibitStatus = vehicleInhibitStatus;
    }

    public LifeCycleModeStatus getLifeCycleModeStatus() {
        return lifeCycleModeStatus;
    }

    public void setLifeCycleModeStatus(LifeCycleModeStatus lifeCycleModeStatus) {
        this.lifeCycleModeStatus = lifeCycleModeStatus;
    }

    public AsuActivationSchedule getAsuActivationSchedule() {
        return asuActivationSchedule;
    }

    public void setAsuActivationSchedule(AsuActivationSchedule asuActivationSchedule) {
        this.asuActivationSchedule = asuActivationSchedule;
    }

    public Object getAsuSettingsStatus() {
        return asuSettingsStatus;
    }

    public void setAsuSettingsStatus(Object asuSettingsStatus) {
        this.asuSettingsStatus = asuSettingsStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Generated("jsonschema2pojo")
    class AsuActivationSchedule {

        @SerializedName("scheduleType")
        @Expose
        private String scheduleType;
        @SerializedName("dayOfWeekAndTime")
        @Expose
        private Object dayOfWeekAndTime;
        @SerializedName("activationScheduleDaysOfWeek")
        @Expose
        private List<Object> activationScheduleDaysOfWeek = null;
        @SerializedName("activationScheduleTimeOfDay")
        @Expose
        private Object activationScheduleTimeOfDay;
        @SerializedName("oemCorrelationId")
        @Expose
        private String oemCorrelationId;
        @SerializedName("vehicleDateTime")
        @Expose
        private String vehicleDateTime;
        @SerializedName("tappsDateTime")
        @Expose
        private String tappsDateTime;

        public String getScheduleType() {
            return scheduleType;
        }

        public void setScheduleType(String scheduleType) {
            this.scheduleType = scheduleType;
        }

        public Object getDayOfWeekAndTime() {
            return dayOfWeekAndTime;
        }

        public void setDayOfWeekAndTime(Object dayOfWeekAndTime) {
            this.dayOfWeekAndTime = dayOfWeekAndTime;
        }

        public List<Object> getActivationScheduleDaysOfWeek() {
            return activationScheduleDaysOfWeek;
        }

        public void setActivationScheduleDaysOfWeek(List<Object> activationScheduleDaysOfWeek) {
            this.activationScheduleDaysOfWeek = activationScheduleDaysOfWeek;
        }

        public Object getActivationScheduleTimeOfDay() {
            return activationScheduleTimeOfDay;
        }

        public void setActivationScheduleTimeOfDay(Object activationScheduleTimeOfDay) {
            this.activationScheduleTimeOfDay = activationScheduleTimeOfDay;
        }

        public String getOemCorrelationId() {
            return oemCorrelationId;
        }

        public void setOemCorrelationId(String oemCorrelationId) {
            this.oemCorrelationId = oemCorrelationId;
        }

        public String getVehicleDateTime() {
            return vehicleDateTime;
        }

        public void setVehicleDateTime(String vehicleDateTime) {
            this.vehicleDateTime = vehicleDateTime;
        }

        public String getTappsDateTime() {
            return tappsDateTime;
        }

        public void setTappsDateTime(String tappsDateTime) {
            this.tappsDateTime = tappsDateTime;
        }

    }

    @Generated("jsonschema2pojo")
     class LifeCycleModeStatus {

        @SerializedName("lifeCycleMode")
        @Expose
        private String lifeCycleMode;
        @SerializedName("oemCorrelationId")
        @Expose
        private String oemCorrelationId;
        @SerializedName("vehicleDateTime")
        @Expose
        private String vehicleDateTime;
        @SerializedName("tappsDateTime")
        @Expose
        private String tappsDateTime;

        public String getLifeCycleMode() {
            return lifeCycleMode;
        }

        public void setLifeCycleMode(String lifeCycleMode) {
            this.lifeCycleMode = lifeCycleMode;
        }

        public String getOemCorrelationId() {
            return oemCorrelationId;
        }

        public void setOemCorrelationId(String oemCorrelationId) {
            this.oemCorrelationId = oemCorrelationId;
        }

        public String getVehicleDateTime() {
            return vehicleDateTime;
        }

        public void setVehicleDateTime(String vehicleDateTime) {
            this.vehicleDateTime = vehicleDateTime;
        }

        public String getTappsDateTime() {
            return tappsDateTime;
        }

        public void setTappsDateTime(String tappsDateTime) {
            this.tappsDateTime = tappsDateTime;
        }
    }

}
