package com.Red.PSTAR_app.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.Locale;

public final class AppSharedMethod {

    public static String getUserId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public static boolean isEnglishLayout(Context context) {
        return getCurrentLocale(context).getLanguage().equals(AppConstants.EN);
    }

    public static boolean isFrenchLayout(Context context) {
        return getCurrentLocale(context).getLanguage().equals(AppConstants.FR);
    }
}
