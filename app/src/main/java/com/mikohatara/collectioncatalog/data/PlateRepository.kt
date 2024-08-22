package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    suspend fun addPlate(plate: Plate)

    suspend fun updatePlate(plate: Plate)

    suspend fun deletePlate(plate: Plate)

    fun getAllPlatesStream(): Flow<List<Plate>>

    fun getPlateStream(id: Int): Flow<Plate?>

    suspend fun addWantedPlate(plate: WantedPlate)

    suspend fun updateWantedPlate(plate: WantedPlate)

    suspend fun deleteWantedPlate(plate: WantedPlate)

    fun getAllWantedPlatesStream(): Flow<List<WantedPlate>>

    fun getWantedPlateStream(id: Int): Flow<WantedPlate?>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {
    override suspend fun addPlate(plate: Plate) = plateDao.insertPlate(plate)

    override suspend fun updatePlate(plate: Plate) = plateDao.updatePlate(plate)

    override suspend fun deletePlate(plate: Plate) = plateDao.deletePlate(plate)

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()

    override fun getPlateStream(id: Int): Flow<Plate?> {
        return plateDao.getPlate(id).map { it }
    }

    override suspend fun addWantedPlate(plate: WantedPlate) = plateDao.insertWantedPlate(plate)

    override suspend fun updateWantedPlate(plate: WantedPlate) = plateDao.updateWantedPlate(plate)

    override suspend fun deleteWantedPlate(plate: WantedPlate) = plateDao.deleteWantedPlate(plate)

    override fun getAllWantedPlatesStream(): Flow<List<WantedPlate>> = plateDao.getAllWantedPlates()

    override fun getWantedPlateStream(id: Int): Flow<WantedPlate?> {
        return plateDao.getWantedPlate(id).map { it }
    }
}
