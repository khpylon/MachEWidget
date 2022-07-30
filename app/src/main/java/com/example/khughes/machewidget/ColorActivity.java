package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ColorActivity extends AppCompatActivity {

    private static InfoRepository info;
    private static VehicleInfo mVehicleInfo;

    private ArrayList<String> arrayList;

    private int wireframeMode = Utils.WIREFRAME_WHITE;

    private ColorPickerView colorPickerView;
    private RadioGroup group;
    private Button auto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        colorPickerView = findViewById(R.id.colorPickerView);
        TextView feedback = findViewById(R.id.colorValue);

        group = findViewById(R.id.radiogroup);
        group.setOnCheckedChangeListener((radioGroup, i) -> {
            View radioButton = radioGroup.findViewById(i);
            switch (radioGroup.indexOfChild(radioButton)) {
                case 0:
                    wireframeMode = Utils.WIREFRAME_WHITE;
                    break;
                case 1:
                    wireframeMode = Utils.WIREFRAME_BLACK;
                    break;
                default:
                    wireframeMode = Utils.WIREFRAME_AUTO;
                    break;
            }
            colorPickerView.setInitialColor(colorPickerView.getColor());
        });


        Button save = findViewById(R.id.ok);
        save.setOnClickListener(view -> {
            mVehicleInfo.setColorValue((colorPickerView.getColor() & Utils.ARGB_MASK) | wireframeMode);
            info.setVehicle(mVehicleInfo);
            CarStatusWidget.updateWidget(getApplicationContext());
        });

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            setCheckedButton(mVehicleInfo.getColorValue());
            colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
        });

        auto = findViewById(R.id.auto_image);
        auto.setOnClickListener(view -> {
            int oldColor = mVehicleInfo.getColorValue();
            mVehicleInfo.setColorValue(Color.WHITE);
            if (Utils.scanImageForColor(this, mVehicleInfo)) {
                colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
            }
            mVehicleInfo.setColorValue(oldColor);
        });
        TooltipCompat.setTooltipText(auto, "Use stored image as color source.");

        colorPickerView.setColorListener((ColorListener) (color, fromUser) -> {
            feedback.setText("RGB value: #" + Integer.toHexString(color).toUpperCase(Locale.ROOT).substring(2));
            drawVehicle((color & Utils.ARGB_MASK) | wireframeMode);
        });

        BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);

        Spinner spinner = findViewById(R.id.spinner);
        arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Handler handler = new Handler(this.getMainLooper());
        new Thread(() -> {
            info = new InfoRepository(getApplicationContext());
            handler.post(() -> {
                List<VehicleInfo> vehicles = info.getVehicles();
                if (vehicles.size() == 1) {
                    spinner.setVisibility(View.GONE);
                    mVehicleInfo = info.getVehicles().get(0);
                    setCheckedButton(mVehicleInfo.getColorValue());
                    colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
                    setAutoButton(mVehicleInfo.getVIN());
                } else {
                    arrayList.clear();
                    for (VehicleInfo vehicle : vehicles) {
                        arrayList.add(vehicle.getVIN());
                    }
                    spinner.setAdapter(arrayAdapter);
                }
            });
        }).start();

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String VIN = parent.getItemAtPosition(position).toString();
                mVehicleInfo = info.getVehicleByVIN(VIN);
                setCheckedButton(mVehicleInfo.getColorValue());
                colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
                setAutoButton(mVehicleInfo.getVIN());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setAutoButton(String VIN) {
        Bitmap bmp = Utils.getVehicleImage(getApplicationContext(), VIN, 4);
        auto.setVisibility(bmp != null ? View.VISIBLE : View.GONE);
    }

    private void setCheckedButton(int color) {
        int index;
        switch (color & Utils.WIREFRAME_MASK) {
            case Utils.WIREFRAME_WHITE:
                index = 0;
                break;
            case Utils.WIREFRAME_BLACK:
                index = 1;
                break;
            default:
                index = 2;
                break;
        }
        ((RadioButton) group.getChildAt(index)).setChecked(true);
    }

    private void drawVehicle(int color) {
        if (mVehicleInfo == null) {
            return;
        }

        Map<String, Integer> vehicleImages = Utils.getVehicleDrawables_1x5(mVehicleInfo.getVIN());

        // Create base bitmap the size of the image
        Bitmap bmp = Bitmap.createBitmap(225, 100, Bitmap.Config.ARGB_8888);
        Utils.drawColoredVehicle(getApplicationContext(), bmp, color, new ArrayList<>(), true, vehicleImages);
        ImageView image = findViewById(R.id.carImage);
        image.setImageBitmap(bmp);
    }

}