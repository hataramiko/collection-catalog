package com.mikohatara.collectioncatalog.di

import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.data.OfflineCollectionRepository
import com.mikohatara.collectioncatalog.data.OfflinePlateRepository
import com.mikohatara.collectioncatalog.data.PlateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsPlateRepository(plateRepository: OfflinePlateRepository): PlateRepository

    @Singleton
    @Binds
    fun bindsCollectionRepository(
        collectionRepository: OfflineCollectionRepository
    ): CollectionRepository
}

/*class FakePlateRepository @Inject constructor() : PlateRepository {
    override val plates: Flow<List<String>> = flowOf(fakePlates)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}*/
