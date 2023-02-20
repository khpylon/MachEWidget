package com.example.khughes.machewidget

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class UserDetails {
    @Generated("jsonschema2pojo")
    inner class Status {
        @SerializedName("cache-control")
        @Expose
        var cacheControl: String? = null

        @SerializedName("last_modified")
        @Expose
        var lastModified: String? = null

        @SerializedName("statusCode")
        @Expose
        var statusCode: String? = null
    }

    @Generated("jsonschema2pojo")
    inner class UserVehicles {
        @SerializedName("vehicleDetails")
        @Expose
        var vehicleDetails: List<VehicleDetail>? = null

        @SerializedName("status")
        @Expose
        var status: Status? = null
    }

    @Generated("jsonschema2pojo")
    inner class VehicleDetail {
        @SerializedName("VIN")
        @Expose
        var vin: String? = null

        @SerializedName("nickName")
        @Expose
        var nickName: String? = null

        @SerializedName("tcuEnabled")
        @Expose
        var tcuEnabled: Boolean? = null

        @SerializedName("isASDN")
        @Expose
        var isASDN: Boolean? = null
    }

    @SerializedName("userVehicles")
    @Expose
    var userVehicles: UserVehicles? = null
}