package com.cloudstuff.tictactoe;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.cloudstuff.tictactoe.dagger.DaggerAppComponent;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.cloudstuff.tictactoe.dagger.AppComponent;
import com.cloudstuff.tictactoe.dagger.AppModule;
import com.cloudstuff.tictactoe.observer.AppLifecycleObserver;
import com.cloudstuff.tictactoe.utils.AdMobUtils;
import com.cloudstuff.tictactoe.utils.Constants;
import com.cloudstuff.tictactoe.utils.CrashReportingTree;
import com.cloudstuff.tictactoe.utils.PreferenceUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.trackier.sdk.TrackierSDK;
import com.trackier.sdk.TrackierSDKConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class TicTacToe extends Application {

    //region #Variables
    private static TicTacToe ticTacToeInstance;
    private AppComponent appComponent;
    private static boolean sIsAppInBackground;

    private static final String TR_DEV_KEY  = "<PLACE_SDK_OR_APP_KEY_HERE>";
    //endregion

    //region #Getter Setter Methods
    public static TicTacToe getInstance() {
        return ticTacToeInstance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static boolean isAppInBackground() {
        return sIsAppInBackground;
    }

    public static void setAppBackgroundStatus(boolean isAppInBackground) {
        sIsAppInBackground = isAppInBackground;
    }
    //endregion

    //region #InBuilt Methods
    @Override
    public void onCreate() {
        super.onCreate();
        ticTacToeInstance = this;

        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);

        //injecting dependencies
        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);

        //Preferences
        PreferenceUtils preferenceUtils = getInstance().getAppComponent().providePreferenceUtils();

        TrackierSDKConfig  sdkConfig = new TrackierSDKConfig(this, TR_DEV_KEY, "development");
        TrackierSDK.initialize(sdkConfig);


        // Fb Sdk

        FacebookSdk.setClientToken(String.valueOf(R.string.facebook_client_token));
        FacebookSdk.setApplicationId(String.valueOf(R.string.FbAppId));
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //region #Timber
        if (Constants.IS_DEBUG_ENABLE) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        //endregion

        //Set value when app load first time
        if (!preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_FIRST_TIME)) {
            //Set Audio On
            preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_AUDIO_ON, true);
            //Set Notification On
            preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_NOTIFICATION_ON, true);
            //Set Vibration On
            preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_VIBRATION_ON, true);
            //Set App Run First Time
            preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_FIRST_TIME, true);
            //Unlock Classic Theme
            preferenceUtils.setBoolean(Constants.Themes.ID_CLASSIC_THEME, true);
            //Selected Theme
            preferenceUtils.setString(Constants.PreferenceConstant.SELECTED_THEME, Constants.Themes.ID_CLASSIC_THEME);
        }

        //region #Setup AdMob
       /* AdMobUtils adMobUtils = getInstance().getAppComponent().provideAdmobUtils();
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        adMobUtils.setTestDeviceId(testDevices);*/
        //endregion

    }

    //endregion
}