package com.example.khughes.machewidget

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object Authenticate {
    private var mContext: Context? = null

    const val ACCOUNT_BAD_USER_OR_PASSWORD: String = "CSIAH0303E"
    const val ACCOUNT_DISABLED_CODE: String = "CSIAH0320E"

    fun newAuthenticate(context: Context?, username: String?, password: String?): String? {
        mContext = context
        val input = File(context!!.dataDir, "token.txt")
        val inStream = BufferedReader(FileReader(input))
        val url = inStream.readLine()
        val index = url.indexOf("code=")
        return if (index != -1) url.substring(index+5) else url
    }
}
