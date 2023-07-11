package com.example.khughes.machewidget.CarStatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class DieselSystemStatus {

    @Generated("jsonschema2pojo")
    class ExhaustFluidLevel {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "exhaustFluidLevel_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class UreaRange {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "ureaRange_value")
        var value: String? = null
    }

    @SerializedName("exhaustFluidLevel")
    @Expose
    @Embedded
    var exhaustFluidLevel: ExhaustFluidLevel? = null

    @SerializedName("ureaRange")
    @Expose
    @Embedded
    var ureaRange: UreaRange? = null
}