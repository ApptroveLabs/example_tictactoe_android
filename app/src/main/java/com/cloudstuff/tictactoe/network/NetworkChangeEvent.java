package com.cloudstuff.tictactoe.network;

public class NetworkChangeEvent {

    private final boolean isNetworkAvailable;

    public NetworkChangeEvent(boolean isNetworkAvailable) {
        this.isNetworkAvailable = isNetworkAvailable;
    }

    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    @Override
    public String toString() {
        return "NetworkChangeEvent{" +
                "isNetworkAvailable=" + isNetworkAvailable +
                '}';
    }
}