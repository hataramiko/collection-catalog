package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    suspend fun addPlate(
        /*country: String,
        region: String? = null,
        area: String? = null,
        type: String,
        period: String? = null,
        year: Int? = null,
        number: String,
        variant: String,
        vehicle: String? = null,
        notes: String? = null,
        date: String? = null,
        cost: Double? = null,
        value: Double? = null,
        status: String? = null,
        isKeeper: Boolean,
        isForTrade: Boolean,
        sourceName: String? = null,
        sourceAlias: String? = null,
        sourceDetails: String? = null,
        sourceType: String? = null,
        sourceCountry: String? = null*/
        plate: Plate
    )/*: Plate*/
    fun getPlateData(number: String, variant: String): Plate
    fun getPlateStream(number: String, variant: String): Flow<Plate?> //
    fun getAllPlatesStream(): Flow<List<Plate>>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {

    override suspend fun addPlate(
        /*country: String,
        region: String?,
        area: String?,
        type: String,
        period: String?,
        year: Int?,
        number: String,
        variant: String,
        vehicle: String?,
        notes: String?,
        date: String?,
        cost: Double?,
        value: Double?,
        status: String?,
        isKeeper: Boolean,
        isForTrade: Boolean,
        sourceName: String?,
        sourceAlias: String?,
        sourceDetails: String?,
        sourceType: String?,
        sourceCountry: String?*/
        plate: Plate
    ) = plateDao.insertPlate(plate)
    /*: Plate {
        val plate = Plate(
            CommonDetails(country, region, area, type, period, year),
            UniqueDetails(number, variant, vehicle, notes, date, cost, value, status),
            Availability(isKeeper, isForTrade),
            Source(sourceName, sourceAlias, sourceDetails, sourceType, sourceCountry)
        )
        plateDao.insertPlate(plate)
        return plate
    }*/

    override fun getPlateData(number: String, variant: String):
        Plate = plateDao.getPlateData(number, variant)

    override fun getPlateStream(number: String, variant: String): Flow<Plate?> {
        return plateDao.getPlate(number, variant).map { it }
    }

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()
}
