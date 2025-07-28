package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import com.cloudstuff.tictactoe.utils.AnalyticsUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.annotation.DifficultyLevel;
import com.cloudstuff.tictactoe.annotation.GameMode;
import com.cloudstuff.tictactoe.db.tables.GameDetails;
import com.cloudstuff.tictactoe.model.Game;
import com.cloudstuff.tictactoe.model.Theme;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.Constants;
import com.cloudstuff.tictactoe.utils.ThemeManager;
import com.trackier.sdk.TrackierEvent;
import com.trackier.sdk.TrackierSDK;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class GameFragment extends BaseFragment {

    //region #Butterknife
    @BindView(R.id.tv_player_one_name)
    MaterialTextView tvPlayerOneName;
    @BindView(R.id.tv_player_two_name)
    MaterialTextView tvPlayerTwoName;
    @BindView(R.id.tv_player_one_score)
    MaterialTextView tvPlayerOneScore;
    @BindView(R.id.tv_player_two_score)
    MaterialTextView tvPlayerTwoScore;
    @BindView(R.id.iv_zero_zero)
    AppCompatImageView ivZeroZero;
    @BindView(R.id.iv_zero_one)
    AppCompatImageView ivZeroOne;
    @BindView(R.id.iv_zero_two)
    AppCompatImageView ivZeroTwo;
    @BindView(R.id.iv_one_zero)
    AppCompatImageView ivOneZero;
    @BindView(R.id.iv_one_one)
    AppCompatImageView ivOneOne;
    @BindView(R.id.iv_one_two)
    AppCompatImageView ivOneTwo;
    @BindView(R.id.iv_two_zero)
    AppCompatImageView ivTwoZero;
    @BindView(R.id.iv_two_one)
    AppCompatImageView ivTwoOne;
    @BindView(R.id.iv_two_two)
    AppCompatImageView ivTwoTwo;
    @BindView(R.id.ll_player_one_box)
    LinearLayout llPlayerOneBox;
    @BindView(R.id.ll_player_two_box)
    LinearLayout llPlayerTwoBox;
    @BindView(R.id.view_win_line_zero)
    View viewWinLineZero;
    @BindView(R.id.view_win_line_one)
    View viewWinLineOne;
    @BindView(R.id.view_win_line_two)
    View viewWinLineTwo;
    @BindView(R.id.view_win_line_three)
    View viewWinLineThree;
    @BindView(R.id.view_win_line_four)
    View viewWinLineFour;
    @BindView(R.id.view_win_line_five)
    View viewWinLineFive;
    @BindView(R.id.view_win_line_six)
    View viewWinLineSix;
    @BindView(R.id.view_win_line_seven)
    View viewWinLineSeven;
    //endregion

    //region #Variables
    private String playerOneName;
    private String playerTwoName;
    @GameMode
    private String gameMode;
    @DifficultyLevel
    private String difficultyLevel;
    private boolean isSinglePlayer;
    private boolean isPlayerOneTurn = true;
    private boolean isVibrationEnable;
    private boolean isGameWin = false;
    private boolean isGameDraw = false;

    private int playerOneSelection = 10; //10 = Circle
    private int playerTwoSelection = 1; //1 = Cross
    private int crossIcon;
    private int circleIcon;
    private int playerOneScore = 0;
    private int playerTwoScore = 0;
    private int drawScore = 0;
    private int selectedBoxCount = 0;
    private int totalGame = 1;
    private boolean isAiTurn = false;
    private boolean isWinCheckerRunning = false;
    private boolean isPlayAgainPositiveButtonClick = false;
    private boolean isBackPageClick = false;
    private static AlertDialog playAgainDialog;

    private Random random = new Random();

    private int[] boxValueHolder = new int[8];
    private int[][] box = new int[3][3];
    private int[][] buttonPressed = new int[3][3];

    private GameDetails gameDetails = null;
    private MainActivity mainActivity;
    private Game onlineGame;
    private boolean isHost = false;
    private int playerTurn;
    private boolean enableClick;
    //endregion


    private FirebaseAnalytics mFirebaseAnalytics;

    //region #Getter Setter
    public boolean isBackPageClick() {
        return isBackPageClick;
    }
    //endregion

    //region #Get Game Details
    public GameDetails getGameDetails() {
        if (gameDetails != null) {
            gameDetails.setPlayerOneName(playerOneName);
            gameDetails.setPlayerTwoName(playerTwoName);
            gameDetails.setTotalGame(playerOneScore + playerTwoScore + drawScore);
            gameDetails.setPlayerOneScore(playerOneScore);
            gameDetails.setPlayerTwoScore(playerTwoScore);
            gameDetails.setDrawScore(drawScore);
            gameDetails.setGameMode(gameMode);
            gameDetails.setTimestamp(System.currentTimeMillis());
        }
        return gameDetails;
    }
    //endregion

    //region #InBuilt Methods
    public GameFragment() {
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
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this, view);

        //initialize views and variables
        initialization();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        return view;
    }
    //endregion

    @OnClick(R.id.iv_back)
    public void onClickBack(View view) {
        if (CommonUtils.isClickDisabled()) {
            return;
        }

        mediaUtils.playButtonSound();

        goToBackPage();
    }

    //region #OnClick
    @OnClick({R.id.iv_zero_zero, R.id.iv_zero_one, R.id.iv_zero_two, R.id.iv_one_zero,
            R.id.iv_one_one, R.id.iv_one_two, R.id.iv_two_zero, R.id.iv_two_one, R.id.iv_two_two})
    public void onViewClicked(View view) {

        if (gameMode.equals(GameMode.ONLINE)) {
            if (!enableClick) return;
        }

        if (CommonUtils.isClickDisabled()) {
            return;
        }

        mediaUtils.playButtonSound();

        switch (view.getId()) {

            //Zero-Zero
            case R.id.iv_zero_zero:
                onZeroZeroClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("0_0");
                }
                break;

            //Zero-One
            case R.id.iv_zero_one:
                onZeroOneClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("0_1");
                }
                break;

            //Zero-Two
            case R.id.iv_zero_two:
                onZeroTwoClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("0_2");
                }
                break;

            //One-Zero
            case R.id.iv_one_zero:
                onOneZeroClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("1_0");
                }
                break;

            //One-One
            case R.id.iv_one_one:
                onOneOneClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("1_1");
                }
                break;

            //One-Two
            case R.id.iv_one_two:
                onOneTwoClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("1_2");
                }
                break;

            //Two-Zero
            case R.id.iv_two_zero:
                onTwoZeroClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("2_0");
                }
                break;

            //Two-One
            case R.id.iv_two_one:
                onTwoOneClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("2_1");
                }
                break;

            //Two-Two
            case R.id.iv_two_two:
                onTwoTwoClick();
                if (gameMode.equals(GameMode.ONLINE)) {
                    mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").setValue("2_2");
                }
                break;

            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("GameFragment Default Click Called.");
                }
                break;
        }

        if (gameMode.equals(GameMode.ONLINE)) {
            if (isHost) {
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("playerTurn").setValue(2);
            } else {
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("playerTurn").setValue(1);
            }
        }

        /*if (gameMode.equals(GameMode.ONLINE)) {
            if (isPlayerOne) {
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("playerTurn").setValue(1);
            } else {
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("playerTurn").setValue(2);
            }
        }*/

    }
    //endregion

    //region #Custom Methods

    /**
     * initialize views and variables
     */
    private void initialization() {
        //Set View Selected
        setViewSelected();

        //Setup AdMob
        setupAdMob();

        if (getArguments() != null) {

            gameMode = getArguments().getString(Constants.BundleExtra.GAME_MODE);

            if (Objects.equals(gameMode, GameMode.ONLINE)) {
                onlineGame = new Game();
                onlineGame.setGameStatus(Game.GAME_STARTED);
                String playerOneId = getArguments().getString(Constants.BundleExtra.PLAYER_ONE_ID);
                String playerTwoId = getArguments().getString(Constants.BundleExtra.PLAYER_TWO_ID);
                onlineGame.setPlayerOneId(playerOneId);
                onlineGame.setPlayerOneName(getArguments().getString(Constants.BundleExtra.PLAYER_ONE_NAME));
                onlineGame.setPlayerTwoId(playerTwoId);
                onlineGame.setPlayerTwoName(getArguments().getString(Constants.BundleExtra.PLAYER_TWO_NAME));
                onlineGame.setWinner(0); // no winner
                onlineGame.setGameId(playerOneId + "_" + playerTwoId);
                isHost = getArguments().getBoolean(Constants.BundleExtra.IS_HOST);
                Timber.d("isHost ==> %s", isHost);
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).setValue(onlineGame);
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("playerTurn").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Timber.d("Player Turn Changed");
                        int turn = Integer.parseInt(dataSnapshot.getValue().toString());
                        isPlayerOneTurn = turn == 1;
                        playerTurn = turn;

                        if (isHost) {
                            enableClick = turn == 1;
                        } else {
                            enableClick = turn != 1;
                        }

                        drawPlayerTurnBox(isPlayerOneTurn);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e("playerTurn onCancelled ==> %s", databaseError.getDetails());
                    }
                });

                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("winner").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int winner = Integer.parseInt(dataSnapshot.getValue().toString());
                        if (winner == 1) {
                            playAgainPopup(isGameDraw, true);
                        } else if (winner == 2) {
                            playAgainPopup(isGameDraw, false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("currentMove").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value = String.valueOf(dataSnapshot.getValue());
                        switch (value) {
                            case "":
                                // ignore
                                break;
                            case "0_0":
                                onZeroZeroClick();
                                break;
                            case "0_1":
                                onZeroOneClick();
                                break;
                            case "0_2":
                                onZeroTwoClick();
                                break;
                            case "1_0":
                                onOneZeroClick();
                                break;
                            case "1_1":
                                onOneOneClick();
                                break;
                            case "1_2":
                                onOneTwoClick();
                                break;
                            case "2_0":
                                onTwoZeroClick();
                                break;
                            case "2_1":
                                onTwoOneClick();
                                break;
                            case "2_2":
                                onTwoTwoClick();
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e("currentMove onCancelled ==> %s", databaseError.getDetails());
                    }
                });
            }
            playerOneName = getArguments().getString(Constants.BundleExtra.PLAYER_ONE_NAME);
            playerTwoName = getArguments().getString(Constants.BundleExtra.PLAYER_TWO_NAME);
            difficultyLevel = getArguments().getString(Constants.BundleExtra.DIFFICULTY_LEVEL);
            boolean isPlayerOneCircleSelected = getArguments().getBoolean(Constants.BundleExtra.PLAYER_ONE_CIRCLE_SELECTED);
            isSinglePlayer = getArguments().getBoolean(Constants.BundleExtra.SELECTED_SINGLE_PLAYER);
            isVibrationEnable = preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_VIBRATION_ON);

            //1 = Cross, 10 = Circle
            if (gameMode.equals(GameMode.ONLINE)) {
                playerOneSelection = 10;
                playerTwoSelection = 1;
            } else {
                if (isPlayerOneCircleSelected) {
                    playerOneSelection = 10;
                    playerTwoSelection = 1;
                } else {
                    playerOneSelection = 1;
                    playerTwoSelection = 10;
                }
            }

            manageAchievement();

            tvPlayerOneName.setText(playerOneName);
            tvPlayerTwoName.setText(playerTwoName);

            drawPlayerTurnBox(isPlayerOneTurn);

            String selectedThemeId = preferenceUtils.getString(Constants.PreferenceConstant.SELECTED_THEME);
            List<Theme> themeList = ThemeManager.getThemeList();
            int selectedTheme = 0;
            int themeListSize = themeList.size();
            for (int i = 0; i < themeListSize; i++) {
                if (selectedThemeId.equals(themeList.get(i).getThemeId())) {
                    selectedTheme = i;
                    break;
                }
            }

            crossIcon = themeList.get(selectedTheme).getCrossIcon();
            circleIcon = themeList.get(selectedTheme).getCircleIcon();

            tvPlayerOneScore.setText(String.valueOf(playerOneScore));
            tvPlayerTwoScore.setText(String.valueOf(playerTwoScore));

            setAllWinLineInVisible();
        }
    }

    /**
     * Set View Selected
     */
    private void setViewSelected() {
        tvPlayerOneName.setSelected(true);
        tvPlayerTwoName.setSelected(true);
    }

    /**
     * Manage Admob Interstitial Ads
     */
    private void setupAdMob() {
        adMobUtils.loadInterstitialAd(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
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
                            showAlert("Interstitial ad Fail Default Called.");
                        }
                        break;
                }
            }

            @Override
            public void onAdOpened() {
                mediaUtils.pauseMusic();
                // Code to be executed when an ad opens an overlay that covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                mediaUtils.pauseMusic();
            }

            public void onAdLeftApplication() {
                mediaUtils.pauseMusic();
                // Code to be executed when the user has left the app.
                mainActivity.clearBackStack();
                replaceFragment(new HomeFragment(), R.id.fl_main_container, false);
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return to the app after tapping on an ad.
                mediaUtils.playMusic();

                if (isPlayAgainPositiveButtonClick) {
                    adMobUtils.requestNewInterstitial();
                    playAgain();
                } else if (isBackPageClick) {
                    isBackPageClick = false;
                    mainActivity.onBackPressed();
                } else {
                    mainActivity.showInAppReview();
                    mainActivity.clearBackStack();
                    replaceFragment(new HomeFragment(), R.id.fl_main_container, false);
                }
            }
        });
    }

    /**
     * Zero-Zero Cell Click on Board
     */
    private void onZeroZeroClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[0][0] == 0) {

                if (playerTurn == 1) {
                    box[0][0] = playerOneSelection;
                } else {
                    box[0][0] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[0][0]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[0][0] == 0) {
            box[0][0] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[0][0]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * Zero-One Cell Click on Board
     */
    private void onZeroOneClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[0][1] == 0) {

                if (playerTurn == 1) {
                    box[0][1] = playerOneSelection;
                } else {
                    box[0][1] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[0][1]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[0][1] == 0) {
            box[0][1] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[0][1]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * Zero-Two Cell Click on Board
     */
    private void onZeroTwoClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[0][2] == 0) {

                if (playerTurn == 1) {
                    box[0][2] = playerOneSelection;
                } else {
                    box[0][2] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[0][2]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[0][2] == 0) {
            box[0][2] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[0][2]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * One-Zero Cell Click on Board
     */
    private void onOneZeroClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[1][0] == 0) {

                if (playerTurn == 1) {
                    box[1][0] = playerOneSelection;
                } else {
                    box[1][0] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[1][0]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[1][0] == 0) {
            box[1][0] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[1][0]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * One-One Cell Click on Board
     */
    private void onOneOneClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[1][1] == 0) {

                if (playerTurn == 1) {
                    box[1][1] = playerOneSelection;
                } else {
                    box[1][1] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[1][1]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[1][1] == 0) {
            box[1][1] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[1][1]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * One-Two Cell Click on Board
     */
    private void onOneTwoClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[1][2] == 0) {

                if (playerTurn == 1) {
                    box[1][2] = playerOneSelection;
                } else {
                    box[1][2] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[1][2]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[1][2] == 0) {
            box[1][2] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[1][2]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * Two-Zero Cell Click on Board
     */
    private void onTwoZeroClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[2][0] == 0) {

                if (playerTurn == 1) {
                    box[2][0] = playerOneSelection;
                } else {
                    box[2][0] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[2][0]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[2][0] == 0) {
            box[2][0] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[2][0]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * Two-One Cell Click on Board
     */
    private void onTwoOneClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[2][1] == 0) {

                if (playerTurn == 1) {
                    box[2][1] = playerOneSelection;
                } else {
                    box[2][1] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[2][1]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[2][1] == 0) {
            box[2][1] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[2][1]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * Two-Two Cell Click on Board
     */
    private void onTwoTwoClick() {
        if (gameMode.equals(GameMode.ONLINE)) {
            if (!isWinCheckerRunning && !isAiTurn && buttonPressed[2][2] == 0) {

                if (playerTurn == 1) {
                    box[2][2] = playerOneSelection;
                } else {
                    box[2][2] = playerTwoSelection;
                }

                drawTicTacToeBoard();
                winChecker();
                cpuTurn();
                buttonPressed[2][2]++;
            }
            return;
        }
        if (!isWinCheckerRunning && !isAiTurn && buttonPressed[2][2] == 0) {
            box[2][2] = isPlayerOneTurn ? playerOneSelection : playerTwoSelection;

            drawTicTacToeBoard();
            winChecker();
            cpuTurn();
            buttonPressed[2][2]++;
            isPlayerOneTurn = !isPlayerOneTurn;
            drawPlayerTurnBox(isPlayerOneTurn);
        }
    }

    /**
     * Draw Tic Tac Toe Board
     */
    private void drawTicTacToeBoard() {
        //Tic Tac Toe Board Position - 0,0
        if (box[0][0] == 1) {
            ivZeroZero.setImageResource(crossIcon);
        } else if (box[0][0] == 10) {
            ivZeroZero.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 0,1
        if (box[0][1] == 1) {
            ivZeroOne.setImageResource(crossIcon);
        } else if (box[0][1] == 10) {
            ivZeroOne.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 0,2
        if (box[0][2] == 1) {
            ivZeroTwo.setImageResource(crossIcon);
        } else if (box[0][2] == 10) {
            ivZeroTwo.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 1,0
        if (box[1][0] == 1) {
            ivOneZero.setImageResource(crossIcon);
        } else if (box[1][0] == 10) {
            ivOneZero.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 1,1
        if (box[1][1] == 1) {
            ivOneOne.setImageResource(crossIcon);
        } else if (box[1][1] == 10) {
            ivOneOne.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 1,2
        if (box[1][2] == 1) {
            ivOneTwo.setImageResource(crossIcon);
        } else if (box[1][2] == 10) {
            ivOneTwo.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 2,0
        if (box[2][0] == 1) {
            ivTwoZero.setImageResource(crossIcon);
        } else if (box[2][0] == 10) {
            ivTwoZero.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 2,1
        if (box[2][1] == 1) {
            ivTwoOne.setImageResource(crossIcon);
        } else if (box[2][1] == 10) {
            ivTwoOne.setImageResource(circleIcon);
        }

        //Tic Tac Toe Board Position - 2,2
        if (box[2][2] == 1) {
            ivTwoTwo.setImageResource(crossIcon);
        } else if (box[2][2] == 10) {
            ivTwoTwo.setImageResource(circleIcon);
        }
    }

    /**
     * Player/AI Win Checker
     */
    private void winChecker() {
        selectedBoxCount++;
        isWinCheckerRunning = true;

        //Calculate Selected Box Count
        boxValueHolder[0] = box[0][0] + box[0][1] + box[0][2];
        boxValueHolder[1] = box[1][0] + box[1][1] + box[1][2];
        boxValueHolder[2] = box[2][0] + box[2][1] + box[2][2];
        boxValueHolder[3] = box[0][0] + box[1][0] + box[2][0];
        boxValueHolder[4] = box[0][1] + box[1][1] + box[2][1];
        boxValueHolder[5] = box[0][2] + box[1][2] + box[2][2];
        boxValueHolder[6] = box[0][0] + box[1][1] + box[2][2];
        boxValueHolder[7] = box[0][2] + box[1][1] + box[2][0];

        //Check Player Win or not
        for (int i = 0; i < boxValueHolder.length; i++) {
            int value = boxValueHolder[i];
            if (value == 3 || value == 30) {
                //Vibrate
                if (isVibrationEnable) {
                    Vibrator v = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
                    if (v != null) {
                        // Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(500);
                        }
                    }
                }

                isGameWin = true;

                switch (i) {
                    //Scenario 0 Win
                    case 0:
                        viewWinLineZero.setVisibility(View.VISIBLE);
                        break;

                    //Scenario 1 Win
                    case 1:
                        viewWinLineOne.setVisibility(View.VISIBLE);
                        break;

                    //Scenario 2 Win
                    case 2:
                        viewWinLineTwo.setVisibility(View.VISIBLE);
                        break;

                    //Scenario 3 Win
                    case 3:
                        viewWinLineThree.setVisibility(View.VISIBLE);
                        break;

                    //Scenario 4 Win
                    case 4:
                        viewWinLineFour.setVisibility(View.VISIBLE);
                        break;

                    //Scenario 5 Win
                    case 5:
                        viewWinLineFive.setVisibility(View.VISIBLE);
                        break;

                    //Scenario 6 Win
                    case 6:
                        viewWinLineSix.setVisibility(View.VISIBLE);
                        break;

                    //Scenario 7 Win
                    case 7:
                        viewWinLineSeven.setVisibility(View.VISIBLE);
                        break;
                }

                //Player One Win
                if ((value == 3 && playerOneSelection == 1) || (value == 30 && playerOneSelection == 10)) {
                    if (gameDetails == null) {
                        gameDetails = new GameDetails();
                    }

                    playerOneScore += 1;

                    //Update Defeat AI Score in Leaderboard
                    if (isSinglePlayer) {
                        int score = preferenceUtils.getInteger(Constants.PreferenceConstant.DEFEAT_AI_COUNT);
                        score += 1;
                        preferenceUtils.setInteger(Constants.PreferenceConstant.DEFEAT_AI_COUNT, score);
                        updateLevelInLeaderboard(getString(R.string.leaderboard_defeat_ai), score);

                        manageAchievement();
                    }
                    tvPlayerOneScore.setText(String.valueOf(playerOneScore));
                    showAlert(getString(R.string.message_player_win, playerOneName));
                    mSafeHandler.postDelayed(() -> playAgainPopup(isGameDraw, true), Constants.Delay.PLAY_AGAIN_DELAY);

                    if (gameMode.equals(GameMode.ONLINE)) {
                        mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("gameStatus").setValue(Game.GAME_COMPLETED);
                        mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("winner").setValue(1);
                    }
                    TrackierEvent event = new TrackierEvent("ErkEjPi4X1");
                    event.param1 = "Player one won";
                    TrackierSDK.trackEvent(event);

                    Bundle bundle = new Bundle();
                    bundle.putString(AnalyticsUtils.AnalyticsEvents.EVENT_GAME_RESULT, "Player one won");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    Log.d("TAG", "winChecker: "+"mFirebaseAnalytics triggered");

                }

                //Player Two Win
                if ((value == 3 && playerTwoSelection == 1) || (value == 30 && playerTwoSelection == 10)) {
                    if (gameDetails == null) {
                        gameDetails = new GameDetails();
                    }

                    playerTwoScore += 1;
                    tvPlayerTwoScore.setText(String.valueOf(playerTwoScore));
                    showAlert(getString(R.string.message_player_win, playerTwoName));
                    mSafeHandler.postDelayed(() -> playAgainPopup(isGameDraw, false), Constants.Delay.PLAY_AGAIN_DELAY);
                    if (gameMode.equals(GameMode.ONLINE)) {
                        mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("gameStatus").setValue(Game.GAME_COMPLETED);
                        mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("winner").setValue(2);
                    }
                    TrackierEvent event = new TrackierEvent("ErkEjPi4X1");
                    event.param1 = "Player two won";
                    TrackierSDK.trackEvent(event);

                    Bundle bundle = new Bundle();
                    bundle.putString(AnalyticsUtils.AnalyticsEvents.EVENT_GAME_RESULT, "Player two won");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    Log.d("TAG", "winChecker: "+"mFirebaseAnalytics triggered");
                }
            }
        }

        //Draw
        if (!isGameWin && (selectedBoxCount == 9)) {
            if (gameDetails == null) {
                gameDetails = new GameDetails();
            }

            drawScore += 1;
            showAlert(getString(R.string.message_draw));
            isGameDraw = true;
            mSafeHandler.postDelayed(() -> playAgainPopup(isGameDraw, false), Constants.Delay.PLAY_AGAIN_DELAY);
            if (gameMode.equals(GameMode.ONLINE)) {
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("gameStatus").setValue(Game.GAME_COMPLETED);
                mainActivity.getGameTableRef().child(onlineGame.getGameId()).child("winner").setValue(0);
            }
        }

        isWinCheckerRunning = false;
    }

    /**
     * Play Again Popup
     */
    private void playAgainPopup(boolean isDraw, boolean isPlayerOneWin) {

        //validation if dialog is null or already open
        if (playAgainDialog != null && playAgainDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setCancelable(false);

        ViewGroup viewGroup = mainActivity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(mainActivity).inflate(R.layout.dialog_play_again, viewGroup, false);

        MaterialTextView tvPlayerNameOne = dialogView.findViewById(R.id.tv_player_name_one);
        MaterialTextView tvPlayerNameTwo = dialogView.findViewById(R.id.tv_player_name_two);
        MaterialTextView tvPlayerScoreOne = dialogView.findViewById(R.id.tv_player_score_one);
        MaterialTextView tvPlayerScoreTwo = dialogView.findViewById(R.id.tv_player_score_two);
        AppCompatImageView ivPlayerOneStatus = dialogView.findViewById(R.id.iv_player_one_status);
        AppCompatImageView ivPlayerTwoStatus = dialogView.findViewById(R.id.iv_player_two_status);
        AppCompatButton btnPlayAgain = dialogView.findViewById(R.id.btn_play_again);
        AppCompatButton btnHome = dialogView.findViewById(R.id.btn_home);

        btnPlayAgain.setSelected(true);
        btnHome.setSelected(true);

        tvPlayerNameOne.setText(playerOneName);
        tvPlayerScoreOne.setText(String.valueOf(playerOneScore));
        tvPlayerNameOne.setSelected(true);

        tvPlayerNameTwo.setText(playerTwoName);
        tvPlayerScoreTwo.setText(String.valueOf(playerTwoScore));
        tvPlayerNameTwo.setSelected(true);

        if (isDraw) {
            tvPlayerScoreOne.setTextColor(ContextCompat.getColor(mainActivity, R.color.game_draw_text_color));
            tvPlayerScoreTwo.setTextColor(ContextCompat.getColor(mainActivity, R.color.game_draw_text_color));
            ivPlayerOneStatus.setVisibility(View.GONE);
            ivPlayerTwoStatus.setVisibility(View.GONE);
        } else {
            if (isPlayerOneWin) {
                tvPlayerScoreOne.setTextColor(ContextCompat.getColor(mainActivity, R.color.game_win_text_color));
                tvPlayerScoreTwo.setTextColor(ContextCompat.getColor(mainActivity, R.color.game_lose_text_color));
                ivPlayerOneStatus.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_win));
                ivPlayerTwoStatus.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_lost));
            } else {
                tvPlayerScoreOne.setTextColor(ContextCompat.getColor(mainActivity, R.color.game_lose_text_color));
                tvPlayerScoreTwo.setTextColor(ContextCompat.getColor(mainActivity, R.color.game_win_text_color));
                ivPlayerOneStatus.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_lost));
                ivPlayerTwoStatus.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.ic_win));
            }
        }

        builder.setView(dialogView);

        playAgainDialog = builder.create();
        Window window = playAgainDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnPlayAgain.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }

            playAgainDialog.dismiss();

            if (!preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
                InterstitialAd interstitialAd = adMobUtils.getInterstitialAd();

                int showInterstitialAdAfterGame = mainActivity.getCommonConfiguration().getShowInterstitialAdAfterGame();
                if (showInterstitialAdAfterGame <= 0) {
                    showInterstitialAdAfterGame = Constants.RemoteConfig.SHOW_INTERSTITIAL_AD_AFTER_GAME;
                }

                if (interstitialAd != null && (totalGame % showInterstitialAdAfterGame == 0)) {
                    isPlayAgainPositiveButtonClick = true;
                    //interstitialAd.show();
                } else {
                    adMobUtils.requestNewInterstitial();
                    playAgain();
                }
            } else {
                playAgain();
            }
        });

        btnHome.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }

            playAgainDialog.dismiss();

            if (!preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
                InterstitialAd interstitialAd = adMobUtils.getInterstitialAd();
                if (interstitialAd != null) {
                    isPlayAgainPositiveButtonClick = false;
                } else {
                    mainActivity.showInAppReview();
                    mainActivity.clearBackStack();
                    replaceFragment(new HomeFragment(), R.id.fl_main_container, false);

                }
            } else {
                mainActivity.showInAppReview();
                mainActivity.clearBackStack();
                replaceFragment(new HomeFragment(), R.id.fl_main_container, false);
            }
        });

        playAgainDialog.show();
    }

    /**
     * Play Again
     */
    private void playAgain() {
        if (isGameDraw || isGameWin) {
            setAllWinLineInVisible();

            totalGame += 1;

            //Reset Selected Box Count
            for (int i = 0; i < 8; i++) {
                boxValueHolder[i] = 0;
            }

            isGameDraw = false;

            ivZeroZero.setImageDrawable(null);
            ivZeroOne.setImageDrawable(null);
            ivZeroTwo.setImageDrawable(null);
            ivOneZero.setImageDrawable(null);
            ivOneOne.setImageDrawable(null);
            ivOneTwo.setImageDrawable(null);
            ivTwoZero.setImageDrawable(null);
            ivTwoOne.setImageDrawable(null);
            ivTwoTwo.setImageDrawable(null);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttonPressed[i][j] = 0;
                    box[i][j] = 0;
                }
            }

            if (totalGame % 2 == 0) {
                llPlayerOneBox.setBackground(null);
                llPlayerTwoBox.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.selected_player));
            } else {
                llPlayerOneBox.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.selected_player));
                llPlayerTwoBox.setBackground(null);
            }

            isGameWin = false;
            selectedBoxCount = 0;
            isPlayerOneTurn = (totalGame % 2 == 1);

            if (isSinglePlayer && (totalGame % 2 == 0)) {
                cpuTurn();
            }

            manageAchievement();
        }
    }

    /**
     * CPU Turn
     */
    private void cpuTurn() {
        if (isSinglePlayer && !isGameWin) {
            isAiTurn = true;

            drawPlayerTurnBox(false);

            Timer t = new Timer();
            t.schedule(new TimerTask() {
                public void run() {
                    mSafeHandler.post(() -> {
                        if (ifCpuWin()) {
                            //Empty Method
                        } else if (ifOpoWin()) {
                            //Empty Method
                        } else if (emptyCentre()) {
                            //Empty Method
                        } else if (emptyCorner()) {
                            //Empty Method
                        } else {
                            emptyAny();
                        }

                        drawTicTacToeBoard();
                        winChecker();

                        isPlayerOneTurn = !isPlayerOneTurn;
                        isAiTurn = false;
                        drawPlayerTurnBox(true);
                    });
                }
            }, Constants.Delay.CPU_TURN_DELAY);
        }
    }

    /**
     * If CPU is Winning
     *
     * @return
     */
    private boolean ifCpuWin() {
        if (!difficultyLevel.equals(DifficultyLevel.EASY)) {
            for (int i = 0; i < boxValueHolder.length; i++) {
                if (boxValueHolder[i] == 2 * playerTwoSelection) {
                    if (i == 0) {
                        for (int x = 0; x < 3; x++) {
                            if (box[0][x] == 0) {
                                box[0][x] = playerTwoSelection;
                            }
                        }
                    }

                    if (i == 1) {
                        for (int x = 0; x < 3; x++) {
                            if (box[1][x] == 0) {
                                box[1][x] = playerTwoSelection;
                            }
                        }

                    }
                    if (i == 2) {
                        for (int x = 0; x < 3; x++) {
                            if (box[2][x] == 0) {
                                box[2][x] = playerTwoSelection;
                            }
                        }
                    }

                    if (i == 3) {
                        for (int x = 0; x < 3; x++) {
                            if (box[x][0] == 0) {
                                box[x][0] = playerTwoSelection;
                            }
                        }
                    }

                    if (i == 4) {
                        for (int x = 0; x < 3; x++) {
                            if (box[x][1] == 0) {
                                box[x][1] = playerTwoSelection;
                            }
                        }
                    }

                    if (i == 5) {
                        for (int x = 0; x < 3; x++) {
                            if (box[x][2] == 0) {
                                box[x][2] = playerTwoSelection;
                            }
                        }
                    }

                    if (i == 6) {
                        for (int y = 0; y < 3; y++) {
                            for (int x = 0; x < 3; x++) {
                                if (x == y) {
                                    if (box[x][y] == 0) {
                                        box[x][y] = playerTwoSelection;
                                    }
                                }
                            }
                        }
                    }

                    if (i == 7) {
                        if (box[0][2] == 0) {
                            box[0][2] = playerTwoSelection;
                        } else if (box[1][1] == 0) {
                            box[1][1] = playerTwoSelection;
                        } else {
                            box[2][0] = playerTwoSelection;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * If Opponent is Winning
     *
     * @return
     */
    private boolean ifOpoWin() {
        if (!difficultyLevel.equals(DifficultyLevel.EASY) || !difficultyLevel.equals(DifficultyLevel.MEDIUM)) {
            for (int i = 0; i < boxValueHolder.length; i++) {
                if (boxValueHolder[i] == 2 * playerOneSelection) {
                    if (i == 0) {
                        for (int x = 0; x < 3; x++)
                            if (box[0][x] == 0) {
                                box[0][x] = playerTwoSelection;
                                buttonPressed[0][x]++;
                            }
                    }

                    if (i == 1) {
                        for (int x = 0; x < 3; x++)
                            if (box[1][x] == 0) {
                                box[1][x] = playerTwoSelection;
                                buttonPressed[1][x]++;
                            }
                    }
                    if (i == 2) {
                        for (int x = 0; x < 3; x++)
                            if (box[2][x] == 0) {
                                box[2][x] = playerTwoSelection;
                                buttonPressed[2][x]++;
                            }
                    }

                    if (i == 3) {
                        for (int x = 0; x < 3; x++)
                            if (box[x][0] == 0) {
                                box[x][0] = playerTwoSelection;
                                buttonPressed[x][0]++;
                            }
                    }

                    if (i == 4) {
                        for (int x = 0; x < 3; x++)
                            if (box[x][1] == 0) {
                                box[x][1] = playerTwoSelection;
                                buttonPressed[x][1]++;
                            }
                    }

                    if (i == 5) {
                        for (int x = 0; x < 3; x++)
                            if (box[x][2] == 0) {
                                box[x][2] = playerTwoSelection;
                                buttonPressed[x][2]++;
                            }
                    }

                    if (i == 6) {
                        for (int y = 0; y < 3; y++)
                            for (int x = 0; x < 3; x++)
                                if (x == y)
                                    if (box[x][y] == 0) {
                                        box[x][y] = playerTwoSelection;
                                        buttonPressed[x][y]++;
                                    }
                    }

                    if (i == 7) {
                        if (box[0][2] == 0) {
                            box[0][2] = playerTwoSelection;
                            buttonPressed[0][2]++;
                        } else if (box[1][1] == 0) {
                            box[1][1] = playerTwoSelection;
                            buttonPressed[1][1]++;
                        } else {
                            box[2][0] = playerTwoSelection;
                            buttonPressed[2][0]++;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for Center Box
     *
     * @return
     */
    private boolean emptyCentre() {
        if (difficultyLevel.equals(DifficultyLevel.EXPERT) || difficultyLevel.equals(DifficultyLevel.HARD)) {
            if (box[1][1] == 0) {
                box[1][1] = playerTwoSelection;
                buttonPressed[1][1]++;
                return true;
            }
        }
        return false;
    }

    /**
     * Check For Empty Corner Box
     *
     * @return
     */
    private boolean emptyCorner() {
        if (difficultyLevel.equals(DifficultyLevel.HARD) || difficultyLevel.equals(DifficultyLevel.EXPERT)) {
            if (((box[0][0] + box[2][2]) == 2 * playerOneSelection) || ((box[0][2] + box[2][0]) == 2 * playerOneSelection)) {
                for (int k = 0; k < 3; k++)
                    for (int j = 0; j < 3; j++)
                        if ((k + j) % 2 == 1) {
                            if (box[k][j] == 0)
                                box[k][j] = playerTwoSelection;
                            buttonPressed[k][j]++;
                            return true;
                        }
            }
        }

        if (difficultyLevel.equals(DifficultyLevel.EXPERT)) {
            if (boxValueHolder[6] == playerTwoSelection || boxValueHolder[7] == playerTwoSelection) {
                if (boxValueHolder[6] == playerTwoSelection) {
                    if ((boxValueHolder[0] + boxValueHolder[3]) > (boxValueHolder[2] + boxValueHolder[5])) {
                        box[0][0] = playerTwoSelection;
                        buttonPressed[0][0]++;
                    } else {
                        box[2][2] = playerTwoSelection;
                        buttonPressed[2][2]++;
                    }
                    return true;
                }

                if (boxValueHolder[7] == playerTwoSelection) {
                    if ((boxValueHolder[0] + boxValueHolder[5]) > (boxValueHolder[3] + boxValueHolder[2])) {
                        box[0][2] = playerTwoSelection;
                        buttonPressed[0][2]++;
                    } else {
                        box[2][0] = playerTwoSelection;
                        buttonPressed[2][0]++;
                    }
                    return true;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (box[0][i] == playerOneSelection) {
                if (box[0][0] == 0) {
                    box[0][0] = playerTwoSelection;
                    buttonPressed[0][0]++;
                    return true;
                }
                if (box[0][2] == 0) {
                    box[0][2] = playerTwoSelection;
                    buttonPressed[0][2]++;
                    return true;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (box[2][i] == playerOneSelection) {
                if (box[2][0] == 0) {
                    box[2][0] = playerTwoSelection;
                    buttonPressed[2][0]++;
                    return true;
                }
                if (box[2][2] == 0) {
                    box[2][2] = playerTwoSelection;
                    buttonPressed[2][2]++;
                    return true;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (box[i][0] == playerOneSelection) {
                if (box[0][0] == 0) {
                    box[0][0] = playerTwoSelection;
                    buttonPressed[0][0]++;
                    return true;
                }
                if (box[2][0] == 0) {
                    box[2][0] = playerTwoSelection;
                    buttonPressed[2][0]++;
                    return true;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (box[i][2] == playerOneSelection) {
                if (box[0][2] == 0) {
                    box[0][2] = playerTwoSelection;
                    buttonPressed[0][2]++;
                    return true;
                }
                if (box[2][2] == 0) {
                    box[2][2] = playerTwoSelection;
                    buttonPressed[2][2]++;
                    return true;
                }
            }
        }
        return false;
    }

    private void emptyAny() {
        if (selectedBoxCount == 0)
            while (true) {
                int x = random.nextInt(3);
                int y = random.nextInt(3);

                if (box[x][y] == 0) {
                    box[x][y] = playerTwoSelection;
                    buttonPressed[x][y]++;
                    return;
                }
            }

        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++)
                if (box[x][y] == 0) {
                    box[x][y] = playerTwoSelection;
                    buttonPressed[x][y]++;
                    return;
                }
    }

    /**
     * Draw Player Turn Box
     *
     * @param playerOneTurn - true if Player One Turn, else false
     */
    private void drawPlayerTurnBox(boolean playerOneTurn) {
        if (playerOneTurn) {
            llPlayerOneBox.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.selected_player));
            llPlayerTwoBox.setBackground(null);
        } else {
            llPlayerOneBox.setBackground(null);
            llPlayerTwoBox.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.selected_player));
        }
    }

    private void manageAchievement() {
        if (isSinglePlayer) {
            int playAICount = preferenceUtils.getInteger(Constants.PreferenceConstant.PLAY_AI_COUNT);
            playAICount += 1;
            preferenceUtils.setInteger(Constants.PreferenceConstant.PLAY_AI_COUNT, playAICount);

            //Play AI - 25
            if (playAICount >= 25) {
                unlockAchievement(getString(R.string.achievement_play_ai__25));
            }

            //Play AI - 50
            if (playAICount >= 50) {
                unlockAchievement(getString(R.string.achievement_play_ai__50));
            }

            //Play AI - 100
            if (playAICount >= 100) {
                unlockAchievement(getString(R.string.achievement_play_ai__100));
            }

            //Play AI - 250
            if (playAICount >= 250) {
                unlockAchievement(getString(R.string.achievement_play_ai__250));
            }

            //Play AI - 500
            if (playAICount > 500) {
                unlockAchievement(getString(R.string.achievement_play_ai__500));
            }

            int defeatAICount = preferenceUtils.getInteger(Constants.PreferenceConstant.DEFEAT_AI_COUNT);

            //Defeat AI - 25
            if (defeatAICount >= 25) {
                unlockAchievement(getString(R.string.achievement_defeat_ai__25));
            }

            //Defeat AI - 50
            if (defeatAICount >= 50) {
                unlockAchievement(getString(R.string.achievement_defeat_ai__50));
            }

            //Defeat AI - 100
            if (defeatAICount >= 100) {
                unlockAchievement(getString(R.string.achievement_defeat_ai__100));
            }

            //Defeat AI - 250
            if (defeatAICount >= 250) {
                unlockAchievement(getString(R.string.achievement_defeat_ai__250));
            }

            //Defeat AI - 500
            if (defeatAICount > 500) {
                unlockAchievement(getString(R.string.achievement_defeat_ai__500));
            }
        } else {
            int playFriendsCount = preferenceUtils.getInteger(Constants.PreferenceConstant.PLAY_FRIENDS_COUNT);
            playFriendsCount += 1;
            preferenceUtils.setInteger(Constants.PreferenceConstant.PLAY_FRIENDS_COUNT, playFriendsCount);

            //Play Friends - 25
            if (playFriendsCount >= 25) {
                unlockAchievement(getString(R.string.achievement_play_friends__25));
            }

            //Play Friends - 50
            if (playFriendsCount >= 50) {
                unlockAchievement(getString(R.string.achievement_play_friends__50));
            }

            //Play Friends - 100
            if (playFriendsCount >= 100) {
                unlockAchievement(getString(R.string.achievement_play_friends__100));
            }

            //Play Friends - 250
            if (playFriendsCount >= 250) {
                unlockAchievement(getString(R.string.achievement_play_friends__250));
            }

            //Play Friends - 500
            if (playFriendsCount > 500) {
                unlockAchievement(getString(R.string.achievement_play_friends__500));
            }
        }
    }

    /**
     * Update Current Level In Leaderboard
     *
     * @param leaderboardId - leaderboard Id
     * @param score         - score
     */
    private void updateLevelInLeaderboard(String leaderboardId, int score) {
        if (mainActivity.isSignedIn()) {
            Games.getLeaderboardsClient(mainActivity, GoogleSignIn.getLastSignedInAccount(getActivity()))
                    .submitScore(leaderboardId, score);
        }
    }

    /**
     * Unlock Achievement
     *
     * @param achievementId - achievement Id
     */
    private void unlockAchievement(String achievementId) {
        if (mainActivity.isSignedIn()) {
            Games.getAchievementsClient(mainActivity, GoogleSignIn.getLastSignedInAccount(mainActivity))
                    .unlock(achievementId);
        }
    }

    /**
     * Set All Win Line InVisible
     */
    private void setAllWinLineInVisible() {
        viewWinLineZero.setVisibility(View.INVISIBLE);
        viewWinLineOne.setVisibility(View.INVISIBLE);
        viewWinLineTwo.setVisibility(View.INVISIBLE);
        viewWinLineThree.setVisibility(View.INVISIBLE);
        viewWinLineFour.setVisibility(View.INVISIBLE);
        viewWinLineFive.setVisibility(View.INVISIBLE);
        viewWinLineSix.setVisibility(View.INVISIBLE);
        viewWinLineSeven.setVisibility(View.INVISIBLE);
    }

    /**
     * Go To Back Page on back press
     */
    public void goToBackPage() {
        if (!preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AD_REMOVED)) {
            InterstitialAd interstitialAd = adMobUtils.getInterstitialAd();
            if (interstitialAd != null) {
                isBackPageClick = true;
                //interstitialAd.show();
            } else {
                mainActivity.onBackPressed();
            }
        } else {
            mainActivity.onBackPressed();
        }
    }
    //endregion
}