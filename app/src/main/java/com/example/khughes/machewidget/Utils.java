package com.example.khughes.machewidget;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.icu.text.MessageFormat;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
        return  WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_GERMANY) ||
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
                (isF150RegularCab(VIN) || isF150SuperCab(VIN) || isF150SuperCrew( VIN) || isF150Raptor(VIN));
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

    // Check to see if we recognize a VIN in general
    public static boolean isVINRecognized(String VIN) {
        return isMachE(VIN) || isF150(VIN) || isBronco(VIN) || isExplorer(VIN);
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
        else if (isF150(VIN) || isBronco(VIN) || isExplorer(VIN)) {
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

    public static String getWMI(String VIN) {
        if (isF150(VIN)) {
            return WIDGETMODE_F150;
        } else if (isBronco(VIN)) {
            return WIDGETMODE_BRONCO;
        } else if (isExplorer(VIN)) {
            return WIDGETMODE_EXPLORER;
        } else {
            return WIDGETMODE_MACHE;
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

    // Get the set of drawables for a particular style of F-150
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
            }
            else if (isBronco(VIN)) {
                return broncobase4x4Drawables;
            }
            else if (isExplorer(VIN) ) {
                return explorerSTDrawables;
            }
        }
        return macheDrawables;
    }

    public static Integer getLayoutByVIN(String VIN) {
        if (VIN != null && !VIN.equals("")) {
            if (isF150(VIN)) {
                return R.layout.f150_widget;
            }
            else if (isBronco(VIN)) {
                return R.layout.bronco_widget;
            }
            else if (isExplorer(VIN)) {
                return R.layout.explorer_widget;
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
        if(year != null) {
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

    // See if there was a crash, and if so dump the logcat output to a file
    public static void checkLogcat(Context context) {
        try {
            // Dump the crash buffer and exit
            Process process = Runtime.getRuntime().exec("logcat -d -b crash");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }

            // If we find something, write to logcat.txt file
            if (log.length() > 0) {
                Uri fileCollection = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    fileCollection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                }
                LocalDateTime time = LocalDateTime.now(ZoneId.systemDefault());
                String crashFile =  "machewidget-logcat-" + time.format(DateTimeFormatter.ofPattern("MM-dd-HH:mm:ss", Locale.US));
                InputStream inStream = new ByteArrayInputStream(log.toString().getBytes(StandardCharsets.UTF_8));
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, crashFile);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
                ContentResolver resolver = context.getContentResolver();
                Uri uri = resolver.insert(fileCollection, contentValues);
                if (uri == null) {
                    throw new IOException("Couldn't create MediaStore Entry");
                }
                OutputStream outStream = resolver.openOutputStream(uri);
                copyStreams(inStream,outStream);
                outStream.close();

                // Clear the crash log.
                Runtime.getRuntime().exec("logcat -c");
                Toast.makeText(context, MessageFormat.format("logcat crash file \"{0}\" copied to output folder.", crashFile), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
        }
    }

    public static boolean OTASupportCheck (String alertStatus) {
        return alertStatus != null && !alertStatus.toLowerCase().replaceAll("[^a-z0-9]", "").contains("doesntsupport");
    }

}
