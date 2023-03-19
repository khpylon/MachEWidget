package com.example.khughes.machewidget.CarStatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class TPMS {

    @Generated("jsonschema2pojo")
    class LeftFrontTireStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "leftfronttirestatus_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class RightFrontTireStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "rightfronttirestatus_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class OuterLeftRearTireStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "outerleftreartirestatus_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class OuterRightRearTireStatus {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "outerrightreartirestatus_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class LeftFrontTirePressure {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "leftfronttirepressure_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class RightFrontTirePressure {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "rightfronttirepressure_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class OuterLeftRearTirePressure {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "outerleftreartirepressure_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class OuterRightRearTirePressure {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "outerrightreartirepressure_value")
        var value: String? = null
    }

    @SerializedName("leftFrontTireStatus")
    @Expose
    @Embedded
    var leftFrontTireStatus: LeftFrontTireStatus? = null

    @SerializedName("leftFrontTirePressure")
    @Expose
    @Embedded
    var leftFrontTirePressure: LeftFrontTirePressure? = null

    @SerializedName("rightFrontTireStatus")
    @Expose
    @Embedded
    var rightFrontTireStatus: RightFrontTireStatus? = null

    @SerializedName("rightFrontTirePressure")
    @Expose
    @Embedded
    var rightFrontTirePressure: RightFrontTirePressure? = null

    @SerializedName("outerLeftRearTireStatus")
    @Expose
    @Embedded
    var outerLeftRearTireStatus: OuterLeftRearTireStatus? = null

    @SerializedName("outerLeftRearTirePressure")
    @Expose
    @Embedded
    var outerLeftRearTirePressure: OuterLeftRearTirePressure? = null

    @SerializedName("outerRightRearTireStatus")
    @Expose
    @Embedded
    var outerRightRearTireStatus: OuterRightRearTireStatus? = null

    @SerializedName("outerRightRearTirePressure")
    @Expose
    @Embedded
    var outerRightRearTirePressure: OuterRightRearTirePressure? = null

}