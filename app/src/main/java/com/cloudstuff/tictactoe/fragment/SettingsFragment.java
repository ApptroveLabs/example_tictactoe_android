package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.annotation.InAppType;
import com.cloudstuff.tictactoe.model.CommonConfiguration;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment implements SwitchCompat.OnCheckedChangeListener {

    //region #Butterknife
    @BindView(R.id.tv_title)
    MaterialTextView tvTitle;
    @BindView(R.id.tv_audio)
    MaterialTextView tvAudio;
    @BindView(R.id.sc_audio)
    SwitchMaterial scAudio;
    @BindView(R.id.tv_vibration)
    MaterialTextView tvVibration;
    @BindView(R.id.sc_vibration)
    SwitchMaterial scVibration;
    @BindView(R.id.tv_notification)
    MaterialTextView tvNotification;
    @BindView(R.id.sc_notification)
    SwitchMaterial scNotification;
    @BindView(R.id.tv_share)
    MaterialTextView tvShare;
    @BindView(R.id.tv_rate)
    MaterialTextView tvRate;
    @BindView(R.id.tv_feedback)
    MaterialTextView tvFeedback;
    @BindView(R.id.tv_terms_condition)
    MaterialTextView tvTermsCondition;
    @BindView(R.id.tv_privacy_policy)
    MaterialTextView tvPrivacyPolicy;
    @BindView(R.id.tv_more_apps)
    MaterialTextView tvMoreApps;
    @BindView(R.id.view_remove_ads)
    View viewRemoveAds;
    @BindView(R.id.tv_remove_ads)
    MaterialTextView tvRemoveAds;
    //endregion

    //region #Variables
    private CommonConfiguration commonConfiguration;
    private MainActivity mainActivity;
    //endregion

    //region #In Built Methods
    public SettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        //initialize views and variables
        initialization();

        return view;
    }

    /**
     * Switch Check Change Listener
     *
     * @param buttonView - view
     * @param isChecked  - current check status
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (CommonUtils.isClickDisabled()) {
            return;
        }

        mediaUtils.playButtonSound();

        switch (buttonView.getId()) {
            //Music
            case R.id.sc_audio:
                preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_AUDIO_ON, isChecked);
                setAudioOnOff();
                break;

            //Vibration
            case R.id.sc_vibration:
                preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_VIBRATION_ON, isChecked);
                setVibrationOnOff();
                break;

            //Notification
            case R.id.sc_notification:
                preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_NOTIFICATION_ON, isChecked);
                setNotificationOnOff();
                break;

            //Default
            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("Setting Switch Change Check Default Called.");
                }
                break;
        }
    }
    //endregion

    //region #On Click Method
    @OnClick({R.id.iv_back, R.id.tv_rate, R.id.tv_feedback, R.id.tv_share, R.id.tv_terms_condition,
            R.id.tv_privacy_policy, R.id.tv_remove_ads, R.id.tv_more_apps})
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

            //Rate
            case R.id.tv_rate:
                rateApp();
                break;

            //Feedback
            case R.id.tv_feedback:
                sendFeedback();
                break;

            //Share
            case R.id.tv_share:
                shareApp();
                break;

            //Terms Condition
            case R.id.tv_terms_condition:
                String termsCondition = commonConfiguration.getTermsConditionUrl();
                if (TextUtils.isEmpty(termsCondition)) {
                    termsCondition = Constants.RemoteConfig.TERMS_CONDITION_URL;
                }

                CommonUtils.callWebBrowser(mainActivity, termsCondition);
                break;

            //Privacy Policy
            case R.id.tv_privacy_policy:
                String privacyPolicy = commonConfiguration.getPrivacyPolicyUrl();
                if (TextUtils.isEmpty(privacyPolicy)) {
                    privacyPolicy = Constants.RemoteConfig.PRIVACY_POLICY_URL;
                }
                CommonUtils.callWebBrowser(mainActivity, privacyPolicy);
                break;

            //Remove Ads
            case R.id.tv_remove_ads:
                if (networkUtils.isConnected()) {
                    mainActivity.purchaseProduct(Constants.InAppPurchase.SKU_REMOVE_ADS, InAppType.ONE_TIME);
                } else {
                    showError(getString(R.string.error_internet_connection));
                }
                break;

            //More Apps
            case R.id.tv_more_apps:
                moreApps();
                break;

            //Default
            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("Settings Screen Default Click called.");
                }
                break;
        }
    }
    //endregion

    //region #Custom Methods

    /**
     * initialize views and variables
     */
    private void initialization() {
        //Set View Selected
        setViewSelected();

        commonConfiguration = mainActivity.getCommonConfiguration();

        //region #Switch OnCheckChangeListener
        scAudio.setOnCheckedChangeListener(this);
        scVibration.setOnCheckedChangeListener(this);
        scNotification.setOnCheckedChangeListener(this);
        //endregion

        setAudioOnOff();
        setNotificationOnOff();
        setVibrationOnOff();

        if (preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
            hideRemoveAdButton();
        }
    }

    /**
     * Set View Selected
     */
    private void setViewSelected() {
        tvTitle.setSelected(true);
        tvAudio.setSelected(true);
        tvVibration.setSelected(true);
        tvNotification.setSelected(true);
        tvShare.setSelected(true);
        tvRate.setSelected(true);
        tvFeedback.setSelected(true);
        tvTermsCondition.setSelected(true);
        tvPrivacyPolicy.setSelected(true);
        tvMoreApps.setSelected(true);
        tvRemoveAds.setSelected(true);
    }

    /**
     * Set audio on or off
     */
    private void setAudioOnOff() {
        if (mediaUtils == null) {
            return;
        }

        if (preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AUDIO_ON)) {
            scAudio.setChecked(true);
            mediaUtils.playMusic();
        } else {
            scAudio.setChecked(false);
            mediaUtils.pauseMusic();
        }
    }

    /**
     * Set notification on or off
     */
    private void setNotificationOnOff() {
        scNotification.setChecked(preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_NOTIFICATION_ON));
    }

    /**
     * Set vibration on or off
     */
    private void setVibrationOnOff() {
        scVibration.setChecked(preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_VIBRATION_ON));
    }

    /**
     * this method open dialog to share app
     */
    private void shareApp() {
        String dynamicLink = commonConfiguration.getDynamicLinkUrl();
        if (TextUtils.isEmpty(dynamicLink)) {
            dynamicLink = Constants.RemoteConfig.DYNAMIC_LINK_URL;
        }

        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType(Constants.AppConstant.TEXT_PLAIN);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_share_app, dynamicLink));
            mainActivity.startActivity(sendIntent);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    /**
     * Rate App
     */
    private void rateApp() {
        goToPlayStore(mainActivity);
    }

    /**
     * Send feedback
     */
    private void sendFeedback() {
        String feedbackEmail = commonConfiguration.getFeedbackEmail();
        if (TextUtils.isEmpty(feedbackEmail)) {
            feedbackEmail = Constants.RemoteConfig.FEEDBACK_EMAIL;
        }

        String defaultMessage = "Do not delete below Information\n" +
                "\nGame Name: " + getString(R.string.app_name) +
                "\nGame Version: " + CommonUtils.getAppVersionCode() + "_" + CommonUtils.getAppVersionName() +
                "\nDevice Model: " + CommonUtils.getPhoneModel() +
                "\nOS Version: " + CommonUtils.getOsVersion() +
                "\n--------------------\n\n";

        Intent feedbackIntent = new Intent(Intent.ACTION_SEND);
        feedbackIntent.setType(Constants.AppConstant.TEXT_PLAIN);
        feedbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{feedbackEmail});
        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " " + getString(R.string.app_feedback));
        feedbackIntent.putExtra(Intent.EXTRA_TEXT, defaultMessage);
        startActivity(Intent.createChooser(feedbackIntent, getString(R.string.feedback)));
    }

    /**
     * More Apps
     */
    private void moreApps() {
        String moreAppUrl = commonConfiguration.getPlayStoreDeveloperName();
        if (TextUtils.isEmpty(moreAppUrl)) {
            moreAppUrl = Constants.RemoteConfig.PLAY_STORE_DEVELOPER_NAME;
        }
        playStorePublication(mainActivity, moreAppUrl);
    }

    /**
     * Hide Remove Ad Text
     */
    public void hideRemoveAdButton() {
        viewRemoveAds.setVisibility(View.GONE);
        tvRemoveAds.setVisibility(View.GONE);
    }
    //endregion
}
