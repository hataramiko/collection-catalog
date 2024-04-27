package com.mikohatara.collectioncatalog.di

import com.mikohatara.collectioncatalog.data.OfflinePlateRepository
import com.mikohatara.collectioncatalog.data.PlateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsPlateRepository(plateRepository: OfflinePlateRepository): PlateRepository
}

/*class FakePlateRepository @Inject constructor() : PlateRepository {
    override val plates: Flow<List<String>> = flowOf(fakePlates)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}

val fakePlates = listOf("ABC-123", "DEF-456", "GHI-789")*/