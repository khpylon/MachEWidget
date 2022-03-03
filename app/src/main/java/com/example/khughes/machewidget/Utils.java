package com.example.khughes.machewidget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {
    public static final int WORLD_MANUFACTURING_IDENTIFIER_START_INDEX = 1 - 1;
    public static final int WORLD_MANUFACTURING_IDENTIFIER_END_INDEX = 3;
    public static final String WORLD_MANUFACTURING_IDENTIFIER_MACHE = "3FM";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_F150 = "1FT";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_BRONCO = "1FM";

    public static final int LINE_SERIES_START_INDEX = 5 - 1;
    public static final int LINE_SERIES_END_INDEX = 7;
    //    public static final String LINE_SERIES_MACHE_SELECT_RWD = "K1R"; // select RWD
//    public static final String LINE_SERIES_MACHE_SELECT_AWD = "K1S"; // select RWD (AWD?
//    public static final String LINE_SERIES_MACHE_CAROUTE1_RWD = "K2R"; // Route 1 RWD
//    public static final String LINE_SERIES_MACHE_PREMIUM_RWD = "K3R"; // Premium RWD
//    public static final String LINE_SERIES_MACHE_PREMIUM_AWD = "K3S"; // Premium AWD?
//    public static final String LINE_SERIES_MACHE_GT_RWD = "K4S"; // GT AWD
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

    public static final String F150_REGULAR_CAB = "F-150 Regular Cab";
    public static final String F150_SUPER_CAB = "F-150 SuperCab";
    public static final String F150_SUPER_CREW = "F-150 SuperCrew";
    public static final String F150_RAPTOR = "F-150 Raptor";

    public static boolean isMachE(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_MACHE);
    }

    public static boolean isF150(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_F150);
    }

    public static boolean isBronco(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_BRONCO);
    }

    private static final Set<String> regularCabs;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_F150_REGULAR_4X2);
        tmpSet.add(LINE_SERIES_F150_REGULAR_4X4);
        regularCabs = tmpSet;
    }

    public static boolean isF150RegularCab(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return regularCabs.contains(lineSeries);
    }

    private static final Set<String> superCabs;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_F150_SUPERCAB_4X2);
        tmpSet.add(LINE_SERIES_F150_SUPERCAB_4X4);
        superCabs = tmpSet;
    }

    public static boolean isF150SuperCab(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return superCabs.contains(lineSeries);
    }

    private static final Set<String> superCrews;

    static {
        Set<String> tmpSet = new HashSet<>();
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X2);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X4);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X4_POLICE);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X2_SSV);
        tmpSet.add(LINE_SERIES_F150_SUPERCREW_4X4_SSV);
        superCrews = tmpSet;
    }

    public static boolean isF150SuperCrew(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return superCrews.contains(lineSeries);
    }

    public static boolean isF150Raptor(String VIN) {
        String lineSeries = VIN.substring(LINE_SERIES_START_INDEX, LINE_SERIES_END_INDEX);
        return lineSeries.equals(LINE_SERIES_F150_SUPERCREW_4X4_RAPTOR);
    }

    public static String identifyF150Line(String VIN) {
        if (isF150RegularCab(VIN)) {
            return F150_REGULAR_CAB;
        } else if (isF150SuperCab(VIN)) {
            return F150_SUPER_CAB;
        } else if (isF150SuperCrew(VIN)) {
            return F150_SUPER_CREW;
        } else if (isF150Raptor(VIN)) {
            return F150_RAPTOR;
        } else {
            return "";
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
        tmpMap.put(TAILGATE, R.drawable.mache_trunk);
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
            if (isBronco(VIN)) {
                return regcabDrawables;
            }
        }
        return macheDrawables;
    }

    public static Integer getLayoutByVIN(String VIN) {
        if (VIN != null && !VIN.equals("")) {
            if (isF150(VIN)) {
                return R.layout.f150_widget;
            }
            if (isBronco(VIN)) {
                return R.layout.f150_widget;
            }
        }
        return R.layout.mache_widget;
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

}
