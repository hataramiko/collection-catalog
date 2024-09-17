package com.mikohatara.collectioncatalog.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [Collection::class],
    //autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class CollectionDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
}
