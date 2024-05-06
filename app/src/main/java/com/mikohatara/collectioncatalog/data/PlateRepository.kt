package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    fun getPlateStream(): Flow<Plate> //
    fun getAllPlatesStream(): Flow<List<Plate>>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {
    override fun getPlateStream(): Flow<Plate> = plateDao.getPlate(number = "", variant = 'a') //
    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()
}
