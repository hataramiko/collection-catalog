package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    suspend fun addPlate(plate: Plate)

    suspend fun updatePlate(plate: Plate)

    suspend fun deletePlate(plate: Plate)

    fun getPlateData(number: String, variant: String): Plate

    fun getPlateStream(number: String, variant: String): Flow<Plate?> //

    fun getAllPlatesStream(): Flow<List<Plate>>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {

    override suspend fun addPlate(plate: Plate) = plateDao.insertPlate(plate)

    override suspend fun updatePlate(plate: Plate) = plateDao.updatePlate(plate)

    override suspend fun deletePlate(plate: Plate) = plateDao.deletePlate(plate)

    override fun getPlateData(number: String, variant: String):
        Plate = plateDao.getPlateData(number, variant)

    override fun getPlateStream(number: String, variant: String): Flow<Plate?> {
        return plateDao.getPlate(number, variant).map { it }
    }

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()
}
