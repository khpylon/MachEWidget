package com.example.khughes.machewidget;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_info")
public class UserInfo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;

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
}
