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
import android.graphics.BitmapFactory;
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
        if(!VIN.equals("")) {
            String newVIN = "";
            for(VehicleIds info: mVehicleViewModel.getAllVehicles().getValue()) {
                // If this vehicle is enabled, then remember its VIN and, if it's the
                // current VIN, exit
                if(info.isEnabled()) {
                    newVIN = info.getVIN();
                    if (VIN.equals(newVIN)) {
                        return;
                    }
                }
            }
            // Change the current VIN and refresh widgets.
            sharedPref.edit().putString(VIN_key, newVIN).apply();
            MainActivity.updateWidget(context);
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

        public VehicleListAdapter(@NonNull DiffUtil.ItemCallback<VehicleIds> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return VehicleViewHolder.create(parent);
        }

        @Override
        public void onBindViewHolder(VehicleViewHolder holder, int position) {
            VehicleIds current = getItem(position);
            String VIN = current.getVIN();

            // if VIN isn't recognized, denote with a strike-through
            SpannableString text = new SpannableString(VIN);
            if(!Utils.isVINRecognized(VIN)) {
                text.setSpan(new StrikethroughSpan(), 0, VIN.length(), 0);
            }
            holder.VINItemView.setText(text);

            holder.nicknameItemView.setText(current.getNickname());
            holder.enabledView.setChecked(current.isEnabled());
            File imageDir = new File(getApplicationContext().getDataDir(), Constants.IMAGES_FOLDER);
            File image = new File(imageDir, VIN + ".png");
            if(image.exists()) {
                holder.imageView.setImageBitmap( BitmapFactory.decodeFile(image.getPath()));
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }

            holder.enabledView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (mVehicleViewModel.countEnabledVehicle() > 1 || isChecked) {
                    mVehicleViewModel.setEnable(VIN, isChecked);
                } else {
                    buttonView.setChecked(true);
                    Toast.makeText(getApplicationContext(),"At least one vehicle must be enabled.",Toast.LENGTH_SHORT).show();
                }
            });
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