package com.example.khughes.machewidget;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class AccessToken {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;
    @SerializedName("grant_id")
    @Expose
    private String grantId;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Integer expiresIn;
    @SerializedName("UserProfile")
    @Expose
    private UserProfile userProfile;
    @SerializedName("userId")
    @Expose
    private String userId;

    @Generated("jsonschema2pojo")
    public class UserProfile {

        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("uomSpeed")
        @Expose
        private String uomSpeed;
        @SerializedName("uomDistance")
        @Expose
        private Integer uomDistance;
        @SerializedName("uomPressure")
        @Expose
        private String uomPressure;

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

        public Integer getUomDistance() {
            return uomDistance;
        }

        public void setUomDistance(Integer uomDistance) {
            this.uomDistance = uomDistance;
        }

        public String getUomPressure() {
            return uomPressure;
        }

        public void setUomPressure(String uomPressure) {
            this.uomPressure = uomPressure;
        }
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

    public String getGrantId() {
        return grantId;
    }

    public void setGrantId(String grantId) {
        this.grantId = grantId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}