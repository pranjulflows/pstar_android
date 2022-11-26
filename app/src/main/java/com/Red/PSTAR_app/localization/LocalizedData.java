package com.Red.PSTAR_app.localization;

/**
 * Created by Alexey Matrosov on 28.09.2018.
 */
public class LocalizedData {
    private String english;
    private String french;

    public LocalizedData(String english, String french) {
        this.english = english;
        this.french = french;
    }

    // TODO: check for null for locale
    public String getLocalizedData(Localizer.Locale locale) {
        switch (locale) {
            case English: return english;
            case French: return french;
            default: throw new RuntimeException("Unknown locale");
        }
    }
}