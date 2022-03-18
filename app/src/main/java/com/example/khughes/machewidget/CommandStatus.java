package com.example.khughes.machewidget;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CommandStatus {

    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("commandId")
    @Expose
    private String commandId;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String id) {
        this.commandId = id;
    }

}
