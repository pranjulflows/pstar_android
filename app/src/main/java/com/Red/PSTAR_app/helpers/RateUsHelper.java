package com.Red.PSTAR_app.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.Red.PSTAR_app.R;
import com.Red.PSTAR_app.localization.Localizer;

/**
 * Created by Alexey Matrosov on 28.09.2018.
 */
public class RateUsHelper {
    private static final String StorageName = "pstar_storage";
    private static final String ShowedKey = "showed_rate_key";
    private static final String Email = "mailto:info@pstarexamapp.com";

    public static void showIfNeed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(StorageName, 0);
        if (prefs.getBoolean(ShowedKey, false))
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(context.getString(R.string.rate_popup_title));
        builder.setMessage(context.getString(R.string.rate_popup_message));
        builder.setNegativeButton(context.getString(R.string.rate_popup_close), null);
        builder.setPositiveButton(context.getString(R.string.rate_popup_rate), (dialog, which) -> {
            markAsShowed(context);

            final String appPackageName = context.getPackageName();
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (Exception ex) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        builder.setNeutralButton(context.getString(R.string.rate_popup_feedback), (dialog, which) -> {
            markAsShowed(context);

            Intent intent = createMailIntent();
            context.startActivity(Intent.createChooser(intent, "Send Email"));
        });

        builder.show();
    }

    private static Intent createMailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(Email));
        return intent;
    }

    private static void markAsShowed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(StorageName, 0);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(ShowedKey, true);
        editor.apply();
    }
}