package com.example.khughes.machewidget.CarStatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class WindowPosition {

    @Generated("jsonschema2pojo")
    class DriverWindowPosition {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "driverwindow_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class PassWindowPosition {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "passwindow_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class RearDriverWindowPos {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "reardriverwindow_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class RearPassWindowPos {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "rearpasswindow_value")
        var value: String? = null
    }

    @SerializedName("driverWindowPosition")
    @Expose
    @Embedded
    var driverWindowPosition: DriverWindowPosition? = null

    @SerializedName("passWindowPosition")
    @Expose
    @Embedded
    var passWindowPosition: PassWindowPosition? = null

    @SerializedName("rearDriverWindowPos")
    @Expose
    @Embedded
    var rearDriverWindowPos: RearDriverWindowPos? = null

    @SerializedName("rearPassWindowPos2")
    @Expose
    @Embedded
    var rearPassWindowPos: RearPassWindowPos? = null
}

