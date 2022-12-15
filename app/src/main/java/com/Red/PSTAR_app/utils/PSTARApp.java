package com.Red.PSTAR_app.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;

import com.Red.PSTAR_app.localization.CategoryKey;

import java.util.HashMap;
import java.util.Locale;

public class PSTARApp extends Application {

    private static PSTARApp instance;
    public static Locale storeAppLocale;
    public static HashMap<String, String> mCatNameEnHashMap = new HashMap<>();
    public static HashMap<String, String> mCatNameFRHashMap = new HashMap<>();


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mCatNameEnHashMap.put(CategoryKey.COLLISION_AVOIDANCE, "COLLISION AVOIDANCE");
        mCatNameEnHashMap.put(CategoryKey.VISUAL_SIGNALS, "VISUAL SIGNALS");
        mCatNameEnHashMap.put(CategoryKey.COMMUNICATIONS, "COMMUNICATIONS");
        mCatNameEnHashMap.put(CategoryKey.AERODROMES, "AERODROMES");
        mCatNameEnHashMap.put(CategoryKey.EQUIPMENT, "EQUIPMENT");
        mCatNameEnHashMap.put(CategoryKey.PILOT_RESPONSIBILITIES, "PILOT RESPONSIBILITIES");
        mCatNameEnHashMap.put(CategoryKey.WAKE_TURBULENCE, "WAKE TURBULENCE");
        mCatNameEnHashMap.put(CategoryKey.AEROMEDICAL, "AEROMEDICAL");
        mCatNameEnHashMap.put(CategoryKey.FLIGHT_PLANS, "FLIGHT PLANS");
        mCatNameEnHashMap.put(CategoryKey.CLEARANCES_AND_INSTRUCTIONS, "CLEARANCES AND INSTRUCTIONS");
        mCatNameEnHashMap.put(CategoryKey.AIRCRAFT_OPERATIONS, "AIRCRAFT OPERATIONS");
        mCatNameEnHashMap.put(CategoryKey.GENERAL_AIRSPACE, "GENERAL AIRSPACE");
        mCatNameEnHashMap.put(CategoryKey.CONTROLLED_AIRSPACE, "CONTROLLED AIRSPACE");
        mCatNameEnHashMap.put(CategoryKey.AVIATION_OCCURRENCES, "AVIATION OCCURRENCES");
        mCatNameEnHashMap.put(CategoryKey.Exam, "PRACTICE EXAM");

        mCatNameFRHashMap.put(CategoryKey.COLLISION_AVOIDANCE, "ÉVITEMENT ABORDAGE");
        mCatNameFRHashMap.put(CategoryKey.VISUAL_SIGNALS, "SIGNAUX VISUELS");
        mCatNameFRHashMap.put(CategoryKey.COMMUNICATIONS, "COMMUNICATIONS");
        mCatNameFRHashMap.put(CategoryKey.AERODROMES, "AÉRODROMES");
        mCatNameFRHashMap.put(CategoryKey.EQUIPMENT, "ÉQUIPEMENTS");
        mCatNameFRHashMap.put(CategoryKey.PILOT_RESPONSIBILITIES, "RESPONSABILITÉ DU PILOTE");
        mCatNameFRHashMap.put(CategoryKey.WAKE_TURBULENCE, "TURBULENCE DE SILLAGE");
        mCatNameFRHashMap.put(CategoryKey.AEROMEDICAL, "AÉROMÉDICAL");
        mCatNameFRHashMap.put(CategoryKey.FLIGHT_PLANS, "PLANS DE VOL ET ITINÉRAIRES DE VOL");
        mCatNameFRHashMap.put(CategoryKey.CLEARANCES_AND_INSTRUCTIONS, "AUTORISATIONS ET INSTRUCTIONS");
        mCatNameFRHashMap.put(CategoryKey.AIRCRAFT_OPERATIONS, "EXPLOITATION D’AÉRONEF");
        mCatNameFRHashMap.put(CategoryKey.GENERAL_AIRSPACE, "ESPACE AÉRIEN − EN GÉNÉRAL");
        mCatNameFRHashMap.put(CategoryKey.CONTROLLED_AIRSPACE, "ESPACE AÉRIEN CONTRÔLÉ");
        mCatNameFRHashMap.put(CategoryKey.AVIATION_OCCURRENCES, "FAITS AÉRONAUTIQUES");
        mCatNameFRHashMap.put(CategoryKey.Exam, "EXAMEN PRATIGUE");

    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PSTARApp getInstance() {
        return instance;
    }

    public static HashMap<String, String> getCategoryEnHashMap() {
        return mCatNameEnHashMap;
    }

    public static HashMap<String, String> getCategoryFRHashMap() {
        return mCatNameFRHashMap;
    }
}
