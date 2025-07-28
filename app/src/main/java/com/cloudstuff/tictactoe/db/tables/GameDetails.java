package com.cloudstuff.tictactoe.db.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GameDetails {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "playerOneName")
    private String playerOneName;

    @ColumnInfo(name = "playerTwoName")
    private String playerTwoName;

    @ColumnInfo(name = "totalGame")
    private int totalGame;

    @ColumnInfo(name = "playerOneScore")
    private int playerOneScore;

    @ColumnInfo(name = "playerTwoScore")
    private int playerTwoScore;

    @ColumnInfo(name = "draw")
    private int drawScore;

    @ColumnInfo(name = "gameMode")
    private String gameMode;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getTotalGame() {
        return totalGame;
    }

    public void setTotalGame(int totalGame) {
        this.totalGame = totalGame;
    }

    public int getPlayerOneScore() {
        return playerOneScore;
    }

    public void setPlayerOneScore(int playerOneScore) {
        this.playerOneScore = playerOneScore;
    }

    public int getPlayerTwoScore() {
        return playerTwoScore;
    }

    public void setPlayerTwoScore(int playerTwoScore) {
        this.playerTwoScore = playerTwoScore;
    }

    public int getDrawScore() {
        return drawScore;
    }

    public void setDrawScore(int drawScore) {
        this.drawScore = drawScore;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GameDetails{" +
                "id=" + id +
                ", playerOneName='" + playerOneName + '\'' +
                ", playerTwoName='" + playerTwoName + '\'' +
                ", totalGame=" + totalGame +
                ", playerOneScore=" + playerOneScore +
                ", playerTwoScore=" + playerTwoScore +
                ", drawScore=" + drawScore +
                ", gameMode='" + gameMode + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
