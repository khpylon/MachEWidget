package com.example.khughes.machewidget

import java.io.File

object Constants {
    const val APID = "71A3AD0A-CF46-4CCF-B473-FC7FE5BC4592"
    const val CLIENTID = "9fb503e0-715b-47e8-adfd-ad4b7770f73b"
    const val OTATIMEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ" // 2022-01-19T05:10:35.239+0000
    const val STATUSTIMEFORMAT = "MM-dd-yyyy HH:mm:ss" // 01-19-2022 13:00:00
    const val LOCALTIMEFORMATUS = "MMM d, HH:mm z" // Jan 29, 13:00 PST
    const val LOCALTIMEFORMAT = "d MMM, HH:mm z" // 29 Jan, 13:00 PST
    const val LASTMODIFIEDFORMAT = "EEE, d MMM yyyy HH:mm:ss zzz" // Sat, 29 Jan 2022 13:00:00 PST

    const val KMTOMILES = 0.6213711922
    const val KPATOPSI = 0.14503774
    const val KPATOBAR = 0.01

    const val STATE_INITIAL_STATE = "INITIAL_STATE"
    const val STATE_ATTEMPT_TO_GET_ACCESS_TOKEN = "ATTEMPT_TO_GET_ACCESS_TOKEN"
    const val STATE_ATTEMPT_TO_REFRESH_ACCESS_TOKEN = "ATTEMPT_TO_REFRESH_ACCESS_TOKEN"
    const val STATE_HAVE_TOKEN = "HAVE_TOKEN"
    const val STATE_HAVE_TOKEN_AND_VIN = "HAVE_TOKEN_AND_VIN"

    const val HTTP_BAD_REQUEST = 400
    const val HTTP_UNAUTHORIZED = 401
    const val HTTP_SERVER_ERROR = 402

    // Make sure these match values in arrays.xml
    const val UNITS_SYSTEM = 0
    const val UNITS_METRIC = 1
    const val UNITS_IMPERIAL = 2

    const val CHARGING_STATUS_NOT_READY = "NotReady"
    const val CHARGING_STATUS_CHARGING_AC = "ChargingAC"
    const val CHARGING_STATUS_CHARGING_DC = "ChargingDC"
    const val CHARGING_STATUS_TARGET_REACHED = "ChargeTargetReached"
    const val CHARGING_STATUS_PRECONDITION = "CabinPreconditioning"
    const val CHARGING_STATUS_PAUSED = "EvsePaused"

    const val REPOURL = "https://github.com/khpylon/MachEWidget"
    const val FSVERSION_1 = "FSVERSION_1"
    const val SHAREDPREFS_FOLDER = "shared_prefs"
    @JvmField
    val IMAGES_FOLDER = SHAREDPREFS_FOLDER + File.separator + "images"
    const val WIDGET_FILE = "widget"
    const val VIN_KEY = "VIN_"

    const val TEMP_ACCOUNT = "temporary"

    const val SURVEY_VERSION = 1;

    const val APPLICATION_JSON = "application/json"
    const val APPLICATION_ZIP = "application/zip"
    const val APPLICATION_OCTETSTREAM = "application/octet-stream"
    const val TEXT_HTML = "text/html"
    const val TEXT_PLAINTEXT = "text/plain"
}