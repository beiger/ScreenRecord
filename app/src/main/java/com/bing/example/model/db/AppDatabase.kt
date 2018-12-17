package com.bing.example.model.db

import android.content.Context

import com.bing.example.model.db.dao.VideoDao
import com.bing.example.model.entity.VideoInfo

import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VideoInfo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

        abstract fun videoDao(): VideoDao

        companion object {
                @VisibleForTesting
                val DATABASE_NAME = "ScreenRecord-db"
                private var sInstance: AppDatabase? = null

                fun getInstance(context: Context): AppDatabase {
                        if (sInstance == null) {
                                synchronized(AppDatabase::class.java) {
                                        if (sInstance == null) {
                                                sInstance = buildDatabase(context.applicationContext)
                                        }
                                }
                        }
                        return sInstance!!
                }

                /**
                 * Build the database. [Builder.build] only sets up the database configuration and
                 * creates a new instance of the database.
                 * The SQLite database is only created when it's accessed for the first time.
                 */
                private fun buildDatabase(appContext: Context): AppDatabase {
                        return Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                                .allowMainThreadQueries()
                                .build()
                }
        }
}
