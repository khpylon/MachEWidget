package com.example.khughes.machewidget.CarStatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class Battery {

    @Generated("jsonschema2pojo")
    class BatteryHealth {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "batteryhealth_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class BatteryStatusActual {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "batterystatusactual_value")
        var value: Double = 12.0
        @ColumnInfo(name = "batterystatusactual_percent")
        var percentage: Double = 0.0
    }

    @SerializedName("batteryHealth")
    @Expose
    @Embedded
    var batteryHealth: BatteryHealth? = null

    @SerializedName("batteryStatusActual")
    @Expose
    @Embedded
    var batteryStatusActual: BatteryStatusActual? = null
}