package com.example.khughes.machewidget.db;

import androidx.room.TypeConverter;

import com.example.khughes.machewidget.OTAStatus.FuseResponseList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Converters {

    static Gson gson = new Gson();

    @TypeConverter
    public static List<FuseResponseList> stringToFuseResponseList(String data) {
        if(data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<FuseResponseList>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String fuseResponseListToString(List<FuseResponseList> list) {
        return gson.toJson(list);
    }
}
