package com.cloudstuff.tictactoe.dagger;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApp() {
        return application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    Bus provideBus() {
        return new Bus();
    }

    @Singleton
    @Provides
    Gson provideGson() {
        return new Gson();
    }
}