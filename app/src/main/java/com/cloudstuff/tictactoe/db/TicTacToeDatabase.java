package com.cloudstuff.tictactoe.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.cloudstuff.tictactoe.db.dao.DatabaseAccessObject;
import com.cloudstuff.tictactoe.db.tables.GameDetails;
import com.cloudstuff.tictactoe.utils.Constants;

@Database(entities = {GameDetails.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class TicTacToeDatabase extends RoomDatabase {

    public abstract DatabaseAccessObject myDatabaseAccessObject();

    private static TicTacToeDatabase ticTacToeDatabaseInstance;

    public static TicTacToeDatabase getDatabase(final Context context) {
        if (ticTacToeDatabaseInstance == null) {
            synchronized (TicTacToeDatabase.class) {
                if (ticTacToeDatabaseInstance == null) {
                    ticTacToeDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            TicTacToeDatabase.class, Constants.DatabaseConstants.DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return ticTacToeDatabaseInstance;
    }
}