package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    suspend fun addPlate(plate: Plate)

    suspend fun updatePlate(plate: Plate)

    suspend fun deletePlate(plate: Plate)

    fun getPlateData(id: Int): Plate

    fun getPlateStream(id: Int): Flow<Plate?> //

    fun getAllPlatesStream(): Flow<List<Plate>>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {

    override suspend fun addPlate(plate: Plate) = plateDao.insertPlate(plate)

    override suspend fun updatePlate(plate: Plate) = plateDao.updatePlate(plate)

    override suspend fun deletePlate(plate: Plate) = plateDao.deletePlate(plate)

    override fun getPlateData(id: Int):
        Plate = plateDao.getPlateData(id)

    override fun getPlateStream(id: Int): Flow<Plate?> {
        return plateDao.getPlate(id).map { it }
    }

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()
}
