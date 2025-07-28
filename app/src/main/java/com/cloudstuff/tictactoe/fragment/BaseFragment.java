package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cloudstuff.tictactoe.BuildConfig;
import com.cloudstuff.tictactoe.TicTacToe;
import com.cloudstuff.tictactoe.activity.BaseActivity;
import com.cloudstuff.tictactoe.dagger.DaggerViewComponent;
import com.cloudstuff.tictactoe.dagger.ViewComponent;
import com.cloudstuff.tictactoe.dagger.ViewModule;
import com.cloudstuff.tictactoe.network.NetworkChangeReceiver;
import com.cloudstuff.tictactoe.network.NetworkUtils;
import com.cloudstuff.tictactoe.utils.AdMobUtils;
import com.cloudstuff.tictactoe.utils.MediaUtils;
import com.cloudstuff.tictactoe.utils.PreferenceUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class BaseFragment extends Fragment {

    public InputMethodManager mInputMethodManager;
    public NetworkChangeReceiver mNetworkChangeReceiver;
    //region #Singleton Instance
    protected PreferenceUtils preferenceUtils = TicTacToe.getInstance().getAppComponent().providePreferenceUtils();
    protected MediaUtils mediaUtils = TicTacToe.getInstance().getAppComponent().provideMediaUtils();
    protected AdMobUtils adMobUtils = TicTacToe.getInstance().getAppComponent().provideAdmobUtils();
    protected NetworkUtils networkUtils = TicTacToe.getInstance().getAppComponent().provideNetworkUtils();
    //endregion

    //region #Variables
    @Inject
    public Bus mBus;
    @Inject
    public Handler mSafeHandler;
    private ViewComponent mViewComponent;
    private BaseActivity baseActivity;
    //endregion

    //region #InBuilt Methods
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewComponent = DaggerViewComponent
                .builder()
                .appComponent(TicTacToe.getInstance().getAppComponent())
                .viewModule(new ViewModule())
                .build();
        mViewComponent.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mSafeHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
    //endregion

    //region #Custom Methods
    public void showAlert(String msg) {
        baseActivity.showAlert(msg);
    }

    public void showError(String msg) {
        baseActivity.showError(msg);
    }

    protected ViewComponent getViewComponent() {
        return mViewComponent;
    }

    /**
     * this method calls {@link BaseActivity#replaceFragment(Fragment, int, boolean)}.
     * So, it will replace fragment in Activity's container
     */
    public void replaceFragment(Fragment fragment, int containerId, boolean addToBackStack) {
        baseActivity.replaceFragment(fragment, containerId, addToBackStack);
    }

    /**
     * this method redirect app to play store
     */
    public static void goToPlayStore(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
    }

    /**
     * Hide Keyboard
     */
    protected void hideKeyBoard(View view) {
        baseActivity.hideKeyBoard(view);
    }

    /**
     * Get PlayStore URL
     *
     * @return - Play Store URL
     */
    protected String getPlayStoreUrl() {
        return "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    }

    /**
     * This method redirect app to play store publication page
     *
     * @param context                - context
     * @param playStoreDeveloperName - playstore developer name from remote config
     */
    public static void playStorePublication(Context context, String playStoreDeveloperName) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreDeveloperName)));
    }
    //endregion
}