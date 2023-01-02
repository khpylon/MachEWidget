package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager extends AppCompatActivity {

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
                        CarStatusWidget.updateWidget(context);
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
}
