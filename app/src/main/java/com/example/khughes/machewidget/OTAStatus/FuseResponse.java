package com.example.khughes.machewidget.OTAStatus;

import androidx.room.Embedded;
import androidx.room.TypeConverters;

import com.example.khughes.machewidget.db.Converters;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class FuseResponse {

    @SerializedName("fuseResponseList")
    @Expose
    @TypeConverters(Converters.class)
    private List<FuseResponseList> fuseResponseList = null;
    @SerializedName("languageText")
    @Expose
    @Embedded
    private LanguageText languageText;

    public FuseResponse() {
        languageText = null;
        fuseResponseList = null;
    }

    public FuseResponse(LanguageText text, List<FuseResponseList> fuselist) {
        languageText = text;
        fuseResponseList = fuselist;
    }

    public List<FuseResponseList> getFuseResponseList() {
        return fuseResponseList;
    }

    public void setFuseResponseList(List<FuseResponseList> fuseResponseList) {
        this.fuseResponseList = fuseResponseList;
    }

    public LanguageText getLanguageText() {
        return languageText;
    }

    public void setLanguageText(LanguageText languageText) {
        this.languageText = languageText;
    }

}