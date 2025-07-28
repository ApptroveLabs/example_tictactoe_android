package com.cloudstuff.tictactoe.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonConfiguration {

    @SerializedName("show_interstitial_ad_after_game")
    @Expose
    private int showInterstitialAdAfterGame;
    @SerializedName("show_in_app_review_after_game")
    @Expose
    private int showInAppReviewAfterGame;
    @SerializedName("unlock_normal_advertise_theme_timer_days")
    @Expose
    private int unlockNormalAdvertiseThemeTimerDays;
    @SerializedName("unlock_special_advertise_theme_timer_days")
    @Expose
    private int unlockSpecialAdvertiseThemeTimerDays;
    @SerializedName("history_record_limit")
    @Expose
    private int historyRecordLimit;
    @SerializedName("feedback_email")
    @Expose
    private String feedbackEmail;
    @SerializedName("terms_condition_url")
    @Expose
    private String termsConditionUrl;
    @SerializedName("privacy_policy_url")
    @Expose
    private String privacyPolicyUrl;
    @SerializedName("dynamic_link_url")
    @Expose
    private String dynamicLinkUrl;
    @SerializedName("play_store_developer_name")
    @Expose
    private String playStoreDeveloperName;

    public CommonConfiguration(int showInterstitialAdAfterGame, int showInAppReviewAfterGame, int unlockNormalAdvertiseThemeTimerDays, int unlockSpecialAdvertiseThemeTimerDays, int historyRecordLimit, String feedbackEmail, String termsConditionUrl, String privacyPolicyUrl, String dynamicLinkUrl, String playStoreDeveloperName) {
        this.showInterstitialAdAfterGame = showInterstitialAdAfterGame;
        this.showInAppReviewAfterGame = showInAppReviewAfterGame;
        this.unlockNormalAdvertiseThemeTimerDays = unlockNormalAdvertiseThemeTimerDays;
        this.unlockSpecialAdvertiseThemeTimerDays = unlockSpecialAdvertiseThemeTimerDays;
        this.historyRecordLimit = historyRecordLimit;
        this.feedbackEmail = feedbackEmail;
        this.termsConditionUrl = termsConditionUrl;
        this.privacyPolicyUrl = privacyPolicyUrl;
        this.dynamicLinkUrl = dynamicLinkUrl;
        this.playStoreDeveloperName = playStoreDeveloperName;
    }

    public int getShowInterstitialAdAfterGame() {
        return showInterstitialAdAfterGame;
    }

    public void setShowInterstitialAdAfterGame(int showInterstitialAdAfterGame) {
        this.showInterstitialAdAfterGame = showInterstitialAdAfterGame;
    }

    public int getShowInAppReviewAfterGame() {
        return showInAppReviewAfterGame;
    }

    public void setShowInAppReviewAfterGame(int showInAppReviewAfterGame) {
        this.showInAppReviewAfterGame = showInAppReviewAfterGame;
    }

    public int getUnlockNormalAdvertiseThemeTimerDays() {
        return unlockNormalAdvertiseThemeTimerDays;
    }

    public void setUnlockNormalAdvertiseThemeTimerDays(int unlockNormalAdvertiseThemeTimerDays) {
        this.unlockNormalAdvertiseThemeTimerDays = unlockNormalAdvertiseThemeTimerDays;
    }

    public int getUnlockSpecialAdvertiseThemeTimerDays() {
        return unlockSpecialAdvertiseThemeTimerDays;
    }

    public void setUnlockSpecialAdvertiseThemeTimerDays(int unlockSpecialAdvertiseThemeTimerDays) {
        this.unlockSpecialAdvertiseThemeTimerDays = unlockSpecialAdvertiseThemeTimerDays;
    }

    public int getHistoryRecordLimit() {
        return historyRecordLimit;
    }

    public void setHistoryRecordLimit(int historyRecordLimit) {
        this.historyRecordLimit = historyRecordLimit;
    }

    public String getFeedbackEmail() {
        return feedbackEmail;
    }

    public void setFeedbackEmail(String feedbackEmail) {
        this.feedbackEmail = feedbackEmail;
    }

    public String getTermsConditionUrl() {
        return termsConditionUrl;
    }

    public void setTermsConditionUrl(String termsConditionUrl) {
        this.termsConditionUrl = termsConditionUrl;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public String getDynamicLinkUrl() {
        return dynamicLinkUrl;
    }

    public void setDynamicLinkUrl(String dynamicLinkUrl) {
        this.dynamicLinkUrl = dynamicLinkUrl;
    }

    public String getPlayStoreDeveloperName() {
        return playStoreDeveloperName;
    }

    public void setPlayStoreDeveloperName(String playStoreDeveloperName) {
        this.playStoreDeveloperName = playStoreDeveloperName;
    }

    @Override
    public String toString() {
        return "CommonConfiguration{" +
                "showInterstitialAdAfterGame=" + showInterstitialAdAfterGame +
                ", showInAppReviewAfterGame=" + showInAppReviewAfterGame +
                ", unlockNormalAdvertiseThemeTimerDays=" + unlockNormalAdvertiseThemeTimerDays +
                ", unlockSpecialAdvertiseThemeTimerDays=" + unlockSpecialAdvertiseThemeTimerDays +
                ", historyRecordLimit=" + historyRecordLimit +
                ", feedbackEmail='" + feedbackEmail + '\'' +
                ", termsConditionUrl='" + termsConditionUrl + '\'' +
                ", privacyPolicyUrl='" + privacyPolicyUrl + '\'' +
                ", dynamicLinkUrl='" + dynamicLinkUrl + '\'' +
                ", playStoreDeveloperName='" + playStoreDeveloperName + '\'' +
                '}';
    }
}
