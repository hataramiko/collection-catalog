package com.mikohatara.collectioncatalog.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Plate::class], version = 6, exportSchema = false)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun plateDao(): PlateDao
}
