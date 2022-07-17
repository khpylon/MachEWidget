package com.example.khughes.machewidget;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDao;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ProfileManager extends AppCompatActivity {
    private static final String BLANK_VIN = "Unused Entry";

//    private ArrayList<Profile> arrayList;
//    private CustomAdapter adapter;

//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                        String VIN = data.getStringExtra(LoginActivity.VINIDENTIFIER);
//                        String alias = data.getStringExtra(LoginActivity.PROFILENAME);
//                        // Update the current active profile
//                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(getApplicationContext().getResources().getString(R.string.VIN_key), VIN).apply();
//
//                        // Find a profile with a matching VIN
//                        Profile match = arrayList.stream().filter(p -> p.getVIN().equals(VIN)).findFirst().orElse(null);
//                        if (match != null) {
//                            // if the alias changed, update it
//                            if (!match.getProfileName().equals(alias)) {
//                                match.setAlias(alias);
////                                new StoredData(getApplicationContext()).setProfileName(VIN, alias);
//                                sortProfiles();
//                                adapter.notifyDataSetChanged();
//                            }
//                            return;
//                        }
//
//                        // Create a new profile in an unused entry
//                        match = arrayList.stream().filter(p -> p.getVIN().equals(BLANK_VIN)).findFirst().orElse(null);
//                        if (match != null) {
//                            match.setAlias(alias);
//                            match.setVIN(VIN);
//                            new StoredData(getApplicationContext()).addProfile(VIN, alias);
//                            sortProfiles();
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile_manager);
//
//        arrayList = getProfiles(this);
//        sortProfiles();
//
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        adapter = new CustomAdapter(arrayList);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//    }
//
//    private void sortProfiles() {
//        arrayList.sort(Comparator.comparing(Profile::getVIN));
//    }
//
//    private class CustomAdapter extends RecyclerView.Adapter<ProfileManager.ViewHolder> {
//        ArrayList<Profile> arrayList;
//
//        public CustomAdapter(ArrayList<Profile> arrayList) {
//            this.arrayList = arrayList;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//            View listItem = layoutInflater.inflate(R.layout.profiles_row, parent, false);
//            return new ViewHolder(listItem);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//            final Profile profile = arrayList.get(position);
//            holder.profileNameView.setText(profile.getProfileName());
//            holder.VINView.setText(profile.getVIN());
//            holder.delete.setOnClickListener(view -> {
//                Profile p = arrayList.get(position);
//
//                // Delete is only valid when there is a VIN defined
//                if (!p.getVIN().equals("")) {
//
//                    // If this profile matches the active VIN, clear the active VIN
//                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    String VIN = sharedPref.getString(getApplicationContext().getResources().getString(R.string.VIN_key), "");
//                    if (p.getVIN().equals(VIN)) {
//                        sharedPref.edit().putString(getApplicationContext().getResources().getString(R.string.VIN_key), "").apply();
//                    }
//
//                    // Remove thh entry
//                    new StoredData(getApplicationContext()).removeProfile(VIN);
//
//                    // Replace this list entry with a blank one
//                    arrayList.set(position, getBlankProfile());
//                    sortProfiles();
//                    adapter.notifyDataSetChanged();
//                }
//            });
//            holder.relativeLayout.setOnClickListener((i) -> {
//                Intent intent = new Intent(getApplicationContext(),
//                        LoginActivity.class);
//                String VIN = arrayList.get(position).getVIN();
//                intent.putExtra(LoginActivity.VINIDENTIFIER, VIN.equals(BLANK_VIN) ? "" : VIN);
//                intent.putExtra(LoginActivity.PROFILENAME, arrayList.get(position).getProfileName());
//                someActivityResultLauncher.launch(intent);
//            });
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return arrayList.size();
//        }
//    }
//
//    private static class ViewHolder extends RecyclerView.ViewHolder { //implements View.OnClickListener {
//        private final TextView profileNameView;
//        private final TextView VINView;
//        private final ImageView delete;
//        private final RelativeLayout relativeLayout;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            profileNameView = itemView.findViewById(R.id.alias);
//            VINView = itemView.findViewById(R.id.VIN);
//            delete = itemView.findViewById(R.id.profiledelete);
//            relativeLayout = itemView.findViewById(R.id.profile_rowlayout);
//        }
//    }
//
//    private ArrayList<Profile> getProfiles(Context context) {
//        ArrayList<Profile> profiles = new ArrayList<>();
//
//        StoredData appInfo = new StoredData(context);
//        for (String VIN : appInfo.getProfiles()) {
////            profiles.add(new Profile(VIN, appInfo.getProfileName(VIN)));
//        }
//
//        while (profiles.size() < 4) {
//            profiles.add(getBlankProfile());
//        }
//        return profiles;
//    }
//
//    private Profile getBlankProfile() {
//        return new Profile(BLANK_VIN, "");
//    }


    public static String changeProfile(Context context, String widget_VIN) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String VIN = context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).getString(widget_VIN, null);

        InfoRepository[] info = {null};

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final String userId = sharedPref.getString(context.getResources().getString(R.string.userId_key), "");
                List<VehicleInfo> vehicles = new ArrayList<>();

                // Get all vehicles owned by the user
                int index = 0;
                for (VehicleInfo tmp : info[0].getVehicles()) {
                    if (tmp.getUserId().equals(userId)) {
                        if (tmp.getVIN().equals(VIN)) {
                            index = vehicles.size();
                        }
                        vehicles.add(tmp);
                    }
                }

                // If there's more than one VIN, look through the list for the next enabled one
                if (vehicles.size() > 1) {
                    do {
                        index = (index + 1) % vehicles.size();
                    } while (!vehicles.get(index).isEnabled());
                    String newVIN = vehicles.get(index).getVIN();
                    // If the VIN is new, apply changes.
                    if (!VIN.equals(newVIN)) {
                        context.getSharedPreferences(Constants.WIDGET_FILE, Context.MODE_PRIVATE).edit().putString(widget_VIN, newVIN).commit();
                        Toast.makeText(context, vehicles.get(index).getNickname(), Toast.LENGTH_SHORT).show();
                        MainActivity.updateWidget(context);
                    }
                }
            }
        };

        new Thread(() -> {
            info[0] = new InfoRepository(context);
            handler.sendEmptyMessage(0);
        }).start();
        return VIN;
    }

    // After a successful login, make any changes necessary to the associated VINs
    public static void updateProfile(Context context, UserInfo userInfo, Map<String, String> vehicles) {
        File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
        String userId = userInfo.getUserId();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        VehicleInfoDao infoDao = VehicleInfoDatabase.getInstance(context)
                .vehicleInfoDao();
        VehicleInfo info;

        // Before getting started, find and remove any unrecognized VINs.
        HashSet<String> unknownVINs = new HashSet<>();
        for (String VIN : vehicles.keySet()) {
            if (!Utils.isVINRecognized(VIN)) {
                unknownVINs.add(VIN);
            }
        }

        // Delete any vehicles and users which are old
        for (VehicleInfo vehicle : infoDao.findVehicleInfo()) {
            if (!vehicle.getUserId().equals(userId)) {
                String VIN = vehicle.getVIN();
                infoDao.deleteVehicleInfoByVIN(VIN);
                Utils.deleteVehicleImages(context,VIN);
                UserInfoDatabase.getInstance(context).userInfoDao().deleteUserInfoByUserId(vehicle.getUserId());
            }
        }

        // Process the new VINs
        for (String VIN : vehicles.keySet()) {
            String nickname = vehicles.get(VIN);
            // If no nickname is specified, user the last 5 digits of the VIN
            if (nickname == null || nickname.equals("")) {
                nickname = VIN.substring(12);
            }

            // Get images
            String accessToken = userInfo.getAccessToken();
            String country = userInfo.getCountry();
            NetworkCalls.getVehicleImage(context, accessToken, VIN, country);

            // If the vehicle is already in the database, check the user ID to see if it was a temporary one and if so
            // delete that user
            info = infoDao.findVehicleInfoByVIN(VIN);
            if (info != null) {
                if (info.getUserId().equals(Constants.TEMP_ACCOUNT)) {
                    UserInfoDatabase.getInstance(context).userInfoDao().deleteUserInfoByUserId(info.getUserId());
                }
                // update all the other stuff
                info.setNickname(nickname);
                info.setUserId(userId);
                infoDao.updateVehicleInfo(info);
            }
            // If the vehicle is new, set the VIN and insert into the database
            else {
                info = new VehicleInfo();
                // Fill in the important fields and update
                info.setVIN(VIN);
                info.setNickname(nickname);
                info.setUserId(userId);
                infoDao.insertVehicleInfo(info);
                // Disable extra earlier vehicles which likely don't have FordPass Connect
                info.setEnabled(vehicles.size() == 1 || Utils.getModelYear(VIN) >= 2018);
            }
            LogFile.d(context, MainActivity.CHANNEL_ID, "info is " + info + ", info.userId = " + info.getUserId());
        }

        MainActivity.updateWidget(context);
    }
}
