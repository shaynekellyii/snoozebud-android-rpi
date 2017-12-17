package com.sleepsense.snoozebud;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by shayne on 2017-12-16.
 */

public class SnoozebudPrefs {

    private static final String DEFAULT_IP = "0.0.0.0";
    private static final String DEFAULT_SENSITIVITY = "25";

    public static void setupPrefs(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.getString("IP_ADDRESS", null) == null) {
            setPref("IP_ADDRESS", DEFAULT_IP, context);
        }
        if (sharedPreferences.getString("SENSITIVITY", null) == null) {
            setPref("SENSITIVITY", DEFAULT_SENSITIVITY, context);
        }
    }

    public static String getPref(String pref, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(pref, null);
    }

    public static void setPref(String key, String value, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
