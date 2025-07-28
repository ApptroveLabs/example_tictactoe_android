package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.adapter.ThemeAdapter;
import com.cloudstuff.tictactoe.annotation.InAppType;
import com.cloudstuff.tictactoe.annotation.ThemeUnlockType;
import com.cloudstuff.tictactoe.model.Theme;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.ConfirmationAlertDialog;
import com.cloudstuff.tictactoe.utils.Constants;
import com.cloudstuff.tictactoe.utils.ThemeManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class ThemeFragment extends BaseFragment {

    //region #Butterknife
    @BindView(R.id.tv_title)
    MaterialTextView tvTitle;
    @BindView(R.id.rv_theme)
    RecyclerView rvTheme;
    //endregion

    //region #Variables
    private String selectedThemeId = "";
    private int selectedThemeUnlockDays = 0;
    private String currentThemeId = "";

    private boolean isRewarded = false;

    private List<Theme> themeList;
    private ThemeAdapter themeAdapter;
    private MainActivity mainActivity;
    //endregion

    //region #InBuilt Methods
    public ThemeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        ButterKnife.bind(this, view);

        //initialize views and variables
        initialization();

        return view;
    }
    //endregion

    //region #Click Listeners
    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        if (CommonUtils.isClickDisabled()) {
            return;
        }

        mediaUtils.playButtonSound();

        switch (view.getId()) {
            //Back
            case R.id.iv_back:
                mainActivity.onBackPressed();
                break;

            //Default
            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("ThemeFragment Default Click Called.");
                }
                break;
        }
    }
    //endregion

    //region #Custom Methods
    private void initialization() {
        //Set View Selected
        setViewSelected();


        //Start Tracing Theme List Performance Monitoring - Fetch, Update and Show List
        Trace themeTrace = FirebasePerformance.getInstance().newTrace(Constants.PerformanceMonitoring.THEME_TRACE);
        themeTrace.start();

        themeList = ThemeManager.getThemeList();
        boolean isThemeSelected = false;
        //Set Selected Theme
        currentThemeId = preferenceUtils.getString(Constants.PreferenceConstant.SELECTED_THEME);
        int themeListSize = themeList.size();
        for (int i = 0; i < themeListSize; i++) {
            Theme theme = themeList.get(i);
            if (currentThemeId.equals(theme.getThemeId())) {
                theme.setSelected(true);
                isThemeSelected = true;
            }

            //Do not accept negative values
            if (theme.getThemeUnlockDays() <= 0) {
                theme.setThemeUnlockDays(0);
            }

            //Only Manage if days are > 0 , 0 = unlock all time
            if (theme.getThemeUnlockDays() > 0) {
                if (theme.getThemeUnlockType().equals(ThemeUnlockType.ADVERTISE) && theme.isThemeUnlock()) {
                    String lastStoredTimestamp = preferenceUtils.getString(theme.getThemeId() + Constants.Themes.THEME_TIMER);
                    if (!TextUtils.isEmpty(lastStoredTimestamp)) {
                        long timestamp = Long.parseLong(lastStoredTimestamp);
                        long difference = System.currentTimeMillis() - timestamp;

                        long calculatedDuration = 0;
                        if (Constants.IS_DEBUG_ENABLE) {
                            calculatedDuration = TimeUnit.MINUTES.toMillis(theme.getThemeUnlockDays());//Test Days in Minutes
                        } else {
                            calculatedDuration = TimeUnit.DAYS.toMillis(theme.getThemeUnlockDays());//Test Days
                        }

                        if (difference >= calculatedDuration) {
                            showAlert(getString(R.string.message_theme_locked, theme.getThemeName()));
                            preferenceUtils.removeKey(theme.getThemeId() + Constants.Themes.THEME_TIMER);
                            preferenceUtils.setBoolean(theme.getThemeId(), false);
                            theme.setThemeUnlock(false);
                            if (theme.isSelected()) {
                                theme.setSelected(false);
                                setClassicTheme();
                            }
                        } else {
                            theme.setThemeUnlock(true);
                        }
                    } else {
                        preferenceUtils.setBoolean(theme.getThemeId(), false);
                        theme.setThemeUnlock(false);
                        if (theme.isSelected()) {
                            theme.setSelected(false);
                            setClassicTheme();
                        }
                    }
                }
            } else {
                preferenceUtils.removeKey(theme.getThemeId() + Constants.Themes.THEME_TIMER);
            }
            ThemeManager.updateTheme(i, theme);
        }

        /**
         * Default Set Classic Theme if any of the theme is not selected.
         * This logic is implemented to manage Special Offer theme like Diwali Themes or Christmas Themes
         * If user select one of these themes and we disable that theme options from remote config then this logic will switch that theme to Classic Theme
         */
        if (!isThemeSelected) {
            setClassicTheme();
        }

        rvTheme.setLayoutManager(new LinearLayoutManager(mainActivity));
        themeAdapter = new ThemeAdapter(mainActivity, themeList, (theme, position) -> {
            mediaUtils.playButtonSound();

            String themeId = preferenceUtils.getString(Constants.PreferenceConstant.SELECTED_THEME);

            if (theme.isThemeUnlock()) {
                //Remove Selection of Old Theme
                for (int i = 0; i < themeListSize; i++) {
                    if (themeId.equals(themeList.get(i).getThemeId())) {
                        themeList.get(i).setSelected(false);
                        break;
                    }
                }
                //Set New Theme
                themeList.get(position).setSelected(true);
                preferenceUtils.setString(Constants.PreferenceConstant.SELECTED_THEME, theme.getThemeId());
                themeAdapter.notifyDataSetChanged();
                ThemeManager.setThemeList(themeList);
            } else {
                selectedThemeId = theme.getThemeId();
                selectedThemeUnlockDays = theme.getThemeUnlockDays();

                //Theme Type - Advertise
                if (theme.getThemeUnlockType().equals(ThemeUnlockType.ADVERTISE)) {
                    if (networkUtils.isConnected()) {
                        RewardedAd rewardedAd = adMobUtils.getRewardedAd();
                        if (rewardedAd != null) {
                            String message = "";
                            if (theme.getThemeUnlockDays() > 0) {
                                message = getString(R.string.message_unlock_theme_advertise_days, theme.getThemeName(), theme.getThemeUnlockDays());
                            } else {
                                message = getString(R.string.message_unlock_theme_advertise, theme.getThemeName());
                            }

                            ConfirmationAlertDialog.showConfirmationDialog(mainActivity, false,
                                    getResources().getString(R.string.title_advertise), message,
                                    View.VISIBLE, getString(R.string.action_watch), View.VISIBLE, getString(R.string.action_cancel),
                                    new ConfirmationAlertDialog.ConfirmationAlertDialogClickListener() {
                                        @Override
                                        public void onPositiveButtonClick() {
                                            //rewardedAd.show(mainActivity);
                                        }
                                    });
                        } else {
                            showError(getString(R.string.error_video_not_available));
                            if (rewardedAd == null) {
                                Timber.e("Create New Rewarded Ad");
                                adMobUtils.createAndLoadRewardedAd();
                            } else {
                                Timber.e("Loading Current Rewarded Ad");
                            }
                        }
                    } else {
                        showError(getString(R.string.error_internet_connection));
                    }
                }
                //Theme Type - InApp
                else if (theme.getThemeUnlockType().equals(ThemeUnlockType.IN_APP)) {
                    if (networkUtils.isConnected()) {
                        ConfirmationAlertDialog.showConfirmationDialog(mainActivity, false,
                                getResources().getString(R.string.title_purchase_theme), getString(R.string.message_unlock_theme_purchase, theme.getThemeName()),
                                View.VISIBLE, getString(R.string.title_purchase), View.VISIBLE, getString(R.string.action_cancel),
                                new ConfirmationAlertDialog.ConfirmationAlertDialogClickListener() {
                                    @Override
                                    public void onPositiveButtonClick() {
                                        if (theme.getThemeName().equals(getString(R.string.title_triangle_theme))) {
                                            mainActivity.purchaseProduct(Constants.InAppPurchase.SKU_TRIANGLE_THEME, InAppType.ONE_TIME);
                                        } else if (theme.getThemeName().equals(getString(R.string.title_diamond_theme))) {
                                            mainActivity.purchaseProduct(Constants.InAppPurchase.SKU_DIAMOND_THEME, InAppType.ONE_TIME);
                                        } else if (theme.getThemeName().equals(getString(R.string.title_star_theme))) {
                                            mainActivity.purchaseProduct(Constants.InAppPurchase.SKU_STAR_THEME, InAppType.ONE_TIME);
                                        } else if (theme.getThemeName().equals(getString(R.string.title_heart_theme))) {
                                            mainActivity.purchaseProduct(Constants.InAppPurchase.SKU_HEART_THEME, InAppType.ONE_TIME);
                                        }
                                    }
                                });
                    } else {
                        showError(getString(R.string.error_internet_connection));
                    }
                }
            }
        });
        rvTheme.setAdapter(themeAdapter);

        //Stop Theme Trace Performance Monitoring
        themeTrace.stop();

        //Setup AdMob
        setupAdMob();
    }

    /**
     * Set View Selected
     */
    private void setViewSelected() {
        tvTitle.setSelected(true);
    }

    //region #Advertise

    /**
     * Setup AdMob Ads
     */
    private void setupAdMob() {
        //Request for Reward Video ad only if Advertise Theme are locked
        loadRewardVideoAd(false);
    }

    /**
     * Load Reward Video Ads
     */
    private void loadRewardVideoAd(boolean loadNewAd) {
        Timber.e("Advertise Lock Count %d", ThemeManager.getAdvertiseLockedThemeCount());
        if (ThemeManager.getAdvertiseLockedThemeCount() > 0) {
            RewardedAd rewardedAd = adMobUtils.getRewardedAd();
            if (loadNewAd) {
                Timber.e("Create New Rewarded Ad");
                adMobUtils.createAndLoadRewardedAd();
            } else {
                if (rewardedAd == null) {
                    Timber.e("Create New Rewarded Ad");
                    adMobUtils.createAndLoadRewardedAd();
                }
            }
        }
    }

    //Reward Ad Callback
 /*   private RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
        @Override
        public void onRewardedAdOpened() {
            // Ad opened.
            mediaUtils.pauseMusic();
        }

        @Override
        public void onRewardedAdClosed() {
            // Ad closed.
            mediaUtils.playMusic();

            if (isRewarded) {
                isRewarded = false;
                if (selectedThemeUnlockDays > 0) {
                    preferenceUtils.setString(selectedThemeId + Constants.Themes.THEME_TIMER, String.valueOf(System.currentTimeMillis()));
                }
                unlockSelectedTheme();
            } else {
                loadRewardVideoAd(true);
            }
        }

        @Override
        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            // User earned reward.
            isRewarded = true;
        }

        @Override
        public void onRewardedAdFailedToShow(AdError adError) {
            // Ad failed to display.
            Timber.e("onRewardedAdFailedToShow %d", adError.getCode());

            switch (adError.getCode()) {
                case ERROR_CODE_INTERNAL_ERROR:
                    //Something happened internally.
                    break;

                case ERROR_CODE_AD_REUSED:
                    //The rewarded ad has already been shown.
                    loadRewardVideoAd(true);
                    break;

                case ERROR_CODE_NOT_READY:
                    //The rewarded ad is not ready.
                    break;

                case ERROR_CODE_APP_NOT_FOREGROUND:
                    //The rewarded ad can not be shown when app is not in foreground.
                    break;

                case ERROR_CODE_MEDIATION_SHOW_ERROR:
                    //A mediation adapter failed to show the ad.
                    break;

                //Default
                default:
                    if (Constants.IS_DEBUG_ENABLE) {
                        showAlert("RewardedAdCallback Fail Default Called.");
                    }
                    break;
            }
        }
    };*/
    //endregion

    /**
     * Unlock Selected Theme
     */
    public void unlockSelectedTheme() {
        switch (selectedThemeId) {
            //Classic Theme
            case Constants.Themes.ID_CLASSIC_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_CLASSIC_THEME, true);
                break;

            //Crackers Theme
            case Constants.Themes.ID_CRACKERS_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_CRACKERS_THEME, true);
                break;

            //Lights Theme
            case Constants.Themes.ID_LIGHTS_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_LIGHTS_THEME, true);
                break;

            //Sweets Theme
            case Constants.Themes.ID_SWEETS_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_SWEETS_THEME, true);
                break;

            //Rangoli Theme
            case Constants.Themes.ID_RANGOLI_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_RANGOLI_THEME, true);
                break;

            //Plus Theme
            case Constants.Themes.ID_PLUS_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_PLUS_THEME, true);
                break;

            //Square Theme
            case Constants.Themes.ID_SQUARE_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_SQUARE_THEME, true);
                break;

            //Polygon Theme
            case Constants.Themes.ID_POLYGON_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_POLYGON_THEME, true);
                break;

            //Hexagon Theme
            case Constants.Themes.ID_HEXAGON_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_HEXAGON_THEME, true);
                break;

            //Octagon Theme
            case Constants.Themes.ID_OCTAGON_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_OCTAGON_THEME, true);
                break;

            //Triangle Theme
            case Constants.Themes.ID_TRIANGLE_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_TRIANGLE_THEME, true);
                break;

            //Diamond Theme
            case Constants.Themes.ID_DIAMOND_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_DIAMOND_THEME, true);
                break;

            //Star Theme
            case Constants.Themes.ID_STAR_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_STAR_THEME, true);
                break;

            //Heart Theme
            case Constants.Themes.ID_HEART_THEME:
                preferenceUtils.setBoolean(Constants.Themes.ID_HEART_THEME, true);
                break;

            //Default
            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("Unlock Theme Default Called.");
                }
                break;
        }

        ThemeManager.updateTheme(mainActivity, preferenceUtils, mainActivity.getCommonConfiguration(), mainActivity.getThemesConfiguration());

        for (int i = 0; i < themeList.size(); i++) {
            if (currentThemeId.equals(themeList.get(i).getThemeId())) {
                themeList.get(i).setSelected(true);
                break;
            }
        }

        themeAdapter.notifyDataSetChanged();

        //Request for Reward Video ad only if Advertise Theme are locked
        loadRewardVideoAd(true);
    }

    /**
     * Set Default Classic Theme
     */
    private void setClassicTheme() {
        currentThemeId = themeList.get(0).getThemeId();
        preferenceUtils.setString(Constants.PreferenceConstant.SELECTED_THEME, currentThemeId);
        themeList.get(0).setSelected(true);
    }
    //endregion
}