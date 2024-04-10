package com.mikohatara.collectioncatalog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Sample::class], version = 1, exportSchema = false)
abstract class SampleDatabase : RoomDatabase() {

    abstract fun sampleDao(): SampleDao

    companion object {
        @Volatile
        private var Instance: SampleDatabase? = null

        fun getDatabase(context: Context): SampleDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    SampleDatabase::class.java,
                    "sample_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}