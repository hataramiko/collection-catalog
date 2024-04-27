package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlateRepository {
    //val plates: Flow<List<String>>

    //suspend fun add(name: String)

    fun getAllPlatesStream(): Flow<List<Plate>>
}

class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {

    /*override val plates: Flow<List<String>> =
        plateDao.getAllPlates().map { plates -> plates.map { it. } }*/

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()

    /*override suspend fun add(name: String) {
        plateDao.insert(Plate(name = name))
    }*/
}

/*
interface AppContainer {
    val platesRepository: PlatesRepository
}

class DataContainer(private val context: Context) : AppContainer {
    override val platesRepository: PlatesRepository by lazy {
        OfflinePlatesRepository(CollectionDatabase.getDatabase(context).plateDao())
    }
}*/