package com.Red.PSTAR_app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.Red.PSTAR_app.utils.PSTARApp.storeAppLocale;

public class MyContextWrapper {
    private final static String TAG = MyContextWrapper.class.getSimpleName();


    public static Context wrap(Context mContext, String language) {

        Log.d(TAG, "MyContextWrapper:called ");


        Log.d(TAG, "currentLanguage: " + AppSharedMethod.getCurrentLocale(mContext).getLanguage() + " " + AppSharedMethod.getCurrentLocale(mContext).getDisplayLanguage());

        SharedPreferences mSharedPreferences = PSTARApp.getInstance().getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE);

        if (language.equalsIgnoreCase("")) {

            language = mSharedPreferences.getString(AppConstants.PREF_LANG, AppConstants.EN);

        } else {

            mSharedPreferences.edit().putString(AppConstants.PREF_LANG, language).apply();
        }

        storeAppLocale = new Locale(language);

        Resources res = mContext.getResources();
        Configuration configuration = res.getConfiguration();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(storeAppLocale);
            LocaleList localeList = new LocaleList(storeAppLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            mContext = mContext.createConfigurationContext(configuration);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(storeAppLocale);
            mContext = mContext.createConfigurationContext(configuration);

        } else {
            configuration.locale = storeAppLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return mContext;
    }
}
