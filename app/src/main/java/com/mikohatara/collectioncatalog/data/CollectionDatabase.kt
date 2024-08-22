package com.mikohatara.collectioncatalog.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 8,
    entities = [Plate::class, WantedPlate::class]
)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun plateDao(): PlateDao
}
