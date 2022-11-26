package com.Red.PSTAR_app.localization;

import android.support.annotation.DrawableRes;

/**
 * Created by Alexey Matrosov on 29.09.2018.
 */
public class LocalizedImage {
    @DrawableRes private int english;
    @DrawableRes private int french;

    public LocalizedImage(int english, int french) {
        this.english = english;
        this.french = french;
    }

    // TODO: check for null for locale
    @DrawableRes
    public int getLocalizedData(Localizer.Locale locale) {
        switch (locale) {
            case English: return english;
            case French: return french;
            default: throw new RuntimeException("Unknown locale");
        }
    }
}