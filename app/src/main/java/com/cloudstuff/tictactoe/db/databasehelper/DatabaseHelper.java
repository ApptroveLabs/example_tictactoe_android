package com.cloudstuff.tictactoe.db.databasehelper;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.cloudstuff.tictactoe.db.TicTacToeDatabase;
import com.cloudstuff.tictactoe.db.dao.DatabaseAccessObject;
import com.cloudstuff.tictactoe.db.tables.GameDetails;

import java.util.List;

public class DatabaseHelper {

    private DatabaseAccessObject databaseAccessObject;

    public DatabaseHelper(Context context) {
        TicTacToeDatabase db = TicTacToeDatabase.getDatabase(context.getApplicationContext());
        databaseAccessObject = db.myDatabaseAccessObject();
    }

    //Insert Game Data
    public void insertGameScore(GameDetails gameDetails) {
        databaseAccessObject.insertGameScore(gameDetails);
    }

    //Get All Game Details
    public LiveData<List<GameDetails>> getAllGameDetails(int limit) {
        return databaseAccessObject.getAllGameDetails(limit);
    }
}
