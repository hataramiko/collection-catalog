package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow

interface SamplesRepository {
    fun getAllSamplesStream(): Flow<List<Sample>>

    fun getSampleStream(id: String): Flow<Sample?>

    suspend fun insertSample(sample: Sample)

    suspend fun deleteSample(sample: Sample)

    suspend fun updateSample(sample: Sample)
}