package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    fun getPlateStream(number: String, variant: Char): Flow<Plate> //
    fun getAllPlatesStream(): Flow<List<Plate>>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {
    override fun getPlateStream(number: String, variant: Char):
        Flow<Plate> = plateDao.getPlate(number, variant) //

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()
}
