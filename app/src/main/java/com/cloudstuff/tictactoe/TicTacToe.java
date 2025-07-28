package com.cloudstuff.tictactoe;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.cloudstuff.tictactoe.activity.MainActivity;
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
import com.trackier.sdk.DeepLink;
import com.trackier.sdk.DeepLinkListener;
import com.trackier.sdk.TrackierEvent;
import com.trackier.sdk.TrackierSDK;
import com.trackier.sdk.TrackierSDKConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class TicTacToe extends Application {

    DeepLinkListener deepLinkListener = new DeepLinkListener() {
        public void onDeepLinking(@NonNull DeepLink deepLink) {
            Log.d("DeepLink", "=== Deep Link Received ===");
            Log.d("DeepLink", "Deep Link Value: " + deepLink.getDeepLinkValue());
            Log.d("DeepLink", "URL: " + deepLink.getUrl());
            Log.d("DeepLink", "Is Deferred: " + deepLink.isDeferred());
            Log.d("DeepLink", "Partner ID: " + deepLink.getPartnerId());
            Log.d("DeepLink", "Site ID: " + deepLink.getSiteId());
            Log.d("DeepLink", "Sub Site ID: " + deepLink.getSubSiteId());
            Log.d("DeepLink", "Campaign: " + deepLink.getCampaign());
            Log.d("DeepLink", "P1: " + deepLink.getP1());
            Log.d("DeepLink", "P2: " + deepLink.getP2());
            Log.d("DeepLink", "P3: " + deepLink.getP3());
            Log.d("DeepLink", "P4: " + deepLink.getP4());
            Log.d("DeepLink", "P5: " + deepLink.getP5());
            Log.d("DeepLink", "All Data: " + deepLink.getData());
            
            if (deepLink.getSdkParams() != null) {
                Log.d("DeepLink", "SDK Params: " + deepLink.getSdkParams());
            }
            
            Log.d("DeepLink", "=== End Deep Link Info ===");
        }
    };



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
        sdkConfig.setDeepLinkListener(deepLinkListener);
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