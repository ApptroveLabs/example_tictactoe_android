package com.cloudstuff.tictactoe.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        GameMode.AI,
        GameMode.FRIEND,
        GameMode.ONLINE
})
public @interface GameMode {
    String AI = "AI";
    String FRIEND = "FRIEND";
    String ONLINE = "ONLINE";
}