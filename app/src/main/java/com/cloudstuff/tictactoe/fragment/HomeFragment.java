package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.annotation.DifficultyLevel;
import com.cloudstuff.tictactoe.annotation.GameMode;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.ConfirmationAlertDialog;
import com.cloudstuff.tictactoe.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    //region #Constants
    private static final int RC_LEADERBOARD_UI = 9004;
    private static final int RC_ACHIEVEMENT_UI = 9003;
    //endregion

    //region #Butterknife
    @BindView(R.id.btn_ai)
    AppCompatButton btnAI;
    @BindView(R.id.btn_friends)
    AppCompatButton btnFriends;
    @BindView(R.id.btn_online)
    AppCompatButton btnOnline;
    //endregion

    //region #Variables
    private AlertDialog selectDifficultyLevelAlertDialog;

    private MainActivity mainActivity;
    //endregion

    //region #Default Methods
    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        //initialize views and variables
        initialization();

        return view;
    }
    //endregion

    //region #Click Listeners
    @OnClick({R.id.btn_ai, R.id.btn_friends, R.id.btn_achievement, R.id.btn_leaderboard, R.id.btn_settings,
            R.id.btn_themes, R.id.btn_history, R.id.btn_online, R.id.btn_how_to_play, R.id.btn_exit})
    public void onViewClicked(View view) {
        if (CommonUtils.isClickDisabled()) {
            return;
        }

        mediaUtils.playButtonSound();

        switch (view.getId()) {
            //AI
            case R.id.btn_ai:
                showSelectDifficultyPopup(GameMode.AI);
                break;

            //Friends
            case R.id.btn_friends:
                selectPlayerName(GameMode.FRIEND, DifficultyLevel.EASY);
                break;

            //Achievements
            case R.id.btn_achievement:
                if (networkUtils.isConnected()) {
                    if (mainActivity.isSignedIn()) {
                        showAchievements();
                    } else {
                        mainActivity.startSignInIntent();
                    }
                } else {
                    showError(getString(R.string.error_internet_connection));
                }
                break;

            //Leaderboard
            case R.id.btn_leaderboard:
                if (networkUtils.isConnected()) {
                    if (mainActivity.isSignedIn()) {
                        showLeaderboard();
                    } else {
                        mainActivity.startSignInIntent();
                    }
                } else {
                    showError(getString(R.string.error_internet_connection));
                }
                break;

            //Settings
            case R.id.btn_settings:
                replaceFragment(new SettingsFragment(), R.id.fl_main_container, true);
                break;

            //Themes
            case R.id.btn_themes:
                replaceFragment(new ThemeFragment(), R.id.fl_main_container, true);
                break;

            //History
            case R.id.btn_history:
                replaceFragment(new HistoryFragment(), R.id.fl_main_container, true);
                break;

            //History
            case R.id.btn_online:
                replaceFragment(new PlayerListFragment(), R.id.fl_main_container, true);
                break;

            //How To Play
            case R.id.btn_how_to_play:
                ConfirmationAlertDialog.showConfirmationDialog(mainActivity, false,
                        getResources().getString(R.string.title_how_to_play), getString(R.string.message_how_to_play),
                        View.VISIBLE, getString(R.string.action_ok), View.GONE, "",
                        new ConfirmationAlertDialog.ConfirmationAlertDialogClickListener() {
                            @Override
                            public void onPositiveButtonClick() {
                                //Empty Method
                            }
                        });
                break;

            //Quit Game
            case R.id.btn_exit:
                mainActivity.showQuitGamePopup();
                break;

            //Default
            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("HomeFragment Default Click Called.");
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
    }

    /**
     * Set View Selected
     */
    private void setViewSelected() {
        btnAI.setSelected(true);
        btnFriends.setSelected(true);
        btnOnline.setSelected(true);
    }

    /**
     * Show Select Difficulty Popup
     *
     * @param gameMode - Game Mode
     */
    public void showSelectDifficultyPopup(@GameMode String gameMode) {
        //validation if dialog is null or already open
        if (selectDifficultyLevelAlertDialog != null && selectDifficultyLevelAlertDialog.isShowing()) {
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setCancelable(true);

        ViewGroup viewGroup = mainActivity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(mainActivity).inflate(R.layout.dialog_select_difficulty_level, viewGroup, false);

        AppCompatButton btnEasy = dialogView.findViewById(R.id.btn_easy);
        AppCompatButton btnMedium = dialogView.findViewById(R.id.btn_medium);
        AppCompatButton btnHard = dialogView.findViewById(R.id.btn_hard);
        AppCompatButton btnExpert = dialogView.findViewById(R.id.btn_expert);

        btnEasy.setSelected(true);
        btnMedium.setSelected(true);
        btnHard.setSelected(true);
        btnExpert.setSelected(true);

        builder.setView(dialogView);

        selectDifficultyLevelAlertDialog = builder.create();
        Window window = selectDifficultyLevelAlertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnEasy.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }
            mediaUtils.playButtonSound();

            selectDifficultyLevelAlertDialog.dismiss();
            selectPlayerName(gameMode, DifficultyLevel.EASY);
        });

        btnMedium.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }
            mediaUtils.playButtonSound();

            selectDifficultyLevelAlertDialog.dismiss();
            selectPlayerName(gameMode, DifficultyLevel.MEDIUM);
        });

        btnHard.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }
            mediaUtils.playButtonSound();

            selectDifficultyLevelAlertDialog.dismiss();
            selectPlayerName(gameMode, DifficultyLevel.HARD);
        });

        btnExpert.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }
            mediaUtils.playButtonSound();

            selectDifficultyLevelAlertDialog.dismiss();
            selectPlayerName(gameMode, DifficultyLevel.EXPERT);
        });

        selectDifficultyLevelAlertDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(selectDifficultyLevelAlertDialog.getWindow().getAttributes());
        layoutParams.width = (width / 5) * 4;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        selectDifficultyLevelAlertDialog.getWindow().setAttributes(layoutParams);
    }

    /**
     * Redirect to Player Name Selection
     *
     * @param gameMode - game Mode from GameMode annotation
     */
    public void selectPlayerName(@GameMode String gameMode, @DifficultyLevel String difficultyLevel) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BundleExtra.GAME_MODE, gameMode);
        bundle.putString(Constants.BundleExtra.DIFFICULTY_LEVEL, difficultyLevel);
        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(bundle);
        replaceFragment(playerFragment, R.id.fl_main_container, true);
    }

    /**
     * Show Leader Board
     */
    private void showLeaderboard() {
        mediaUtils.playButtonSound();

        Games.getLeaderboardsClient(mainActivity, GoogleSignIn.getLastSignedInAccount(mainActivity))
                .getLeaderboardIntent(getString(R.string.leaderboard_defeat_ai))
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_LEADERBOARD_UI));
    }

    /**
     * Show Achievements
     */
    private void showAchievements() {
        Games.getAchievementsClient(mainActivity, GoogleSignIn.getLastSignedInAccount(mainActivity))
                .getAchievementsIntent()
                .addOnSuccessListener(intent -> startActivityForResult(intent, RC_ACHIEVEMENT_UI));
    }
    //endregion
}
