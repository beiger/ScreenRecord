package com.bing.example.model.db;

import android.content.Context;

import com.bing.example.model.db.dao.VideoDao;
import com.bing.example.model.entity.VideoInfo;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {VideoInfo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
        @VisibleForTesting
        public static final String DATABASE_NAME = "ScreenRecord-db";
        private static AppDatabase sInstance;

        public static AppDatabase getInstance(final Context context) {
                if (sInstance == null) {
                        synchronized (AppDatabase.class) {
                                if (sInstance == null) {
                                        sInstance = buildDatabase(context.getApplicationContext());
                                }
                        }
                }
                return sInstance;
        }

        /**
         * Build the database. {@link Builder#build()} only sets up the database configuration and
         * creates a new instance of the database.
         * The SQLite database is only created when it's accessed for the first time.
         */
        private static AppDatabase buildDatabase(final Context appContext) {
                return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
        }

        public abstract VideoDao videoDao();
}
