package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    suspend fun addPlate(plate: Plate): Long
    suspend fun addPlateWithCollections(plate: Plate, collectionIds: List<Int>)

    suspend fun updatePlate(plate: Plate)
    suspend fun updatePlateWithCollections(plate: Plate, collectionIds: List<Int>)

    suspend fun deletePlate(plate: Plate)
    suspend fun deletePlateWithCollections(plate: Plate)

    fun getAllPlatesStream(): Flow<List<Plate>>

    fun getPlateStream(id: Int): Flow<Plate?>
    fun getPlateWithCollectionsStream(id: Int): Flow<PlateWithCollections?>

    //

    suspend fun addWantedPlate(plate: WantedPlate)

    suspend fun updateWantedPlate(plate: WantedPlate)

    suspend fun deleteWantedPlate(plate: WantedPlate)

    fun getAllWantedPlatesStream(): Flow<List<WantedPlate>>

    fun getWantedPlateStream(id: Int): Flow<WantedPlate?>

    //

    suspend fun addFormerPlate(plate: FormerPlate)

    suspend fun updateFormerPlate(plate: FormerPlate)

    suspend fun deleteFormerPlate(plate: FormerPlate)

    fun getAllFormerPlatesStream(): Flow<List<FormerPlate>>

    fun getFormerPlateStream(id: Int): Flow<FormerPlate?>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {
    override suspend fun addPlate(plate: Plate) = plateDao.insertPlate(plate)
    override suspend fun addPlateWithCollections(plate: Plate, collectionIds: List<Int>) =
        plateDao.insertPlateWithCollections(plate, collectionIds)

    override suspend fun updatePlate(plate: Plate) = plateDao.updatePlate(plate)
    override suspend fun updatePlateWithCollections(plate: Plate, collectionIds: List<Int>) =
        plateDao.updatePlateWithCollections(plate, collectionIds)

    override suspend fun deletePlate(plate: Plate) = plateDao.deletePlate(plate)
    override suspend fun deletePlateWithCollections(plate: Plate) =
        plateDao.deletePlateWithCollections(plate)

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()

    override fun getPlateStream(id: Int): Flow<Plate?> {
        return plateDao.getPlate(id).map { it }
    }
    override fun getPlateWithCollectionsStream(id: Int): Flow<PlateWithCollections?> {
        return plateDao.getPlateWithCollections(id).map { it }
    }

    //

    override suspend fun addWantedPlate(plate: WantedPlate) = plateDao.insertWantedPlate(plate)

    override suspend fun updateWantedPlate(plate: WantedPlate) = plateDao.updateWantedPlate(plate)

    override suspend fun deleteWantedPlate(plate: WantedPlate) = plateDao.deleteWantedPlate(plate)

    override fun getAllWantedPlatesStream(): Flow<List<WantedPlate>> = plateDao.getAllWantedPlates()

    override fun getWantedPlateStream(id: Int): Flow<WantedPlate?> {
        return plateDao.getWantedPlate(id).map { it }
    }

    //

    override suspend fun addFormerPlate(plate: FormerPlate) = plateDao.insertFormerPlate(plate)

    override suspend fun updateFormerPlate(plate: FormerPlate) = plateDao.updateFormerPlate(plate)

    override suspend fun deleteFormerPlate(plate: FormerPlate) = plateDao.deleteFormerPlate(plate)

    override fun getAllFormerPlatesStream(): Flow<List<FormerPlate>> = plateDao.getAllFormerPlates()

    override fun getFormerPlateStream(id: Int): Flow<FormerPlate?> {
        return plateDao.getFormerPlate(id).map { it }
    }
}
