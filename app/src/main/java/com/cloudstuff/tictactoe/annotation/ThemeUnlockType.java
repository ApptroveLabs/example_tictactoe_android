package com.cloudstuff.tictactoe.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        ThemeUnlockType.NORMAL,
        ThemeUnlockType.ADVERTISE,
        ThemeUnlockType.IN_APP
})
public @interface ThemeUnlockType {
    String NORMAL = "NORMAL";
    String ADVERTISE = "ADVERTISE";
    String IN_APP = "IN_APP";
}