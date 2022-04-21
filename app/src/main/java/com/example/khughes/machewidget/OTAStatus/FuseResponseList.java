package com.example.khughes.machewidget.OTAStatus;

import androidx.room.Embedded;
import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class FuseResponseList {

    @SerializedName("vin")
    @Expose
    @Ignore
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
    @Embedded
    private LatestStatus latestStatus;
//    @SerializedName("packageUpdateDetails")
//    @Expose
//    private OTAStatus.PackageUpdateDetails packageUpdateDetails;
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

//    public OTAStatus.PackageUpdateDetails getPackageUpdateDetails() {
//        return packageUpdateDetails;
//    }
//
//    public void setPackageUpdateDetails(OTAStatus.PackageUpdateDetails packageUpdateDetails) {
//        this.packageUpdateDetails = packageUpdateDetails;
//    }

    public String getDeploymentFinalConsumerAction() {
        return deploymentFinalConsumerAction;
    }

    public void setDeploymentFinalConsumerAction(String deploymentFinalConsumerAction) {
        this.deploymentFinalConsumerAction = deploymentFinalConsumerAction;
    }

}
