package com.mikohatara.collectioncatalog.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 9,
    entities = [Plate::class, WantedPlate::class, FormerPlate::class],
    autoMigrations = [AutoMigration(from = 8, to = 9)]
)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun plateDao(): PlateDao
}
