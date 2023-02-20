package com.example.khughes.machewidget

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class CommandStatus {
    @SerializedName("version")
    @Expose
    var version: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("commandId")
    @Expose
    var commandId: String? = null
}