package com.cloudstuff.tictactoe.utils;

import android.util.Log;

import timber.log.Timber;

/**
 * tree for timber that will report all the warnings and crashes to your crash reporting tool
 */
public class CrashReportingTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable throwable) {
        //logging only exceptions
        if (priority >= Log.ERROR && throwable != null) {
            //Use Firebase or Fabric to log exceptions here
        }
    }
}