package com.example.khughes.machewidget

import android.content.Intent
import android.icu.text.MessageFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.LogFile.i
import com.example.khughes.machewidget.NetworkCalls.Companion.getAccessToken
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private var usernameWidget: TextInputLayout? = null
    private var passwordWidget: TextInputLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        usernameWidget = findViewById(R.id.username)
        passwordWidget = findViewById(R.id.password)

        val login = findViewById<Button>(R.id.login)
        login.setOnClickListener { view: View? ->
            if (usernameWidget == null || usernameWidget!!.editText == null || passwordWidget == null || passwordWidget!!.editText == null) {
                return@setOnClickListener
            }
            val username = usernameWidget!!.editText!!.text.toString()
            val password = passwordWidget!!.editText!!.text.toString()
            if (username.length == 0) {
                Toast.makeText(applicationContext,
                    getString(R.string.activity_login_enter_username_toast), Toast.LENGTH_SHORT)
                    .show()
            } else if (password.length == 0) {
                Toast.makeText(applicationContext,
                    getString(R.string.activity_login_enter_password_toast), Toast.LENGTH_SHORT)
                    .show()
            } else {
                getAccess(username, password)
            }
        }
    }

    private fun getAccess(username: String, password: String) {
        val context = applicationContext
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

        val h: Handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val bb = msg.data
                val action = bb.getString("action")
                i(context, MainActivity.CHANNEL_ID, "Access: $action")

                when (action) {
                    Constants.STATE_HAVE_TOKEN_AND_VIN -> {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.activity_login_login_successful_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                        nextAlarm(context, 5)
                        val data = Intent()
                        setResult(RESULT_OK, data)
                        finish()
                    }

                    Constants.STATE_HAVE_TOKEN -> {
                        // Update global userId key
                        val userId = bb.getString("userId")
                        sharedPref.edit()
                            .putString(context.resources.getString(R.string.userId_key), userId)
                            .apply()

                        // Prompt the user to add vehicles
                        val intent = Intent(context, VehicleActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }

                    Constants.STATE_ACCOUNT_DISABLED -> Toast.makeText(
                        applicationContext,
                        MessageFormat.format (
                            getString(R.string.activity_login_account_disabled_error_toast),
                                 Authenticate.ACCOUNT_DISABLED_CODE ),
                        Toast.LENGTH_LONG
                    ).show()

                    Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN -> Toast.makeText(
                        applicationContext,
                        getString(R.string.activity_login_check_username__password_toast),
                        Toast.LENGTH_LONG
                    ).show()

                    else -> Toast.makeText(
                        applicationContext,
                        getString(R.string.activity_login_check_username__password_toast),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        Toast.makeText(applicationContext,
            getString(R.string.activity_login_attempting_to_log_in_toast), Toast.LENGTH_SHORT).show()
        getAccessToken(h, applicationContext, username, password)
    }
}