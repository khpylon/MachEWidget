package com.example.khughes.machewidget;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Generated("jsonschema2pojo")
public class ChargeStation {

    @SerializedName("vin")
    @Expose
    private String vin;
    @SerializedName("tags")
    @Expose
    private Object tags;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("dataSource")
    @Expose
    private Object dataSource;
    @SerializedName("locationId")
    @Expose
    private String locationId;
    @SerializedName("locationName")
    @Expose
    private String locationName;
    @SerializedName("chargeTarget")
    @Expose
    private Object chargeTarget;
    @SerializedName("locationRadiusToAct")
    @Expose
    private Object locationRadiusToAct;
    @SerializedName("minsoc")
    @Expose
    private Object minsoc;
    @SerializedName("chargeDuration")
    @Expose
    private Object chargeDuration;
    @SerializedName("savedLocationId")
    @Expose
    private Integer savedLocationId;
    @SerializedName("unSavedLocationId")
    @Expose
    private Integer unSavedLocationId;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("chargerType")
    @Expose
    private Object chargerType;
    @SerializedName("energy")
    @Expose
    private Object energy;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private Coordinates coordinates;
    @SerializedName("chargeProfile")
    @Expose
    private ChargeProfile chargeProfile;
    @SerializedName("curntTrgtSoc")
    @Expose
    private Integer curntTrgtSoc;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Object getTags() {
        return tags;
    }

    public void setTags(Object tags) {
        this.tags = tags;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getDataSource() {
        return dataSource;
    }

    public void setDataSource(Object dataSource) {
        this.dataSource = dataSource;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Object getChargeTarget() {
        return chargeTarget;
    }

    public void setChargeTarget(Object chargeTarget) {
        this.chargeTarget = chargeTarget;
    }

    public Object getLocationRadiusToAct() {
        return locationRadiusToAct;
    }

    public void setLocationRadiusToAct(Object locationRadiusToAct) {
        this.locationRadiusToAct = locationRadiusToAct;
    }

    public Object getMinsoc() {
        return minsoc;
    }

    public void setMinsoc(Object minsoc) {
        this.minsoc = minsoc;
    }

    public Object getChargeDuration() {
        return chargeDuration;
    }

    public void setChargeDuration(Object chargeDuration) {
        this.chargeDuration = chargeDuration;
    }

    public Integer getSavedLocationId() {
        return savedLocationId;
    }

    public void setSavedLocationId(Integer savedLocationId) {
        this.savedLocationId = savedLocationId;
    }

    public Integer getUnSavedLocationId() {
        return unSavedLocationId;
    }

    public void setUnSavedLocationId(Integer unSavedLocationId) {
        this.unSavedLocationId = unSavedLocationId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Object getChargerType() {
        return chargerType;
    }

    public void setChargerType(Object chargerType) {
        this.chargerType = chargerType;
    }

    public Object getEnergy() {
        return energy;
    }

    public void setEnergy(Object energy) {
        this.energy = energy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ChargeProfile getChargeProfile() {
        return chargeProfile;
    }

    public void setChargeProfile(ChargeProfile chargeProfile) {
        this.chargeProfile = chargeProfile;
    }

    public Integer getCurntTrgtSoc() {
        return curntTrgtSoc;
    }

    public void setCurntTrgtSoc(Integer curntTrgtSoc) {
        this.curntTrgtSoc = curntTrgtSoc;
    }

    @Generated("jsonschema2pojo")
    public class Address {

        @SerializedName("address1")
        @Expose
        private String address1;
        @SerializedName("address2")
        @Expose
        private Object address2;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("postalCode")
        @Expose
        private String postalCode;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("phoneNo")
        @Expose
        private Object phoneNo;

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public Object getAddress2() {
            return address2;
        }

        public void setAddress2(Object address2) {
            this.address2 = address2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Object getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(Object phoneNo) {
            this.phoneNo = phoneNo;
        }
    }

    @Generated("jsonschema2pojo")
    public class ChargeProfile {

        @SerializedName("chargeNow")
        @Expose
        private Boolean chargeNow;
        @SerializedName("utilityProvider")
        @Expose
        private Object utilityProvider;
        @SerializedName("chargeSchedules")
        @Expose
        private List<ChargeSchedule> chargeSchedules = null;

        public Boolean getChargeNow() {
            return chargeNow;
        }

        public void setChargeNow(Boolean chargeNow) {
            this.chargeNow = chargeNow;
        }

        public Object getUtilityProvider() {
            return utilityProvider;
        }

        public void setUtilityProvider(Object utilityProvider) {
            this.utilityProvider = utilityProvider;
        }

        public List<ChargeSchedule> getChargeSchedules() {
            return chargeSchedules;
        }

        public void setChargeSchedules(List<ChargeSchedule> chargeSchedules) {
            this.chargeSchedules = chargeSchedules;
        }

    }

    @Generated("jsonschema2pojo")
    public class ChargeSchedule {

        @SerializedName("days")
        @Expose
        private String days;
        @SerializedName("chargeWindows")
        @Expose
        private List<ChargeWindow> chargeWindows = null;
        @SerializedName("desiredChargeLevel")
        @Expose
        private Integer desiredChargeLevel;

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public List<ChargeWindow> getChargeWindows() {
            return chargeWindows;
        }

        public void setChargeWindows(List<ChargeWindow> chargeWindows) {
            this.chargeWindows = chargeWindows;
        }

        public Integer getDesiredChargeLevel() {
            return desiredChargeLevel;
        }

        public void setDesiredChargeLevel(Integer desiredChargeLevel) {
            this.desiredChargeLevel = desiredChargeLevel;
        }

    }

    @Generated("jsonschema2pojo")
    public class ChargeWindow {

        @SerializedName("startTime")
        @Expose
        private String startTime;
        @SerializedName("endTime")
        @Expose
        private String endTime;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

    }

    @Generated("jsonschema2pojo")
    public class Coordinates {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lon")
        @Expose
        private Double lon;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

    }

}
