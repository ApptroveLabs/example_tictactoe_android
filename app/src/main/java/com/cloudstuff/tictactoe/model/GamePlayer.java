package com.cloudstuff.tictactoe.model;

public class GamePlayer {
    private String displayName;
    private String givenName;
    private String photoUrl;
    private boolean isOnline;
    private String playerId;
    private long lastPlayedTimestamp;
    private String fcmToken;

    public GamePlayer() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getLastPlayedTimestamp() {
        return lastPlayedTimestamp;
    }

    public void setLastPlayedTimestamp(long lastPlayedTimestamp) {
        this.lastPlayedTimestamp = lastPlayedTimestamp;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
