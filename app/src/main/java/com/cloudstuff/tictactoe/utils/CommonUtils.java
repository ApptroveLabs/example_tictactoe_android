package com.cloudstuff.tictactoe.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.cloudstuff.tictactoe.TicTacToe;

public class CommonUtils {

    private static long lastClickTime;

    /**
     * Check Google Play Service is installed or not in device
     *
     * @param context - context
     * @return - true if GooglePlayService is installed else false
     */
    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    /**
     * Get OS Version
     *
     * @return - OS Version
     */
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Get Phone Model
     *
     * @return - phone Model
     */
    public static String getPhoneModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalizeAll(model);
        }
        return capitalizeAll(manufacturer) + " " + model;
    }

    /**
     * Capitalize all character in string
     *
     * @param str - data
     * @return - capitalized data
     */
    private static String capitalizeAll(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    /**
     * Get Device ID
     *
     * @return - android id
     */
    public static String getDeviceId() {
        return Settings.Secure.getString(TicTacToe.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Get App Version Name
     *
     * @return - App version name
     */
    public static String getAppVersionName() {
        String versionName = "";
        try {
            PackageInfo pInfo = TicTacToe.getInstance().getPackageManager().getPackageInfo(TicTacToe.getInstance().getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * Get App Version Code
     *
     * @return - App version code
     */
    public static String getAppVersionCode() {
        String versionCode = "";
        try {
            PackageInfo pInfo = TicTacToe.getInstance().getPackageManager().getPackageInfo(TicTacToe.getInstance().getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = String.valueOf(pInfo.getLongVersionCode());
            } else {
                versionCode = String.valueOf(pInfo.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * Open Web Browser
     *
     * @param context - activity
     * @param url     - url
     */
    public static void callWebBrowser(Context context, String url) {
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse(url));
        context.startActivity(httpIntent);
    }

    /**
     * it will return true if consecutive click occurs within {@link Constants.Delay#MIN_TIME_BETWEEN_CLICKS}
     *
     * @return true indicating do not allow any click, false otherwise
     */
    public static boolean isClickDisabled() {
        if ((SystemClock.elapsedRealtime() - lastClickTime) < Constants.Delay.MIN_TIME_BETWEEN_CLICKS) {
            return true;
        } else {
            lastClickTime = SystemClock.elapsedRealtime();
            return false;
        }
    }
}
