package com.mikohatara.collectioncatalog.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "plate_collection_cross_ref",
    primaryKeys = ["plate_id", "collection_id"]
)
data class PlateCollectionCrossRef(
    @ColumnInfo(name = "plate_id") val plateId: Int,
    @ColumnInfo(name = "collection_id") val collectionId: Int
)

data class PlateWithCollections(
    @Embedded val plate: Plate,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            PlateCollectionCrossRef::class,
            parentColumn = "plate_id",
            entityColumn = "collection_id"
        )
    )
    val collections: List<Collection>
)

data class CollectionWithPlates(
    @Embedded val collection: Collection,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            PlateCollectionCrossRef::class,
            parentColumn = "collection_id",
            entityColumn = "plate_id"
        )
    )
    val plates: List<Plate>
)
