package com.example.Safenow;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ContactList.class}, version = 2, exportSchema = false)
public abstract class InstaSOSRoomDatabase extends RoomDatabase {
    public abstract ContactListDao contactListDao();

    public static volatile InstaSOSRoomDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static InstaSOSRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (InstaSOSRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            InstaSOSRoomDatabase.class, "femiguard_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
