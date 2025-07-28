package com.cloudstuff.tictactoe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThemesConfiguration {
    @SerializedName("is_shape_theme_enable")
    @Expose
    private boolean isShapeThemeEnable;
    @SerializedName("is_diwali_theme_enable")
    @Expose
    private boolean isDiwaliThemeEnable;
    @SerializedName("is_christmas_theme_enable")
    @Expose
    private boolean isChristmasThemeEnable;
    @SerializedName("is_halloween_theme_enable")
    @Expose
    private boolean isHalloweenThemeEnable;
    @SerializedName("is_easter_egg_theme_enable")
    @Expose
    private boolean isEasterEggThemeEnable;

    public ThemesConfiguration(boolean isShapeThemeEnable, boolean isDiwaliThemeEnable, boolean isChristmasThemeEnable, boolean isHalloweenThemeEnable, boolean isEasterEggThemeEnable) {
        this.isShapeThemeEnable = isShapeThemeEnable;
        this.isDiwaliThemeEnable = isDiwaliThemeEnable;
        this.isChristmasThemeEnable = isChristmasThemeEnable;
        this.isHalloweenThemeEnable = isHalloweenThemeEnable;
        this.isEasterEggThemeEnable = isEasterEggThemeEnable;
    }

    public boolean isIsShapeThemeEnable() {
        return isShapeThemeEnable;
    }

    public void setIsShapeThemeEnable(boolean isShapeThemeEnable) {
        this.isShapeThemeEnable = isShapeThemeEnable;
    }

    public boolean isIsDiwaliThemeEnable() {
        return isDiwaliThemeEnable;
    }

    public void setIsDiwaliThemeEnable(boolean isDiwaliThemeEnable) {
        this.isDiwaliThemeEnable = isDiwaliThemeEnable;
    }

    public boolean isIsChristmasThemeEnable() {
        return isChristmasThemeEnable;
    }

    public void setIsChristmasThemeEnable(boolean isChristmasThemeEnable) {
        this.isChristmasThemeEnable = isChristmasThemeEnable;
    }

    public boolean isIsHalloweenThemeEnable() {
        return isHalloweenThemeEnable;
    }

    public void setIsHalloweenThemeEnable(boolean isHalloweenThemeEnable) {
        this.isHalloweenThemeEnable = isHalloweenThemeEnable;
    }

    public boolean isIsEasterEggThemeEnable() {
        return isEasterEggThemeEnable;
    }

    public void setIsEasterEggThemeEnable(boolean isEasterEggThemeEnable) {
        this.isEasterEggThemeEnable = isEasterEggThemeEnable;
    }

    @Override
    public String toString() {
        return "ThemesConfiguration{" +
                "isShapeThemeEnable=" + isShapeThemeEnable +
                ", isDiwaliThemeEnable=" + isDiwaliThemeEnable +
                ", isChristmasThemeEnable=" + isChristmasThemeEnable +
                ", isHalloweenThemeEnable=" + isHalloweenThemeEnable +
                ", isEasterEggThemeEnable=" + isEasterEggThemeEnable +
                '}';
    }
}
