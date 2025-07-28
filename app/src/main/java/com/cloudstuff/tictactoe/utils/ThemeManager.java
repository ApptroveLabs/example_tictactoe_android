package com.cloudstuff.tictactoe.utils;

import android.content.Context;

import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.annotation.ThemeUnlockType;
import com.cloudstuff.tictactoe.model.CommonConfiguration;
import com.cloudstuff.tictactoe.model.Theme;
import com.cloudstuff.tictactoe.model.ThemesConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    //region #Variables
    private static List<Theme> themeList;
    private static int advertiseLockedThemeCount;
    //endregion

    //region #Getter Setter
    public static List<Theme> getThemeList() {
        return themeList;
    }

    public static void setThemeList(List<Theme> themeList) {
        ThemeManager.themeList = themeList;
    }

    public static int getAdvertiseLockedThemeCount() {
        return advertiseLockedThemeCount;
    }

    public static void setAdvertiseLockedThemeCount(int advertiseLockedThemeCount) {
        ThemeManager.advertiseLockedThemeCount = advertiseLockedThemeCount;
    }
    //endregion

    //region #Custom Method
    public static void createTheme(Context context, PreferenceUtils preferenceUtils, CommonConfiguration commonConfiguration, ThemesConfiguration themesConfiguration) {
        themeList = new ArrayList<>();
        updateTheme(context, preferenceUtils, commonConfiguration, themesConfiguration);
    }

    public static void updateTheme(int position, Theme theme) {
        themeList.set(position, theme);
    }

    public static void updateTheme(Context context, PreferenceUtils preferenceUtils, CommonConfiguration commonConfiguration, ThemesConfiguration themesConfiguration) {
        themeList.clear();
        advertiseLockedThemeCount = 0;

        int unlockSpecialAdvertiseThemeTimerDays = commonConfiguration.getUnlockSpecialAdvertiseThemeTimerDays();
        if (unlockSpecialAdvertiseThemeTimerDays < 0) {
            unlockSpecialAdvertiseThemeTimerDays = Constants.RemoteConfig.UNLOCK_SPECIAL_ADVERTISE_THEME_TIMER_DAYS;
        }

        int unlockNormalAdvertiseThemeTimerDays = commonConfiguration.getUnlockNormalAdvertiseThemeTimerDays();
        if (unlockNormalAdvertiseThemeTimerDays < 0) {
            unlockNormalAdvertiseThemeTimerDays = Constants.RemoteConfig.UNLOCK_NORMAL_ADVERTISE_THEME_TIMER_DAYS;
        }

        //region #Classic Theme - Normal
        boolean isClassicThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_CLASSIC_THEME);
        //Added this logic as updated Theme Lock/Unlock Flow with ID
        if (!isClassicThemeUnlocked) {
            preferenceUtils.setBoolean(Constants.Themes.ID_CLASSIC_THEME, true);
            preferenceUtils.setString(Constants.PreferenceConstant.SELECTED_THEME, Constants.Themes.ID_CLASSIC_THEME);
            isClassicThemeUnlocked = true;
        }
        themeList.add(new Theme(Constants.Themes.ID_CLASSIC_THEME, context.getString(R.string.title_classic_theme),
                0, isClassicThemeUnlocked,
                R.drawable.theme_circle, R.drawable.theme_classic_cross,
                ThemeUnlockType.NORMAL, false));
        //endregion

        //Diwali Themes
        if (themesConfiguration.isIsDiwaliThemeEnable()) {
            //region #Crackers Theme - Advertise
            boolean isCrackersThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_CRACKERS_THEME);
            themeList.add(new Theme(Constants.Themes.ID_CRACKERS_THEME, context.getString(R.string.title_crackers_theme),
                    unlockSpecialAdvertiseThemeTimerDays, isCrackersThemeUnlocked,
                    R.drawable.ic_cracker_theme_circle, R.drawable.ic_cracker_theme_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isCrackersThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Light Theme - Advertise
            boolean isLightThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_LIGHTS_THEME);
            themeList.add(new Theme(Constants.Themes.ID_LIGHTS_THEME, context.getString(R.string.title_lights_theme),
                    unlockSpecialAdvertiseThemeTimerDays, isLightThemeUnlocked,
                    R.drawable.ic_light_theme_circle, R.drawable.ic_light_theme_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isLightThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Sweets Theme - Advertise
            boolean isSweetsThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_SWEETS_THEME);
            themeList.add(new Theme(Constants.Themes.ID_SWEETS_THEME, context.getString(R.string.title_sweets_theme),
                    unlockSpecialAdvertiseThemeTimerDays, isSweetsThemeUnlocked,
                    R.drawable.ic_sweet_theme_circle, R.drawable.ic_sweet_theme_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isSweetsThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Rangoli Theme - Advertise
            boolean isRangoliThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_RANGOLI_THEME);
            themeList.add(new Theme(Constants.Themes.ID_RANGOLI_THEME, context.getString(R.string.title_rangoli_theme),
                    unlockSpecialAdvertiseThemeTimerDays, isRangoliThemeUnlocked,
                    R.drawable.ic_rangoli_theme_circle, R.drawable.ic_rangoli_theme_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isRangoliThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion
        }

        //Shape Themes
        if (themesConfiguration.isIsShapeThemeEnable()) {
            //region #Plus Theme - Advertise
            boolean isPlusThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_PLUS_THEME);
            themeList.add(new Theme(Constants.Themes.ID_PLUS_THEME, context.getString(R.string.title_plus_theme),
                    unlockNormalAdvertiseThemeTimerDays, isPlusThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_plus_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isPlusThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Square Theme - Advertise
            boolean isSquareThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_SQUARE_THEME);
            themeList.add(new Theme(Constants.Themes.ID_SQUARE_THEME, context.getString(R.string.title_square_theme),
                    unlockNormalAdvertiseThemeTimerDays, isSquareThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_square_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isSquareThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Polygon Theme - Advertise
            boolean isPolygonThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_POLYGON_THEME);
            themeList.add(new Theme(Constants.Themes.ID_POLYGON_THEME, context.getString(R.string.title_polygon_theme),
                    unlockNormalAdvertiseThemeTimerDays, isPolygonThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_polygon_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isPolygonThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Hexagon Theme - Advertise
            boolean isHexagonThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_HEXAGON_THEME);
            themeList.add(new Theme(Constants.Themes.ID_HEXAGON_THEME, context.getString(R.string.title_hexagon_theme),
                    unlockNormalAdvertiseThemeTimerDays, isHexagonThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_hexagon_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isHexagonThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Octagon Theme - Advertise
            boolean isOctagonThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_OCTAGON_THEME);
            themeList.add(new Theme(Constants.Themes.ID_OCTAGON_THEME, context.getString(R.string.title_octagon_theme),
                    unlockNormalAdvertiseThemeTimerDays, isOctagonThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_octagon_cross,
                    ThemeUnlockType.ADVERTISE, false));
            if (!isOctagonThemeUnlocked) {
                advertiseLockedThemeCount += 1;
            }
            //endregion

            //region #Triangle Theme - InApp
            boolean isTriangleThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_TRIANGLE_THEME);
            themeList.add(new Theme(Constants.Themes.ID_TRIANGLE_THEME, context.getString(R.string.title_triangle_theme),
                    0, isTriangleThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_triangle_cross,
                    ThemeUnlockType.IN_APP, false));
            //endregion

            //region #Diamond Theme - InApp
            boolean isDiamondThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_DIAMOND_THEME);
            themeList.add(new Theme(Constants.Themes.ID_DIAMOND_THEME, context.getString(R.string.title_diamond_theme),
                    0, isDiamondThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_diamond_cross,
                    ThemeUnlockType.IN_APP, false));
            //endregion

            //region #Star Theme - InApp
            boolean isStarThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_STAR_THEME);
            themeList.add(new Theme(Constants.Themes.ID_STAR_THEME, context.getString(R.string.title_star_theme),
                    0, isStarThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_star_cross,
                    ThemeUnlockType.IN_APP, false));
            //endregion

            //region Heart Theme - InApp
            boolean isHeartThemeUnlocked = preferenceUtils.getBoolean(Constants.Themes.ID_HEART_THEME);
            themeList.add(new Theme(Constants.Themes.ID_HEART_THEME, context.getString(R.string.title_heart_theme),
                    0, isHeartThemeUnlocked,
                    R.drawable.theme_circle, R.drawable.theme_heart_cross,
                    ThemeUnlockType.IN_APP, false));
            //endregion
        }
    }
    //endregion
}