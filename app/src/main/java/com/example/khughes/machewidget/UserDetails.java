package com.example.khughes.machewidget;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Generated("jsonschema2pojo")
public class UserDetails {

    @Generated("jsonschema2pojo")
    public class Status {

        @SerializedName("cache-control")
        @Expose
        private String cacheControl;
        @SerializedName("last_modified")
        @Expose
        private String lastModified;
        @SerializedName("statusCode")
        @Expose
        private String statusCode;

        public String getCacheControl() {
            return cacheControl;
        }

        public void setCacheControl(String cacheControl) {
            this.cacheControl = cacheControl;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

    }

    @Generated("jsonschema2pojo")
    public class UserVehicles {

        @SerializedName("vehicleDetails")
        @Expose
        private List<VehicleDetail> vehicleDetails = null;
        @SerializedName("status")
        @Expose
        private Status status;

        public List<VehicleDetail> getVehicleDetails() {
            return vehicleDetails;
        }

        public void setVehicleDetails(List<VehicleDetail> vehicleDetails) {
            this.vehicleDetails = vehicleDetails;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

    }

    @Generated("jsonschema2pojo")
    public class VehicleDetail {

        @SerializedName("VIN")
        @Expose
        private String vin;
        @SerializedName("nickName")
        @Expose
        private String nickName;
        @SerializedName("tcuEnabled")
        @Expose
        private Boolean tcuEnabled;
        @SerializedName("isASDN")
        @Expose
        private Boolean isASDN;

        public String getVin() {
            return vin;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public Boolean getTcuEnabled() {
            return tcuEnabled;
        }

        public void setTcuEnabled(Boolean tcuEnabled) {
            this.tcuEnabled = tcuEnabled;
        }

        public Boolean getIsASDN() {
            return isASDN;
        }

        public void setIsASDN(Boolean isASDN) {
            this.isASDN = isASDN;
        }
    }

    @SerializedName("userVehicles")
    @Expose
    private UserVehicles userVehicles;

    public UserVehicles getUserVehicles() {
        return userVehicles;
    }

    public void setUserVehicles(UserVehicles userVehicles) {
        this.userVehicles = userVehicles;
    }

}
