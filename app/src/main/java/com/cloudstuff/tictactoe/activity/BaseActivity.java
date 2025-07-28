package com.cloudstuff.tictactoe.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cloudstuff.tictactoe.dagger.DaggerViewComponent;
import com.cloudstuff.tictactoe.dagger.ViewComponent;
import com.google.android.material.snackbar.Snackbar;
import com.cloudstuff.tictactoe.BuildConfig;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.TicTacToe;
import com.cloudstuff.tictactoe.network.NetworkChangeReceiver;
import com.cloudstuff.tictactoe.utils.AdMobUtils;
import com.cloudstuff.tictactoe.utils.AnalyticsUtils;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.ConfirmationAlertDialog;
import com.cloudstuff.tictactoe.utils.GsonUtils;
import com.cloudstuff.tictactoe.utils.MediaUtils;
import com.cloudstuff.tictactoe.utils.PlayCoreUtils;
import com.cloudstuff.tictactoe.utils.PreferenceUtils;
import com.cloudstuff.tictactoe.utils.RootUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity {

    //region #Singleton Instance
    protected PreferenceUtils preferenceUtils = TicTacToe.getInstance().getAppComponent().providePreferenceUtils();
    protected MediaUtils mediaUtils = TicTacToe.getInstance().getAppComponent().provideMediaUtils();
    protected AdMobUtils adMobUtils = TicTacToe.getInstance().getAppComponent().provideAdmobUtils();
    protected PlayCoreUtils playCoreUtils = TicTacToe.getInstance().getAppComponent().providePlayCoreUtils();
    protected GsonUtils gsonUtils = TicTacToe.getInstance().getAppComponent().provideMasterGson();
    protected AnalyticsUtils analyticsUtils = TicTacToe.getInstance().getAppComponent().provideAnalyticsUtils();
    //endregion

    //region #Variables
    @Inject
    public Bus mBus;
    @Inject
    public NetworkChangeReceiver mNetworkChangeReceiver;
    @Inject
    public InputMethodManager mInputMethodManager;
    @Inject
    public Handler mSafeHandler;
    //endregion

    //region #InBuilt Methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Using the static 'create' method to instantiate the component
        ViewComponent viewComponent = DaggerViewComponent.builder()
                .appComponent(TicTacToe.getInstance().getAppComponent())
                .build();
        viewComponent.inject(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
        IntentFilter connectivityChangeFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkChangeReceiver, connectivityChangeFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check Device is rooted or not
        if (!BuildConfig.DEBUG && RootUtils.isDeviceRooted()) {
            ConfirmationAlertDialog.showConfirmationDialog(this, false,
                    "", getString(R.string.message_root_device_supported, getString(R.string.app_name)),
                    View.VISIBLE, getString(R.string.action_ok), View.GONE, "",
                    new ConfirmationAlertDialog.ConfirmationAlertDialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            mediaUtils.playButtonSound();
                            finish();
                        }
                    });
            return;
        }

        // Check if Google Play Service is installed or not
        if (!CommonUtils.isGooglePlayServicesAvailable(this)) {
            ConfirmationAlertDialog.showConfirmationDialog(this, false,
                    "", getString(R.string.message_install_google_play_service),
                    View.VISIBLE, getString(R.string.action_ok), View.GONE, "",
                    new ConfirmationAlertDialog.ConfirmationAlertDialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            mediaUtils.playButtonSound();

                            final String appPackageName = "com.google.android.gms";
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    });
            return;
        }
    }

    @Override
    protected void onStop() {
        mBus.unregister(this);
        unregisterReceiver(mNetworkChangeReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mSafeHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    //endregion

    //region #Custom Methods

    public void showAlert(String msg) {
        if (msg == null) return;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showError(String msg) {
        if (msg == null) return;

        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    public void replaceFragment(Fragment fragment, int containerId, boolean isAddedToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, fragment.getClass().getSimpleName());
        if (isAddedToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        fragmentTransaction.commit();
    }

    public void hideKeyBoard(View view) {
        if (view != null) {
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //endregion
}
