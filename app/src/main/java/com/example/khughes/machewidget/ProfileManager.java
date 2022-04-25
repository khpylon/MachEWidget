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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.khughes.machewidget.db.VehicleInfoDao;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class ProfileManager extends AppCompatActivity {
    private static final String BLANK_VIN = "Unused Entry";

    private ArrayList<Profile> arrayList;
    private CustomAdapter adapter;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        String VIN = data.getStringExtra(LoginActivity.VINIDENTIFIER);
                        String alias = data.getStringExtra(LoginActivity.PROFILENAME);
                        // Update the current active profile
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(getApplicationContext().getResources().getString(R.string.VIN_key), VIN).apply();

                        // Find a profile with a matching VIN
                        Profile match = arrayList.stream().filter(p -> p.getVIN().equals(VIN)).findFirst().orElse(null);
                        if (match != null) {
                            // if the alias changed, update it
                            if (!match.getProfileName().equals(alias)) {
                                match.setAlias(alias);
//                                new StoredData(getApplicationContext()).setProfileName(VIN, alias);
                                sortProfiles();
                                adapter.notifyDataSetChanged();
                            }
                            return;
                        }

                        // Create a new profile in an unused entry
                        match = arrayList.stream().filter(p -> p.getVIN().equals(BLANK_VIN)).findFirst().orElse(null);
                        if (match != null) {
                            match.setAlias(alias);
                            match.setVIN(VIN);
                            new StoredData(getApplicationContext()).addProfile(VIN, alias);
                            sortProfiles();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manager);

        arrayList = getProfiles(this);
        sortProfiles();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new CustomAdapter(arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void sortProfiles() {
        arrayList.sort(Comparator.comparing(Profile::getVIN));
    }

    private class CustomAdapter extends RecyclerView.Adapter<ProfileManager.ViewHolder> {
        ArrayList<Profile> arrayList;

        public CustomAdapter(ArrayList<Profile> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.profiles_row, parent, false);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            final Profile profile = arrayList.get(position);
            holder.profileNameView.setText(profile.getProfileName());
            holder.VINView.setText(profile.getVIN());
            holder.delete.setOnClickListener(view -> {
                Profile p = arrayList.get(position);

                // Delete is only valid when there is a VIN defined
                if (!p.getVIN().equals("")) {

                    // If this profile matches the active VIN, clear the active VIN
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String VIN = sharedPref.getString(getApplicationContext().getResources().getString(R.string.VIN_key), "");
                    if (p.getVIN().equals(VIN)) {
                        sharedPref.edit().putString(getApplicationContext().getResources().getString(R.string.VIN_key), "").apply();
                    }

                    // Remove thh entry
                    new StoredData(getApplicationContext()).removeProfile(VIN);

                    // Replace this list entry with a blank one
                    arrayList.set(position, getBlankProfile());
                    sortProfiles();
                    adapter.notifyDataSetChanged();
                }
            });
            holder.relativeLayout.setOnClickListener((i) -> {
                Intent intent = new Intent(getApplicationContext(),
                        LoginActivity.class);
                String VIN = arrayList.get(position).getVIN();
                intent.putExtra(LoginActivity.VINIDENTIFIER, VIN.equals(BLANK_VIN) ? "" : VIN);
                intent.putExtra(LoginActivity.PROFILENAME, arrayList.get(position).getProfileName());
                someActivityResultLauncher.launch(intent);
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder { //implements View.OnClickListener {
        private final TextView profileNameView;
        private final TextView VINView;
        private final ImageView delete;
        private final RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            profileNameView = itemView.findViewById(R.id.alias);
            VINView = itemView.findViewById(R.id.VIN);
            delete = itemView.findViewById(R.id.profiledelete);
            relativeLayout = itemView.findViewById(R.id.profile_rowlayout);
        }
    }

    private ArrayList<Profile> getProfiles(Context context) {
        ArrayList<Profile> profiles = new ArrayList<>();

        StoredData appInfo = new StoredData(context);
        for (String VIN : appInfo.getProfiles()) {
//            profiles.add(new Profile(VIN, appInfo.getProfileName(VIN)));
        }

        while (profiles.size() < 4) {
            profiles.add(getBlankProfile());
        }
        return profiles;
    }

    private Profile getBlankProfile() {
        return new Profile(BLANK_VIN, "");
    }

    private static int index = 0;

    public static String changeProfile(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String VIN = sharedPref.getString(context.getResources().getString(R.string.VIN_key), "");
        StoredData appInfo = new StoredData(context);
 //       ArrayList<String> profiles = appInfo.getVINs();
//
//        index = profiles.indexOf(VIN);
//        if (index >= 0) {
//            index = (index + 1) % profiles.size();
//            VIN = profiles.get(index);
//            sharedPref.edit().putString(context.getResources().getString(R.string.VIN_key), VIN).apply();
//            StatusReceiver.nextAlarm(context, 5);
//            return appInfo.getNickname(VIN);
//        }
        return null;
    }

    // After a successful login, make any changes necessary to the associated VINs
    public static void updateProfile(Context context, UserInfo userInfo, Map<String, String> vehicles) {
        StoredData appInfo = new StoredData(context);
        File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
//        Set<String> currentVINs = appInfo.getUserIdVINs(userId);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean profilesActive = prefs.getBoolean(context.getResources().getString(R.string.show_profiles_key), false);
//
//        // When profiles are active, if the current VIN isn't one of the new VINs, remove the old profile information
//        String currentVIN = prefs.getString(context.getResources().getString(R.string.VIN_key), "");
//        if ( !currentVIN.equals("") && !vehicles.containsKey(currentVIN)) {
//            String currentUserId = appInfo.getUserId(currentVIN);
//            if (!currentUserId.equals(userId)) {
//                for (String otherVIN : appInfo.getUserIdVINs(currentUserId)) {
//                    appInfo.removeVIN(otherVIN);
//                }
//                appInfo.removeTmpAccount(currentUserId);
//            }
//        }
//
        // Set the current VIN
        prefs.edit().putString(context.getResources().getString(R.string.VIN_key), vehicles.keySet().toArray(new String[0])[0]).apply();

//        // Remove any current VINs which are now missing from the new list
//        for (String VIN : currentVINs) {
//            if (!vehicles.containsKey(VIN)) {
//                appInfo.removeVIN(VIN);
//                VehicleInfoDatabase.getInstance(context)
//                        .vehicleInfoDao().deleteVehicleInfoByVIN(VIN);
//                File image = new File(imageDir, VIN + ".png");
//                if (image.exists()) {
//                    image.delete();
//                }
//            }
//        }
//
//        // Process the new VINs
        for (String VIN : vehicles.keySet()) {
            String nickname = vehicles.get(VIN);
            // If no nickname is specified, user the last 5 digits of the VIN
            if (nickname == null || nickname.equals("")) {
                nickname = VIN.substring(12);
            }
            File image = new File(imageDir, VIN + ".png");
            if (!image.exists()) {
                String accessToken = userInfo.getAccessToken();
                String country = userInfo.getCountry();
                NetworkCalls.getVehicleImage(context, accessToken, country, image.toPath());
            }
//            if (currentVINs.contains(VIN)) {
//                //  fix the user id for this VIN if it was unknown
//                String oldUserId = appInfo.getUserId(VIN);
//                if (userId.startsWith(Constants.TMP_ACCOUNT_PREFIX)) {
//                    appInfo.removeTmpAccount(oldUserId);
//                    appInfo.setUserId(VIN, userId);
//                }
//            }
//            appInfo.addVIN(VIN, nickname);
//            appInfo.setUserId(VIN, userId);

            VehicleInfoDao infoDao = VehicleInfoDatabase.getInstance(context)
                    .vehicleInfoDao();
            infoDao.deleteVehicleInfoByVIN(VIN);
            VehicleInfo info = new VehicleInfo();
            info.setVIN(VIN);
            info.setNickname(nickname);
            info.setUserId(userInfo.getUserId());
            infoDao.insertVehicleInfo(info);
        }

        // Update the user's VIN list
//        appInfo.setUserIdVINs(userId, vehicles.keySet());
    }

}
