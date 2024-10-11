package com.example.khughes.machewidget

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class CommandStatus {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("commandStatus")
    @Expose
    var commandStatus: String? = null

    @SerializedName("commandId")
    @Expose
    var commandId: String? = null
}