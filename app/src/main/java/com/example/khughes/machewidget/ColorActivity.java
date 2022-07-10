package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.ColorUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

    private static final int ARGB_MASK = 0xffffff;  // only use RGB components

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        ColorPickerView colorPickerView = findViewById(R.id.colorPickerView);
        TextView feedback = findViewById(R.id.colorValue);

        Button save = findViewById(R.id.ok);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVehicleInfo.setColorValue(colorPickerView.getColor() & ARGB_MASK);
                info.setVehicle(mVehicleInfo);
                MainActivity.updateWidget(getApplicationContext());
            }
        });

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
            }
        });

        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(int color, boolean fromUser) {
                feedback.setText("RGB value: #" + Integer.toHexString(color).toUpperCase(Locale.ROOT).substring(2));
                drawVehicle(color);
            }
        });

        BrightnessSlideBar brightnessSlideBar = findViewById(R.id.brightnessSlide);
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);

        Spinner spinner = findViewById(R.id.spinner);
        arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        new Thread(() -> {
            info = new InfoRepository(this);
            List<VehicleInfo> vehicles = info.getVehicles();
            if (vehicles.size() == 1) {
                spinner.setVisibility(View.GONE);
                mVehicleInfo = info.getVehicles().get(0);
                colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
            } else {
                arrayList.clear();
                for (VehicleInfo vehicle : vehicles) {
                    arrayList.add(vehicle.getVIN());
                }
                spinner.setAdapter(arrayAdapter);
            }
        }).start();

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String VIN = parent.getItemAtPosition(position).toString();
                mVehicleInfo = info.getVehicleByVIN(VIN);
                colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void drawVehicle(int color) {
        if (mVehicleInfo == null) {
            return;
        }

        Bitmap bmp = Bitmap.createBitmap(225, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        Map<String, Integer> vehicleImages = Utils.getVehicleDrawables_1x5(mVehicleInfo.getVIN());

        // Set the color mask
        Paint paint = new Paint();
        paint.setColor(color);

        // Set the alpha based on whether something is open
        paint.setAlpha(0xff);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

        // Draw the primary body in color
        Drawable drawable = AppCompatResources.getDrawable(this, vehicleImages.get(Utils.BODY_PRIMARY));
        Bitmap car = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(car);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas2);
        canvas.drawBitmap(car, 0, 0, paint);

        // If secondary colors exist, add them
        Drawable icon;
        Integer secondary = vehicleImages.get((Utils.BODY_SECONDARY));
        if (secondary != null) {
            icon = AppCompatResources.getDrawable(this, secondary);
            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            icon.draw(canvas);
        }

        // Figure out whether wireframe should be drawn light or dark
//        float[] hsl = new float[3];
//        ColorUtils.colorToHSL(color & ARGB_MASK, hsl);

        // Finally, draw the wireframe and set the image
        icon = AppCompatResources.getDrawable(this, vehicleImages.get(Utils.WIREFRAME));

        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);

        ImageView image = findViewById(R.id.carImage);
        image.setImageBitmap(bmp);

    }
}