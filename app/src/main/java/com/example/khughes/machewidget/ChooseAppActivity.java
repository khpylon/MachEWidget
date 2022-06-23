package com.example.khughes.machewidget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChooseAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<AppList> apps = getAppList(getApplicationContext());
        if (apps.size() == 1) {
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                    .setTitle("Error")
                    .setMessage("No other apps were found.")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Continue with delete operation
                        finish();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            setContentView(R.layout.activity_choose_app);
            RecyclerView recyclerView = findViewById(R.id.recyclerview);

            RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                public void onClick(int index) {
                    RadioButton right = findViewById(R.id.rightIcon);
                    boolean rightButton = right.isChecked();
                    AppList app = apps.get(index);
                    StoredData appInfo = new StoredData(getApplicationContext());
                    if (!rightButton) {
                        appInfo.setLeftAppPackage(app.packageName);
                    } else {
                        appInfo.setRightAppPackage(app.packageName);
                    }
                    MainActivity.updateWidget(getApplicationContext());
                    finish();
                }
            };
            AppListAdapter adapter = new AppListAdapter(getApplicationContext(), apps, listener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private static abstract class RecyclerViewClickListener {
        public void onClick(int index) {
        }
    }

    private static class AppViewHolder extends RecyclerView.ViewHolder {
        private final TextView appNameItemView;
        private final TextView packageNameItemView;
        private final ImageView iconView;
        private final View view;

        private AppViewHolder(View itemView) {
            super(itemView);
            appNameItemView = itemView.findViewById(R.id.title);
            packageNameItemView = itemView.findViewById(R.id.appname);
            iconView = itemView.findViewById(R.id.list_image);
            view = itemView;
        }
    }

    private static class AppListAdapter extends RecyclerView.Adapter<AppViewHolder> {

        private final Context context;
        private final RecyclerViewClickListener listener;
        private final ArrayList<AppList> list;

        public AppListAdapter(Context context, ArrayList<AppList> list,
                              RecyclerViewClickListener listener) {
            this.context = context;
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            // Inflate the layout
            View row = inflater.inflate(R.layout.applist_row, parent, false);
            return new AppViewHolder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
            final int index = holder.getAdapterPosition();
            holder.appNameItemView.setText(list.get(position).getAppName());
            holder.packageNameItemView.setText(list.get(position).getPackageName());
            holder.iconView.setImageDrawable(list.get(position).getIcon());
            holder.view.setOnClickListener(view -> listener.onClick(index));

            int nightModeFlags =  holder.itemView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            holder.appNameItemView.setTextColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#000000" : "#FFFFFF"));
            holder.packageNameItemView.setTextColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#000000" : "#FFFFFF"));
            if(position %2 == 1)  {
                holder.itemView.setBackgroundColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#FFFFFF" : "#000000"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#F0F0F0" : "#202020"));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private static class AppList {
        String appName;
        String packageName;
        Drawable icon;

        public AppList(String app, String pack, Drawable iconName) {
            this.appName = app;
            this.packageName = pack;
            this.icon = iconName;
        }

        public String getAppName() {
            return appName;
        }

        public String getPackageName() {
            return packageName;
        }

        public Drawable getIcon() {
            return icon;
        }
    }

    private static ArrayList<AppList> getAppList(Context context) {
        ArrayList<AppList> arrayList = new ArrayList<>();

        List<String> packages = Arrays.asList(context.getResources().getStringArray(R.array.packages));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        boolean noFilter = sharedPref.getBoolean(context.getResources().getString(R.string.show_all_apps_key), false);

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
            String packName = resolveInfo.activityInfo.applicationInfo.packageName;
            boolean isSystemPackage = (resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            // If this is not a system app package
            if ((noFilter && !isSystemPackage) ||
                    (!noFilter && packages.contains(packName))) {
                ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
                CharSequence appName = context.getPackageManager().getApplicationLabel(appInfo);
                Drawable icon = context.getPackageManager().getApplicationIcon(appInfo);
                arrayList.add(new AppList(appName.toString(), packName, icon));
            }
        }

        // Sort by names
        arrayList.sort(Comparator.comparing(subjectData -> subjectData.appName));

        // Add an entry to the top of the list to ley user remove the current app.
        arrayList.add(0, new AppList("Remove current app", null, null));

        return arrayList;
    }

}
