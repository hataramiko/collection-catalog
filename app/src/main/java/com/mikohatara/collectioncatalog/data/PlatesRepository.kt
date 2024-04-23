package com.mikohatara.collectioncatalog.data

import android.content.Context
import com.mikohatara.collectioncatalog.data.sample.SampleAppContainer
import com.mikohatara.collectioncatalog.data.sample.SampleDatabase
import com.mikohatara.collectioncatalog.data.sample.SamplesOfflineRepository
import com.mikohatara.collectioncatalog.data.sample.SamplesRepository

interface PlatesRepository {

}

class OfflinePlatesRepository(private val plateDao: PlateDao) : PlatesRepository {

}

interface AppContainer {
    val platesRepository: PlatesRepository
}

class DataContainer(private val context: Context) : AppContainer {
    override val platesRepository: PlatesRepository by lazy {
        OfflinePlatesRepository(CollectionDatabase.getDatabase(context).plateDao())
    }
}