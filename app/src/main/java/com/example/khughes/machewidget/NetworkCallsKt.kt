package com.example.khughes.machewidget

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkCallsKt {

    val COMMAND_SUCCESSFUL = "Command successful."
    val COMMAND_FAILED = "Command failed."
    val COMMAND_NO_NETWORK = "Network error."
    val COMMAND_EXCEPTION = "Exception occurred."
    val COMMAND_REMOTE_START_LIMIT = "Cannot extend remote start time without driving."

    private val CMD_STATUS_SUCCESS = 200
    private val CMD_STATUS_INPROGRESS = 552
    private val CMD_STATUS_FAILED = 411
    private val CMD_REMOTE_START_LIMIT = 590

    companion object {

        @JvmStatic
        private fun checkInternetConnection(context: Context): Boolean {
            // Get Connectivity Manager
            val connManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Details about the currently active default data network
            return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                val networkInfo = connManager.activeNetworkInfo
                networkInfo?.let { networkInfo.isConnected && networkInfo.isAvailable } ?: false
            } else {
                val networkInfo = connManager.activeNetwork ?: return false
                val networkCapabilities = connManager.getNetworkCapabilities(networkInfo)
                networkCapabilities?.let {
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                } ?: false
            }
        }
    }

}