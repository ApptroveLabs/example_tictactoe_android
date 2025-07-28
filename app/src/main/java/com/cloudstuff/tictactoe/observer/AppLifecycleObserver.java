package com.cloudstuff.tictactoe.observer;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.cloudstuff.tictactoe.TicTacToe;

import timber.log.Timber;

public class AppLifecycleObserver implements LifecycleObserver {

    public static final String TAG = AppLifecycleObserver.class.getName();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Timber.d("onEnterForeground");
        TicTacToe.setAppBackgroundStatus(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Timber.d("onEnterBackground");
        TicTacToe.setAppBackgroundStatus(true);
    }

}