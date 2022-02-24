package com.example.khughes.machewidget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {
    public static final int WORLD_MANUFACTURING_IDENTIFIER_START_INDEX = 1 - 1;
    public static final int WORLD_MANUFACTURING_IDENTIFIER_END_INDEX = 3 - 1;
    public static final String WORLD_MANUFACTURING_IDENTIFIER_MACHE = "3FM";
    public static final String WORLD_MANUFACTURING_IDENTIFIER_F150 = "1FT";

    public static final int LINE_SERIES_START_INDEX = 5 - 1;
    public static final int LINE_SERIES_END_INDEX = 7 - 1;
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

    public static final String F150_REGULAR_CAB = "F150 Regular Cab";
    public static final String F150_SUPER_CAB = "F150 SuperCab";
    public static final String F150_SUPER_CREW = "F150 SuperCrew";
    public static final String F150_RAPTOR = "F150 Raptor";


    public static boolean isMachE(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_MACHE);
    }

    public static boolean isF150(String VIN) {
        String WMI = VIN.substring(WORLD_MANUFACTURING_IDENTIFIER_START_INDEX, WORLD_MANUFACTURING_IDENTIFIER_END_INDEX);
        return WMI.equals(WORLD_MANUFACTURING_IDENTIFIER_F150);
    }

    private static final Set<String> regularCabs;

    static {
        Set<String> tmpSet = new HashSet<String>();
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
        Set<String> tmpSet = new HashSet<String>();
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
        Set<String> tmpSet = new HashSet<String>();
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

    public static final Map<String, String> states;
    static {
        Map<String, String> tmpStates = new HashMap<String, String>();
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
