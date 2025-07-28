package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.annotation.DifficultyLevel;
import com.cloudstuff.tictactoe.annotation.GameMode;
import com.cloudstuff.tictactoe.annotation.ThemeUnlockType;
import com.cloudstuff.tictactoe.model.Theme;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.Constants;
import com.cloudstuff.tictactoe.utils.ThemeManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerFragment extends BaseFragment {

    //region #Butterknife
    @BindView(R.id.tv_title)
    MaterialTextView tvTitle;
    @BindView(R.id.et_player_one)
    TextInputEditText etPlayerOne;
    @BindView(R.id.tv_player_two_header)
    MaterialTextView tvPlayerTwoHeader;
    @BindView(R.id.et_player_two)
    TextInputEditText etPlayerTwo;
    @BindView(R.id.rb_circle)
    MaterialRadioButton rbCircle;
    @BindView(R.id.rb_cross)
    MaterialRadioButton rbCross;
    @BindView(R.id.tv_select_side)
    MaterialTextView tvSelectSide;
    @BindView(R.id.btn_continue)
    AppCompatButton btnContinue;
    //endregion

    //region #Variables
    @GameMode
    private String currentGameMode;
    @DifficultyLevel
    private String difficultyLevel;
    private String playerOneName;
    private String playerTwoName;
    private boolean singlePlayer;
    private boolean playerOneCircleSelected;

    private int selectedTheme = 0;
    private String currentThemeId;
    private List<Theme> themeList;

    private MainActivity mainActivity;
    //endregion

    //region #InBuilt Methods
    public PlayerFragment() {
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
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, view);

        //initialize views and variables
        initialization();

        return view;
    }
    //endregion

    //region #OnClick
    @OnClick({R.id.iv_back, R.id.rb_circle, R.id.rb_cross, R.id.btn_continue})
    public void onViewClicked(View view) {
        if (CommonUtils.isClickDisabled()) {
            return;
        }

        mediaUtils.playButtonSound();

        //Hide Keyboard
        hideKeyBoard(view);

        switch (view.getId()) {
            //Back
            case R.id.iv_back:
                mainActivity.onBackPressed();
                break;

            //Circle
            case R.id.rb_circle:
                playerOneCircleSelected = true;
                break;

            //Cross
            case R.id.rb_cross:
                playerOneCircleSelected = false;
                break;

            //Continue
            case R.id.btn_continue:
                if (isPlayerValid()) {
                    if (singlePlayer) {
                        preferenceUtils.setString(Constants.PreferenceConstant.PLAYER_ONE_LAST_NAME, playerOneName);
                    } else {
                        preferenceUtils.setString(Constants.PreferenceConstant.PLAYER_ONE_LAST_NAME, playerOneName);
                        preferenceUtils.setString(Constants.PreferenceConstant.PLAYER_TWO_LAST_NAME, playerTwoName);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BundleExtra.GAME_MODE, currentGameMode);
                    bundle.putString(Constants.BundleExtra.DIFFICULTY_LEVEL, difficultyLevel);
                    bundle.putString(Constants.BundleExtra.PLAYER_ONE_NAME, playerOneName);
                    bundle.putString(Constants.BundleExtra.PLAYER_TWO_NAME, playerTwoName);
                    bundle.putBoolean(Constants.BundleExtra.SELECTED_SINGLE_PLAYER, singlePlayer);
                    bundle.putBoolean(Constants.BundleExtra.PLAYER_ONE_CIRCLE_SELECTED, playerOneCircleSelected);
                    GameFragment gameFragment = new GameFragment();
                    gameFragment.setArguments(bundle);
                    replaceFragment(gameFragment, R.id.fl_main_container, true);
                }
                break;

            //Default
            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("PlayerFragment Default Called.");
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

        if (getArguments() != null) {
            currentGameMode = getArguments().getString(Constants.BundleExtra.GAME_MODE);
            difficultyLevel = getArguments().getString(Constants.BundleExtra.DIFFICULTY_LEVEL);

            switch (currentGameMode) {
                //AI
                case GameMode.AI:
                    tvPlayerTwoHeader.setVisibility(View.GONE);
                    etPlayerTwo.setVisibility(View.GONE);
                    break;

                //FRIENDS
                case GameMode.FRIEND:
                    tvPlayerTwoHeader.setVisibility(View.VISIBLE);
                    etPlayerTwo.setVisibility(View.VISIBLE);
                    break;

                //Default
                default:
                    FirebaseCrashlytics.getInstance().log("Game Mode: Player : " + currentGameMode);
                    if (Constants.IS_DEBUG_ENABLE) {
                        showAlert("GameMode Default Called.");
                    }
                    break;
            }
        }

        String playerOneLastName = preferenceUtils.getString(Constants.PreferenceConstant.PLAYER_ONE_LAST_NAME);
        etPlayerOne.setText(playerOneLastName);

        String playerTwoLastName = preferenceUtils.getString(Constants.PreferenceConstant.PLAYER_TWO_LAST_NAME);
        etPlayerTwo.setText(playerTwoLastName);

        etPlayerOne.requestFocus();

        setupTicTacToeIcons();
    }

    /**
     * Set View Selected
     */
    private void setViewSelected() {
        tvTitle.setSelected(true);
        tvSelectSide.setSelected(true);
        btnContinue.setSelected(true);
    }

    /**
     * Setup TicTacToe Icon as per theme
     */
    private void setupTicTacToeIcons() {

        //Start Tracing Player Name Theme List Performance Monitoring - Fetch, Update and Show List
        Trace playerNameThemeTrace = FirebasePerformance.getInstance().newTrace(Constants.PerformanceMonitoring.PLAYER_NAME_THEME_TRACE);
        playerNameThemeTrace.start();

        themeList = ThemeManager.getThemeList();
        boolean isThemeSelected = false;
        //Set Selected Theme
        currentThemeId = preferenceUtils.getString(Constants.PreferenceConstant.SELECTED_THEME);
        int themeListSize = themeList.size();
        for (int i = 0; i < themeListSize; i++) {
            Theme theme = themeList.get(i);
            if (currentThemeId.equals(theme.getThemeId())) {
                selectedTheme = i;
                isThemeSelected = true;
                theme.setSelected(true);
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
                                isThemeSelected = false;
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
                            isThemeSelected = false;
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

        rbCircle.setCompoundDrawablesWithIntrinsicBounds(themeList.get(selectedTheme).getCircleIcon(), 0, 0, 0);
        rbCross.setCompoundDrawablesWithIntrinsicBounds(themeList.get(selectedTheme).getCrossIcon(), 0, 0, 0);

        //Stop Player Name Theme Trace Performance Monitoring
        playerNameThemeTrace.stop();
    }

    /**
     * Check Player is Valid or not
     *
     * @return - true if all details are filled properly else false
     */
    private boolean isPlayerValid() {
        if (currentGameMode.equals(GameMode.AI)) {
            singlePlayer = true;
            playerOneName = etPlayerOne.getText().toString().trim();
            playerTwoName = Constants.AppConstant.PLAYER_AI;
        } else {
            singlePlayer = false;
            playerOneName = etPlayerOne.getText().toString().trim();
            playerTwoName = etPlayerTwo.getText().toString().trim();
        }

        //Check Player One Name is Valid or not
        if (TextUtils.isEmpty(playerOneName)) {
            showAlert(getString(R.string.error_enter_player_one_name));
            return false;
        }

        //Check Player Two Name is Valid or not
        if (TextUtils.isEmpty(playerTwoName)) {
            showAlert(getString(R.string.error_enter_player_two_name));
            return false;
        }

        //Check Player1 and Player 2 name must not be same
        if (playerOneName.equals(playerTwoName)) {
            showAlert(getString(R.string.error_enter_different_player_one_two_name));
            return false;
        }

        //Check Player One has selected 0 or X
        if (!rbCircle.isChecked() && !rbCross.isChecked()) {
            showAlert(getString(R.string.error_player_one_select_your_side));
            return false;
        }
        return true;
    }

    /**
     * Set Default Classic Theme
     */
    private void setClassicTheme() {
        selectedTheme = 0;
        currentThemeId = themeList.get(selectedTheme).getThemeId();
        preferenceUtils.setString(Constants.PreferenceConstant.SELECTED_THEME, currentThemeId);
        themeList.get(selectedTheme).setSelected(true);
    }
    //endregion
}