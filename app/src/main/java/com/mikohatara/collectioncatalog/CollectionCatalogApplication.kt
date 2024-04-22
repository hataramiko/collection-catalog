package com.mikohatara.collectioncatalog

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.mikohatara.collectioncatalog.data.SampleAppContainer
import com.mikohatara.collectioncatalog.data.SampleDataContainer

//@HiltAndroidApp
class CollectionCatalogApplication: Application() {
    //private lateinit var container: SampleAppContainer

    override fun onCreate() {
        super.onCreate()
        //container = SampleDataContainer(this)
    }
}
