@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.khughes.machewidget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.Icon
import android.icu.text.MessageFormat
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.LogFile.i
import com.example.khughes.machewidget.NetworkCalls.Companion.getAccessToken
import com.example.khughes.machewidget.StatusReceiver.Companion.nextAlarm
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MacheWidgetTheme(dynamicColor = false) {
                Surface(
//                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

private
fun getAccess(context: Context, username: String, password: String) {
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    val activity = context as Activity

    CoroutineScope(Dispatchers.Main).launch {
        val msg = getAccessToken(context, username, password)
        val bundle = msg.data
        val action = bundle.getString("action")
        i(context, MainActivity.CHANNEL_ID, "Access: $action")

        when (action) {
            Constants.STATE_HAVE_TOKEN_AND_VIN -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.activity_login_login_successful_toast),
                    Toast.LENGTH_SHORT
                ).show()
                nextAlarm(context, 5)
                val data = Intent()
                activity.setResult(RESULT_OK, data)
                activity.finish()
            }

            Constants.STATE_HAVE_TOKEN -> {
                // Update global userId key
                val userId = bundle.getString("userId")
                sharedPref.edit()
                    .putString(context.resources.getString(R.string.userId_key), userId)
                    .apply()

                // Prompt the user to add vehicles
                val intent = Intent(context, VehicleActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.startActivity(intent)
                activity.finish()
            }

            Constants.STATE_ACCOUNT_DISABLED -> Toast.makeText(
                context,
                MessageFormat.format(
                    context.getString(R.string.activity_login_account_disabled_error_toast),
                    Authenticate.ACCOUNT_DISABLED_CODE
                ),
                Toast.LENGTH_LONG
            ).show()

            Constants.STATE_ATTEMPT_TO_GET_ACCESS_TOKEN -> Toast.makeText(
                context,
                context.getString(R.string.activity_login_check_username__password_toast),
                Toast.LENGTH_LONG
            ).show()

            else -> Toast.makeText(
                context,
                context.getString(R.string.activity_login_check_username__password_toast),
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Toast.makeText(
        context,
        context.getString(R.string.activity_login_attempting_to_log_in_toast), Toast.LENGTH_SHORT
    ).show()
}

@Composable
fun MainScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxHeight()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = context.getString(R.string.disclaimer),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp
        )
        OutlinedTextField(value = username,
            onValueChange = { username = it },
            label = { Text(context.getString(R.string.username_or_e_mail)) }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(context.getString(R.string.password)) },
            visualTransformation = if (passwordVisibility)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisibility) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = image,
                        contentDescription = ""
                    )
                }
            }
        )
        Button(
            onClick = {
                if (username.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.activity_login_enter_username_toast),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (password.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.activity_login_enter_password_toast),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    getAccess(context, username, password)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            ) {
            Text("Login")
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun LightPreview() {
    MacheWidgetTheme(dynamicColor = false) {
        MainScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    MacheWidgetTheme(dynamicColor = false) {
        MainScreen()
    }
}