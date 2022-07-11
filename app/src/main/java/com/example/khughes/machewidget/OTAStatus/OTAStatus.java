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
public class OTAStatus {

    @PrimaryKey
    @ColumnInfo(name = "vin")
    @NonNull
    public String VIN;

    public void setVIN(String VIN) {this.VIN = VIN;}
    public String getVIN() {return VIN;}

    // simpler getters go here
    public String getOTADateTime() {
        try {
            FuseResponseList a = getFuseResponse().getFuseResponseList().get(0);
            return getFuseResponse().getFuseResponseList().get(0).getLatestStatus().getDateTimestamp();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getOTAAggregateStatus() {
        try {
            return getFuseResponse().getFuseResponseList().get(0).getLatestStatus().getAggregateStatus();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getDescription() {
        try {
            return getFuseResponse().getLanguageText().getText();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public List<FuseResponseList> getFuseResponseList() {
        try {
            return getFuseResponse().getFuseResponseList();
        } catch (NullPointerException e) {
            return null;
        }
    }

//    @Generated("jsonschema2pojo")
//    public class AsuActivationSchedule {
//
//        @SerializedName("scheduleType")
//        @Expose
//        private String scheduleType;
//        @SerializedName("dayOfWeekAndTime")
//        @Expose
//        private Object dayOfWeekAndTime;
//        @SerializedName("activationScheduleDaysOfWeek")
//        @Expose
//        private List<Object> activationScheduleDaysOfWeek = null;
//        @SerializedName("activationScheduleTimeOfDay")
//        @Expose
//        private Object activationScheduleTimeOfDay;
//        @SerializedName("oemCorrelationId")
//        @Expose
//        private String oemCorrelationId;
//        @SerializedName("vehicleDateTime")
//        @Expose
//        private String vehicleDateTime;
//        @SerializedName("tappsDateTime")
//        @Expose
//        private String tappsDateTime;
//
//        public String getScheduleType() {
//            return scheduleType;
//        }
//
//        public void setScheduleType(String scheduleType) {
//            this.scheduleType = scheduleType;
//        }
//
//        public Object getDayOfWeekAndTime() {
//            return dayOfWeekAndTime;
//        }
//
//        public void setDayOfWeekAndTime(Object dayOfWeekAndTime) {
//            this.dayOfWeekAndTime = dayOfWeekAndTime;
//        }
//
//        public List<Object> getActivationScheduleDaysOfWeek() {
//            return activationScheduleDaysOfWeek;
//        }
//
//        public void setActivationScheduleDaysOfWeek(List<Object> activationScheduleDaysOfWeek) {
//            this.activationScheduleDaysOfWeek = activationScheduleDaysOfWeek;
//        }
//
//        public Object getActivationScheduleTimeOfDay() {
//            return activationScheduleTimeOfDay;
//        }
//
//        public void setActivationScheduleTimeOfDay(Object activationScheduleTimeOfDay) {
//            this.activationScheduleTimeOfDay = activationScheduleTimeOfDay;
//        }
//
//        public String getOemCorrelationId() {
//            return oemCorrelationId;
//        }
//
//        public void setOemCorrelationId(String oemCorrelationId) {
//            this.oemCorrelationId = oemCorrelationId;
//        }
//
//        public String getVehicleDateTime() {
//            return vehicleDateTime;
//        }
//
//        public void setVehicleDateTime(String vehicleDateTime) {
//            this.vehicleDateTime = vehicleDateTime;
//        }
//
//        public String getTappsDateTime() {
//            return tappsDateTime;
//        }
//
//        public void setTappsDateTime(String tappsDateTime) {
//            this.tappsDateTime = tappsDateTime;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class CcsStatus {
//
//        @SerializedName("ccsConnectivity")
//        @Expose
//        private String ccsConnectivity;
//        @SerializedName("ccsVehicleData")
//        @Expose
//        private String ccsVehicleData;
//
//        public String getCcsConnectivity() {
//            return ccsConnectivity;
//        }
//
//        public void setCcsConnectivity(String ccsConnectivity) {
//            this.ccsConnectivity = ccsConnectivity;
//        }
//
//        public String getCcsVehicleData() {
//            return ccsVehicleData;
//        }
//
//        public void setCcsVehicleData(String ccsVehicleData) {
//            this.ccsVehicleData = ccsVehicleData;
//        }
//
//    }


//    @Generated("jsonschema2pojo")
//    public class LifeCycleModeStatus {
//
//        @SerializedName("lifeCycleMode")
//        @Expose
//        private String lifeCycleMode;
//        @SerializedName("oemCorrelationId")
//        @Expose
//        private String oemCorrelationId;
//        @SerializedName("vehicleDateTime")
//        @Expose
//        private String vehicleDateTime;
//        @SerializedName("tappsDateTime")
//        @Expose
//        private String tappsDateTime;
//
//        public String getLifeCycleMode() {
//            return lifeCycleMode;
//        }
//
//        public void setLifeCycleMode(String lifeCycleMode) {
//            this.lifeCycleMode = lifeCycleMode;
//        }
//
//        public String getOemCorrelationId() {
//            return oemCorrelationId;
//        }
//
//        public void setOemCorrelationId(String oemCorrelationId) {
//            this.oemCorrelationId = oemCorrelationId;
//        }
//
//        public String getVehicleDateTime() {
//            return vehicleDateTime;
//        }
//
//        public void setVehicleDateTime(String vehicleDateTime) {
//            this.vehicleDateTime = vehicleDateTime;
//        }
//
//        public String getTappsDateTime() {
//            return tappsDateTime;
//        }
//
//        public void setTappsDateTime(String tappsDateTime) {
//            this.tappsDateTime = tappsDateTime;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class PackageUpdateDetails {
//
//        @SerializedName("releaseNotesUrl")
//        @Expose
//        private String releaseNotesUrl;
//        @SerializedName("updateDisplayTime")
//        @Expose
//        private Integer updateDisplayTime;
//        @SerializedName("wifiRequired")
//        @Expose
//        private Boolean wifiRequired;
//        @SerializedName("packagePriority")
//        @Expose
//        private Integer packagePriority;
//        @SerializedName("failedOnResponse")
//        @Expose
//        private String failedOnResponse;
//        @SerializedName("cdnreleaseNotesUrl")
//        @Expose
//        private String cdnreleaseNotesUrl;
//
//        public String getReleaseNotesUrl() {
//            return releaseNotesUrl;
//        }
//
//        public void setReleaseNotesUrl(String releaseNotesUrl) {
//            this.releaseNotesUrl = releaseNotesUrl;
//        }
//
//        public Integer getUpdateDisplayTime() {
//            return updateDisplayTime;
//        }
//
//        public void setUpdateDisplayTime(Integer updateDisplayTime) {
//            this.updateDisplayTime = updateDisplayTime;
//        }
//
//        public Boolean getWifiRequired() {
//            return wifiRequired;
//        }
//
//        public void setWifiRequired(Boolean wifiRequired) {
//            this.wifiRequired = wifiRequired;
//        }
//
//        public Integer getPackagePriority() {
//            return packagePriority;
//        }
//
//        public void setPackagePriority(Integer packagePriority) {
//            this.packagePriority = packagePriority;
//        }
//
//        public String getFailedOnResponse() {
//            return failedOnResponse;
//        }
//
//        public void setFailedOnResponse(String failedOnResponse) {
//            this.failedOnResponse = failedOnResponse;
//        }
//
//        public String getCdnreleaseNotesUrl() {
//            return cdnreleaseNotesUrl;
//        }
//
//        public void setCdnreleaseNotesUrl(String cdnreleaseNotesUrl) {
//            this.cdnreleaseNotesUrl = cdnreleaseNotesUrl;
//        }
//
//    }

//    @Generated("jsonschema2pojo")
//    public class TappsResponse {
//
//        @SerializedName("vin")
//        @Expose
//        private String vin;
//        @SerializedName("status")
//        @Expose
//        private Integer status;
//        @SerializedName("vehicleInhibitStatus")
//        @Expose
//        private Object vehicleInhibitStatus;
//        @SerializedName("lifeCycleModeStatus")
//        @Expose
//        private LifeCycleModeStatus lifeCycleModeStatus;
//        @SerializedName("asuActivationSchedule")
//        @Expose
//        private AsuActivationSchedule asuActivationSchedule;
//        @SerializedName("asuSettingsStatus")
//        @Expose
//        private Object asuSettingsStatus;
//        @SerializedName("version")
//        @Expose
//        private String version;
//
//        public String getVin() {
//            return vin;
//        }
//
//        public void setVin(String vin) {
//            this.vin = vin;
//        }
//
//        public Integer getStatus() {
//            return status;
//        }
//
//        public void setStatus(Integer status) {
//            this.status = status;
//        }
//
//        public Object getVehicleInhibitStatus() {
//            return vehicleInhibitStatus;
//        }
//
//        public void setVehicleInhibitStatus(Object vehicleInhibitStatus) {
//            this.vehicleInhibitStatus = vehicleInhibitStatus;
//        }
//
//        public LifeCycleModeStatus getLifeCycleModeStatus() {
//            return lifeCycleModeStatus;
//        }
//
//        public void setLifeCycleModeStatus(LifeCycleModeStatus lifeCycleModeStatus) {
//            this.lifeCycleModeStatus = lifeCycleModeStatus;
//        }
//
//        public AsuActivationSchedule getAsuActivationSchedule() {
//            return asuActivationSchedule;
//        }
//
//        public void setAsuActivationSchedule(AsuActivationSchedule asuActivationSchedule) {
//            this.asuActivationSchedule = asuActivationSchedule;
//        }
//
//        public Object getAsuSettingsStatus() {
//            return asuSettingsStatus;
//        }
//
//        public void setAsuSettingsStatus(Object asuSettingsStatus) {
//            this.asuSettingsStatus = asuSettingsStatus;
//        }
//
//        public String getVersion() {
//            return version;
//        }
//
//        public void setVersion(String version) {
//            this.version = version;
//        }
//
//    }

//    @SerializedName("displayOTAStatusReport")
//    @Expose
//    private String displayOTAStatusReport;
//    @SerializedName("ccsStatus")
//    @Expose
//    private CcsStatus ccsStatus;
    @SerializedName("error")
    @Expose
    @Embedded
    private Object error;
    @SerializedName("fuseResponse")
    @Expose
    @Embedded
    private FuseResponse fuseResponse;
//    @SerializedName("tappsResponse")
//    @Expose
//    private TappsResponse tappsResponse;
    @SerializedName("updatePendingState")
    @Expose
    @Embedded
    private Object updatePendingState;
    @SerializedName("otaAlertStatus")
    @Expose
    private String otaAlertStatus;

//    public String getDisplayOTAStatusReport() {
//        return displayOTAStatusReport;
//    }
//
//    public void setDisplayOTAStatusReport(String displayOTAStatusReport) {
//        this.displayOTAStatusReport = displayOTAStatusReport;
//    }

//    public CcsStatus getCcsStatus() {
//        return ccsStatus;
//    }
//
//    public void setCcsStatus(CcsStatus ccsStatus) {
//        this.ccsStatus = ccsStatus;
//    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public FuseResponse getFuseResponse() {
        return fuseResponse;
    }

    public void setFuseResponse(FuseResponse fuseResponse) {
        this.fuseResponse = fuseResponse;
    }

//    public TappsResponse getTappsResponse() {
//        return tappsResponse;
//    }
//
//    public void setTappsResponse(TappsResponse tappsResponse) {
//        this.tappsResponse = tappsResponse;
//    }

    public Object getUpdatePendingState() {
        return updatePendingState;
    }

    public void setUpdatePendingState(Object updatePendingState) {
        this.updatePendingState = updatePendingState;
    }

    public String getOtaAlertStatus() {
        return otaAlertStatus;
    }

    public void setOtaAlertStatus(String otaAlertStatus) {
        this.otaAlertStatus = otaAlertStatus;
    }

}
