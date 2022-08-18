package com.example.khughes.machewidget

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khughes.machewidget.databinding.ActivityChooseAppBinding

private lateinit var binding: ActivityChooseAppBinding

class ChooseAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apps = getAppList(applicationContext)

        // If there aren't any apps, show a dialog and exit
        if (apps.size == 1) {
            AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle("Error")
                .setMessage("No other apps were found.")
                .setPositiveButton(
                    android.R.string.ok
                ) { _: DialogInterface?, _: Int -> finish() }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        } else {
            binding = ActivityChooseAppBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val appListAdapter = AppListAdapter(applicationContext, apps)
            // Assign the app to one of the buttons
            appListAdapter.onItemClick = { app ->
                val rightButton = binding.rightIcon.isChecked
                val appInfo = StoredData(applicationContext)
                if (!rightButton) {
                    appInfo.leftAppPackage = app.packageName
                } else {
                    appInfo.rightAppPackage = app.packageName
                }
                CarStatusWidget.updateWidget(applicationContext)
                finish()
            }
            binding.recyclerview.adapter = appListAdapter
            binding.recyclerview.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun getAppList(context: Context): MutableList<AppList> {
        val applications: MutableList<AppList> = mutableListOf()
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
                applications.add(AppList(appName, packageName, icon))
            }
        }

        // Sort by app name, then add a blank at the top of the list (for removing an item)
        applications.sortWith(compareBy { it.appName })
        applications.add(0, AppList("Remove this app", null, null))
        return applications
    }

    // Information about each app
    private class AppList(
        var appName: String,
        var packageName: String?,
        var icon: Drawable?
    )

    private class AppListAdapter(
        val context: Context,
        val list: List<AppList>
    ) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

        var onItemClick: ((AppList) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val inflater = LayoutInflater.from(context)
            val row = inflater.inflate(R.layout.applist_row, parent, false)
            return AppViewHolder(row)
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            val app = list[position]
            holder.appNameView.text = app.appName
            holder.packageNameView.text = app.packageName
            holder.iconView.setImageDrawable(app.icon)

            // Draw alternating item backgrounds in different shades, depending on Dark Mode settings
            val black = context.resources.getString(R.color.black)
            val black20 = context.resources.getString(R.color.black20percent)
            val white = context.resources.getString(R.color.white)
            val white95 = context.resources.getString(R.color.white95percent)

            val nightModeFlags =
                holder.itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            holder.appNameView.setTextColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) black else white))
            holder.packageNameView.setTextColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) black else white))
            if (position % 2 == 1) {
                holder.itemView.setBackgroundColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) white else black))
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor(if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) white95 else black20))
            }
        }

        override fun getItemCount(): Int = list.size

        inner class AppViewHolder(
            view: View,
        ) : RecyclerView.ViewHolder(view) {
            var appNameView: TextView = itemView.findViewById(R.id.title)
            var packageNameView: TextView = itemView.findViewById(R.id.appname)
            val iconView: ImageView = itemView.findViewById(R.id.list_image)

            init {
                view.setOnClickListener {
                    onItemClick?.invoke(list[adapterPosition])
                }
            }
        }
    }
}


