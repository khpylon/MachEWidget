package com.example.khughes.machewidget;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.MessageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class Utils {
    public static final String WIDGETMODE_MACHE = "ModeMachE";
    public static final String WIDGETMODE_F150 = "ModeF150";
    public static final String WIDGETMODE_BRONCO = "ModeBrondo";
    public static final String WIDGETMODE_EXPLORER = "ModeExplorer";

    public static final int WORLD_MANUFACTURING_IDENTIFIER_START_INDEX = 1 - 1;
    public static final int WORLD_MANUFACTURING_IDENTIFIER_END_INDEX = 3;
    public static final String WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV = "3FM";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_GERMANY = "WF0";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK = "1FT";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_USA_MPV = "1FM";

    public static final int LINE_SERIES_START_INDEX = 5 - 1;
    public static final int LINE_SERIES_END_INDEX = 7;

    public static final String LINE_SERIES_MACHE_SELECT_RWD = "K1R"; // select RWD
    public static final String LINE_SERIES_MACHE_SELECT_AWD = "K1S"; // select RWD (AWD?
    public static final String LINE_SERIES_MACHE_CAROUTE1_RWD = "K2R"; // Route 1 RWD
    public static final String LINE_SERIES_MACHE_PREMIUM_RWD = "K3R"; // Premium RWD
    public static final String LINE_SERIES_MACHE_PREMIUM_AWD = "K3S"; // Premium AWD?
    public static final String LINE_SERIES_MACHE_GT_RWD = "K4S"; // GT AWD

    public static final String LINE_SERIES_F150_REGULAR_4X2 = "F1C"; // 4x2 chassis, regular cab
    public static final String LINE_SERIES_F150_REGULAR_4X4 = "F1E"; // 4x4 chassis, regular cab
    public static final String LINE_SERIES_F150_SUPERCREW_4X2 = "W1C"; // 4x2, SuperCrew
    public static final String LINE_SERIES_F150_SUPERCREW_4X4 = "W1E"; // 4x4, superCrew
    public static final String LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR = "W1R"; // 4x4, SuperCrew, Raptor
    public static final String LINE_SERIES_F150_SUPERCREW_4X4_POLICE = "W1P"; // 4x4, SuperCrew, Police
    public static final String LINE_SERIES_F150_SUPERCREW_4X2_SSV = "W1S"; // 4x2, SuperCrew, SSV (Special Service Vehicle), government
    public static final String LINE_SERIES_F150_SUPERCREW_4X4_SSV = "W1T"; // 4x4, superCrew, SSV (Special Service Vehicle), government
    public static final String LINE_SERIES_F150_SUPERCAB_4X2 = "X1C"; // 4x2, SuperCab
    public static final String LINE_SERIES_F150_SUPERCAB_4X4 = "X1E"; // 4x4, SuperCab

    public static final String LINE_SERIES_BRONCO_BASE_2DOOR_4X4 = "E5A"; //
    public static final String LINE_SERIES_BRONCO_BASE_4DOOR_4X4 = "E5B"; //
    public static final String LINE_SERIES_BRONCO_BASE_2DOOR_AWD = "E5C"; //
    public static final String LINE_SERIES_BRONCO_BASE_4DOOR_AWD = "E5D"; //
    public static final String LINE_SERIES_BRONCO_FE_4DOOR_AWD = "E5E"; //
    public static final String LINE_SERIES_BRONCO_FE_2DOOR_AWD = "E5F"; //
    public static final String LINE_SERIES_BRONCO_BASE_4DOOR_AWD_RAPTOR = "E5J"; //

    public static final String LINE_SERIES_BRONCOSPORT_BASE_4x4 = "R9A";
    public static final String LINE_SERIES_BRONCOSPORT_BIGBEND_4x4 = "R9B";
    public static final String LINE_SERIES_BRONCOSPORT_OUTERBANKS_4x4 = "R9C";
    public static final String LINE_SERIES_BRONCOSPORT_BADLANDS_4x4 = "R9D";
    public static final String LINE_SERIES_BRONCOSPORT_WILDTRAK_4x4 = "R9E";

    public static final String LINE_SERIES_EXPLORER_BASE_RWD = "K7B";
    public static final String LINE_SERIES_EXPLORER_XLT_RWD = "K7D";
    public static final String LINE_SERIES_EXPLORER_LIMITED_RWD = "K7F";
    public static final String LINE_SERIES_EXPLORER_PLATINUM_RWD = "K7H";
    public static final String LINE_SERIES_EXPLORER_KING_RWD = "K7L";
    public static final String LINE_SERIES_EXPLORER_ST_RWD = "K7G";
    public static final String LINE_SERIES_EXPLORER_STLINE_RWD = "K7K";
    public static final String LINE_SERIES_EXPLORER_POLICE = "K8A";
    public static final String LINE_SERIES_EXPLORER_BASE_4WD = "K8B";
    public static final String LINE_SERIES_EXPLORER_XLT_4WD = "K8D";
    public static final String LINE_SERIES_EXPLORER_LIMITED_4WD = "K8F";
    public static final String LINE_SERIES_EXPLORER_ST_4WD = "K8G";
    public static final String LINE_SERIES_EXPLORER_PLATINUM_4WD = "K8H";
    public static final String LINE_SERIES_EXPLORER_KING_4WD = "K8L";
    public static final String LINE_SERIES_EXPLORER_STLINE_4WD = "K8K";
    public static final String LINE_SERIES_EXPLORER_TIMBERLINE_4WD = "K8J";

    public static final String LINE_SERIES_ESCAPE_S_RWD = "U0F";
    public static final String LINE_SERIES_ESCAPE_SE_RWD = "U0G";
    public static final String LINE_SERIES_ESCAPE_SEL_RWD = "U0H";
    public static final String LINE_SERIES_ESCAPE_SE_FHEV_RWD = "U0B";
    public static final String LINE_SERIES_ESCAPE_SEL_FHEV_RWD = "U0C";
    public static final String LINE_SERIES_ESCAPE_TITANIUM_FHEV_RWD = "U0D";
    public static final String LINE_SERIES_ESCAPE_SE_PHEV_RWD = "U0E";
    public static final String LINE_SERIES_ESCAPE_SEL_PHEV_RWD = "U0K";
    public static final String LINE_SERIES_ESCAPE_TITANIUM_PHEV_RWD = "U0L";
    public static final String LINE_SERIES_ESCAPE_S_4WD = "U9F";
    public static final String LINE_SERIES_ESCAPE_SE_4WD = "U9G";
    public static final String LINE_SERIES_ESCAPE_SEL_4WD = "U9H";
    public static final String LINE_SERIES_ESCAPE_TITANIUM_4WD = "U9J";
    public static final String LINE_SERIES_ESCAPE_SE_FHEV_4WD = "U9B";
    public static final String LINE_SERIES_ESCAPE_SEL_FHEV_4WD = "U9C";
    public static final String LINE_SERIES_ESCAPE_TITANIUM_FHEV_4WD = "U9D";

    public static final int FUEL_TYPE_START_INDEX = 8 - 1;
    public static final int FUEL_TYPE_END_INDEX = 8;
    public static final String HYBRID_TRUCK_2_5_LITER = "3";
    public static final String ELEC_TRUCK_EXT_BATT_REAR_MOTOR = "7";
    public static final String HYBRID_TRUCK_3_5_LITER = "D";
    public static final String ELEC_TRUCK_EXT_BATT_DUAL_LIMITED_MOTOR = "E";
    public static final String ELEC_TRUCK_STD_BATT_DUAL_MOTOR = "L";
    public static final String ELEC_TRUCK_STD_BATT_REAR_MOTOR = "M";
    public static final String ELEC_TRUCK_STD_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "S";
    public static final String ELEC_TRUCK_EXT_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "U";
    public static final String ELEC_TRUCK_EXT_BATT_DUAL_MOTOR = "V";
    public static final String ELEC_TRUCK_EXT_BATT_DUAL_LARGER_SECONDARY_MOTOR = "X";
    public static final String HYBRID_TRUCK_3_3_LITER = "W";
    public static final String PHEV_TRUCK_3_0_LITER = "Y";
    public static final String PHEV_TRUCK_2_5_LITER = "Z";
    public static final String ELEC_CAR_EXT_BATT_REAR_MOTOR = "7";
    public static final String ELEC_CAR_EXT_BATT_DUAL_LIMITED_MOTOR = "E";
    public static final String ELEC_CAR_STD_BATT_REAR_MOTOR = "M";
    public static final String ELEC_CAR_STD_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "S";
    public static final String ELEC_CAR_EXT_BATT_DUAL_SMALLER_SECONDARY_MOTOR = "U";
    public static final String ELEC_CAR_EXT_BATT_DUAL_LARGER_SECONDARY_MOTOR = "X";

    public static final int FUEL_UNKNOWN = 0;
    public static final int FUEL_GAS = FUEL_UNKNOWN + 1;
    public static final int FUEL_HYBRID = FUEL_GAS + 1;
    public static final int FUEL_PHEV = FUEL_HYBRID + 1;
    public static final int FUEL_ELECTRIC = FUEL_PHEV + 1;

    public static final int MODEL_YEAR_START_INDEX = 10 - 1;
    public static final int MODEL_YEAR_END_INDEX = 10;

    private static final Set<String> macheLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_MACHE_SELECT_RWD);
        tmpSet.add(LINE_SERIES_MACHE_SELECT_AWD);
        tmpSet.add(LINE_SERIES_MACHE_CAROUTE1_RWD);
        tmpSet.add(LINE_SERIES_MACHE_PREMIUM_RWD);
        tmpSet.add(LINE_SERIES_MACHE_PREMIUM_AWD);
        tmpSet.add(LINE_SERIES_MACHE_GT_RWD);
        macheLineSeries = tmpSet;
    }

    public static boolean isMachE(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_GERMANY) ||
                (WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_MEXICO_MPV) && macheLineSeries.contains(lineSeries));
    }

    private static final Set<String> f150RegularCabsLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_F150_REGULAR_4X2);
        tmpSet.add(LINE_SERIES_F150_REGULAR_4X4);
        f150RegularCabsLineSeries = tmpSet;
    }

    public static boolean isF150RegularCab(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return f150RegularCabsLineSeries.contains(lineSeries);
    }

    private static final Set<String> f150SuperCabsLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_F150_SUPERCAB_4X2);
        tmpSet.add(LINE_SERIES_F150_SUPERCAB_4X4);
        f150SuperCabsLineSeries = tmpSet;
    }

    public static boolean isF150SuperCab(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return f150SuperCabsLineSeries.contains(lineSeries);
    }

    private static final Set<String> f150SuperCrewsLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X2);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X4);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X4_POLICE);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X2_SSV);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X4_SSV);
        f150SuperCrewsLineSeries = tmpSet;
    }

    public static boolean isF150SuperCrew(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return f150SuperCrewsLineSeries.contains(lineSeries);
    }

    public static boolean isF150Raptor(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return lineSeries.equals(LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR);
    }

    public static boolean isF150(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_TRUCK) &&
                (isF150RegularCab(VIN) || isF150SuperCab(VIN) || isF150SuperCrew(VIN) || isF150Raptor(VIN));
    }

    private static final Set<String> explorerLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_EXPLORER_BASE_RWD);
        tmpSet.add(LINE_SERIES_EXPLORER_XLT_RWD);
        tmpSet.add(LINE_SERIES_EXPLORER_LIMITED_RWD);
        tmpSet.add(LINE_SERIES_EXPLORER_PLATINUM_RWD);
        tmpSet.add(LINE_SERIES_EXPLORER_KING_RWD);
        tmpSet.add(LINE_SERIES_EXPLORER_ST_RWD);
        tmpSet.add(LINE_SERIES_EXPLORER_STLINE_RWD);
        tmpSet.add(LINE_SERIES_EXPLORER_POLICE);
        tmpSet.add(LINE_SERIES_EXPLORER_BASE_4WD);
        tmpSet.add(LINE_SERIES_EXPLORER_XLT_4WD);
        tmpSet.add(LINE_SERIES_EXPLORER_LIMITED_4WD);
        tmpSet.add(LINE_SERIES_EXPLORER_ST_4WD);
        tmpSet.add(LINE_SERIES_EXPLORER_PLATINUM_4WD);
        tmpSet.add(LINE_SERIES_EXPLORER_KING_4WD);
        tmpSet.add(LINE_SERIES_EXPLORER_STLINE_4WD);
        tmpSet.add(LINE_SERIES_EXPLORER_TIMBERLINE_4WD);
        explorerLineSeries = tmpSet;
    }

    public static boolean isExplorer(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && explorerLineSeries.contains(lineSeries);
    }

    private static final Set<String> broncoLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_BRONCO_BASE_2DOOR_4X4);
        tmpSet.add(LINE_SERIES_BRONCO_BASE_4DOOR_4X4);
        tmpSet.add(LINE_SERIES_BRONCO_BASE_2DOOR_AWD);
        tmpSet.add(LINE_SERIES_BRONCO_BASE_4DOOR_AWD);
        tmpSet.add(LINE_SERIES_BRONCO_FE_4DOOR_AWD);
        tmpSet.add(LINE_SERIES_BRONCO_FE_2DOOR_AWD);
        tmpSet.add(LINE_SERIES_BRONCO_BASE_4DOOR_AWD_RAPTOR);
        broncoLineSeries = tmpSet;
    }

    public static boolean isBronco(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && broncoLineSeries.contains(lineSeries);
    }

    private static final Set<String> broncoSportLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_BRONCOSPORT_BASE_4x4);
        tmpSet.add(LINE_SERIES_BRONCOSPORT_BIGBEND_4x4);
        tmpSet.add(LINE_SERIES_BRONCOSPORT_OUTERBANKS_4x4);
        tmpSet.add(LINE_SERIES_BRONCOSPORT_BADLANDS_4x4);
        tmpSet.add(LINE_SERIES_BRONCOSPORT_WILDTRAK_4x4);
        broncoSportLineSeries = tmpSet;
    }

    public static boolean isBroncoSport(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && broncoSportLineSeries.contains(lineSeries);
    }

    private static final Set<String> escapeLineSeries;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_ESCAPE_S_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_SE_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_SEL_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_SE_FHEV_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_SEL_FHEV_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_TITANIUM_FHEV_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_SE_PHEV_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_SEL_PHEV_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_TITANIUM_PHEV_RWD);
        tmpSet.add(LINE_SERIES_ESCAPE_S_4WD);
        tmpSet.add(LINE_SERIES_ESCAPE_SE_4WD);
        tmpSet.add(LINE_SERIES_ESCAPE_SEL_4WD);
        tmpSet.add(LINE_SERIES_ESCAPE_TITANIUM_4WD);
        tmpSet.add(LINE_SERIES_ESCAPE_SE_FHEV_4WD);
        tmpSet.add(LINE_SERIES_ESCAPE_SEL_FHEV_4WD);
        tmpSet.add(LINE_SERIES_ESCAPE_TITANIUM_FHEV_4WD);
        escapeLineSeries = tmpSet;
    }

    public static boolean isEscape(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_USA_MPV) && escapeLineSeries.contains(lineSeries);
    }

    // Check to see if we recognize a VIN in general
    public static boolean isVINRecognized(String VIN) {
        return isMachE(VIN) || isF150(VIN) || isBronco(VIN) || isExplorer(VIN) | isBroncoSport(VIN)
                | isEscape(VIN);
    }

    private static final Set<String> fuelElectric;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(ELEC_TRUCK_STD_BATT_DUAL_MOTOR);
        tmpSet.add(ELEC_TRUCK_EXT_BATT_REAR_MOTOR);
        tmpSet.add(ELEC_TRUCK_EXT_BATT_DUAL_LIMITED_MOTOR);
        tmpSet.add(ELEC_TRUCK_STD_BATT_REAR_MOTOR);
        tmpSet.add(ELEC_TRUCK_STD_BATT_DUAL_SMALLER_SECONDARY_MOTOR);
        tmpSet.add(ELEC_TRUCK_EXT_BATT_DUAL_SMALLER_SECONDARY_MOTOR);
        tmpSet.add(ELEC_TRUCK_EXT_BATT_DUAL_MOTOR);
        tmpSet.add(ELEC_TRUCK_EXT_BATT_DUAL_LARGER_SECONDARY_MOTOR);
        fuelElectric = tmpSet;
    }

    private static final Set<String> fuelHybrid;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(HYBRID_TRUCK_2_5_LITER);
        tmpSet.add(HYBRID_TRUCK_3_3_LITER);
        tmpSet.add(HYBRID_TRUCK_3_5_LITER);
        fuelHybrid = tmpSet;
    }

    private static final Set<String> fuelPHEV;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(PHEV_TRUCK_2_5_LITER);
        tmpSet.add(PHEV_TRUCK_3_0_LITER);
        fuelPHEV = tmpSet;
    }

    public static int getFuelType(String VIN) {
        // Default is a Mach-E
        if (VIN == null || VIN.equals("") || isMachE(VIN)) {
            return FUEL_ELECTRIC;
        }
        // Otherwise check the VIN
        else if (isF150(VIN) || isBronco(VIN) || isExplorer(VIN) || isBroncoSport(VIN)) {
            String fuelType = VIN.substring(FUEL_TYPE_START_INDEX, FUEL_TYPE_END_INDEX);
            if (fuelElectric.contains(fuelType)) {
                return FUEL_ELECTRIC;
            } else if (fuelHybrid.contains(fuelType)) {
                return FUEL_HYBRID;
            } else if (fuelPHEV.contains(fuelType)) {
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

    // Drawables for Mach-E
    private static final Map<String, Integer> macheDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.mache_wireframe);
        tmpMap.put(HOOD, R.drawable.mache_frunk);
        tmpMap.put(TAILGATE, R.drawable.mache_hatch);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.mache_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.mache_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.mache_lrdoor);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.mache_rrdoor);
        macheDrawables = tmpMap;
    }

    // Drawables for Regular Cab (two door) F-150
    private static final Map<String, Integer> regcabDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.regularcab_wireframe);
        tmpMap.put(HOOD, R.drawable.regularcab_hood);
        tmpMap.put(TAILGATE, R.drawable.regularcab_tailgate);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.regularcab_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.regularcab_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.filler);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.filler);
        regcabDrawables = tmpMap;
    }

    // Drawables for SuperCab F-150
    private static final Map<String, Integer> supercabDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.supercab_wireframe);
        tmpMap.put(HOOD, R.drawable.supercab_hood);
        tmpMap.put(TAILGATE, R.drawable.supercab_tailgate);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.supercab_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.supercab_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.supercab_lrdoor);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.supercab_rrdoor);
        supercabDrawables = tmpMap;
    }

    // Drawables for SuperCrew F-150
    private static final Map<String, Integer> supercrewDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.supercrew_wireframe);
        tmpMap.put(HOOD, R.drawable.supercrew_hood);
        tmpMap.put(TAILGATE, R.drawable.supercrew_tailgate);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.supercrew_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.supercrew_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.supercrew_lrdoor);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.supercrew_rrdoor);
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
        tmpMap.put(WIREFRAME, R.drawable.bronco_base_4x4_wireframe);
        tmpMap.put(HOOD, R.drawable.bronco_base_4x4_hood);
        tmpMap.put(TAILGATE, R.drawable.bronco_base_4x4_tailgate);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.bronco_base_4x4_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.bronco_base_4x4_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.bronco_base_4x4_lrdoor);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.bronco_base_4x4_rrdoor);
        broncobase4x4Drawables = tmpMap;
    }

    // Drawables for Explorer ST
    private static final Map<String, Integer> explorerSTDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.explorer_wireframe);
        tmpMap.put(HOOD, R.drawable.explorer_hood);
        tmpMap.put(TAILGATE, R.drawable.explorer_tailgate);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.explorer_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.explorer_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.explorer_lrdoor);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.explorer_rrdoor);
        explorerSTDrawables = tmpMap;
    }

    // Drawables for Escape
    private static final Map<String, Integer> escapeDrawables;

    static {
        Map<String, Integer> tmpMap = new HashMap<>();
        tmpMap.put(WIREFRAME, R.drawable.mache_wireframe);
        tmpMap.put(HOOD, R.drawable.mache_frunk);
        tmpMap.put(TAILGATE, R.drawable.mache_hatch);
        tmpMap.put(LEFT_FRONT_DOOR, R.drawable.mache_lfdoor);
        tmpMap.put(RIGHT_FRONT_DOOR, R.drawable.mache_rfdoor);
        tmpMap.put(LEFT_REAR_DOOR, R.drawable.mache_lrdoor);
        tmpMap.put(RIGHT_REAR_DOOR, R.drawable.mache_rrdoor);
        escapeDrawables = tmpMap;
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
            } else if (isEscape(VIN)) {
                return escapeDrawables;
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
            } else if (isEscape(VIN)) {
                return R.layout.escape_widget;
            }
        }
        return R.layout.mache_widget;
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

    public static int getModelYear(String VIN) {
        String vehicleYearCode = VIN.substring(MODEL_YEAR_START_INDEX, MODEL_YEAR_END_INDEX);
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

    public static String writeExternalFile(Context context, InputStream inStream, String baseFilename, String extension) {
        LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
        String outputFilename = baseFilename + time.format(DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss", Locale.US));

        try {
            OutputStream outStream;
            Uri fileCollection;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                fileCollection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, outputFilename);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, Constants.TEXT_PLAINTEXT);
                ContentResolver resolver = context.getContentResolver();
                Uri uri = resolver.insert(fileCollection, contentValues);
                if (uri == null) {
                    throw new IOException("Couldn't create MediaStore Entry");
                }
                outStream = resolver.openOutputStream(uri);
            } else {
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

                String outputFilename = writeExternalFile(context, inStream, "fsw_logcat-", ".txt");

//                    LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
//                String outputFilename = "fsw_logcat-" + time.format(DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss", Locale.US));
//
//                OutputStream outStream;
//                Uri fileCollection = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                    fileCollection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(MediaStore.Downloads.DISPLAY_NAME, outputFilename);
//                    contentValues.put(MediaStore.Downloads.MIME_TYPE, Constants.TEXT_PLAINTEXT);
//                    ContentResolver resolver = context.getContentResolver();
//                    Uri uri = resolver.insert(fileCollection, contentValues);
//                    if (uri == null) {
//                        throw new IOException("Couldn't create MediaStore Entry");
//                    }
//                    outStream = resolver.openOutputStream(uri);
//                } else {
//                    File outputFile = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), outputFilename+".txt");
//                    outputFile.delete();
//                    outputFile.createNewFile();
//                    outStream = new FileOutputStream(outputFile);
//                }
//
//                copyStreams(inStream, outStream);
//                outStream.close();

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

    private static final int JSON_SETTINGS_VERSION = 2;

    public static void savePrefs(Context context) {

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String jsonOutput = bundle.getString("json");
                InputStream inStream = new ByteArrayInputStream(jsonOutput.getBytes(StandardCharsets.UTF_8));
                String outputFilename = writeExternalFile(context, inStream, "fsw_settings-", ".json");
                Toast.makeText(context, MessageFormat.format("Settings file \"{0}.txt\" copied to Download folder.", outputFilename), Toast.LENGTH_SHORT).show();
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

            // Insert missing users into the database, and remove all IDs from the current list
            JsonArray users = jsonObject.getAsJsonArray("users");
            for (JsonElement items : users) {
                UserInfo info = gson.fromJson(items.toString(), new TypeToken<UserInfo>() {
                }.getType());
                UserInfo current = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(info.getUserId());
                if (current == null) {
                    info.setId(0);
                    UserInfoDatabase.getInstance(context).userInfoDao().insertUserInfo(info); // BUG?
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
                File image = new File(imageDir, newVIN + ".png");
                if (!image.exists()) {
                    UserInfo user = UserInfoDatabase.getInstance(context).userInfoDao().findUserInfo(info.getUserId());
                    if (user != null) {
                        NetworkCalls.getVehicleImage(context, user.getAccessToken(), newVIN, user.getCountry(), image.toPath());
                    }
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
                File image = new File(imageDir, VIN + ".png");
                if (image.exists()) {
                    image.delete();
                }
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
            MainActivity.updateWidget(context);
            handler.sendEmptyMessage(0);
        }).start();
    }
}
