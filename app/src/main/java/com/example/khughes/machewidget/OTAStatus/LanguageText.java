package com.example.khughes.machewidget.OTAStatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

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
