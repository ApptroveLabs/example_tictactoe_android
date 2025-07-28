package com.cloudstuff.tictactoe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GamePlayRequest implements Parcelable {
    public String playerOneId;
    public String playerTwoId;
    public String playerOneName;
    public String playerTwoName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.playerOneId);
        dest.writeString(this.playerTwoId);
        dest.writeString(this.playerOneName);
        dest.writeString(this.playerTwoName);
    }

    public GamePlayRequest() {
    }

    protected GamePlayRequest(Parcel in) {
        this.playerOneId = in.readString();
        this.playerTwoId = in.readString();
        this.playerOneName = in.readString();
        this.playerTwoName = in.readString();
    }

    public static final Creator<GamePlayRequest> CREATOR = new Creator<GamePlayRequest>() {
        @Override
        public GamePlayRequest createFromParcel(Parcel source) {
            return new GamePlayRequest(source);
        }

        @Override
        public GamePlayRequest[] newArray(int size) {
            return new GamePlayRequest[size];
        }
    };
}
