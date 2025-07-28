package com.cloudstuff.tictactoe.utils;

public class Constants {

    //True - Show Custom Logs and Messages [Make it true in Development]
    //False - Disable Custom logs and messages [Make it false in Release Build]
    public static final boolean IS_DEBUG_ENABLE = false;

    //True - Maintain Analytics Data
    //False - Disable Analytics Data
    public static final boolean IS_ANALYTICS_ENABLE = true;

    /**
     * Default Values used in Game
     */
    public interface AppConstant {
        String PLAYER_AI = "AI";
        String TEXT_PLAIN = "text/html";

        int REMOTE_CONFIG_FETCH_INTERVAL = 12; //in Hours
    }

    /**
     * Manage Preference Keys
     */
    public interface PreferenceConstant {
        String IS_FIRST_TIME = "IS_FIRST_TIME";
        String IS_AUDIO_ON = "IS_AUDIO_ON";
        String IS_NOTIFICATION_ON = "IS_NOTIFICATION_ON";
        String IS_VIBRATION_ON = "IS_VIBRATION_ON";

        String DEFEAT_AI_COUNT = "DEFEAT_AI_COUNT";
        String PLAY_AI_COUNT = "PLAY_AI_COUNT";
        String PLAY_FRIENDS_COUNT = "PLAY_FRIENDS_COUNT";
        String HOME_BUTTON_PRESS_COUNT = "HOME_BUTTON_PRESS_COUNT";
        String PLAYER_ONE_LAST_NAME = "PLAYER_ONE_LAST_NAME";
        String PLAYER_TWO_LAST_NAME = "PLAYER_TWO_LAST_NAME";

        String SELECTED_THEME = "SELECTED_THEME";
        String IS_AD_REMOVED = "IS_AD_REMOVED";

        String INSTANT_UPDATE_REMOTE_CONFIG = "INSTANT_UPDATE_REMOTE_CONFIG";
        String FCM_TOKEN = "TOKEN";
        String MY_PLAYER_ID = "PLAYER_ID";
        String MY_PLAYER_NAME = "PLAYER_NAME";
    }

    /**
     * Manage Database Names, Columns, Type etc.
     */
    public interface DatabaseConstants {
        String DATABASE_NAME = "TicTacToeDB";
        String DB_VERSION = "v2";
        String TABLE_PLAYER = "players";
        String TABLE_GAMES = "games";
    }

    /**
     * Manage Bundles Keys
     */
    public interface BundleExtra {
        String GAME_MODE = "GAME_MODE";
        String DIFFICULTY_LEVEL = "DIFFICULTY_LEVEL";
        String SELECTED_SINGLE_PLAYER = "SELECTED_SINGLE_PLAYER";
        String PLAYER_ONE_NAME = "PLAYER_ONE_NAME";
        String PLAYER_TWO_NAME = "PLAYER_TWO_NAME";
        String PLAYER_ONE_CIRCLE_SELECTED = "PLAYER_ONE_CIRCLE_SELECTED";
        String PLAYER_ONE_ID = "PLAYER_ONE_ID";
        String PLAYER_TWO_ID = "PLAYER_TWO_ID";
        String IS_HOST = "IS_HOST";
    }

    /**
     * Manage Custom Delays in Game
     */
    public interface Delay {
        int MIN_TIME_BETWEEN_CLICKS = 300; //in ms
        int SPLASH_INTERVAL = 3000; //in ms
        int CPU_TURN_DELAY = 500; //in ms
        int PLAY_AGAIN_DELAY = 300; //in ms
        int WIDGET_DELAY = 500; //in ms
    }

    /**
     * Manage In-App Purchase SKU Keys
     */
    public interface InAppPurchase {
        String SKU_TRIANGLE_THEME = "com.progressio.tictactoe.triangletheme";
        String SKU_DIAMOND_THEME = "com.progressio.tictactoe.diamondtheme";
        String SKU_STAR_THEME = "com.progressio.tictactoe.startheme";
        String SKU_HEART_THEME = "com.progressio.tictactoe.hearttheme";
        String SKU_REMOVE_ADS = "com.progressio.tictactoe.removeads";
    }

    /**
     * Manage Theme IDs
     */
    public interface Themes {
        String THEME_TIMER = "_TIMER";

        String ID_CLASSIC_THEME = "ID_CLASSIC_THEME";
        String ID_CRACKERS_THEME = "ID_CRACKERS_THEME";
        String ID_LIGHTS_THEME = "ID_LIGHTS_THEME";
        String ID_SWEETS_THEME = "ID_SWEETS_THEME";
        String ID_RANGOLI_THEME = "ID_RANGOLI_THEME";
        String ID_TRIANGLE_THEME = "ID_TRIANGLE_THEME";
        String ID_DIAMOND_THEME = "ID_DIAMOND_THEME";
        String ID_STAR_THEME = "ID_STAR_THEME";
        String ID_PLUS_THEME = "ID_PLUS_THEME";
        String ID_HEART_THEME = "ID_HEART_THEME";
        String ID_SQUARE_THEME = "ID_SQUARE_THEME";
        String ID_HEXAGON_THEME = "ID_HEXAGON_THEME";
        String ID_POLYGON_THEME = "ID_POLYGON_THEME";
        String ID_OCTAGON_THEME = "ID_OCTAGON_THEME";
    }

    /**
     * Manage Performance Monitoring Keys and Traces
     */
    public interface PerformanceMonitoring {
        String THEME_TRACE = "theme_trace";
        String HISTORY_TRACE = "history_trace";
        String PLAYER_NAME_THEME_TRACE = "player_name_theme_trace";
    }

    /**
     * Manage Remote config Parameter Keys
     */
    public interface RemoteConfig {
        //region #Remote Config Default Value
        int SHOW_INTERSTITIAL_AD_AFTER_GAME = 5;
        int SHOW_IN_APP_REVIEW_AFTER_GAME = 100;
        int UNLOCK_NORMAL_ADVERTISE_THEME_TIMER_DAYS = 7; //In Days
        int UNLOCK_SPECIAL_ADVERTISE_THEME_TIMER_DAYS = 1; //In Days
        int HISTORY_RECORD_LIMIT = 10;

        String FEEDBACK_EMAIL = "<FEEDBACK_EMAIL>";
        String TERMS_CONDITION_URL = "<TERMS_CONDITION_URL>";
        String PRIVACY_POLICY_URL = "<PRIVACY_POLICY_URL>";
        String DYNAMIC_LINK_URL = "<DYNAMIC_LINK_URL>";
        String PLAY_STORE_DEVELOPER_NAME = "<MORE_APP_URL>";

        boolean IS_SHAPE_THEME_ENABLE = true; // Default
        boolean IS_DIWALI_THEME_ENABLE = false; // Oct - 2021 Update
        boolean IS_CHRISTMAS_THEME_ENABLE = false; // December - 2020 Update
        boolean IS_HALLOWEEN_THEME_ENABLE = false; //October - 2021 Update
        boolean IS_EASTER_EGG_THEME_ENABLE = false; //March/April - 2021 Update
        //endregion

        //region #Remote Config Keys
        String TIC_TAC_TOE_COMMON_CONFIGURATION = "tictactoe_common_configuration";
        String TIC_TAC_TOE_THEMES_CONFIGURATION = "tictactoe_themes_configuration";
        //endregion
    }
}