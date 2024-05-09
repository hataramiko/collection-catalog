package com.mikohatara.collectioncatalog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Plate::class], version = 2, exportSchema = false)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun plateDao(): PlateDao
}
