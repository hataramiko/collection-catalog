package com.mikohatara.collectioncatalog.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/*
@Singleton
class PlateRepository @Inject constructor(private val plateDao: PlateDao) {


    companion object {
        @Volatile private var instance: PlateRepository? = null

        fun getInstance(plateDao: PlateDao) =
            instance ?: synchronized(this) {
                instance ?: PlateRepository(plateDao).also { instance = it }
            }
    }
}*/
/*
interface PlateRepository {
    //val plates: Flow<List<String>>

    //suspend fun add(name: String)

    fun getAllPlatesStream(): Flow<List<Plate>>
}*/
/*
class OfflinePlateRepository @Inject constructor(
    private val plateDao: PlateDao
) : PlateRepository {

    /*override val plates: Flow<List<String>> =
        plateDao.getAllPlates().map { plates -> plates.map { it. } }*/

    override fun getAllPlatesStream(): Flow<List<Plate>> = plateDao.getAllPlates()

    /*override suspend fun add(name: String) {
        plateDao.insert(Plate(name = name))
    }*/
}*/

/*
class OfflinePlateRepository(private val plateDao: PlateDao) : PlateRepository {

}*/

/*
interface AppContainer {
    val platesRepository: PlatesRepository
}

class DataContainer(private val context: Context) : AppContainer {
    override val platesRepository: PlatesRepository by lazy {
        OfflinePlatesRepository(CollectionDatabase.getDatabase(context).plateDao())
    }
}*/