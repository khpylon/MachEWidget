package com.example.khughes.machewidget

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.khughes.machewidget.ui.theme.MacheWidgetTheme
import kotlin.math.roundToInt

class ChooseAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Find info fpr all the eligible apps
        val apps = getAppList(this)

        // If there aren't any, display a dialog then exit the activity
        if (apps.size == 0) {
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle(this.getString(R.string.error_title))
                .setMessage(this.getString(R.string.no_apps_found))
                .setPositiveButton(
                    android.R.string.ok
                ) { _: DialogInterface?, _: Int -> this.finish() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        } else {
            setContent {
                MacheWidgetTheme {
                    ChooseApplications(apps = apps)
                }
            }
        }
    }

    // When the activity ends, make sure widgets are updated
    override fun onStop() {
        super.onStop()
        CarStatusWidget.updateWidget(this)
    }
}

// Get information about all possible applications that can be used
private fun getAppList(context: Context): MutableList<AppItem> {
    val applications: MutableList<AppItem> = mutableListOf()
    val packages = context.resources.getStringArray(R.array.packages)
    val noFilters = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        .getBoolean(context.resources.getString(R.string.show_all_apps_key), false)

    // Create an intent for the search
    val intent = Intent(Intent.ACTION_MAIN, null)
        .addCategory(Intent.CATEGORY_LAUNCHER)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

    // Get a list of applications based on the intent filter
    val resolveInfoList =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0)
            )
        } else {
            context.packageManager.queryIntentActivities(intent, 0)
        }

    // Check each app on the list
    for (app in resolveInfoList) {
        val packageName = app.activityInfo.applicationInfo.packageName
        val isSystemPackage =
            (app.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        // Look for either any non-system package, or only specific packages
        if (packageName != BuildConfig.APPLICATION_ID &&
            ((noFilters && !isSystemPackage) ||
                    (!noFilters && packages.contains(packageName)))
        ) {
            val appInfo = app.activityInfo.applicationInfo
            val appName = context.packageManager.getApplicationLabel(appInfo).toString()
            val icon = context.packageManager.getApplicationIcon(appInfo)
            applications.add(AppItem(appName, packageName, icon))
        }
    }

    // Sort by app name
    applications.sortWith(compareBy { it.appName })
    return applications
}

// Since our icons are Drawables, create a custom painter to convert them to Painter
private class DrawablePainter(
    private val drawable: Drawable,
    private val srcOffset: IntOffset = IntOffset.Zero,
    private val srcSize: IntSize = IntSize(drawable.intrinsicWidth, drawable.intrinsicHeight),
) : Painter() {

    private fun validateSize(srcOffset: IntOffset, srcSize: IntSize): IntSize {
        require(
            srcOffset.x >= 0 &&
                    srcOffset.y >= 0 &&
                    srcSize.width >= 0 &&
                    srcSize.height >= 0 &&
                    srcSize.width <= drawable.intrinsicWidth &&
                    srcSize.height <= drawable.intrinsicHeight
        )
        return srcSize
    }

    private val size: IntSize = validateSize(srcOffset, srcSize)

    override val intrinsicSize: Size get() = size.toSize()

    override fun DrawScope.onDraw() {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        drawImage(
            bitmap.asImageBitmap(),
            srcOffset,
            srcSize,
            dstSize = IntSize(
                this@onDraw.size.width.roundToInt(),
                this@onDraw.size.height.roundToInt()
            )
        )
    }
}

private class AppItem(
    var appName: String,
    var packageName: String?,
    var icon: Drawable?
)

@Composable
private fun AppInformation(app: AppItem) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Image(
            painter = DrawablePainter(app.icon!!),
            contentDescription = "",
            modifier = Modifier
                .size(54.dp)
                .padding(start = 4.dp)
        )
        Column {
            Text(
                text = app.appName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            if (app.packageName != null) {
                Text(
                    text = app.packageName!!,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseApplications(apps: List<AppItem>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.action_chooseapp)) })
        },
        modifier = Modifier.fillMaxSize()
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            val context = LocalContext.current
            val appInfo = StoredData(context)

            // Set up everything for the two radio buttons
            val leftString =
                context.resources.getString(R.string.activity_choose_left_button_text)
            val rightString =
                context.resources.getString(R.string.activity_choose_right_button_text)
            val radioOptions = listOf(leftString, rightString)
            var selectedOption by remember { mutableStateOf(radioOptions[1]) }

            Text(text = context.resources.getString(R.string.activity_choose_app_link_app_to))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                radioOptions.forEach { buttonName ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (buttonName == selectedOption),
                            onClick = { selectedOption = buttonName },
                            modifier = Modifier
                                .size(30.dp)
                                .padding(start = 8.dp)
                        )
                        Text(
                            text = buttonName,
                            modifier = Modifier
                                .padding(start = 2.dp)
                        )
                    }
                }
            }

            // Button to remove app from the widget
            Button(
                onClick = {
                    if (selectedOption == leftString) {
                        appInfo.leftAppPackage = null
                    } else {
                        appInfo.rightAppPackage = null
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(text = context.resources.getString(R.string.remove_app_selection))
            }

            LazyColumn(
                contentPadding = PaddingValues(2.dp)
            ) {
                itemsIndexed(apps) { index, app ->
                    // Get Dark Mode Setting, then choose alternating colors for each row's background
                    val nightModeFlags =
                        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    val bgColor1 = ContextCompat.getColor(
                        context,
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO)
                            R.color.white else R.color.black
                    )
                    val bgColor2 = ContextCompat.getColor(
                        context,
                        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO)
                            R.color.white95percent else R.color.black20percent
                    )

                    // Draw the container, then show the app information inside it
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(if (index % 2 == 1) bgColor1 else bgColor2),
                                shape = RectangleShape// RoundedCornerShape(0.dp)
                            )
                            .clickable(
                                onClick = {
                                    if (selectedOption == leftString) {
                                        appInfo.leftAppPackage = app.packageName
                                    } else {
                                        appInfo.rightAppPackage = app.packageName
                                    }
                                }
                            )

                    ) {
                        AppInformation(app)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun LightPreview() {
    val appsList: MutableList<AppItem> = mutableListOf(
        AppItem(
            appName = LocalContext.current.resources.getString(R.string.app_name),
            packageName = BuildConfig.APPLICATION_ID,
            icon = AppCompatResources.getDrawable(LocalContext.current, R.drawable.mache_logo)
        )
    )

    MacheWidgetTheme {
        ChooseApplications(apps = appsList)
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DarkPreview() {
    val appsList: MutableList<AppItem> = mutableListOf(
        AppItem(
            appName = LocalContext.current.resources.getString(R.string.app_name),
            packageName = BuildConfig.APPLICATION_ID,
            icon = AppCompatResources.getDrawable(LocalContext.current, R.drawable.mache_logo)
        )
    )

    MacheWidgetTheme {
        ChooseApplications(apps = appsList)
    }
}
