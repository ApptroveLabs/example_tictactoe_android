package com.cloudstuff.tictactoe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.utils.Constants;
import com.trackier.sdk.TrackierEvent;
import com.trackier.sdk.TrackierSDK;

public class SplashActivity extends BaseActivity {
    //region #Variables
    private final Launcher mLauncher = new Launcher();
    //endregion

    //region #InBuilt Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //initialize views and variables
        initialization();
        TrackierEvent event = new TrackierEvent("sEMWSCTXeu");
        event.param1 = "App Opened";
        TrackierSDK.trackEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSafeHandler.postDelayed(mLauncher, Constants.Delay.SPLASH_INTERVAL);
    }

    @Override
    protected void onStop() {
        mSafeHandler.removeCallbacks(mLauncher);
        super.onStop();
    }
    //endregion

    /**
     * initialize views and variables
     */
    private void initialization() {

    }

    //region #Launcher class
    private class Launcher implements Runnable {
        @Override
        public void run() {
            if (!isFinishing()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    }
    //endregion
}