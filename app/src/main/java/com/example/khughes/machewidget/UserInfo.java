package com.example.khughes.machewidget;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_info")
public class UserInfo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private String username;
    private String password;

    private String programState;

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String country;
    private String language;
    private String uomSpeed;
    private int uomDistance;
    private String uomPressure;

    private String lastModified;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUomSpeed() {
        return uomSpeed;
    }

    public void setUomSpeed(String uomSpeed) {
        this.uomSpeed = uomSpeed;
    }

    public int getUomDistance() {
        return uomDistance;
    }

    public void setUomDistance(int uomDistance) {
        this.uomDistance = uomDistance;
    }

    public String getUomPressure() {
        return uomPressure;
    }

    public void setUomPressure(String uomPressure) {
        this.uomPressure = uomPressure;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProgramState() {
        return programState;
    }

    public void setProgramState(String programState) {
        this.programState = programState;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    private String sparetext1;
    private String sparetext2;
    private String sparetext3;
    private Integer spareint1;
    private Integer spareint2;
    private Integer spareint3;

    public String getSparetext1() { return sparetext1; }

    public void setSparetext1(String sparetext) { this.sparetext1 = sparetext; }

    public String getSparetext2() { return sparetext2; }

    public void setSparetext2(String sparetext) { this.sparetext2 = sparetext; }

    public String getSparetext3() { return sparetext3; }

    public void setSparetext3(String sparetext) { this.sparetext3 = sparetext; }

    public void setSpareint1(Integer spareint) { this.spareint1 = spareint; }

    public Integer getSpareint1() { return spareint1; }

    public void setSpareint2(Integer spareint) { this.spareint2 = spareint; }

    public Integer getSpareint2() { return spareint2; }

    public void setSpareint3(Integer spareint) { this.spareint3 = spareint; }

    public Integer getSpareint3() { return spareint3; }

}
