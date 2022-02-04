package com.example.khughes.machewidget;

public class Profile {
    private String VIN;         // the "key" for the object
    private String alias;

    private long lastUpdateTime;
    private ProgramStateMachine.States state;
    private CarStatus carStatus;
    private OTAStatus OTAStatus;
    private long lastOTATime;
    private String accessToken;
    private String refreshToken;
    private long tokenTimeout;
    private String HBVStatus;
    private String TPMSStatus;
    private String country;
    private String language;
    private String timeFormat;
    private String speedUnits;
    private String distanceUnits;
    private String pressureUnits;
    private String leftAppPackage;
    private String rightAppPackage;

    public Profile(String VIN) {
        this.VIN = VIN;
        this.alias = "";

        this.lastUpdateTime = 0;
        this.state = ProgramStateMachine.States.INITIAL_STATE;
        this.carStatus = null;
        this.OTAStatus = null;
        this.lastOTATime = 0;
        this.accessToken = null;
        this.refreshToken = null;
        this.tokenTimeout = 0;
        this.HBVStatus = null;
        this.TPMSStatus = null;
        this.country = null;
        this.language = null;
        this.timeFormat = null;
        this.speedUnits = null;
        this.distanceUnits = null;
        this.pressureUnits = null;
        this.leftAppPackage = null;
        this.rightAppPackage = null;
    }

    public Profile(String VIN, String alias) {
        this.VIN = VIN;
        this.alias = alias;
    }

//    public Profile(String VIN, String alias, String accessToken, String refreshToken, long timeout) {
//        this(VIN);
//        this.tokenTimeout = timeout;
//        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
//    }
//
    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
//
//    public long getLastUpdateTime() {
//        return lastUpdateTime;
//    }
//
//    public void setLastUpdateTime(long lastUpdateTime) {
//        this.lastUpdateTime = lastUpdateTime;
//    }
//
//    public ProgramStateMachine.States getState() {
//        return state;
//    }
//
//    public void setState(ProgramStateMachine.States state) {
//        this.state = state;
//    }
//
//    public CarStatus getCarStatus() {
//        return carStatus;
//    }
//
//    public void setCarStatus(CarStatus carStatus) {
//        this.carStatus = carStatus;
//    }
//
//    public com.example.khughes.machewidget.OTAStatus getOTAStatus() {
//        return OTAStatus;
//    }
//
//    public void setOTAStatus(com.example.khughes.machewidget.OTAStatus OTAStatus) {
//        this.OTAStatus = OTAStatus;
//    }
//
//    public long getLastOTATime() {
//        return lastOTATime;
//    }
//
//    public void setLastOTATime(long lastOTATime) {
//        this.lastOTATime = lastOTATime;
//    }
//
//    public String getAccessToken() {
//        return accessToken;
//    }
//
//    public void setAccessToken(String accessToken) {
//        this.accessToken = accessToken;
//    }
//
//    public String getRefreshToken() {
//        return refreshToken;
//    }
//
//    public void setRefreshToken(String refreshToken) {
//        this.refreshToken = refreshToken;
//    }
//
//    public long getTokenTimeout() {
//        return tokenTimeout;
//    }
//
//    public void setTokenTimeout(long tokenTimeout) {
//        this.tokenTimeout = tokenTimeout;
//    }
//
//    public String getHBVStatus() {
//        return HBVStatus;
//    }
//
//    public void setHBVStatus(String HBVStatus) {
//        this.HBVStatus = HBVStatus;
//    }
//
//    public String getTPMSStatus() {
//        return TPMSStatus;
//    }
//
//    public void setTPMSStatus(String TPMSStatus) {
//        this.TPMSStatus = TPMSStatus;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public String getLanguage() {
//        return language;
//    }
//
//    public void setLanguage(String language) {
//        this.language = language;
//    }
//
//    public String getTimeFormat() {
//        return timeFormat;
//    }
//
//    public void setTimeFormat(String timeFormat) {
//        this.timeFormat = timeFormat;
//    }
//
//    public String getSpeedUnits() {
//        return speedUnits;
//    }
//
//    public void setSpeedUnits(String speedUnits) {
//        this.speedUnits = speedUnits;
//    }
//
//    public String getDistanceUnits() {
//        return distanceUnits;
//    }
//
//    public void setDistanceUnits(String distanceUnits) {
//        this.distanceUnits = distanceUnits;
//    }
//
//    public String getPressureUnits() {
//        return pressureUnits;
//    }
//
//    public void setPressureUnits(String pressureUnits) {
//        this.pressureUnits = pressureUnits;
//    }
//
//    public String getLeftAppPackage() {
//        return leftAppPackage;
//    }
//
//    public void setLeftAppPackage(String leftAppPackage) {
//        this.leftAppPackage = leftAppPackage;
//    }
//
//    public String getRightAppPackage() {
//        return rightAppPackage;
//    }
//
//    public void setRightAppPackage(String rightAppPackage) {
//        this.rightAppPackage = rightAppPackage;
//    }

}
