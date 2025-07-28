package com.cloudstuff.tictactoe.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        InAppType.ONE_TIME,
        InAppType.CONSUMABLE,
        InAppType.SUBSCRIPTION
})
public @interface InAppType {
    String ONE_TIME = "ONE_TIME";
    String CONSUMABLE = "CONSUMABLE";
    String SUBSCRIPTION = "SUBSCRIPTION";
}