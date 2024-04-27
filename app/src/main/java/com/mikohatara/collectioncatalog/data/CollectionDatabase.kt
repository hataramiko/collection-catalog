package com.mikohatara.collectioncatalog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Plate::class], version = 1, exportSchema = false)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun plateDao(): PlateDao

    /*
    companion object {
        @Volatile
        private var Instance: CollectionDatabase? = null

        fun getDatabase(context: Context): CollectionDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    CollectionDatabase::class.java,
                    "plate_database"
                )
                    //.fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }*/
}