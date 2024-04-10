package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow

class SamplesOfflineRepository(private val sampleDao: SampleDao) : SamplesRepository {
    override fun getAllSamplesStream(): Flow<List<Sample>> = sampleDao.getAllSamples()

    override fun getSampleStream(id: String): Flow<Sample?> = sampleDao.getSample(id)

    override suspend fun insertSample(sample: Sample) = sampleDao.insert(sample)

    override suspend fun updateSample(sample: Sample) = sampleDao.update(sample)

    override suspend fun deleteSample(sample: Sample) = sampleDao.delete(sample)
}