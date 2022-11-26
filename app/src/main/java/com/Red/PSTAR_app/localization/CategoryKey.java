package com.Red.PSTAR_app.localization;

/**
 * Created by Alexey Matrosov on 29.09.2018.
 */
public class CategoryKey {
    public static final String COLLISION_AVOIDANCE = "COLLISION AVOIDANCE";
    public static final String VISUAL_SIGNALS = "VISUAL SIGNALS";
    public static final String COMMUNICATIONS = "COMMUNICATIONS";
    public static final String AERODROMES = "AERODROMES";
    public static final String EQUIPMENT = "EQUIPMENT";
    public static final String PILOT_RESPONSIBILITIES = "PILOT RESPONSIBILITIES";
    public static final String WAKE_TURBULENCE = "WAKE TURBULENCE";
    public static final String AEROMEDICAL = "AEROMEDICAL";
    public static final String FLIGHT_PLANS = "FLIGHT PLANS";
    public static final String CLEARANCES_AND_INSTRUCTIONS = "CLEARANCES AND INSTRUCTIONS";
    public static final String AIRCRAFT_OPERATIONS = "AIRCRAFT OPERATIONS";
    public static final String GENERAL_AIRSPACE = "GENERAL AIRSPACE";
    public static final String CONTROLLED_AIRSPACE = "CONTROLLED AIRSPACE";
    public static final String AVIATION_OCCURRENCES = "AVIATION OCCURRENCES";
    public static final String Exam = "Exam";
    public static final String[] CATEGORIES = {
            COLLISION_AVOIDANCE,
            VISUAL_SIGNALS,
            COMMUNICATIONS,
            AERODROMES,
            EQUIPMENT,
            PILOT_RESPONSIBILITIES,
            WAKE_TURBULENCE,
            AEROMEDICAL,
            FLIGHT_PLANS,
            CLEARANCES_AND_INSTRUCTIONS,
            AIRCRAFT_OPERATIONS,
            GENERAL_AIRSPACE,
            CONTROLLED_AIRSPACE,
            AVIATION_OCCURRENCES,
            Exam
    };

    public static String getKeyByNumber(String number) {
        String[] numberParts = number.split("\\.");
        switch (numberParts[0]) {
            case "1":
                return COLLISION_AVOIDANCE;
            case "2":
                return VISUAL_SIGNALS;
            case "3":
                return COMMUNICATIONS;
            case "4":
                return AERODROMES;
            case "5":
                return EQUIPMENT;
            case "6":
                return PILOT_RESPONSIBILITIES;
            case "7":
                return WAKE_TURBULENCE;
            case "8":
                return AEROMEDICAL;
            case "9":
                return FLIGHT_PLANS;
            case "10":
                return CLEARANCES_AND_INSTRUCTIONS;
            case "11":
                return AIRCRAFT_OPERATIONS;
            case "12":
                return GENERAL_AIRSPACE;
            case "13":
                return CONTROLLED_AIRSPACE;
            case "14":
                return AVIATION_OCCURRENCES;
        }
        return null;
    }
}