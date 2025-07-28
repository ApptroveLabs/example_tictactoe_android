package com.cloudstuff.tictactoe.annotation;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        DifficultyLevel.EASY,
        DifficultyLevel.MEDIUM,
        DifficultyLevel.HARD,
        DifficultyLevel.EXPERT
})
public @interface DifficultyLevel {
    String EASY = "EASY";
    String MEDIUM = "MEDIUM";
    String HARD = "HARD";
    String EXPERT = "EXPERT";
}