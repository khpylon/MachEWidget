package com.example.khughes.machewidget;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChooseApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);
        String VIN = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getResources().getString(R.string.VIN_key), "");

        final ListView list = findViewById(R.id.list);
        ArrayList<AppList> arrayList = getApps(this);
        if (arrayList.size() > 0) {
            CustomAdapter customAdapter = new CustomAdapter(this, arrayList);
            list.setAdapter(customAdapter);
            list.setOnItemClickListener((adapterView, view, i, l) -> {
                RadioButton right = findViewById(R.id.rightIcon);
                Boolean rightButton = right.isChecked();
                AppList app = arrayList.get(i);
                StoredData appInfo = new StoredData(getApplicationContext());
                if (rightButton == false) {
                    appInfo.setLeftAppPackage(VIN,app.packageName);
                } else {
                    appInfo.setRightAppPackage(VIN,app.packageName);
                }
                MainActivity.updateWidget(getApplicationContext());
                finish();
            });
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("No other apps were found.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private class CustomAdapter implements ListAdapter {
        ArrayList<AppList> arrayList;
        Context context;

        public CustomAdapter(Context context, ArrayList<AppList> arrayList) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppList subjectData = arrayList.get(position);
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.applist_row, null);
                TextView tittle = convertView.findViewById(R.id.title);
                TextView app = convertView.findViewById(R.id.appname);
                ImageView imag = convertView.findViewById(R.id.list_image);

                tittle.setText(subjectData.appName);
                app.setText(subjectData.packageName);
                imag.setImageDrawable(subjectData.icon);
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return arrayList.size();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    private ArrayList<AppList> getApps(Context context) {
        ArrayList<AppList> arrayList = new ArrayList<AppList>();
        List<String> packages = Arrays.asList(getResources().getStringArray(R.array.packages));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean noFilter = sharedPref.getBoolean(context.getResources().getString(R.string.show_all_apps_key), false);

        Intent intent = new Intent(Intent.ACTION_MAIN, null);

        // Set the newly created intent category to launcher
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Set the intent flags
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        );
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Generate a list of ResolveInfo object based on intent filter
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);

        // Loop through the ResolveInfo list
        for (ResolveInfo resolveInfo : resolveInfoList) {
            // Get the ActivityInfo from current ResolveInfo
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            String packName = resolveInfo.activityInfo.applicationInfo.packageName;
            Boolean isSystemPackage = (resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            // If this is not a system app package
            if ((noFilter == true && !isSystemPackage) ||
                    (noFilter == false && packages.contains(packName))) {
                ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
                CharSequence appName = getPackageManager().getApplicationLabel(appInfo);
                Drawable icon = context.getPackageManager().getApplicationIcon(appInfo);
                arrayList.add(new AppList(appName.toString(), packName, icon));
                // Add the non system package to the listpackageNames.add(activityInfo.applicationInfo.packageName);
            }
        }

        // Sort by names
        arrayList.sort(new Comparator<AppList>() {
            @Override
            public int compare(AppList subjectData, AppList t1) {
                return subjectData.appName.compareTo(t1.appName);
            }
        });

        // Add an entry to the top of the list to ley user remove the current app.
        arrayList.add(0, new AppList("Remove current app", null, null));
        return arrayList;
    }

    private class AppList {
        String appName;
        String packageName;
        Drawable icon;

        public AppList(String app, String pack, Drawable iconName) {
            this.appName = app;
            this.packageName = pack;
            this.icon = iconName;
        }
    }

}
