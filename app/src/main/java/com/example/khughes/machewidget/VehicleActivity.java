package com.example.khughes.machewidget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class VehicleActivity extends AppCompatActivity {

    private VehicleViewModel mVehicleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VehicleListAdapter adapter = new VehicleListAdapter(new VehicleDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mVehicleViewModel = new ViewModelProvider(this).get(VehicleViewModel.class);
        mVehicleViewModel.getAllVehicles().observe(this, adapter::submitList);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Check to see if the current VIN is still enabled.  If not, then pick another VIN to be the current.
        Context context = getApplicationContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String VIN_key = context.getResources().getString(R.string.VIN_key);
        String VIN = sharedPref.getString(VIN_key, "");
        if (!VIN.equals("")) {
            String newVIN = "";
            for (VehicleIds info : mVehicleViewModel.getAllVehicles().getValue()) {
                // If this vehicle is enabled, then remember its VIN and, if it's the
                // current VIN, exit
                if (info.isEnabled()) {
                    newVIN = info.getVIN();
                    if (VIN.equals(newVIN)) {
                        return;
                    }
                }
            }
            // Change the current VIN and refresh widgets.
            sharedPref.edit().putString(VIN_key, newVIN).apply();
            CarStatusWidget.updateWidget(context);
        }
    }

    private static class VehicleViewHolder extends ViewHolder {
        private final TextView VINItemView;
        private final TextView nicknameItemView;
        private final CheckBox enabledView;
        private final ImageView imageView;

        private VehicleViewHolder(View itemView) {
            super(itemView);
            VINItemView = itemView.findViewById(R.id.VIN);
            nicknameItemView = itemView.findViewById(R.id.nickname);
            enabledView = itemView.findViewById(R.id.checkBox);
            imageView = itemView.findViewById(R.id.image);
        }

        static VehicleViewHolder create(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vehicleview_item, parent, false);
            return new VehicleViewHolder(view);
        }
    }

    public class VehicleListAdapter extends ListAdapter<VehicleIds, VehicleViewHolder> {

        private boolean changing;

        public VehicleListAdapter(@NonNull DiffUtil.ItemCallback<VehicleIds> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            VehicleViewHolder tmp = VehicleViewHolder.create(parent);
            tmp.enabledView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!changing) {
                    VehicleIds vehicle = this.getItem(tmp.getAdapterPosition());
                    String VIN = vehicle.getVIN();
                    mVehicleViewModel.setEnable(VIN, isChecked);
                    vehicle.setEnabled(isChecked);
                    notifyDataSetChanged();
                }
            });
            return tmp;
        }

        @Override
        public void onBindViewHolder(VehicleViewHolder holder, int position) {
            VehicleIds current = getItem(position);
            String VIN = current.getVIN();

            // if VIN isn't recognized, denote with a strike-through
            SpannableString text = new SpannableString(VIN);
            if (!Utils.isVINRecognized(VIN)) {
                text.setSpan(new StrikethroughSpan(), 0, VIN.length(), 0);
            }
            holder.VINItemView.setText(text);

            holder.nicknameItemView.setText(current.getNickname());
            Bitmap bmp = Utils.getRandomVehicleImage(getApplicationContext(), VIN);
            if (bmp != null) {
                holder.imageView.setImageBitmap(bmp);
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }

            int nightModeFlags = holder.itemView.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            holder.VINItemView.setTextColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#000000" : "#FFFFFF"));
            holder.nicknameItemView.setTextColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#000000" : "#FFFFFF"));
            if (position % 2 == 1) {
                holder.itemView.setBackgroundColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#FFFFFF" : "#000000"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor(nightModeFlags == Configuration.UI_MODE_NIGHT_NO ? "#F0F0F0" : "#202020"));
            }

            changing = true;
            holder.enabledView.setChecked(current.isEnabled());
            holder.enabledView.setEnabled(!current.isEnabled() || mVehicleViewModel.countEnabledVehicle() > 1);
            changing = false;

        }
    }

    public static class VehicleDiff extends DiffUtil.ItemCallback<VehicleIds> {

        @Override
        public boolean areItemsTheSame(@NonNull VehicleIds oldItem, @NonNull VehicleIds newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull VehicleIds oldItem, @NonNull VehicleIds newItem) {
            return oldItem.equals(newItem);
        }
    }

}