package com.example.khughes.machewidget.CarStatus

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class DoorStatus {

    @Generated("jsonschema2pojo")
    class DriverDoor {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "driverdoor_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class PassengerDoor {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "passengerdoor_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class LeftRearDoor {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "leftreardoor_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class RightRearDoor {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "rightreardoor_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class HoodDoor {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "hooddoor_value")
        var value: String? = null
    }

    @Generated("jsonschema2pojo")
    class TailgateDoor {
        @SerializedName("value")
        @Expose
        @ColumnInfo(name = "tailgate_value")
        var value: String? = null
    }

    @SerializedName("driverDoor")
    @Expose
    @Embedded
    var driverDoor: DriverDoor? = null

    @SerializedName("passengerDoor")
    @Expose
    @Embedded
    var passengerDoor: PassengerDoor? = null

    @SerializedName("rightRearDoor")
    @Expose
    @Embedded
    var rightRearDoor: RightRearDoor? = null

    @SerializedName("leftRearDoor")
    @Expose
    @Embedded
    var leftRearDoor: LeftRearDoor? = null

    @SerializedName("hoodDoor")
    @Expose
    @Embedded
    var hoodDoor: HoodDoor? = null

    @SerializedName("tailgateDoor")
    @Expose
    @Embedded
    var tailgateDoor: TailgateDoor? = null
}