package com.example.khughes.machewidget;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.icu.text.MessageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.db.UserInfoDatabase;
import com.example.khughes.machewidget.db.VehicleInfoDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;

public class Utils {

    public static final int WORLD_MANUFACTURING_IDENTIFIER_START_INDEX = 1 - 1;
    public static final int WORLD_MANUFACTURING_IDENTIFIER_END_INDEX = 3;
    public static final String WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV = "3FM";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_GERMANY = "WF0";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK = "1FT";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_USA_MPV = "1FM";

    public static final int NA_LINE_SERIES_START_INDEX = 5 - 1;
    public static final int NA_LINE_SERIES_END_INDEX = 7;

    public static final String NA_LINE_SERIES_MACHE_SELECT_RWD = "K1R"; // select RWD
    public static final String NA_LINE_SERIES_MACHE_SELECT_AWD = "K1S"; // select RWD (AWD?
    public static final String NA_LINE_SERIES_MACHE_CAROUTE1_RWD = "K2R"; // Route 1 RWD
    public static final String NA_LINE_SERIES_MACHE_PREMIUM_RWD = "K3R"; // Premium RWD
    public static final String NA_LINE_SERIES_MACHE_PREMIUM_AWD = "K3S"; // Premium AWD?
    public static final String NA_LINE_SERIES_MACHE_GT_RWD = "K4S"; // GT AWD

    public static final String NA_LINE_SERIES_F150_REGULAR_4X2 = "F1C"; // 4x2 chassis, regular cab
    public static final String NA_LINE_SERIES_F150_REGULAR_4X4 = "F1E"; // 4x4 chassis, regular cab
    public static final String NA_LINE_SERIES_F150_SUPERCREW_4X2 = "W1C"; // 4x2, SuperCrew
    public static final String NA_LINE_SERIES_F150_SUPERCREW_4X4 = "W1E"; // 4x4, superCrew
    public static final String NA_LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR = "W1R"; // 4x4, SuperCrew, Raptor
    public static final String NA_LINE_SERIES_F150_SUPERCREW_4X4_POLICE = "W1P"; // 4x4, SuperCrew, Police
    public static final String NA_LINE_SERIES_F150_SUPERCREW_4X2_SSV = "W1S"; // 4x2, SuperCrew, SSV (Special Service Vehicle), government
    public static final String NA_LINE_SERIES_F150_SUPERCREW_4X4_SSV = "W1T"; // 4x4, superCrew, SSV (Special Service Vehicle), government
    public static final String NA_LINE_SERIES_F150_SUPERCAB_4X2 = "X1C"; // 4x2, SuperCab
    public static final String NA_LINE_SERIES_F150_SUPERCAB_4X4 = "X1E"; // 4x4, SuperCab

    public static final String NA_LINE_SERIES_BRONCO_BASE_2DOOR_4X4 = "E5A"; //
    public static final String NA_LINE_SERIES_BRONCO_BASE_4DOOR_4X4 = "E5B"; //
    public static final String NA_LINE_SERIES_BRONCO_BASE_2DOOR_AWD = "E5C"; //
    public static final String NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD = "E5D"; //
    public static final String NA_LINE_SERIES_BRONCO_FE_4DOOR_AWD = "E5E"; //
    public static final String NA_LINE_SERIES_BRONCO_FE_2DOOR_AWD = "E5F"; //
    public static final String NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD_RAPTOR = "E5J"; //

    public static final String NA_LINE_SERIES_BRONCOSPORT_BASE_4x4 = "R9A";
    public static final String NA_LINE_SERIES_BRONCOSPORT_BIGBEND_4x4 = "R9B";
    public static final String NA_LINE_SERIES_BRONCOSPORT_OUTERBANKS_4x4 = "R9C";
    public static final String NA_LINE_SERIES_BRONCOSPORT_BADLANDS_4x4 = "R9D";
    public static final String NA_LINE_SERIES_BRONCOSPORT_WILDTRAK_4x4 = "R9E";

    public static final String NA_LINE_SERIES_EXPLORER_BASE_RWD = "K7B";
    public static final String NA_LINE_SERIES_EXPLORER_XLT_RWD = "K7D";
    public static final String NA_LINE_SERIES_EXPLORER_LIMITED_RWD = "K7F";
    public static final String NA_LINE_SERIES_EXPLORER_PLATINUM_RWD = "K7H";
    public static final String NA_LINE_SERIES_EXPLORER_KING_RWD = "K7L";
    public static final String NA_LINE_SERIES_EXPLORER_ST_RWD = "K7G";
    public static final String NA_LINE_SERIES_EXPLORER_STLINE_RWD = "K7K";
    public static final String NA_LINE_SERIES_EXPLORER_POLICE = "K8A";
    public static final String NA_LINE_SERIES_EXPLORER_BASE_4WD = "K8B";
    public static final String NA_LINE_SERIES_EXPLORER_XLT_4WD = "K8D";
    public static final String NA_LINE_SERIES_EXPLORER_LIMITED_4WD = "K8F";
    public static final String NA_LINE_SERIES_EXPLORER_ST_4WD = "K8G";
    public static final String NA_LINE_SERIES_EXPLORER_PLATINUM_4WD = "K8H";
    public static final String NA_LINE_SERIES_EXPLORER_KING_4WD = "K8L";
    public static final String NA_LINE_SERIES_EXPLORER_STLINE_4WD = "K8K";
    public static final String NA_LINE_SERIES_EXPLORER_TIMBERLINE_4WD = "K8J";

    public static final String NA_LINE_SERIES_ESCAPE_S_RWD = "U0F";
    public static final String NA_LINE_SERIES_ESCAPE_SE_RWD = "U0G";
    public static final String NA_LINE_SERIES_ESCAPE_SEL_RWD = "U0H";
    public static final String NA_LINE_SERIES_ESCAPE_SE_FHEV_RWD = "U0B";
    public static final String NA_LINE_SERIES_ESCAPE_SEL_FHEV_RWD = "U0C";
    public static final String NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_RWD = "U0D";
    public static final String NA_LINE_SERIES_ESCAPE_SE_PHEV_RWD = "U0E";
    public static final String NA_LINE_SERIES_ESCAPE_SEL_PHEV_RWD = "U0K";
    public static final String NA_LINE_SERIES_ESCAPE_TITANIUM_PHEV_RWD = "U0L";
    public static final String NA_LINE_SERIES_ESCAPE_S_4WD = "U9F";
    public static final String NA_LINE_SERIES_ESCAPE_SE_4WD = "U9G";
    public static final String NA_LINE_SERIES_ESCAPE_SEL_4WD = "U9H";
    public static final String NA_LINE_SERIES_ESCAPE_TITANIUM_4WD = "U9J";
    public static final String NA_LINE_SERIES_ESCAPE_SE_FHEV_4WD = "U9B";
    public static final String NA_LINE_SERIES_ESCAPE_SEL_FHEV_4WD = "U9C";
    public static final String NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_4WD = "U9D";

    public static final String NA_LINE_SERIES_EDGE_ST_AWD = "K4A";
    public static final String NA_LINE_SERIES_EDGE_SE_AWD = "K4G";
    public static final String NA_LINE_SERIES_EDGE_SEL_AWD = "K4J";
    public static final String NA_LINE_SERIES_EDGE_TITANIUM_AWD = "K4K";

    public static final String NA_LINE_SERIES_EXPEDITION_MAX_XL_4x2 = "K1F";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_XL_4x4 = "K1G";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x2 = "K1H";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x4 = "K1J";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x2 = "K1N";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x4 = "K1P";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x2 = "K1K";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x4 = "K2A";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x2 = "K1L";
    public static final String NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x4 = "K1M";
    public static final String NA_LINE_SERIES_EXPEDITION_XL_4x2 = "U1F";
    public static final String NA_LINE_SERIES_EXPEDITION_XL_4x4 = "U1G";
    public static final String NA_LINE_SERIES_EXPEDITION_XLT_4x2 = "U1H";
    public static final String NA_LINE_SERIES_EXPEDITION_XLT_4x4 = "U1J";
    public static final String NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x2 = "U1N";
    public static final String NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x4 = "U1P";
    public static final String NA_LINE_SERIES_EXPEDITION_LIMITED_4x2 = "U1K";
    public static final String NA_LINE_SERIES_EXPEDITION_LIMITED_4x4 = "U2A";
    public static final String NA_LINE_SERIES_EXPEDITION_PLATINUM_4x2 = "U1L";
    public static final String NA_LINE_SERIES_EXPEDITION_PLATINUM_4x4 = "U1M";
    public static final String NA_LINE_SERIES_EXPEDITION_TIMBERLINE_4x4 = "U1R";

    public static final int EURO_LINE_SERIES_START_INDEX = 7 - 1;
    public static final int EURO_LINE_SERIES_END_INDEX = 9;

    public static final String EURO_LINE_SERIES_KUGA = "WPM";
    public static final String EURO_LINE_SERIES_PUMA = "ERK";

    private static final Set<String> macheLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_MACHE_SELECT_RWD);
        tmpSet.add(NA_LINE_SERIES_MACHE_SELECT_AWD);
        tmpSet.add(NA_LINE_SERIES_MACHE_CAROUTE1_RWD);
        tmpSet.add(NA_LINE_SERIES_MACHE_PREMIUM_RWD);
        tmpSet.add(NA_LINE_SERIES_MACHE_PREMIUM_AWD);
        tmpSet.add(NA_LINE_SERIES_MACHE_GT_RWD);
        macheLineSeries = tmpSet;
    }

    public static boolean isMachE(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return (WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_GERMANY) ||
                WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV)) && macheLineSeries.contains(lineSeries);
    }

    private static final Set<String> f150RegularCabsLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_F150_REGULAR_4X2);
        tmpSet.add(NA_LINE_SERIES_F150_REGULAR_4X4);
        f150RegularCabsLineSeries = tmpSet;
    }

    public static boolean isF150RegularCab(String VIN) {
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return f150RegularCabsLineSeries.contains(lineSeries);
    }

    private static final Set<String> f150SuperCabsLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_F150_SUPERCAB_4X2);
        tmpSet.add(NA_LINE_SERIES_F150_SUPERCAB_4X4);
        f150SuperCabsLineSeries = tmpSet;
    }

    public static boolean isF150SuperCab(String VIN) {
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return f150SuperCabsLineSeries.contains(lineSeries);
    }

    private static final Set<String> f150SuperCrewsLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_F150_SUPERCREW_4X2);
        tmpSet.add(NA_LINE_SERIES_F150_SUPERCREW_4X4);
        tmpSet.add(NA_LINE_SERIES_F150_SUPERCREW_4X4_POLICE);
        tmpSet.add(NA_LINE_SERIES_F150_SUPERCREW_4X2_SSV);
        tmpSet.add(NA_LINE_SERIES_F150_SUPERCREW_4X4_SSV);
        f150SuperCrewsLineSeries = tmpSet;
    }

    public static boolean isF150SuperCrew(String VIN) {
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return f150SuperCrewsLineSeries.contains(lineSeries);
    }

    public static boolean isF150Raptor(String VIN) {
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return lineSeries.equals(NA_LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR);
    }

    public static boolean isF150(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK) &&
                (isF150RegularCab(VIN) || isF150SuperCab(VIN) || isF150SuperCrew(VIN) || isF150Raptor(VIN));
    }

    private static final Set<String> explorerLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_EXPLORER_BASE_RWD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_XLT_RWD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_LIMITED_RWD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_PLATINUM_RWD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_KING_RWD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_ST_RWD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_STLINE_RWD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_POLICE);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_BASE_4WD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_XLT_4WD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_LIMITED_4WD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_ST_4WD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_PLATINUM_4WD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_KING_4WD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_STLINE_4WD);
        tmpSet.add(NA_LINE_SERIES_EXPLORER_TIMBERLINE_4WD);
        explorerLineSeries = tmpSet;
    }

    public static boolean isExplorer(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && explorerLineSeries.contains(lineSeries);
    }

    private static final Set<String> broncoLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_BRONCO_BASE_2DOOR_4X4);
        tmpSet.add(NA_LINE_SERIES_BRONCO_BASE_4DOOR_4X4);
        tmpSet.add(NA_LINE_SERIES_BRONCO_BASE_2DOOR_AWD);
        tmpSet.add(NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD);
        tmpSet.add(NA_LINE_SERIES_BRONCO_FE_4DOOR_AWD);
        tmpSet.add(NA_LINE_SERIES_BRONCO_FE_2DOOR_AWD);
        tmpSet.add(NA_LINE_SERIES_BRONCO_BASE_4DOOR_AWD_RAPTOR);
        broncoLineSeries = tmpSet;
    }

    public static boolean isBronco(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && broncoLineSeries.contains(lineSeries);
    }

    private static final Set<String> broncoSportLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_BRONCOSPORT_BASE_4x4);
        tmpSet.add(NA_LINE_SERIES_BRONCOSPORT_BIGBEND_4x4);
        tmpSet.add(NA_LINE_SERIES_BRONCOSPORT_OUTERBANKS_4x4);
        tmpSet.add(NA_LINE_SERIES_BRONCOSPORT_BADLANDS_4x4);
        tmpSet.add(NA_LINE_SERIES_BRONCOSPORT_WILDTRAK_4x4);
        broncoSportLineSeries = tmpSet;
    }

    public static boolean isBroncoSport(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && broncoSportLineSeries.contains(lineSeries);
    }

    private static final Set<String> escapeLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_ESCAPE_S_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SE_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SEL_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SE_FHEV_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SEL_FHEV_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SE_PHEV_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SEL_PHEV_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_TITANIUM_PHEV_RWD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_S_4WD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SE_4WD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SEL_4WD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_TITANIUM_4WD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SE_FHEV_4WD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_SEL_FHEV_4WD);
        tmpSet.add(NA_LINE_SERIES_ESCAPE_TITANIUM_FHEV_4WD);
        escapeLineSeries = tmpSet;
    }

    public static boolean isEscape(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && escapeLineSeries.contains(lineSeries);
    }

    private static final Set<String> edgeLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_EDGE_ST_AWD);
        tmpSet.add(NA_LINE_SERIES_EDGE_SE_AWD);
        tmpSet.add(NA_LINE_SERIES_EDGE_SEL_AWD);
        tmpSet.add(NA_LINE_SERIES_EDGE_TITANIUM_AWD);
        edgeLineSeries = tmpSet;
    }

    public static boolean isEdge(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && edgeLineSeries.contains(lineSeries);
    }

    private static final Set<String> expeditionLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_XL_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_XL_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_XLT_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_KINGRANCH_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_LIMITED_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_MAX_PLATINUM_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_XL_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_XL_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_XLT_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_XLT_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_KINGRANCH_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_LIMITED_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_LIMITED_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_PLATINUM_4x2);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_PLATINUM_4x4);
        tmpSet.add(NA_LINE_SERIES_EXPEDITION_TIMBERLINE_4x4);
        expeditionLineSeries = tmpSet;
    }

    public static boolean isExpedition(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(NA_LINE_SERIES_START_INDEX, NA_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && expeditionLineSeries.contains(lineSeries);
    }

    public static boolean isKuga(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(EURO_LINE_SERIES_START_INDEX, EURO_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_GERMANY) && lineSeries.equals(EURO_LINE_SERIES_KUGA);
    }

    public static boolean isPuma(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(EURO_LINE_SERIES_START_INDEX, EURO_LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_GERMANY) && lineSeries.equals(EURO_LINE_SERIES_PUMA);
    }

    // Check to see if we recognize a VIN in general
    public static boolean isVINRecognized(String VIN) {
        return isMachE(VIN) || isF150(VIN) || isBronco(VIN) || isExplorer(VIN) || isBroncoSport(VIN)
                || isEscape(VIN) || isEdge(VIN) || isExpedition(VIN)
                || isKuga(VIN) || isPuma(VIN);
    }

    public static final int NA_FUEL_TYPE_START_INDEX = 8 - 1;
    public static final int NA_FUEL_TYPE_END_INDEX = 8;

    public static final String NA_HYBRID_TRUCK_2_5_LITER = "3";
    public static final String NA_ELEC_TRUCK_EXT_BATT_REAR_MOTOR = "7";
    public static final String NA_HYBRID_TRUCK_3_5_LITER = "D";
    public static final String NA_ELEC_TRUCK_EXT_BATT_DUAL_LIMITED_MOTOR = "E";
    public static final String NA_ELEC_TRUCK_STD_BATT_DUAL_MOTOR = "L";
    public static final String NA_ELEC_TRUCK_STD_BATT_REAR_MOTOR = "M";
    public static final String NA_ELEC_TRUCK_STD_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "S";
    public static final String NA_ELEC_TRUCK_EXT_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "U";
    public static final String NA_ELEC_TRUCK_EXT_BATT_DUAL_MOTOR = "V";
    public static final String NA_ELEC_TRUCK_EXT_BATT_DUAL_LARGER_SECONDARY_MOTOR = "X";
    public static final String NA_HYBRID_TRUCK_3_3_LITER = "W";
    public static final String NA_PHEV_TRUCK_3_0_LITER = "Y";
    public static final String NA_PHEV_TRUCK_2_5_LITER = "Z";
//    public static final String NA_ELEC_CAR_EXT_BATT_REAR_MOTOR = "7";
//    public static final String NA_ELEC_CAR_EXT_BATT_DUAL_LIMITED_MOTOR = "E";
//    public static final String NA_ELEC_CAR_STD_BATT_REAR_MOTOR = "M";
//    public static final String NA_ELEC_CAR_STD_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "S";
//    public static final String NA_ELEC_CAR_EXT_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "U";
//    public static final String NA_ELEC_CAR_EXT_BATT_DUAL_LARGER_SECONDARY_MOTOR = "X";

    public static final int EURO_FUEL_TYPE_START_INDEX = 10 - 1;
    public static final int EURO_FUEL_TYPE_END_INDEX = 10;

    public static final String EURO_PHEV = "H";

    public static final int FUEL_UNKNOWN = 0;
    public static final int FUEL_GAS = FUEL_UNKNOWN + 1;
    public static final int FUEL_HYBRID = FUEL_GAS + 1;
    public static final int FUEL_PHEV = FUEL_HYBRID + 1;
    public static final int FUEL_ELECTRIC = FUEL_PHEV + 1;

    private static final Set<String> na_fuelElectric;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_ELEC_TRUCK_STD_BATT_DUAL_MOTOR);
        tmpSet.add(NA_ELEC_TRUCK_EXT_BATT_REAR_MOTOR);
        tmpSet.add(NA_ELEC_TRUCK_EXT_BATT_DUAL_LIMITED_MOTOR);
        tmpSet.add(NA_ELEC_TRUCK_STD_BATT_REAR_MOTOR);
        tmpSet.add(NA_ELEC_TRUCK_STD_BATT_DUAL_SMALLER_SECONDARY_MOTOR);
        tmpSet.add(NA_ELEC_TRUCK_EXT_BATT_DUAL_SMALLER_SECONDARY_MOTOR);
        tmpSet.add(NA_ELEC_TRUCK_EXT_BATT_DUAL_MOTOR);
        tmpSet.add(NA_ELEC_TRUCK_EXT_BATT_DUAL_LARGER_SECONDARY_MOTOR);
        na_fuelElectric = tmpSet;
    }

    private static final Set<String> na_fuelHybrid;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_HYBRID_TRUCK_2_5_LITER);
        tmpSet.add(NA_HYBRID_TRUCK_3_3_LITER);
        tmpSet.add(NA_HYBRID_TRUCK_3_5_LITER);
        na_fuelHybrid = tmpSet;
    }

    private static final Set<String> na_fuelPHEV;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(NA_PHEV_TRUCK_2_5_LITER);
        tmpSet.add(NA_PHEV_TRUCK_3_0_LITER);
        na_fuelPHEV = tmpSet;
    }

    private static final Set<String> euro_fuelPHEV;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(EURO_PHEV);
        euro_fuelPHEV = tmpSet;
    }

    public static int getFuelType(String VIN) {
        // Default is a Mach-E
        if (VIN == null || VIN.equals("") || isMachE(VIN)) {
            return FUEL_ELECTRIC;
        }
        // Otherwise check the VIN
        else if (isF150(VIN) || isBronco(VIN) || isBroncoSport(VIN) || isExplorer(VIN)
                || isEscape(VIN) || isEdge(VIN) || isExpedition(VIN)) {
            String fuelType = VIN.substring(NA_FUEL_TYPE_START_INDEX, NA_FUEL_TYPE_END_INDEX);
            if (na_fuelElectric.contains(fuelType)) {
                return FUEL_ELECTRIC;
            } else if (na_fuelHybrid.contains(fuelType)) {
                return FUEL_HYBRID;
            } else if (na_fuelPHEV.contains(fuelType)) {
                return FUEL_PHEV;
            } else {
                return FUEL_GAS;
            }
        } else if (isKuga(VIN) || isPuma(VIN)) {
            String fuelType = VIN.substring(EURO_FUEL_TYPE_START_INDEX, EURO_FUEL_TYPE_END_INDEX);
            if (euro_fuelPHEV.contains(fuelType)) {
                return FUEL_PHEV;
            } else {
                return FUEL_GAS;
            }
        } else {
            return FUEL_ELECTRIC;
        }
    }

    public static final String WIREFRAME = "wireframe";
    public static final String HOOD = "hood";
    public static final String TAILGATE = "tailgate_open";
    public static final String LEFT_FRONT_DOOR = "lfdoor_open";
    public static final String RIGHT_FRONT_DOOR = "rfdoor_open";
    public static final String LEFT_REAR_DOOR = "lrdoor_open";
    public static final String RIGHT_REAR_DOOR = "rrdoor_open";
    public static final String LEFT_FRONT_WINDOW = "lfwindow_open";
    public static final String RIGHT_FRONT_WINDOW = "rfwindow_open";
    public static final String LEFT_REAR_WINDOW = "lrwindow_open";
    public static final String RIGHT_REAR_WINDOW = "rrwindow_open";
    public static final String BODY_PRIMARY = "body1st";
    public static final String BODY_SECONDARY = "body2nd";

    // Drawables for original widget

    // Drawables for Mach-E
    private static final Map<String, Integer> macheDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.mache_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.mache_frunk_vert);
        tmpMap.put(TAILGATE, R.drawable.mache_hatch_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.mache_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.mache_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.mache_lrdoor_vert);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.mache_rrdoor_vert);
        tmpMap.put(BODY_PRIMARY, R.drawable.mache_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.mache_secondary_vert);
        macheDrawables = tmpMap;
    }

    // Drawables for Regular Cab (two door) F-150
    private static final Map<String, Integer> regcabDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.regularcab_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.regularcab_hood_vert);
        tmpMap.put(TAILGATE, R.drawable.regularcab_tailgate_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.regularcab_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.regularcab_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.filler);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.filler);
        tmpMap.put(BODY_PRIMARY, R.drawable.regularcab_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.regularcab_secondary_vert);
        regcabDrawables = tmpMap;
    }

    // Drawables for SuperCab F-150
    private static final Map<String, Integer> supercabDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.supercab_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.supercab_hood_vert);
        tmpMap.put(TAILGATE, R.drawable.supercab_tailgate_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.supercab_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.supercab_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.supercab_lrdoor_vert);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.supercab_rrdoor_vert);
        tmpMap.put(BODY_PRIMARY, R.drawable.supercab_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.supercab_secondary_vert);
        supercabDrawables = tmpMap;
    }

    // Drawables for SuperCrew F-150
    private static final Map<String, Integer> supercrewDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.supercrew_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.supercrew_hood_vert);
        tmpMap.put(TAILGATE, R.drawable.supercrew_tailgate_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.supercrew_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.supercrew_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.supercrew_lrdoor_vert);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.supercrew_rrdoor_vert);
        tmpMap.put(BODY_PRIMARY, R.drawable.supercrew_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.supercrew_secondary_vert);
        supercrewDrawables = tmpMap;
    }

    // Drawables for F-150 Raptor
    private static final Map<String, Integer> raptorDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.raptor_wireframe);
        tmpMap.put(HOOD, R.drawable.raptor_hood);
        tmpMap.put(TAILGATE, R.drawable.raptor_tailgate);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.raptor_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.raptor_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.raptor_lrdoor);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.raptor_rrdoor);
        raptorDrawables = tmpMap;
    }

    // Drawables for Bronco Base 4x4
    private static final Map<String, Integer> broncobase4x4Drawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.bronco_base_4x4_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.bronco_base_4x4_hood_vert);
        tmpMap.put(TAILGATE, R.drawable.bronco_base_4x4_tailgate_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.bronco_base_4x4_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.bronco_base_4x4_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.bronco_base_4x4_lrdoor_vert);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.bronco_base_4x4_rrdoor_vert);
        tmpMap.put(BODY_PRIMARY, R.drawable.bronco_base_4x4_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.bronco_base_4x4_secondary_vert);
        broncobase4x4Drawables = tmpMap;
    }

    // Drawables for Explorer ST
    private static final Map<String, Integer> explorerSTDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.explorer_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.explorer_hood_vert);
        tmpMap.put(TAILGATE, R.drawable.explorer_tailgate_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.explorer_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.explorer_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.explorer_lrdoor_vert);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.explorer_rrdoor_vert);
        tmpMap.put(BODY_PRIMARY, R.drawable.explorer_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.explorer_secondary_vert);
        explorerSTDrawables = tmpMap;
    }

    // Drawables for Escape
    private static final Map<String, Integer> escapeDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.escape_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.escape_hood_vert);
        tmpMap.put(TAILGATE, R.drawable.escape_hatch_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.escape_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.escape_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.escape_lrdoor_vert);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.escape_rrdoor_vert);
        tmpMap.put(BODY_PRIMARY, R.drawable.escape_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.escape_secondary_vert);
        escapeDrawables = tmpMap;
    }

    // Drawables for Edge
    private static final Map<String, Integer> edgeDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.edge_wireframe_vert);
        tmpMap.put(HOOD, R.drawable.edge_hood_vert);
        tmpMap.put(TAILGATE, R.drawable.edge_liftgate_vert);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.edge_lfdoor_vert);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.edge_rfdoor_vert);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.edge_lrdoor_vert);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.edge_rrdoor_vert);
        tmpMap.put(BODY_PRIMARY, R.drawable.edge_primary_vert);
        tmpMap.put(BODY_SECONDARY, R.drawable.edge_secondary_vert);
        edgeDrawables = tmpMap;
    }

    // Get the set of drawables for a particular style of vehicle
    public static Map<String, Integer> getVehicleDrawables(String VIN) {
        if (VIN != null && !VIN.equals("")) {
            if (isF150(VIN)) {
                if (isF150RegularCab(VIN)) {
                    return regcabDrawables;
                } else if (isF150SuperCab(VIN)) {
                    return supercabDrawables;
                } else if (isF150SuperCrew(VIN)) {
                    return supercrewDrawables;
                } else if (isF150Raptor(VIN)) {
                    return raptorDrawables;
                }
            } else if (isBronco(VIN) || isBroncoSport(VIN)) {
                return broncobase4x4Drawables;
            } else if (isExplorer(VIN)) {
                return explorerSTDrawables;
            } else if (isEscape(VIN) || isKuga(VIN) || isPuma(VIN)) {
                return escapeDrawables;
            } else if (isEdge(VIN)) {
                return edgeDrawables;
            } else if (isExpedition(VIN)) {
                return explorerSTDrawables;
            }
        }
        return macheDrawables;
    }

    public static Integer getLayoutByVIN(String VIN) {
        if (VIN != null && !VIN.equals("")) {
            if (isF150(VIN)) {
                return R.layout.f150_widget;
            } else if (isBronco(VIN) || isBroncoSport(VIN)) {
                return R.layout.bronco_widget;
            } else if (isExplorer(VIN)) {
                return R.layout.explorer_widget;
            } else if (isEscape(VIN) || isKuga(VIN) || isPuma(VIN)) {
                return R.layout.escape_widget;
            } else if (isEdge(VIN)) {
                return R.layout.edge_widget;
            } else if (isExpedition(VIN)) {
                return R.layout.explorer_widget;
            }
        }
        return R.layout.mache_widget;
    }

    // Drawables for 1x5 widget

    // Drawables for Mach-E
    private static final Map<String, Integer> macheDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.mache_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.mache_frunk_horz);
        tmpMap.put(TAILGATE, R.drawable.mache_hatch_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.mache_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.mache_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.mache_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.mache_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.mache_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.mache_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.mache_lrwindow_horz);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.mache_rrwindow_horz);
        tmpMap.put(BODY_PRIMARY, R.drawable.mache_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.mache_secondary_horz);
        macheDrawables_1x5 = tmpMap;
    }

    // Drawables for Regular Cab (two door) F-150
    private static final Map<String, Integer> regcabDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.regularcab_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.regularcab_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.regularcab_tailgate_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.regularcab_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.regularcab_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.filler);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.filler);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.regularcab_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.regularcab_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.filler);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.filler);
        tmpMap.put(BODY_PRIMARY, R.drawable.regularcab_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.regularcab_secondary_horz);
        regcabDrawables_1x5 = tmpMap;
    }

    // Drawables for SuperCab F-150
    private static final Map<String, Integer> supercabDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.supercab_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.supercab_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.supercab_tailgate_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.supercab_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.supercab_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.supercab_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.supercab_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.supercab_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.supercab_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.filler);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.filler);
        tmpMap.put(BODY_PRIMARY, R.drawable.supercab_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.supercab_secondary_horz);
        supercabDrawables_1x5 = tmpMap;
    }

    // Drawables for SuperCrew F-150
    private static final Map<String, Integer> supercrewDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.supercrew_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.supercrew_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.supercrew_tailgate_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.supercrew_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.supercrew_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.supercrew_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.supercrew_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.supercrew_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.supercrew_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.supercrew_lrwindow_horz);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.supercrew_rrwindow_horz);
        tmpMap.put(BODY_PRIMARY, R.drawable.supercrew_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.supercrew_secondary_horz);
        supercrewDrawables_1x5 = tmpMap;
    }

    // Drawables for F-150 Raptor
    private static final Map<String, Integer> raptorDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.raptor_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.raptor_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.raptor_tailgate_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.raptor_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.raptor_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.raptor_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.raptor_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.raptor_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.raptor_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.raptor_lrwindow_horz);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.raptor_rrwindow_horz);
        raptorDrawables_1x5 = tmpMap;
    }

    // Drawables for Bronco Base 4x4
    private static final Map<String, Integer> broncobase4x4Drawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.bronco_base_4x4_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.bronco_base_4x4_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.bronco_base_4x4_tailgate_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.bronco_base_4x4_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.bronco_base_4x4_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.bronco_base_4x4_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.bronco_base_4x4_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.bronco_base_4x4_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.bronco_base_4x4_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.bronco_base_4x4_lrwindow_horz);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.bronco_base_4x4_rrwindow_horz);
        tmpMap.put(BODY_PRIMARY, R.drawable.bronco_base_4x4_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.bronco_base_4x4_secondary_horz);
        broncobase4x4Drawables_1x5 = tmpMap;
    }

    // Drawables for Explorer ST
    private static final Map<String, Integer> explorerSTDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.explorer_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.explorer_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.explorer_tailgate_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.explorer_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.explorer_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.explorer_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.explorer_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.explorer_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.explorer_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.explorer_lrwindow_horz);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.explorer_rrwindow_horz);
        tmpMap.put(BODY_PRIMARY, R.drawable.explorer_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.explorer_secondary_horz);
        explorerSTDrawables_1x5 = tmpMap;
    }

    // Drawables for Escape
    private static final Map<String, Integer> escapeDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.escape_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.escape_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.escape_hatch_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.escape_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.escape_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.escape_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.escape_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.escape_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.escape_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.escape_lrwindow_horz);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.escape_rrwindow_horz);
        tmpMap.put(BODY_PRIMARY, R.drawable.escape_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.escape_secondary_horz);
        escapeDrawables_1x5 = tmpMap;
    }

    // Drawables for Edge
    private static final Map<String, Integer> edgeDrawables_1x5;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.edge_wireframe_horz);
        tmpMap.put(HOOD, R.drawable.edge_hood_horz);
        tmpMap.put(TAILGATE, R.drawable.edge_liftgate_horz);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.edge_lfdoor_horz);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.edge_rfdoor_horz);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.edge_lrdoor_horz);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.edge_rrdoor_horz);
        tmpMap.put(LEFT_FRONT_WINDOW, R.drawable.edge_lfwindow_horz);
        tmpMap.put(RIGHT_FRONT_WINDOW, R.drawable.edge_rfwindow_horz);
        tmpMap.put(LEFT_REAR_WINDOW, R.drawable.edge_lrwindow_horz);
        tmpMap.put(RIGHT_REAR_WINDOW, R.drawable.edge_rrwindow_horz);
        tmpMap.put(BODY_PRIMARY, R.drawable.edge_primary_horz);
        tmpMap.put(BODY_SECONDARY, R.drawable.edge_secondary_horz);
        edgeDrawables_1x5 = tmpMap;
    }

    // Get the set of drawables for a particular style of vehicle
    public static Map<String, Integer> getVehicleDrawables_1x5(String VIN) {
        if (VIN != null && !VIN.equals("")) {
            if (isF150(VIN)) {
                if (isF150RegularCab(VIN)) {
                    return regcabDrawables_1x5;
                } else if (isF150SuperCab(VIN)) {
                    return supercabDrawables_1x5;
                } else if (isF150SuperCrew(VIN)) {
                    return supercrewDrawables_1x5;
                } else if (isF150Raptor(VIN)) {
                    return raptorDrawables_1x5;
                }
            } else if (isBronco(VIN) || isBroncoSport(VIN)) {
                return broncobase4x4Drawables_1x5;
            } else if (isExplorer(VIN)) {
                return explorerSTDrawables_1x5;
            } else if (isEscape(VIN) || isKuga(VIN) || isPuma(VIN)) {
                return escapeDrawables_1x5;
            } else if (isEdge(VIN)) {
                return edgeDrawables_1x5;
            } else if (isExpedition(VIN)) {
                return explorerSTDrawables_1x5;
            }
        }
        return macheDrawables_1x5;
    }

    // Model year decoder
    private static final Map<String, Integer> modelYears;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put("G", 2016);
        tmpMap.put("H", 2017);
        tmpMap.put("J", 2018);
        tmpMap.put("K", 2019);
        tmpMap.put("L", 2020);
        tmpMap.put("M", 2021);
        tmpMap.put("N", 2022);
        tmpMap.put("P", 2023);
        tmpMap.put("R", 2024);
        tmpMap.put("S", 2025);
        tmpMap.put("T", 2026);
        tmpMap.put("V", 2027);
        tmpMap.put("W", 2028);
        tmpMap.put("X", 2029);
        tmpMap.put("Y", 2030);
        modelYears = tmpMap;
    }

    public static final int NA_MODEL_YEAR_START_INDEX = 10 - 1;
    public static final int NA_MODEL_YEAR_END_INDEX = 10;

    public static final int EURO_MODEL_YEAR_START_INDEX = 11 - 1;
    public static final int EURO_MODEL_YEAR_END_INDEX = 11;

    public static int getModelYear(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String vehicleYearCode;
        if (WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_GERMANY)) {
            vehicleYearCode = VIN.substring(EURO_MODEL_YEAR_START_INDEX, EURO_MODEL_YEAR_END_INDEX);
        } else {
            vehicleYearCode = VIN.substring(NA_MODEL_YEAR_START_INDEX, NA_MODEL_YEAR_END_INDEX);
        }
        Integer year = modelYears.get(vehicleYearCode);
        if (year != null) {
            return year;
        }
        return 0;
    }

    // Mapping from long state/territory names to abbreviations
    public static final Map<String, String> states;

    static {
        Map<String, String> tmpStates = new HashMap<>();
        tmpStates.put("Alabama", "AL");
        tmpStates.put("Alaska", "AK");
        tmpStates.put("Alberta", "AB");
        tmpStates.put("American Samoa", "AS");
        tmpStates.put("Arizona", "AZ");
        tmpStates.put("Arkansas", "AR");
        tmpStates.put("Armed Forces (AE)", "AE");
        tmpStates.put("Armed Forces Americas", "AA");
        tmpStates.put("Armed Forces Pacific", "AP");
        tmpStates.put("British Columbia", "BC");
        tmpStates.put("California", "CA");
        tmpStates.put("Colorado", "CO");
        tmpStates.put("Connecticut", "CT");
        tmpStates.put("Delaware", "DE");
        tmpStates.put("District Of Columbia", "DC");
        tmpStates.put("Florida", "FL");
        tmpStates.put("Georgia", "GA");
        tmpStates.put("Guam", "GU");
        tmpStates.put("Hawaii", "HI");
        tmpStates.put("Idaho", "ID");
        tmpStates.put("Illinois", "IL");
        tmpStates.put("Indiana", "IN");
        tmpStates.put("Iowa", "IA");
        tmpStates.put("Kansas", "KS");
        tmpStates.put("Kentucky", "KY");
        tmpStates.put("Louisiana", "LA");
        tmpStates.put("Maine", "ME");
        tmpStates.put("Manitoba", "MB");
        tmpStates.put("Maryland", "MD");
        tmpStates.put("Massachusetts", "MA");
        tmpStates.put("Michigan", "MI");
        tmpStates.put("Minnesota", "MN");
        tmpStates.put("Mississippi", "MS");
        tmpStates.put("Missouri", "MO");
        tmpStates.put("Montana", "MT");
        tmpStates.put("Nebraska", "NE");
        tmpStates.put("Nevada", "NV");
        tmpStates.put("New Brunswick", "NB");
        tmpStates.put("New Hampshire", "NH");
        tmpStates.put("New Jersey", "NJ");
        tmpStates.put("New Mexico", "NM");
        tmpStates.put("New York", "NY");
        tmpStates.put("Newfoundland", "NF");
        tmpStates.put("North Carolina", "NC");
        tmpStates.put("North Dakota", "ND");
        tmpStates.put("Northwest Territories", "NT");
        tmpStates.put("Nova Scotia", "NS");
        tmpStates.put("Nunavut", "NU");
        tmpStates.put("Ohio", "OH");
        tmpStates.put("Oklahoma", "OK");
        tmpStates.put("Ontario", "ON");
        tmpStates.put("Oregon", "OR");
        tmpStates.put("Pennsylvania", "PA");
        tmpStates.put("Prince Edward Island", "PE");
        tmpStates.put("Puerto Rico", "PR");
        tmpStates.put("Quebec", "QC");
        tmpStates.put("Rhode Island", "RI");
        tmpStates.put("Saskatchewan", "SK");
        tmpStates.put("South Carolina", "SC");
        tmpStates.put("South Dakota", "SD");
        tmpStates.put("Tennessee", "TN");
        tmpStates.put("Texas", "TX");
        tmpStates.put("Utah", "UT");
        tmpStates.put("Vermont", "VT");
        tmpStates.put("Virgin Islands", "VI");
        tmpStates.put("Virginia", "VA");
        tmpStates.put("Washington", "WA");
        tmpStates.put("West Virginia", "WV");
        tmpStates.put("Wisconsin", "WI");
        tmpStates.put("Wyoming", "WY");
        tmpStates.put("Yukon Territory", "YT");
        states = tmpStates;
    }

    public static void copyStreams(InputStream inStream, OutputStream outStream) {
        try {
            int len;
            byte[] buffer = new byte[65536];
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.e(MainActivity.CHANNEL_ID, "exception in LogFile.copyStream()", e);
        }
    }

    public static String writeExternalFile(Context context, InputStream inStream, String baseFilename, String mimeType) {
        LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
        String outputFilename = baseFilename + time.format(DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss", Locale.US));

        try {
            OutputStream outStream;
            Uri fileCollection;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                fileCollection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, outputFilename);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, mimeType);
                ContentResolver resolver = context.getContentResolver();
                Uri uri = resolver.insert(fileCollection, contentValues);
                if (uri == null) {
                    throw new IOException("Couldn't create MediaStore Entry");
                }
                outStream = resolver.openOutputStream(uri);
            } else {
                String extension;
                switch (mimeType) {
                    case Constants.APPLICATION_JSON:
                        extension = ".json";
                        break;
                    case Constants.APPLICATION_ZIP:
                        extension = ".zip";
                        break;
                    case Constants.TEXT_HTML:
                        extension = ".html";
                        break;
                    default:
                        extension = ".txt";
                        break;
                }
                File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), outputFilename + extension);
                outputFile.delete();
                outputFile.createNewFile();
                outStream = new FileOutputStream(outputFile);
            }
            copyStreams(inStream, outStream);
            outStream.close();
        } catch (IOException e) {
        }
        return outputFilename;
    }


    // See if there was a crash, and if so dump the logcat output to a file
    public static String checkLogcat(Context context) {
        try {
            // Dump the crash buffer and exit
            Process process = Runtime.getRuntime().exec("logcat -d -b crash");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }

            // If we find something, write to logcat.txt file
            if (log.length() > 0) {
                InputStream inStream = new ByteArrayInputStream(log.toString().getBytes(StandardCharsets.UTF_8));

                String outputFilename = writeExternalFile(context, inStream, "fsw_logcat-", Constants.TEXT_PLAINTEXT);

                // Clear the crash log.
                Runtime.getRuntime().exec("logcat -c");

                return java.text.MessageFormat.format("Logcat crash file \"{0}.txt\" copied to Download folder.", outputFilename);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public static boolean OTASupportCheck(String alertStatus) {
        return alertStatus == null || !alertStatus.toLowerCase().replaceAll("[^a-z0-9]", "").contains("doesntsupport");
    }

    public static File removeAPK(Context context) {
        File apkFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app-release.apk");
        apkFile.delete();
        return apkFile;
    }

    public static String elapsedSecondsToDescription(long seconds) {
        StringBuilder result = new StringBuilder();

        long minutes = seconds / 60;
        long hours = minutes / 60;
        // less than 1 minute
        if (minutes == 0) {
            result.append(seconds + " sec");
        }
        // less than an hour
        else if (hours == 0) {
            result.append(minutes + " min");
            // not right on the minute
            if ((seconds % 60) != 0) {
                result.append(", " + (seconds % 60) + " sec");
            }
        }
        // more than an hour
        else {
            result.append(hours == 1 ? "1 hr" : hours + " hrs");
            // not right on the hour
            if ((minutes % 60) != 0) {
                result.append(", " + (minutes % 60) + " min");
            }
        }
        return result.toString();
    }

    public static String elapsedMinutesToDescription(long minutes) {
        StringBuilder result = new StringBuilder();

        // less than an hour
        if (minutes < 60) {
            result.append(minutes + " min");
            // less than a day
        } else if (minutes / 60 < 24) {
            result.append((minutes / 60) + " hr");
            // right on the hour
            if ((minutes % 60) == 0) {
                if (minutes != 60) {
                    result.append("s");
                }
                // hours and minutes
            } else {
                if (minutes >= 120) {
                    result.append("s");
                }
                result.append(", " + (minutes % 60) + " min");
            }
        } else {
            long days = minutes / (24 * 60);
            result.append(days == 1 ? "1 day" : days + " days");
        }
        return result.toString();
    }

    private static final int JSON_SETTINGS_VERSION = 2;

    public static void savePrefs(Context context) {

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String jsonOutput = bundle.getString("json");
                InputStream inStream = new ByteArrayInputStream(jsonOutput.getBytes(StandardCharsets.UTF_8));
                String outputFilename = writeExternalFile(context, inStream, "fsw_settings-", Constants.APPLICATION_JSON);
                Toast.makeText(context, MessageFormat.format("Settings file \"{0}.json\" copied to Download folder.", outputFilename), Toast.LENGTH_SHORT).show();
            }
        };

        new Thread(() -> {
            LinkedHashMap<String, Object> jsonData = new LinkedHashMap<>();
            jsonData.put("version", JSON_SETTINGS_VERSION);
            Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(context).getAll();
            LinkedHashMap<String, Object> prefData = new LinkedHashMap<>();

            for (String key : prefs.keySet()) {
                Object value = prefs.get(key);
                if (value instanceof String) {
                    prefData.put(key, new String[]{"String", value.toString()});
                } else {
                    prefData.put(key, new String[]{"Boolean", value.toString()});
                }
            }
            jsonData.put("prefs", prefData.clone());
            prefData.clear();

            prefs = context.getSharedPreferences(StoredData.TAG, MODE_PRIVATE).getAll();
            for (String key : prefs.keySet()) {
                Object value = prefs.get(key);
                if (value instanceof String) {
                    prefData.put(key, new String[]{"String", value.toString()});
                } else if (value instanceof Long) {
                    prefData.put(key, new String[]{"Long", value.toString()});
                } else if (value instanceof Integer) {
                    prefData.put(key, new String[]{"Integer", value.toString()});
                } else {
                    prefData.put(key, new String[]{"Boolean", value.toString()});
                }
            }
            jsonData.put(StoredData.TAG, prefData.clone());
            prefData.clear();

            jsonData.put("users", UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo());
            jsonData.put("vehicles", VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo());

            Bundle bundle = new Bundle();
            bundle.putString("json", new GsonBuilder().create().toJson(jsonData));
            Message m = Message.obtain();
            m.setData(bundle);
            handler.sendMessage(m);
        }).start();
    }

    public static void restorePrefs(Context context, Uri jsonFile) throws IOException {
        InputStream inStream = context.getContentResolver().openInputStream(jsonFile);
        StringBuilder json = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        final JsonObject jsonObject = JsonParser.parseString(json.toString()).getAsJsonObject();

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(context, "Settings restored.", Toast.LENGTH_SHORT).show();
            }
        };

        new Thread(() -> {
            Gson gson = new GsonBuilder().create();
            final File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
            if (!imageDir.exists()) {
                imageDir.mkdir();
            }

            JsonPrimitive versionItem = jsonObject.getAsJsonPrimitive("version");
            int version = versionItem.getAsInt();

            // Get the current set of user IDs and VINs
            ArrayList<String> userIds = new ArrayList<>();
            for (UserInfo info : UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo()) {
                userIds.add(info.getUserId());
            }
            ArrayList<String> VINs = new ArrayList<>();
            for (VehicleInfo info : VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfo()) {
                VINs.add(info.getVIN());
            }

            // Update users in the database, and remove all IDs from the current list
            JsonArray users = jsonObject.getAsJsonArray("users");
            for (JsonElement items : users) {
                UserInfo info = gson.fromJson(items.toString(), new TypeToken<UserInfo>() {
                }.getType());
                UserInfo current = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(info.getUserId());
                if (current == null) {
                    info.setId(0);
                    UserInfoDatabase.getInstance(context).userInfoDao().insertUserInfo(info);
                } else {
                    UserInfoDatabase.getInstance(context).userInfoDao().updateUserInfo(info);
                }
                userIds.remove(info.getUserId());
            }

            String newVIN = "";
            String newUserId = "";
            // Insert missing VINs into the database, and remove all VINs from the current list
            JsonArray vehicles = jsonObject.getAsJsonArray("vehicles");
            for (JsonElement items : vehicles) {
                VehicleInfo info = gson.fromJson(items.toString(), new TypeToken<VehicleInfo>() {
                }.getType());
                // Save a valid VIN in case we need to change the current VIN
                newVIN = info.getVIN();
                newUserId = info.getUserId();
                VehicleInfo current = VehicleInfoDatabase.getInstance(context).vehicleInfoDao().findVehicleInfoByVIN(info.getVIN());
                if (current == null) {
                    info.setId(0);
                    VehicleInfoDatabase.getInstance(context).vehicleInfoDao().insertVehicleInfo(info);
                }
                UserInfo user = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(info.getUserId());
                if (user != null) {
                    NetworkCalls.getVehicleImage(context, user.getAccessToken(), newVIN, user.getCountry());
                }
                VINs.remove(info.getVIN());
            }

            // If the current VIN is still in the current list, change it to one of the "good" VINs
            String VINkey = context.getResources().getString(R.string.VIN_key);
            String currentVIN = PreferenceManager.getDefaultSharedPreferences(context).getString(VINkey, "");
            if (VINs.contains(currentVIN)) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(VINkey, newVIN).apply();
            }

            // Version 1 preferences didn't include user Id
            if (version == 1) {
                String UserIdkey = context.getResources().getString(R.string.userId_key);
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(UserIdkey, newUserId).apply();
            }

            // Any user IDs or VINs which weren't restored get deleted
            for (String VIN : VINs) {
                VehicleInfoDatabase.getInstance(context).vehicleInfoDao().deleteVehicleInfoByVIN(VIN);
                deleteVehicleImages(context, VIN);
            }
            for (String user : userIds) {
                UserInfoDatabase.getInstance(context).userInfoDao().deleteUserInfoByUserId(user);
            }

            // Update all the default preferences
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
            JsonObject prefs = jsonObject.getAsJsonObject("prefs");
            for (Map.Entry<String, JsonElement> item : prefs.entrySet()) {
                String key = item.getKey();
                JsonArray value = item.getValue().getAsJsonArray();
                if (value.get(0).getAsString().equals("String")) {
                    edit.putString(key, value.get(1).getAsString()).commit();
                } else {
                    edit.putBoolean(key, value.get(1).getAsBoolean()).commit();
                }
            }

            // Update all the shared preferences
            edit = context.getSharedPreferences(StoredData.TAG, MODE_PRIVATE).edit();
            prefs = jsonObject.getAsJsonObject(StoredData.TAG);
            for (Map.Entry<String, JsonElement> item : prefs.entrySet()) {
                String key = item.getKey();
                JsonArray value = item.getValue().getAsJsonArray();
                switch (value.get(0).getAsString()) {
                    case "String":
                        edit.putString(key, value.get(1).getAsString()).commit();
                        break;
                    case "Long":
                        edit.putLong(key, value.get(1).getAsLong()).commit();
                        break;
                    case "Integer":
                        edit.putInt(key, value.get(1).getAsInt()).commit();
                        break;
                    default:
                        edit.putBoolean(key, value.get(1).getAsBoolean()).commit();
                        break;
                }
            }

            // Tell the widget to update
            CarStatusWidget.updateWidget(context);
            handler.sendEmptyMessage(0);
        }).start();
    }

    public static boolean scanImageForColor(Context context, VehicleInfo vehicleInfo) {
        // If vehicle color has been set, do nothing
        if ((vehicleInfo.getColorValue() & ARGB_MASK) != (Color.WHITE & ARGB_MASK)) {
            return false;
        }

        // If the vehicle image doesn't exist, do nothing
        String VIN = vehicleInfo.getVIN();
        Bitmap bmp = Utils.getVehicleImage(context, VIN, 4);
        if (bmp == null || vehicleInfo.getColorValue() != Color.WHITE) {
            return false;
        }

        // Based on the vehicle type, choose a small image patch to sample
        int startx;
        int starty;
        if (isMachE(VIN)) {
            startx = 352; // 324;
            starty = 288; // 244;
        } else if (isF150(VIN)) {
            startx = 344; // 460;
            starty = 220; // 220;
        } else if (isBronco(VIN) || isBroncoSport(VIN)) {
            startx = 244; // 572;
            starty = 200; // 188;
        } else if (isExplorer(VIN)) {
            startx = 320; // 628;
            starty = 280; // 176;
        } else if (isEscape(VIN)) {
            startx = 340; // 300;
            starty = 244; // 204;
        } else if (isKuga(VIN)) {
            startx = 340;
            starty = 280;
        } else if (isPuma(VIN)) {
            startx = 172;
            starty = 288;
        } else if (isEdge(VIN)) {
            startx = 240; // 284;
            starty = 200; // 208;
        } else if (isExpedition(VIN)) {
            startx = 324; // 628;
            starty = 304; // 176;
        } else {
            return false;
        }
        int[] RGB = new int[3];
        final int patchSize = 10;

        // get the RBG value of each pixel in the patch
        for (int y = 0; y < patchSize; ++y) {
            for (int x = 0; x < patchSize; ++x) {
                int color = bmp.getPixel(startx + x, starty + y);
                RGB[0] += (color >> 16) & 0xff;
                RGB[1] += (color >> 8) & 0xff;
                RGB[2] += color & 0xff;
            }
        }

        // average the components
        RGB[0] /= patchSize * patchSize;
        RGB[1] /= patchSize * patchSize;
        RGB[2] /= patchSize * patchSize;

        // Set the color and exit
        vehicleInfo.setColorValue((RGB[0] << 16 | RGB[1] << 8 | RGB[2]) & ARGB_MASK | WIREFRAME_AUTO);
        return true;
    }

    public static final int ARGB_MASK = 0xffffff;  // only use RGB components
    public static final int WIREFRAME_MASK = 0x03 << 24;
    public static final int WIREFRAME_WHITE = 0;
    public static final int WIREFRAME_BLACK = 1 << 24;
    public static final int WIREFRAME_AUTO = 2 << 24;

    public static void drawColoredVehicle(Context context, Bitmap bmp, int color, ArrayList<Integer> whatsOpen,
                                          boolean useColor, Map<String, Integer> vehicleImages) {
        // Create base canvas the size of the image
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        Drawable icon;
        Bitmap bmp2;
        Canvas canvas2;

        Drawable drawable = AppCompatResources.getDrawable(context, vehicleImages.get(Utils.BODY_PRIMARY));
        if (vehicleImages.get(Utils.BODY_PRIMARY) != null && useColor) {
            bmp2 = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            canvas2 = new Canvas(bmp2);

            // Fill with the primary color mask
            paint.setColor(color & Utils.ARGB_MASK);
            // Set the alpha based on whether something is open
            paint.setAlpha(whatsOpen.isEmpty() ? 0xff : 0xbf);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);

            // Draw the primary body in color
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas2);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawBitmap(bmp2, 0, 0, paint);

            // If secondary colors exist, add them
            Integer secondary = vehicleImages.get((Utils.BODY_SECONDARY));
            if (secondary != null) {
                icon = AppCompatResources.getDrawable(context, secondary);
                icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                icon.draw(canvas);
            }
        }

        // Draw anything that's open
        for (Integer id : whatsOpen) {
            icon = context.getDrawable(id);
            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            icon.draw(canvas);
        }

        // Create a second bitmap the same size as the primary
        bmp2 = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        canvas2 = new Canvas(bmp2);

        // If not using colors, draw wireframe in white
        if (!useColor) {
            paint.setColor(Color.WHITE);
        }
        // Figure out whether wireframe should be drawn light or dark
        else {
            float[] hsl = new float[3];
            ColorUtils.colorToHSL(color & ARGB_MASK, hsl);
            int wireframeMode = color & WIREFRAME_MASK;
            paint.setColor(wireframeMode == WIREFRAME_WHITE ? Color.WHITE :
                    wireframeMode == WIREFRAME_BLACK ? Color.BLACK :
                            hsl[2] > 0.5 ? Color.BLACK : Color.WHITE);
        }
        paint.setAlpha(0xff);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        // Fill with a contrasting color
        paint.setStyle(Paint.Style.FILL);
        canvas2.drawPaint(paint);

        // Draw the wireframe body
        drawable = AppCompatResources.getDrawable(context, vehicleImages.get(Utils.WIREFRAME));
        Bitmap bmp3 = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas3 = new Canvas(bmp3);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas3);

        // Set the wireframe's color
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        canvas2.drawBitmap(bmp3, 0, 0, paint);

        // Draw wireframe over the colored body
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(bmp2, 0, 0, paint);
    }

    public static Bitmap getVehicleImage(Context context, String VIN, int angle) {
        File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
        File image = new File(imageDir, VIN + "_angle" + angle + ".png");
        if (image.exists()) {
            return BitmapFactory.decodeFile(image.getPath());
        }
        return null;
    }

    public static Bitmap getRandomVehicleImage(Context context, String VIN) {
        File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
        ArrayList<String> allImages = new ArrayList<>(Arrays.asList(imageDir.list()));
        Predicate<String> byVIN = thisVIN -> thisVIN.contains(VIN);
        ArrayList<String> myImages = new ArrayList<>(allImages.stream().filter(byVIN).collect(Collectors.toList()));
        if (!myImages.isEmpty()) {
            int angle = new Random(System.currentTimeMillis()).nextInt(myImages.size());
            File image = new File(imageDir, myImages.get(angle));
            return BitmapFactory.decodeFile(image.getPath());
        }
        return null;
    }

    public static void deleteVehicleImages(Context context, String VIN) {
        File imageDir = new File(context.getDataDir(), Constants.IMAGES_FOLDER);
        for (int angle = 1; angle <= 5; ++angle) {
            File image = new File(imageDir, VIN + "_angle" + angle + ".png");
            if (image.exists()) {
                image.delete();
            }
        }
    }

    public static Boolean ignoringBatteryOptimizations(Context context) {
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(packageName);
    }


}
