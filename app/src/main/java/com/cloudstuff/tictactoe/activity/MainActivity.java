package com.cloudstuff.tictactoe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.annotation.DifficultyLevel;
import com.cloudstuff.tictactoe.annotation.GameMode;
import com.cloudstuff.tictactoe.annotation.InAppType;
import com.cloudstuff.tictactoe.db.databasehelper.DatabaseHelper;
import com.cloudstuff.tictactoe.db.tables.GameDetails;
import com.cloudstuff.tictactoe.fragment.GameFragment;
import com.cloudstuff.tictactoe.fragment.HomeFragment;
import com.cloudstuff.tictactoe.fragment.PlayerFragment;
import com.cloudstuff.tictactoe.fragment.SettingsFragment;
import com.cloudstuff.tictactoe.fragment.ThemeFragment;
import com.cloudstuff.tictactoe.model.CommonConfiguration;
import com.cloudstuff.tictactoe.model.FCMRequest;
import com.cloudstuff.tictactoe.model.GamePlayRequest;
import com.cloudstuff.tictactoe.model.GamePlayer;
import com.cloudstuff.tictactoe.model.ThemesConfiguration;
import com.cloudstuff.tictactoe.network.NetworkChangeEvent;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.ConfirmationAlertDialog;
import com.cloudstuff.tictactoe.utils.Constants;
import com.cloudstuff.tictactoe.utils.FCMUtils;
import com.cloudstuff.tictactoe.utils.ThemeManager;
import com.squareup.otto.Subscribe;
import com.trackier.sdk.TrackierSDK;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    //region #Constants
    private static final int RC_SIGN_IN = 1000;
    //endregion

    //region #Butterknife
    @BindView(R.id.ad_banner)
    AdView adBanner;
    //endregion

    //region #Variables
    //In-App Purchase
    private BillingClient billingClient;
    private List<SkuDetails> inAppSkuDetailsList = new ArrayList<>();

    private DatabaseHelper databaseHelper;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private CommonConfiguration commonConfiguration;
    private ThemesConfiguration themesConfiguration;

    //Play Games
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private AchievementsClient achievementsClient;
    private LeaderboardsClient leaderboardsClient;
    private PlayersClient playersClient;

    private AlertDialog quitGameAlertDialog;
    //endregion

    //region #Getter-Setter
    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null && GoogleSignIn.hasPermissions(googleSignInAccount);
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public DatabaseReference getGameTableRef() {
        return gameTable;
    }

    public CommonConfiguration getCommonConfiguration() {
        if (commonConfiguration == null) {
            Timber.e("Common Configuration Remote Config is null.");
            FirebaseCrashlytics.getInstance().log("Common Configuration Remote Config is null.");

            commonConfiguration = new CommonConfiguration(Constants.RemoteConfig.SHOW_INTERSTITIAL_AD_AFTER_GAME,
                    Constants.RemoteConfig.SHOW_IN_APP_REVIEW_AFTER_GAME,
                    Constants.RemoteConfig.UNLOCK_NORMAL_ADVERTISE_THEME_TIMER_DAYS,
                    Constants.RemoteConfig.UNLOCK_SPECIAL_ADVERTISE_THEME_TIMER_DAYS,
                    Constants.RemoteConfig.HISTORY_RECORD_LIMIT,
                    Constants.RemoteConfig.FEEDBACK_EMAIL,
                    Constants.RemoteConfig.TERMS_CONDITION_URL,
                    Constants.RemoteConfig.PRIVACY_POLICY_URL,
                    Constants.RemoteConfig.DYNAMIC_LINK_URL,
                    Constants.RemoteConfig.PLAY_STORE_DEVELOPER_NAME);
        }

        return commonConfiguration;
    }

    public ThemesConfiguration getThemesConfiguration() {
        if (commonConfiguration == null) {
            Timber.e("Themes Configuaration Remote Config is null.");
            FirebaseCrashlytics.getInstance().log("Themes Configuaration Remote Config is null.");

            themesConfiguration = new ThemesConfiguration(Constants.RemoteConfig.IS_SHAPE_THEME_ENABLE,
                    Constants.RemoteConfig.IS_DIWALI_THEME_ENABLE,
                    Constants.RemoteConfig.IS_CHRISTMAS_THEME_ENABLE,
                    Constants.RemoteConfig.IS_HALLOWEEN_THEME_ENABLE,
                    Constants.RemoteConfig.IS_EASTER_EGG_THEME_ENABLE);
        }

        return themesConfiguration;
    }

    //endregion

    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private DatabaseReference playerTable;
    private DatabaseReference gameTable;
    private Player player;

    //region #InBuilt Methods

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //initialize views and variables
        initialization();

        intent = new Intent();
        Uri data =intent.getData();

        if(Intent.ACTION_VIEW == intent.getAction()){
            Uri uri = getUri();
            if(uri != null){
                Log.d("MainActivity", "onCreate: "+ getDeepLinkParams(uri).toString());
            }
        }


        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setUserProperty("ct_objectId", Objects.requireNonNull(TrackierSDK.getTrackierId()));
        Log.d("TAG", "onCreate: "+TrackierSDK.getTrackierId());

        // Log an app event
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        // Initialize the Facebook logger
        logger = AppEventsLogger.newLogger(this);

        // Log app launch event
        logger.logEvent("App_Launched");

    }

    private Map<String,String> getDeepLinkParams(Uri uri) {
        Map<String,String> deepLinkParams = new HashMap<String,String>();
        if(uri != null){
            Set<String> paramNames = uri.getQueryParameterNames();
            for(String name : paramNames){
               deepLinkParams.put(name,uri.getQueryParameter(name));
            }
        }
        return deepLinkParams;
    }

    private Uri getUri() {
        Uri uri = intent.getData();
        if(intent.hasExtra("deferred_deeplink")){
            return Uri.parse(intent.getStringExtra("deferred_deeplink"));
        } else {
            return null;
        }
    }

    @Override
    protected void onResume() {
        mediaUtils.playMusic();

        adMobUtils.resumeBannerAd(adBanner);

        signInSilently();

        checkForceUpdate();

        super.onResume();
    }

    @Override
    protected void onPause() {
        mediaUtils.pauseMusic();
        adMobUtils.pauseBannerAd(adBanner);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (billingClient.isReady()) {
            billingClient.endConnection();
        }
        adMobUtils.destroyBannerAd(adBanner);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //InApp Update
        if (requestCode == playCoreUtils.RC_FORCE_UPDATE) {
            if (resultCode != Activity.RESULT_OK) {
                checkForceUpdate();
            }
        }

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                googleSignInAccount = result.getSignInAccount();
                if (googleSignInAccount != null) {
                    Timber.d("Display Name: %s", googleSignInAccount.getDisplayName());
                    Timber.d("Given Name: %s", googleSignInAccount.getGivenName());
                    Timber.d("Family Name: %s", googleSignInAccount.getFamilyName());
                    Timber.d("Photo URL: %s", googleSignInAccount.getPhotoUrl());
                    Timber.d("Email: %s", googleSignInAccount.getEmail());
                }
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.error_sign_in_play_games);
                }

                ConfirmationAlertDialog.showConfirmationDialog(this, false,
                        "", message,
                        View.VISIBLE, getString(R.string.action_ok), View.GONE, "",
                        new ConfirmationAlertDialog.ConfirmationAlertDialogClickListener() {
                            @Override
                            public void onPositiveButtonClick() {
                                mediaUtils.playButtonSound();
                            }
                        });
            }
        }
    }

    /**
     * Back Press
     */
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //showAlert("Stack Count: " + fragmentManager.getBackStackEntryCount());

        //Fragment Stack is > 0 - pop fragment
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.fl_main_container);
            if (fragment instanceof GameFragment) {
                GameDetails gameDetails = ((GameFragment) fragment).getGameDetails();
                if (gameDetails != null) {
                    databaseHelper.insertGameScore(gameDetails);
                }

                if (((GameFragment) fragment).isBackPageClick()) {
                    fragmentManager.popBackStackImmediate();
                } else {
                    if (!preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
						InterstitialAd interstitialAd = adMobUtils.getInterstitialAd();
                        if (interstitialAd != null) {
                            ((GameFragment) fragment).goToBackPage();
                        } else {
                            fragmentManager.popBackStackImmediate();
                        }
                    } else {
                        fragmentManager.popBackStackImmediate();
                    }
                }
            } else {
                fragmentManager.popBackStackImmediate();
            }
        }
        //Fragment Stack is 0 - Press ic_back to exit
        else {
            showQuitGamePopup();
        }
    }
    //endregion

    //region #Custom Methods

    /**
     * initialize views and variables
     */
    private void initialization() {
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(Constants.DatabaseConstants.DB_VERSION);
        playerTable = dbRef.child(Constants.DatabaseConstants.TABLE_PLAYER);
        gameTable = dbRef.child(Constants.DatabaseConstants.TABLE_GAMES);

        //Initialize InApp Review
        playCoreUtils.initializeInAppReview();

        //Setup RemoteConfig
        setupRemoteConfig();

        //Setup Themes
        ThemeManager.createTheme(getApplicationContext(), preferenceUtils, commonConfiguration, themesConfiguration);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showGamePlayRequestAlert(intent);
            }
        }, new IntentFilter("ACTION_GAME_PLAY_REQUEST"));

        broadcastManager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("START_GAME");
                GamePlayRequest gamePlayRequest = intent.getParcelableExtra("request");
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BundleExtra.GAME_MODE, GameMode.ONLINE);
                bundle.putBoolean(Constants.BundleExtra.IS_HOST, true);
                bundle.putString(Constants.BundleExtra.DIFFICULTY_LEVEL, DifficultyLevel.MEDIUM);
                bundle.putString(Constants.BundleExtra.PLAYER_ONE_ID, gamePlayRequest.playerOneId);
                bundle.putString(Constants.BundleExtra.PLAYER_ONE_NAME, gamePlayRequest.playerOneName);
                bundle.putString(Constants.BundleExtra.PLAYER_TWO_ID, gamePlayRequest.playerTwoId);
                bundle.putString(Constants.BundleExtra.PLAYER_TWO_NAME, gamePlayRequest.playerTwoName);
                bundle.putBoolean(Constants.BundleExtra.SELECTED_SINGLE_PLAYER, false);
                bundle.putBoolean(Constants.BundleExtra.PLAYER_ONE_CIRCLE_SELECTED, true);
                GameFragment gameFragment = new GameFragment();
                gameFragment.setArguments(bundle);
                replaceFragment(gameFragment, R.id.fl_main_container, true);

            }
        }, new IntentFilter("ACTION_START_GAME"));

        //Get Firebase token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                FirebaseCrashlytics.getInstance().recordException(task.getException());
                Timber.w("Fetching FCM registration token failed " + task.getException());
                return;
            }

            String firebaseToken = task.getResult();
            Timber.e("FCM Token: %s", firebaseToken);
            preferenceUtils.setString(Constants.PreferenceConstant.FCM_TOKEN, firebaseToken);
        });

        //Setup AdMob
        setupAdMob();

        databaseHelper = new DatabaseHelper(this);

        //Setup In-App Purchase
        initInAppBilling();

        //Load HomeFragment
        replaceFragment(new HomeFragment(), R.id.fl_main_container, false);

        //Get Game Type - Widgets or Shortcuts
        if (getIntent() != null && getIntent().hasExtra(Constants.BundleExtra.GAME_MODE)) {
            String gameMode = getIntent().getStringExtra(Constants.BundleExtra.GAME_MODE);
            if (gameMode.equals(GameMode.AI)) {
                mSafeHandler.postDelayed(() -> {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
                    if (fragment instanceof HomeFragment) {
                        ((HomeFragment) fragment).showSelectDifficultyPopup(GameMode.AI);
                    }
                }, Constants.Delay.WIDGET_DELAY);
            } else if (gameMode.equals(GameMode.FRIEND)) {
                //Send Data to player fragment
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BundleExtra.GAME_MODE, gameMode);
                bundle.putString(Constants.BundleExtra.DIFFICULTY_LEVEL, DifficultyLevel.EASY);
                PlayerFragment playerFragment = new PlayerFragment();
                playerFragment.setArguments(bundle);
                replaceFragment(playerFragment, R.id.fl_main_container, true);
            } else {
                FirebaseCrashlytics.getInstance().log("Game Mode - Widgets & Shortcuts: " + gameMode);
            }
        }
    }

    /**
     * Setup Remote Config
     */
    private void setupRemoteConfig() {
        //region #Setup Default RemoteConfig Value
        commonConfiguration = new CommonConfiguration(Constants.RemoteConfig.SHOW_INTERSTITIAL_AD_AFTER_GAME,
                Constants.RemoteConfig.SHOW_IN_APP_REVIEW_AFTER_GAME,
                Constants.RemoteConfig.UNLOCK_NORMAL_ADVERTISE_THEME_TIMER_DAYS,
                Constants.RemoteConfig.UNLOCK_SPECIAL_ADVERTISE_THEME_TIMER_DAYS,
                Constants.RemoteConfig.HISTORY_RECORD_LIMIT,
                Constants.RemoteConfig.FEEDBACK_EMAIL,
                Constants.RemoteConfig.TERMS_CONDITION_URL,
                Constants.RemoteConfig.PRIVACY_POLICY_URL,
                Constants.RemoteConfig.DYNAMIC_LINK_URL,
                Constants.RemoteConfig.PLAY_STORE_DEVELOPER_NAME);

        themesConfiguration = new ThemesConfiguration(Constants.RemoteConfig.IS_SHAPE_THEME_ENABLE,
                Constants.RemoteConfig.IS_DIWALI_THEME_ENABLE,
                Constants.RemoteConfig.IS_CHRISTMAS_THEME_ENABLE,
                Constants.RemoteConfig.IS_HALLOWEEN_THEME_ENABLE,
                Constants.RemoteConfig.IS_EASTER_EGG_THEME_ENABLE);

        HashMap<String, Object> defaultRemoteConfigValue = new HashMap<>();
        defaultRemoteConfigValue.put(Constants.RemoteConfig.TIC_TAC_TOE_COMMON_CONFIGURATION, commonConfiguration);
        defaultRemoteConfigValue.put(Constants.RemoteConfig.TIC_TAC_TOE_THEMES_CONFIGURATION, themesConfiguration);
        //endregion

        boolean instantUpdateData = preferenceUtils.getBoolean(Constants.PreferenceConstant.INSTANT_UPDATE_REMOTE_CONFIG);
        //showAlert(instantUpdateData + "");
        long intervalDuration = 0;
        if (Constants.IS_DEBUG_ENABLE) {
            intervalDuration = 0;
        } else {
            if (instantUpdateData) {
                intervalDuration = 0;
            } else {
                intervalDuration = TimeUnit.HOURS.toMillis(Constants.AppConstant.REMOTE_CONFIG_FETCH_INTERVAL);
            }
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(intervalDuration)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(firebaseRemoteConfigSettings);
        firebaseRemoteConfig.setDefaultsAsync(defaultRemoteConfigValue);

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                boolean updated = task.getResult();
                Timber.d("Config params updated: " + updated);
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("Fetch and activate succeeded");
                }

                preferenceUtils.setBoolean(Constants.PreferenceConstant.INSTANT_UPDATE_REMOTE_CONFIG, false);

                Timber.e(Constants.RemoteConfig.TIC_TAC_TOE_COMMON_CONFIGURATION + ": " + firebaseRemoteConfig.getString(Constants.RemoteConfig.TIC_TAC_TOE_COMMON_CONFIGURATION));
                try {
                    commonConfiguration = (CommonConfiguration) gsonUtils.createPOJOFromString(firebaseRemoteConfig.getString(Constants.RemoteConfig.TIC_TAC_TOE_COMMON_CONFIGURATION), CommonConfiguration.class);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }

                Timber.e(Constants.RemoteConfig.TIC_TAC_TOE_THEMES_CONFIGURATION + ": " + firebaseRemoteConfig.getString(Constants.RemoteConfig.TIC_TAC_TOE_THEMES_CONFIGURATION));
                try {
                    themesConfiguration = (ThemesConfiguration) gsonUtils.createPOJOFromString(firebaseRemoteConfig.getString(Constants.RemoteConfig.TIC_TAC_TOE_THEMES_CONFIGURATION), ThemesConfiguration.class);
                    ThemeManager.updateTheme(MainActivity.this, preferenceUtils, commonConfiguration, themesConfiguration);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
            } else {
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("Fetch failed");
                }
            }
        });
    }

    private void showGamePlayRequestAlert(Intent intent) {
        GamePlayRequest gamePlayRequest = intent.getParcelableExtra("request");
        // setup the alert builder
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New Request Received");
        builder.setMessage(gamePlayRequest.playerOneName + " has sent you request to play TicTacToe game.");
        // add a button
        builder.setPositiveButton("ACCEPT", (dialog, which) -> {
            // send confirmation notification
            Query query = playerTable.orderByChild("playerId").equalTo(gamePlayRequest.playerOneId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        GamePlayer gamePlayer = ds.getValue(GamePlayer.class);
                        FCMRequest fcmRequest = new FCMRequest();
                        FCMRequest.Data data = new FCMRequest.Data();
                        data.setSenderId(preferenceUtils.getString(Constants.PreferenceConstant.MY_PLAYER_ID));
                        data.setSenderName(preferenceUtils.getString(Constants.PreferenceConstant.MY_PLAYER_NAME));
                        data.setReceiverId(gamePlayer.getPlayerId());
                        data.setReceiverName(gamePlayer.getDisplayName());
                        data.setNotificationType(101); // 101 For Game Play Request Accepted Notification
                        fcmRequest.setData(data);
                        fcmRequest.setRegistrationIds(Collections.singletonList(gamePlayer.getFcmToken()));
                        FCMUtils.sendFCMMessage(gamePlayer, fcmRequest, null);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BundleExtra.GAME_MODE, GameMode.ONLINE);
                    bundle.putBoolean(Constants.BundleExtra.IS_HOST, false);
                    bundle.putString(Constants.BundleExtra.DIFFICULTY_LEVEL, DifficultyLevel.MEDIUM);
                    bundle.putString(Constants.BundleExtra.PLAYER_ONE_ID, gamePlayRequest.playerOneId);
                    bundle.putString(Constants.BundleExtra.PLAYER_ONE_NAME, gamePlayRequest.playerOneName);
                    bundle.putString(Constants.BundleExtra.PLAYER_TWO_ID, preferenceUtils.getString(Constants.PreferenceConstant.MY_PLAYER_ID));
                    bundle.putString(Constants.BundleExtra.PLAYER_TWO_NAME, preferenceUtils.getString(Constants.PreferenceConstant.MY_PLAYER_NAME));
                    bundle.putBoolean(Constants.BundleExtra.SELECTED_SINGLE_PLAYER, false);
                    bundle.putBoolean(Constants.BundleExtra.PLAYER_ONE_CIRCLE_SELECTED, false);
                    GameFragment gameFragment = new GameFragment();
                    gameFragment.setArguments(bundle);
                    replaceFragment(gameFragment, R.id.fl_main_container, true);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
        builder.setNegativeButton("DECLINE", (dialog, which) -> dialog.dismiss());
        // create and show the alert dialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Check for Force Update
     */
    private void checkForceUpdate() {
        if (!Constants.IS_DEBUG_ENABLE) {
            playCoreUtils.checkInAppUpdate(this);
        }
    }

    /**
     * Show Quit Game Popup
     */
    public void showQuitGamePopup() {
        //validation if dialog is null or already open
        if (quitGameAlertDialog != null && quitGameAlertDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_quit_app, viewGroup, false);

        MaterialTextView tvTitle = dialogView.findViewById(R.id.tv_title);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        AppCompatButton btnQuit = dialogView.findViewById(R.id.btn_quit);

        tvTitle.setSelected(true);
        btnCancel.setSelected(true);
        btnQuit.setSelected(true);

        builder.setView(dialogView);

        quitGameAlertDialog = builder.create();
        Window window = quitGameAlertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnCancel.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }
            mediaUtils.playButtonSound();
            quitGameAlertDialog.dismiss();
        });

        btnQuit.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }

            mediaUtils.playButtonSound();
            quitGameAlertDialog.dismiss();
            finish();
        });

        quitGameAlertDialog.show();
    }

    //region #Advertise

    /**
     * Setup AdMob
     */
    private void setupAdMob() {
        adMobUtils.loadBannerAd(adBanner, new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Timber.e("AD Loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                switch (adError.getCode()) {
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                        //Something happened internally; for instance, an invalid response was received from the ad server.
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
                            showAlert("Banner ad Fail Default Called.");
                        }
                        break;
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that covers the screen.
                mediaUtils.pauseMusic();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                mediaUtils.pauseMusic();
            }
			
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                mediaUtils.pauseMusic();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return to the app after tapping on an ad.
                mediaUtils.playMusic();
            }
        });
    }
    //endregion

    //region #InApp Purchase
    /**
     * InApp purchase listener
     */
    private PurchasesUpdatedListener purchaseUpdateListener = (billingResult, purchases) -> {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            if (Constants.IS_DEBUG_ENABLE) {
                showAlert("Purchase Success:" + billingResult.getResponseCode());
            }

            for (Purchase purchase : purchases) {
                Timber.e("InApp: Purchase Success: " + purchase.getProducts());

	            List<String> products = purchase.getProducts();//Remove Ads
	            if (Constants.InAppPurchase.SKU_REMOVE_ADS.equals(products)) {
		            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
			            Timber.d("You have successfully removed the Ads! Congratulations!!!");
			            showAlert(getString(R.string.message_remove_ads_purchase_success));
			            preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_AD_REMOVED, true);
			            manageRemoveAdPurchase();

			            acknowledgeProduct(purchase);
		            } else {
			            showAlert("Error: Purchase State: " + purchase.getPurchaseState());
		            }

		            //Triangle Theme
	            } else if (Constants.InAppPurchase.SKU_TRIANGLE_THEME.equals(products)) {
		            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
			            Timber.d("Triangle purchase success! Congratulations!!!");
			            showAlert(getString(R.string.message_triangle_theme_purchase_success));
			            preferenceUtils.setBoolean(Constants.Themes.ID_TRIANGLE_THEME, true);
			            manageThemePurchase();

			            acknowledgeProduct(purchase);
		            } else {
			            showAlert("Error: Purchase State: " + purchase.getPurchaseState());
		            }

		            //Diamond Theme
	            } else if (Constants.InAppPurchase.SKU_DIAMOND_THEME.equals(products)) {
		            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
			            Timber.d("Diamond Theme purchase success! Congratulations!!!");
			            showAlert(getString(R.string.message_diamond_theme_purchase_success));
			            preferenceUtils.setBoolean(Constants.Themes.ID_DIAMOND_THEME, true);
			            manageThemePurchase();

			            acknowledgeProduct(purchase);
		            } else {
			            showAlert("Error: Purchase State: " + purchase.getPurchaseState());
		            }

		            //Star Theme
	            } else if (Constants.InAppPurchase.SKU_STAR_THEME.equals(products)) {
		            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
			            Timber.d("Star Theme purchase success! Congratulations!!!");
			            showAlert(getString(R.string.message_star_theme_purchase_success));
			            preferenceUtils.setBoolean(Constants.Themes.ID_STAR_THEME, true);
			            manageThemePurchase();

			            acknowledgeProduct(purchase);
		            } else {
			            showAlert("Error: Purchase State: " + purchase.getPurchaseState());
		            }

		            //Heart Theme
	            } else if (Constants.InAppPurchase.SKU_HEART_THEME.equals(products)) {
		            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
			            Timber.d("Heart Theme purchase success! Congratulations!!!");
			            showAlert(getString(R.string.message_heart_theme_purchase_success));
			            preferenceUtils.setBoolean(Constants.Themes.ID_HEART_THEME, true);
			            manageThemePurchase();

			            acknowledgeProduct(purchase);
		            } else {
			            showAlert("Error: Purchase State: " + purchase.getPurchaseState());
		            }

		            //Default
	            } else {
		            if (Constants.IS_DEBUG_ENABLE) {
			            showAlert("InApp Purchase onPurchasesUpdated default called.");
		            } else {
			            FirebaseCrashlytics.getInstance().log("InApp Purchase onPurchasesUpdated default called: " + purchase.getProducts());
		            }
	            }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            showAlert(getString(R.string.error_in_app_user_cancel));
        } else {
            // Handle any other error codes.
            FirebaseCrashlytics.getInstance().log("Purchase Error:" + billingResult.getResponseCode());
            showAlert("Purchase Error:" + billingResult.getResponseCode());
        }
    };

    //One Time Product Acknowledge listener
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            showAlert("Purchase Acknowledge Error: " + billingResult.getResponseCode());
            FirebaseCrashlytics.getInstance().log("Purchase Acknowledge Error: " + billingResult.getResponseCode());
        }
    };

    /**
     * initialize InApp Purchase
     */
    private void initInAppBilling() {
        //BillingClient is the main interface for communication between the Google Play Billing Library and the rest of your app. BillingClient provides convenience methods, both synchronous and asynchronous, for many common billing operations.
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()//Not used for subscriptions
                .build();

        if (!billingClient.isReady()) {
            //Connect Billing services
            startBillingConnection();
        }
    }

    /**
     * Connect Billing Services
     */
    private void startBillingConnection() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //Fetch Product details from SKU
                    showAvailableProduct();

                    //Manage Purchased Products
                    //checkAvailableProduct();
                } else {
                    FirebaseCrashlytics.getInstance().log("Billing Connection Error: " + billingResult.getResponseCode());
                    showAlert("Billing Connection Error: " + billingResult.getResponseCode());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Timber.e("InApp: onBillingServiceDisconnected");
            }
        });
    }

    /**
     * Fetch Product details from SKU
     */
    private void showAvailableProduct() {
        //Get OneTime and Consumable InApp Details
        List<String> inAppSkuList = new ArrayList<>();
        inAppSkuList.add(Constants.InAppPurchase.SKU_REMOVE_ADS);
        inAppSkuList.add(Constants.InAppPurchase.SKU_TRIANGLE_THEME);
        inAppSkuList.add(Constants.InAppPurchase.SKU_DIAMOND_THEME);
        inAppSkuList.add(Constants.InAppPurchase.SKU_STAR_THEME);
        inAppSkuList.add(Constants.InAppPurchase.SKU_HEART_THEME);
        SkuDetailsParams.Builder inAppParams = SkuDetailsParams.newBuilder();
        inAppParams.setSkusList(inAppSkuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(inAppParams.build(), (billingResult, skuDetailsList) -> {
            // Process the result.
            inAppSkuDetailsList.clear();
            inAppSkuDetailsList.addAll(skuDetailsList);

            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                FirebaseCrashlytics.getInstance().log("Get InApp SKU Detail Error: " + billingResult.getResponseCode());
                showAlert("Get InApp SKU Detail Error: " + billingResult.getResponseCode());
            }
        });
    }

    /**
     * Check Available Products
     */
    /*private void checkAvailableProduct() {
        Purchase.PurchasesResult inAppPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);

        int inAppPurchaseListSize = inAppPurchasesResult.getPurchasesList().size();
        for (int i = 0; i < inAppPurchaseListSize; i++) {
            Purchase purchase = inAppPurchasesResult.getPurchasesList().get(i);
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                switch (purchase.getSku()) {
                    //Remove Ads
                    case Constants.InAppPurchase.SKU_REMOVE_ADS:
                        preferenceUtils.setBoolean(Constants.PreferenceConstant.IS_AD_REMOVED, true);
                        manageRemoveAdPurchase();
                        break;

                    // Triangle Theme
                    case Constants.InAppPurchase.SKU_TRIANGLE_THEME:
                        preferenceUtils.setBoolean(Constants.Themes.ID_TRIANGLE_THEME, true);
                        manageThemePurchase();
                        break;

                    //Diamond Theme
                    case Constants.InAppPurchase.SKU_DIAMOND_THEME:
                        preferenceUtils.setBoolean(Constants.Themes.ID_DIAMOND_THEME, true);
                        manageThemePurchase();
                        break;

                    //Star Theme
                    case Constants.InAppPurchase.SKU_STAR_THEME:
                        preferenceUtils.setBoolean(Constants.Themes.ID_STAR_THEME, true);
                        manageThemePurchase();
                        break;

                    //Heart Theme
                    case Constants.InAppPurchase.SKU_HEART_THEME:
                        preferenceUtils.setBoolean(Constants.Themes.ID_HEART_THEME, true);
                        manageThemePurchase();
                        break;

                    //Default
                    default:
                        if (Constants.IS_DEBUG_ENABLE) {
                            showAlert("InApp Available Product Default called.");
                        }
                        break;
                }
            } else {
                FirebaseCrashlytics.getInstance().log("Get Purchased Product Error: " + purchase.getPurchaseState() + " : " + purchase.getSku());
                showAlert("Get Purchased Product Error: " + purchase.getPurchaseState() + " : " + purchase.getSku());
            }
        }
    }*/

    /**
     * Purchase product
     *
     * @param skuId     - sku id
     * @param inAppType - InApp type
     */
    public void purchaseProduct(String skuId, @InAppType String inAppType) {
        //InApp Type - One Time or Consumable
        if (inAppType.equals(InAppType.ONE_TIME) || inAppType.equals(InAppType.CONSUMABLE)) {
            int inAppSkuSize = inAppSkuDetailsList.size();

            for (int i = 0; i < inAppSkuSize; i++) {
                SkuDetails skuDetails = inAppSkuDetailsList.get(i);
                if (skuDetails.getSku().equals(skuId)) {
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    int responseCode = billingClient.launchBillingFlow(this, billingFlowParams).getResponseCode();

                    if (responseCode != BillingClient.BillingResponseCode.OK) {
                        FirebaseCrashlytics.getInstance().log("Purchase Product Error: " + responseCode);
                        showAlert("Purchase Product Error: " + responseCode);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Acknowledge One Time Purchase
     *
     * @param purchase - purchase
     */
    private void acknowledgeProduct(Purchase purchase) {
        if (!purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
        }
    }

    /**
     * Manage Remove Ads Purchase
     */
    private void manageRemoveAdPurchase() {
        if (preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
            adBanner.setVisibility(View.GONE);
            adMobUtils.destroyBannerAd(adBanner);
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
            if (fragment instanceof SettingsFragment) {
                ((SettingsFragment) fragment).hideRemoveAdButton();
            }
        } else {
            adBanner.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Manage Theme Purchase
     */
    private void manageThemePurchase() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
        if (fragment instanceof ThemeFragment) {
            ((ThemeFragment) fragment).unlockSelectedTheme();
        } else {
            ThemeManager.updateTheme(this, preferenceUtils, commonConfiguration, themesConfiguration);
        }
    }
    //endregion

    //region #Play Games
    public void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            googleSignInAccount = account;
        } else {
            googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            googleSignInClient.silentSignIn().addOnCompleteListener(this,
                    task -> {
                        if (task.isSuccessful()) {
                            Timber.d("signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Timber.d(task.getException(), "signInSilently(): failure");
                            FirebaseCrashlytics.getInstance().recordException(task.getException());
                            onDisconnected();
                        }
                    });
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Timber.d("onConnected(): connected to Google APIs");

        achievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
        leaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);
        playersClient = Games.getPlayersClient(this, googleSignInAccount);

        // Set the greeting appropriately on main menu
        playersClient.getCurrentPlayer()
                .addOnCompleteListener(task -> {
                    String displayName;
                    if (task.isSuccessful()) {
                        player = task.getResult();
                        if (player != null) {
                            displayName = task.getResult().getDisplayName();
                            Timber.d("Title: %s", player.getTitle());
                            Timber.d("Display Name: %s", player.getDisplayName());
                            Timber.d("Given Name: %s", player.getPlayerId());

                            GamePlayer gamePlayer = new GamePlayer();
                            gamePlayer.setDisplayName(player.getDisplayName());
                            gamePlayer.setGivenName(player.getPlayerId());
                            gamePlayer.setOnline(true);
                            gamePlayer.setPlayerId(player.getPlayerId());
                            gamePlayer.setLastPlayedTimestamp(player.getLastPlayedWithTimestamp());
                            gamePlayer.setFcmToken(preferenceUtils.getString(Constants.PreferenceConstant.FCM_TOKEN));

                            preferenceUtils.setString(Constants.PreferenceConstant.MY_PLAYER_ID, player.getPlayerId());
                            preferenceUtils.setString(Constants.PreferenceConstant.MY_PLAYER_NAME, player.getDisplayName());

                            playerTable.child(player.getPlayerId()).setValue(gamePlayer);
                            /*Game game = new Game();
                            game.setGameId(gamePlayer.getPlayerId() + "_" + "h17737576541431034047");
                            game.setGameStatus(0);
                            game.setCurrentMove("NA");
                            gameTable.child(game.getGameId()).setValue(game);*/

                        } else {
                            displayName = "???";
                        }
                    } else {
                        Exception e = task.getException();
                        FirebaseCrashlytics.getInstance().recordException(task.getException());
                        handleException(e, "Exception:");
                        displayName = "???";
                    }
                    Timber.d("Hello, %s", displayName);
                });
    }

    private void onDisconnected() {
        Timber.d("onDisconnected()");
        achievementsClient = null;
        leaderboardsClient = null;
        if (player != null) {
            GamePlayer gamePlayer = new GamePlayer();
            gamePlayer.setDisplayName(player.getDisplayName());
            gamePlayer.setGivenName(player.getPlayerId());
            gamePlayer.setOnline(false);
            gamePlayer.setPlayerId(player.getPlayerId());
            gamePlayer.setLastPlayedTimestamp(player.getLastPlayedWithTimestamp());
            gamePlayer.setFcmToken(preferenceUtils.getString(Constants.PreferenceConstant.FCM_TOKEN));
        }
    }

    private void handleException(Exception e, String details) {
        int status = 0;

        FirebaseCrashlytics.getInstance().recordException(e);

        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        ConfirmationAlertDialog.showConfirmationDialog(MainActivity.this, false,
                "", getString(R.string.error_status_code_play_game, status),
                View.VISIBLE, getString(R.string.action_ok), View.GONE, "",
                new ConfirmationAlertDialog.ConfirmationAlertDialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        mediaUtils.playButtonSound();
                    }
                });
    }

    public void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
    //endregion

    /**
     * Clear Fragment Back Stack and add game record in database
     */
    public void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.fl_main_container);
        if (fragment instanceof GameFragment) {
            GameDetails gameDetails = ((GameFragment) fragment).getGameDetails();
            if (gameDetails != null) {
                databaseHelper.insertGameScore(gameDetails);
            }
        }

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Show InApp Review
     */
    public void showInAppReview() {
        int homePageCount = preferenceUtils.getInteger(Constants.PreferenceConstant.HOME_BUTTON_PRESS_COUNT);
        homePageCount += 1;

        int showInAppReviewAfterGame = commonConfiguration.getShowInAppReviewAfterGame();
        if (showInAppReviewAfterGame <= 0) {
            showInAppReviewAfterGame = Constants.RemoteConfig.SHOW_IN_APP_REVIEW_AFTER_GAME;
        }

        if (homePageCount == 1 || (homePageCount % showInAppReviewAfterGame) == 0) {
            playCoreUtils.callInAppReview(this);
        }
        preferenceUtils.setInteger(Constants.PreferenceConstant.HOME_BUTTON_PRESS_COUNT, homePageCount);
    }

    //endregion

    //region #Event Bus

    /**
     * EventBus
     * <p>
     * Check internet
     * connection change
     * event
     *
     * @param networkChangeEvent -
     */
    @Keep
    @Subscribe
    public void onNetworkChange(NetworkChangeEvent networkChangeEvent) {
        if (networkChangeEvent.isNetworkAvailable()) {
            signInSilently();
        } else {
            if (preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
                adBanner.setVisibility(View.GONE);
            } else {
                adBanner.setVisibility(View.INVISIBLE);
            }
        }
    }
    //endregion
}