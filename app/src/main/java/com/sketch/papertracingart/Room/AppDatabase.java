package com.sketch.papertracingart.Room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.sketch.papertracingart.MyApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ImageEntry.class, FavoriteImage.class}, version = MyApp.DB_Version, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ImageEntryDao imageEntryDao();
    public abstract FavoriteImageDao favoriteImageDao();

    private static volatile AppDatabase INSTANCE;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, MyApp.DB_NAme)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}
