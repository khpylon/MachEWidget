package com.example.khughes.machewidget

class Profile {
    var vIN // the "key" for the object
            : String
    var profileName: String
        private set

    constructor(VIN: String) {
        vIN = VIN
        profileName = ""
    }

    constructor(VIN: String, alias: String) {
        vIN = VIN
        profileName = alias
    }

    fun setAlias(alias: String) {
        profileName = alias
    }
}