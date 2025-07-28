package com.cloudstuff.tictactoe.model;

public class Game {

    public static final int GAME_STARTED = 1;
    public static final int GAME_COMPLETED = 2;
    public static final int GAME_PENDING = 0;
    public static final int GAME_QUIT = 4;

    private String gameId;
    private int gameStatus = GAME_PENDING;
    private String currentMove = "";
    private String playerOneId;
    private String playerTwoId;
    private String playerOneName;
    private String playerTwoName;
    private int winner = 0;
    private int playerTurn = 1;

    public Game() {
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(String currentMove) {
        this.currentMove = currentMove;
    }

    public String getPlayerOneId() {
        return playerOneId;
    }

    public void setPlayerOneId(String playerOneId) {
        this.playerOneId = playerOneId;
    }

    public String getPlayerTwoId() {
        return playerTwoId;
    }

    public void setPlayerTwoId(String playerTwoId) {
        this.playerTwoId = playerTwoId;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public void setTurn(int turn) {
        this.playerTurn = turn;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }
}

