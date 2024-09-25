package com.mikohatara.collectioncatalog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollection(collection: Collection)

    @Update
    suspend fun updateCollection(collection: Collection)

    @Delete
    suspend fun deleteCollection(collection: Collection)

    @Query("DELETE from plate_collection_cross_ref WHERE collection_id = :collectionId")
    suspend fun deletePlateCollectionCrossRefsByCollection(collectionId: Int)

    @Transaction
    suspend fun deleteCollectionWithPlates(collection: Collection) {
        deletePlateCollectionCrossRefsByCollection(collection.id)
        deleteCollection(collection)
    }

    @Query("SELECT * from collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<Collection>>

    @Query("SELECT * from collections WHERE id = :id")
    fun getCollection(id: Int): Flow<Collection>

    @Transaction
    @Query("SELECT * from collections WHERE id = :id")
    fun getCollectionWithPlates(id: Int): Flow<CollectionWithPlates>
}
