package com.example.khughes.machewidget;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class OTAStatus {

    // simpler getters go here
    public String getOTADateTime() {
        try {
            return getFuseResponse().getFuseResponseList().get(0).getLatestStatus().getDateTimestamp();
        } catch (NullPointerException e) {
            return null;
        }
    }
        
    @Generated("jsonschema2pojo")
    public class AsuActivationSchedule {

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
    public class CcsStatus {

        @SerializedName("ccsConnectivity")
        @Expose
        private String ccsConnectivity;
        @SerializedName("ccsVehicleData")
        @Expose
        private String ccsVehicleData;

        public String getCcsConnectivity() {
            return ccsConnectivity;
        }

        public void setCcsConnectivity(String ccsConnectivity) {
            this.ccsConnectivity = ccsConnectivity;
        }

        public String getCcsVehicleData() {
            return ccsVehicleData;
        }

        public void setCcsVehicleData(String ccsVehicleData) {
            this.ccsVehicleData = ccsVehicleData;
        }

    }

    @Generated("jsonschema2pojo")
    public class FuseResponse {

        @SerializedName("fuseResponseList")
        @Expose
        private List<FuseResponse__1> fuseResponseList = null;
        @SerializedName("languageText")
        @Expose
        private LanguageText languageText;

        public List<FuseResponse__1> getFuseResponseList() {
            return fuseResponseList;
        }

        public void setFuseResponseList(List<FuseResponse__1> fuseResponseList) {
            this.fuseResponseList = fuseResponseList;
        }

        public LanguageText getLanguageText() {
            return languageText;
        }

        public void setLanguageText(LanguageText languageText) {
            this.languageText = languageText;
        }

    }

    @Generated("jsonschema2pojo")
    public class FuseResponse__1 {

        @SerializedName("vin")
        @Expose
        private String vin;
        @SerializedName("oemCorrelationId")
        @Expose
        private String oemCorrelationId;
        @SerializedName("deploymentId")
        @Expose
        private String deploymentId;
        @SerializedName("deploymentCreationDate")
        @Expose
        private String deploymentCreationDate;
        @SerializedName("deploymentExpirationTime")
        @Expose
        private String deploymentExpirationTime;
        @SerializedName("otaTriggerExpirationTime")
        @Expose
        private String otaTriggerExpirationTime;
        @SerializedName("communicationPriority")
        @Expose
        private String communicationPriority;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("triggerType")
        @Expose
        private String triggerType;
        @SerializedName("inhibitRequired")
        @Expose
        private Boolean inhibitRequired;
        @SerializedName("additionalConsentLevel")
        @Expose
        private Integer additionalConsentLevel;
        @SerializedName("tmcEnvironment")
        @Expose
        private String tmcEnvironment;
        @SerializedName("latestStatus")
        @Expose
        private LatestStatus latestStatus;
        @SerializedName("packageUpdateDetails")
        @Expose
        private PackageUpdateDetails packageUpdateDetails;
        @SerializedName("deploymentFinalConsumerAction")
        @Expose
        private String deploymentFinalConsumerAction;

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public String getOemCorrelationId() {
            return oemCorrelationId;
        }

        public void setOemCorrelationId(String oemCorrelationId) {
            this.oemCorrelationId = oemCorrelationId;
        }

        public String getDeploymentId() {
            return deploymentId;
        }

        public void setDeploymentId(String deploymentId) {
            this.deploymentId = deploymentId;
        }

        public String getDeploymentCreationDate() {
            return deploymentCreationDate;
        }

        public void setDeploymentCreationDate(String deploymentCreationDate) {
            this.deploymentCreationDate = deploymentCreationDate;
        }

        public String getDeploymentExpirationTime() {
            return deploymentExpirationTime;
        }

        public void setDeploymentExpirationTime(String deploymentExpirationTime) {
            this.deploymentExpirationTime = deploymentExpirationTime;
        }

        public String getOtaTriggerExpirationTime() {
            return otaTriggerExpirationTime;
        }

        public void setOtaTriggerExpirationTime(String otaTriggerExpirationTime) {
            this.otaTriggerExpirationTime = otaTriggerExpirationTime;
        }

        public String getCommunicationPriority() {
            return communicationPriority;
        }

        public void setCommunicationPriority(String communicationPriority) {
            this.communicationPriority = communicationPriority;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTriggerType() {
            return triggerType;
        }

        public void setTriggerType(String triggerType) {
            this.triggerType = triggerType;
        }

        public Boolean getInhibitRequired() {
            return inhibitRequired;
        }

        public void setInhibitRequired(Boolean inhibitRequired) {
            this.inhibitRequired = inhibitRequired;
        }

        public Integer getAdditionalConsentLevel() {
            return additionalConsentLevel;
        }

        public void setAdditionalConsentLevel(Integer additionalConsentLevel) {
            this.additionalConsentLevel = additionalConsentLevel;
        }

        public String getTmcEnvironment() {
            return tmcEnvironment;
        }

        public void setTmcEnvironment(String tmcEnvironment) {
            this.tmcEnvironment = tmcEnvironment;
        }

        public LatestStatus getLatestStatus() {
            return latestStatus;
        }

        public void setLatestStatus(LatestStatus latestStatus) {
            this.latestStatus = latestStatus;
        }

        public PackageUpdateDetails getPackageUpdateDetails() {
            return packageUpdateDetails;
        }

        public void setPackageUpdateDetails(PackageUpdateDetails packageUpdateDetails) {
            this.packageUpdateDetails = packageUpdateDetails;
        }

        public String getDeploymentFinalConsumerAction() {
            return deploymentFinalConsumerAction;
        }

        public void setDeploymentFinalConsumerAction(String deploymentFinalConsumerAction) {
            this.deploymentFinalConsumerAction = deploymentFinalConsumerAction;
        }

    }

    @Generated("jsonschema2pojo")
    public class LanguageText {

        @SerializedName("Language")
        @Expose
        private String language;
        @SerializedName("LanguageCode")
        @Expose
        private String languageCode;
        @SerializedName("LanguageCodeMobileApp")
        @Expose
        private String languageCodeMobileApp;
        @SerializedName("Text")
        @Expose
        private String text;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }

        public String getLanguageCodeMobileApp() {
            return languageCodeMobileApp;
        }

        public void setLanguageCodeMobileApp(String languageCodeMobileApp) {
            this.languageCodeMobileApp = languageCodeMobileApp;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

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

    @Generated("jsonschema2pojo")
    public class LifeCycleModeStatus {

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

    @Generated("jsonschema2pojo")
    public class PackageUpdateDetails {

        @SerializedName("releaseNotesUrl")
        @Expose
        private String releaseNotesUrl;
        @SerializedName("updateDisplayTime")
        @Expose
        private Integer updateDisplayTime;
        @SerializedName("wifiRequired")
        @Expose
        private Boolean wifiRequired;
        @SerializedName("packagePriority")
        @Expose
        private Integer packagePriority;
        @SerializedName("failedOnResponse")
        @Expose
        private String failedOnResponse;
        @SerializedName("cdnreleaseNotesUrl")
        @Expose
        private String cdnreleaseNotesUrl;

        public String getReleaseNotesUrl() {
            return releaseNotesUrl;
        }

        public void setReleaseNotesUrl(String releaseNotesUrl) {
            this.releaseNotesUrl = releaseNotesUrl;
        }

        public Integer getUpdateDisplayTime() {
            return updateDisplayTime;
        }

        public void setUpdateDisplayTime(Integer updateDisplayTime) {
            this.updateDisplayTime = updateDisplayTime;
        }

        public Boolean getWifiRequired() {
            return wifiRequired;
        }

        public void setWifiRequired(Boolean wifiRequired) {
            this.wifiRequired = wifiRequired;
        }

        public Integer getPackagePriority() {
            return packagePriority;
        }

        public void setPackagePriority(Integer packagePriority) {
            this.packagePriority = packagePriority;
        }

        public String getFailedOnResponse() {
            return failedOnResponse;
        }

        public void setFailedOnResponse(String failedOnResponse) {
            this.failedOnResponse = failedOnResponse;
        }

        public String getCdnreleaseNotesUrl() {
            return cdnreleaseNotesUrl;
        }

        public void setCdnreleaseNotesUrl(String cdnreleaseNotesUrl) {
            this.cdnreleaseNotesUrl = cdnreleaseNotesUrl;
        }

    }

    @Generated("jsonschema2pojo")
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

    }

    @SerializedName("displayOTAStatusReport")
    @Expose
    private String displayOTAStatusReport;
    @SerializedName("ccsStatus")
    @Expose
    private CcsStatus ccsStatus;
    @SerializedName("error")
    @Expose
    private Object error;
    @SerializedName("fuseResponse")
    @Expose
    private FuseResponse fuseResponse;
    @SerializedName("tappsResponse")
    @Expose
    private TappsResponse tappsResponse;
    @SerializedName("updatePendingState")
    @Expose
    private Object updatePendingState;
    @SerializedName("otaAlertStatus")
    @Expose
    private String otaAlertStatus;

    public String getDisplayOTAStatusReport() {
        return displayOTAStatusReport;
    }

    public void setDisplayOTAStatusReport(String displayOTAStatusReport) {
        this.displayOTAStatusReport = displayOTAStatusReport;
    }

    public CcsStatus getCcsStatus() {
        return ccsStatus;
    }

    public void setCcsStatus(CcsStatus ccsStatus) {
        this.ccsStatus = ccsStatus;
    }

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

    public TappsResponse getTappsResponse() {
        return tappsResponse;
    }

    public void setTappsResponse(TappsResponse tappsResponse) {
        this.tappsResponse = tappsResponse;
    }

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
