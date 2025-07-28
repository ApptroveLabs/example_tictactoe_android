package com.cloudstuff.tictactoe.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.cloudstuff.tictactoe.TicTacToe;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AnalyticsUtils {

    private Bundle bundle;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Inject
    public AnalyticsUtils(Context context) {
        bundle = new Bundle();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        PreferenceUtils preferenceUtils = TicTacToe.getInstance().getAppComponent().providePreferenceUtils();

        //region #Common Parameters Value
        String deviceId = CommonUtils.getDeviceId();
        String phoneModel = CommonUtils.getPhoneModel();
        String osVersion = CommonUtils.getOsVersion();
        String versionName = CommonUtils.getAppVersionName();
        String versionCode = CommonUtils.getAppVersionCode();
        String currentLanguage = "en"; //preferenceUtils.getString(Constants.PreferenceConstant.CURRENT_LANGUAGE);
        //endregion

        //region #Firebase Common Parameters
        bundle.putString(AnalyticsCommonKeys.DEVICE_ID, deviceId);
        bundle.putString(AnalyticsCommonKeys.PHONE_MODEL, phoneModel);
        bundle.putString(AnalyticsCommonKeys.OS_VERSION, osVersion);
        bundle.putString(AnalyticsCommonKeys.VERSION_NAME, versionName);
        bundle.putString(AnalyticsCommonKeys.VERSION_CODE, versionCode);
        bundle.putString(AnalyticsCommonKeys.CURRENT_LANGUAGE, currentLanguage);
        //endregion
    }

    /**
     * Post Analytics Events
     *
     * @param eventName       - Event Name
     * @param parameterValues - Extra Parameters Value
     */
    public void logAnalyticsEvent(String eventName, Map<String, String> parameterValues) {
        //Check Analytics is enable or not
        if (!Constants.IS_ANALYTICS_ENABLE) {
            return;
        }

        //region #Firebase Events
        if (parameterValues != null) {
            for (String key : parameterValues.keySet()) {
                bundle.putString(key, parameterValues.get(key));
            }
        }

        mFirebaseAnalytics.logEvent(eventName, bundle);
        //endregion
    }

    /**
     * Analytics Common Parameters
     */
    public interface AnalyticsCommonKeys {
        String DEVICE_ID = "DEVICE_ID";
        String PHONE_MODEL = "PHONE_MODEL";
        String OS_VERSION = "OS_VERSION";
        String VERSION_NAME = "VERSION_NAME";
        String VERSION_CODE = "VERSION_CODE";
        String CURRENT_LANGUAGE = "CURRENT_LANGUAGE";
    }

    /**
     * Analytics Events
     */
    public interface AnalyticsEvents {
        String EVENT_SHOW_LEADERBOARD = "EVENT_SHOW_LEADERBOARD";
        String EVENT_SHOW_ACHIEVEMENT = "EVENT_SHOW_ACHIEVEMENT";

        String EVENT_SHOW_THEMES_PAGE = "EVENT_SHOW_THEMES_PAGE";
        String EVENT_OWNED_THEMES = "EVENT_OWNED_THEMES";

        String EVENT_SHOW_HISTORY_PAGE = "EVENT_SHOW_HISTORY_PAGE";

        String EVENT_SHARE = "EVENT_SHARE";
        String EVENT_RATE = "EVENT_RATE";
        String EVENT_FEEDBACK = "EVENT_FEEDBACK";
        String EVENT_MORE_APP = "EVENT_MORE_APP";

        String EVENT_REMOVE_ADS = "EVENT_REMOVE_ADS";

        String EVENT_PLAY_GAME = "EVENT_PLAY_GAME";
        String EVENT_GAME_RESULT = "EVENT_GAME_RESULT";
    }

    /**
     * Analytics Parameters
     */
    public interface AnalyticsKeys {
        String THEME_ID = "THEME_ID";
        String GAME_MODE = "GAME_MODE";
        String GAME_DIFFICULTY_LEVEL = "GAME_DIFFICULTY_LEVEL";
        String SELECTED_THEME_SHAPE = "SELECTED_THEME_SHAPE";
    }

    /**
     * Analytics static Value which we pass
     */
    public interface AnalyticsValue {
        String AI = "AI";
        String FRIENDS = "FRIENDS";

        String EASY = "EASY";
        String MEDIUM = "MEDIUM";
        String HARD = "HARD";
        String EXPERT = "EXPERT";

        String O = "O";
        String X = "X";

        String WIN = "WIN";
        String LOSE = "LOSE";
        String DRAW = "DRAW";
    }
}