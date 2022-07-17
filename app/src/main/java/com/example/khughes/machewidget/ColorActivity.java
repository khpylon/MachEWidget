package com.example.khughes.machewidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.File;
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
            MainActivity.updateWidget(getApplicationContext());
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
        colorPickerView.getPreferenceName();

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
                    setImageThing(mVehicleInfo.getVIN());
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
                setImageThing(VIN);
                colorPickerView.setInitialColor(mVehicleInfo.getColorValue());
                setAutoButton(mVehicleInfo.getVIN());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setImageThing(String VIN) {
//        File imageDir = new File(getApplicationContext().getDataDir(), Constants.IMAGES_FOLDER);
//        File image = new File(imageDir, VIN + ".png");
//        if (image.exists() ) {
//            Bitmap bmp = BitmapFactory.decodeFile(image.getPath());
//            Drawable drawable = new BitmapDrawable(getResources(), bmp);
//            colorPickerView.setPaletteDrawable(drawable);
//            return;
//        }
        colorPickerView.setHsvPaletteDrawable();
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

//        Canvas canvas = new Canvas(bmp);
//
//        Drawable drawable = AppCompatResources.getDrawable(this, vehicleImages.get(Utils.BODY_PRIMARY));
//        Bitmap bmp2 = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas2 = new Canvas(bmp2);
//
//        Paint paint = new Paint();
//        // Fill with the primary color mask
//        paint.setColor(color);
//        paint.setAlpha(0xff);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawPaint(paint);
//
//        // Draw the primary body in color
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas2);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
//        canvas.drawBitmap(bmp2, 0, 0, paint);
//
//        // If secondary colors exist, add them
//        Drawable icon;
//        Integer secondary = vehicleImages.get((Utils.BODY_SECONDARY));
//        if (secondary != null) {
//            icon = AppCompatResources.getDrawable(this, secondary);
//            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//            icon.draw(canvas);
//        }
//
//        // Create a second bitmap the same size as the primary
//        bmp2 = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//        canvas2 = new Canvas(bmp2);
//
//        // Figure out whether wireframe should be drawn light or dark
//        float[] hsl = new float[3];
//        ColorUtils.colorToHSL(color & ARGB_MASK, hsl);
//        paint.setColor(hsl[2] > 0.5 ? Color.BLACK : Color.WHITE);
//        paint.setAlpha(0xff);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//
//        // Fill with a contrasting color
//        paint.setStyle(Paint.Style.FILL);
//        canvas2.drawPaint(paint);
//
//        // Draw the wireframe body
//        drawable = AppCompatResources.getDrawable(this, vehicleImages.get(Utils.WIREFRAME));
//        Bitmap bmp3 = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas3 = new Canvas(bmp3);
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas3);
//
//        // Set the wireframe's color
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
//        canvas2.drawBitmap(bmp3, 0, 0, paint);
//
//        // Draw wireframe over the colored body
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
//        canvas.drawBitmap(bmp2, 0, 0, paint);

        ImageView image = findViewById(R.id.carImage);
        image.setImageBitmap(bmp);
    }

}