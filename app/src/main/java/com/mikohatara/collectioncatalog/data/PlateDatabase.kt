package com.mikohatara.collectioncatalog.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 13,
    entities = [
        Plate::class,
        WantedPlate::class,
        FormerPlate::class,
        Collection::class,
        PlateCollectionCrossRef::class
    ],
    autoMigrations = [AutoMigration(from = 12, to = 13)]
)
abstract class PlateDatabase : RoomDatabase() { // TODO rename class
    abstract fun plateDao(): PlateDao
    abstract fun collectionDao(): CollectionDao
}
