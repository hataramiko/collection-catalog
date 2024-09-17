package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CollectionRepository {
    suspend fun addCollection(collection: Collection)

    suspend fun updateCollection(collection: Collection)

    suspend fun deleteCollection(collection: Collection)

    fun getAllCollectionsStream(): Flow<List<Collection>>

    fun getCollectionStream(id: Int): Flow<Collection?>
}

class OfflineCollectionRepository @Inject constructor(
    private val collectionDao: CollectionDao
) : CollectionRepository {
    override suspend fun addCollection(collection: Collection) =
        collectionDao.insertCollection(collection)

    override suspend fun updateCollection(collection: Collection) =
        collectionDao.updateCollection(collection)

    override suspend fun deleteCollection(collection: Collection) =
        collectionDao.deleteCollection(collection)

    override fun getAllCollectionsStream(): Flow<List<Collection>> =
        collectionDao.getAllCollections()

    override fun getCollectionStream(id: Int): Flow<Collection?> {
        return collectionDao.getCollection(id).map { it }
    }
}
