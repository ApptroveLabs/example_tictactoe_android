package com.cloudstuff.tictactoe.model;

import com.cloudstuff.tictactoe.annotation.ThemeUnlockType;

public class Theme {
    private String themeId;
    private String themeName;
    private int themeUnlockDays;
    private boolean isThemeUnlock;
    private int circleIcon;
    private int crossIcon;
    @ThemeUnlockType
    private String themeUnlockType;
    private boolean isSelected;

    public Theme(String themeId, String themeName, int themeUnlockDays, boolean isThemeUnlock, int circleIcon, int crossIcon, @ThemeUnlockType String themeUnlockType, boolean isSelected) {
        this.themeId = themeId;
        this.themeName = themeName;
        this.themeUnlockDays = themeUnlockDays;
        this.isThemeUnlock = isThemeUnlock;
        this.circleIcon = circleIcon;
        this.crossIcon = crossIcon;
        this.themeUnlockType = themeUnlockType;
        this.isSelected = isSelected;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getThemeUnlockDays() {
        return themeUnlockDays;
    }

    public void setThemeUnlockDays(int themeUnlockDays) {
        this.themeUnlockDays = themeUnlockDays;
    }

    public boolean isThemeUnlock() {
        return isThemeUnlock;
    }

    public void setThemeUnlock(boolean themeUnlock) {
        isThemeUnlock = themeUnlock;
    }

    public int getCircleIcon() {
        return circleIcon;
    }

    public void setCircleIcon(int circleIcon) {
        this.circleIcon = circleIcon;
    }

    public int getCrossIcon() {
        return crossIcon;
    }

    public void setCrossIcon(int crossIcon) {
        this.crossIcon = crossIcon;
    }

    public String getThemeUnlockType() {
        return themeUnlockType;
    }

    public void setThemeUnlockType(String themeUnlockType) {
        this.themeUnlockType = themeUnlockType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Theme{" +
                "themeId='" + themeId + '\'' +
                ", themeName='" + themeName + '\'' +
                ", themeUnlockDays=" + themeUnlockDays +
                ", isThemeUnlock=" + isThemeUnlock +
                ", circleIcon=" + circleIcon +
                ", crossIcon=" + crossIcon +
                ", themeUnlockType='" + themeUnlockType + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
