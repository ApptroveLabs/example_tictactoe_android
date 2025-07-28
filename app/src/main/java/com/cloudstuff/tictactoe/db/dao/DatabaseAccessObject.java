package com.cloudstuff.tictactoe.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.cloudstuff.tictactoe.db.tables.GameDetails;

import java.util.List;

@Dao
public interface DatabaseAccessObject {

    @Insert
    void insertGameScore(GameDetails gameDetails);

    @Query("SELECT * FROM GameDetails ORDER BY timestamp desc LIMIT :limit")
    LiveData<List<GameDetails>> getAllGameDetails(int limit);
}