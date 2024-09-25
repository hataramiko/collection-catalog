package com.mikohatara.collectioncatalog.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    version = 12,
    entities = [
        Plate::class,
        WantedPlate::class,
        FormerPlate::class,
        Collection::class,
        PlateCollectionCrossRef::class
    ],
    autoMigrations = [AutoMigration(from = 11, to = 12, spec = PlateDatabase.DeleteGrading::class)]
)
abstract class PlateDatabase : RoomDatabase() { // TODO rename class
    abstract fun plateDao(): PlateDao
    abstract fun collectionDao(): CollectionDao

    @DeleteColumn.Entries(
        DeleteColumn(tableName = "plates", columnName = "keeper"),
        DeleteColumn(tableName = "plates", columnName = "for_trade")
    )
    class DeleteGrading : AutoMigrationSpec
}
