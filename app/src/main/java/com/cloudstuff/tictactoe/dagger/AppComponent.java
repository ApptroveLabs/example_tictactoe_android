package com.cloudstuff.tictactoe.dagger;

import android.content.Context;

import com.google.gson.Gson;
import com.cloudstuff.tictactoe.TicTacToe;
import com.cloudstuff.tictactoe.network.NetworkUtils;
import com.cloudstuff.tictactoe.utils.AdMobUtils;
import com.cloudstuff.tictactoe.utils.AnalyticsUtils;
import com.cloudstuff.tictactoe.utils.GsonUtils;
import com.cloudstuff.tictactoe.utils.MediaUtils;
import com.cloudstuff.tictactoe.utils.PlayCoreUtils;
import com.cloudstuff.tictactoe.utils.PreferenceUtils;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(TicTacToe ticTacToe);

    //required dependencies
    Context provideContext();

    NetworkUtils provideNetworkUtils();

    Gson provideGson();

    Bus provideBus();

    PreferenceUtils providePreferenceUtils();

    GsonUtils provideMasterGson();

    AdMobUtils provideAdmobUtils();

    MediaUtils provideMediaUtils();

    PlayCoreUtils providePlayCoreUtils();

    AnalyticsUtils provideAnalyticsUtils();
}