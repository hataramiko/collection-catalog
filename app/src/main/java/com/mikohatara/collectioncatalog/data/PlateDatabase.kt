package com.mikohatara.collectioncatalog.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 19,
    entities = [
        Plate::class,
        WantedPlate::class,
        FormerPlate::class,
        Collection::class,
        PlateCollectionCrossRef::class
    ]
)
abstract class PlateDatabase : RoomDatabase() { // TODO rename class
    abstract fun plateDao(): PlateDao
    abstract fun collectionDao(): CollectionDao
}
