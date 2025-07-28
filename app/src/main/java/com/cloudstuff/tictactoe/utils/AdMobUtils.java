package com.cloudstuff.tictactoe.utils;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.TicTacToe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * 1. Initialize AdMob
 * 2. Banner Ad Implemented
 * 3. Interstitial Ad Implemented
 * 4. Reward Video Ad Implemented
 */
@Singleton
public class AdMobUtils {
    private Context context;
    private List<String> testDeviceIds;

    private InterstitialAd interstitialAd;

    private RewardedAd rewardedAd;

    private PreferenceUtils preferenceUtils;

    //region #Getter Setter
    public RewardedAd getRewardedAd() {
        return rewardedAd;
    }

    public InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }

    /**
     * Set Test Device Id
     *
     * @param testDeviceIds - list of deviceId
     */
    public void setTestDeviceId(List<String> testDeviceIds) {
        this.testDeviceIds = testDeviceIds;
    }
    //endregion

    @Inject
    public AdMobUtils(Context context) {
        this.context = context;

        preferenceUtils = TicTacToe.getInstance().getAppComponent().providePreferenceUtils();

        //Initialize AdMob
        MobileAds.initialize(context, initializationStatus -> {
        });

       /* interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.admob_interstitial_ad_id));*/
    }

    /**
     * Generate Ad Request
     *
     * @return - adRequest
     */
    private AdRequest generateAdRequest() {
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        if (testDeviceIds != null) {
            RequestConfiguration requestConfiguration
                    = new RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        }
        return adRequestBuilder.build();
    }

    /**
     * Load Banner Ad
     *
     * @param adView - adView
     */
    public void loadBannerAd(AdView adView, AdListener adListener) {
        if (preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
            return;
        }

        if (adView != null) {
            adView.setAdListener(adListener);
            AdRequest adRequest = generateAdRequest();
            adView.loadAd(adRequest);
        }
    }

    /**
     * Pause Banner Ad
     *
     * @param adView - adView
     */
    public void pauseBannerAd(AdView adView) {
        if (adView != null) {
            adView.pause();
        }
    }

    /**
     * Resume Banner Ad
     *
     * @param adView - adView
     */
    public void resumeBannerAd(AdView adView) {
        if (adView != null) {
            adView.resume();
        }
    }

    /**
     * Destroy Banner Ad
     *
     * @param adView - adView
     */
    public void destroyBannerAd(AdView adView) {
        if (adView != null) {
            adView.destroy();
        }
    }

    /**
     * load Interstitial Ad
     *
     * @param adListener - ad Click Listener
     */
    public void loadInterstitialAd(AdListener adListener) {
        if (preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
            return;
        }

       // interstitialAd.setAdListener(adListener);

        /*if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            interstitialAd.loadAd(generateAdRequest());
        }*/
    }

    /**
     * Request new interstitial Ad
     */
    public void requestNewInterstitial() {
        if (preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
            return;
        }

        /*if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            interstitialAd.loadAd(generateAdRequest());
        }*/
    }

    public void createAndLoadRewardedAd() {
        //rewardedAd = new RewardedAd(context, context.getString(R.string.admob_reward_video_ad_id));
        ///rewardedAd.loadAd(generateAdRequest(), rewardedAdLoadCallback);
    }

    //Reward Video Ad Load Callback
    private RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback() {


        public void onRewardedAdLoaded() {
            // Ad successfully loaded.
            Timber.e("Reward Ad Loaded");
        }

        public void onRewardedAdFailedToLoad(LoadAdError adError) {
            // Ad failed to load.
            Timber.e("onRewardedAdFailedToLoad %d", adError.getCode());

            switch (adError.getCode()) {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    //Something happened internally.
                    break;

                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    //The ad request was invalid; for instance, the ad unit ID was incorrect.
                    break;

                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    //The ad request was unsuccessful due to network connectivity.
                    break;

                case AdRequest.ERROR_CODE_NO_FILL:
                    //The ad request was successful, but no ad was returned due to lack of ad inventory.
                    break;

                //Default
                default:
                    if (Constants.IS_DEBUG_ENABLE) {
                        Timber.e("RewardedAdLoadCallback Fail Default Called.");
                    }
                    break;
            }
        }
    };
}