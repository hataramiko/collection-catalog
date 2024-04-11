package com.mikohatara.collectioncatalog

import android.app.Application
import com.mikohatara.collectioncatalog.data.SampleAppContainer
import com.mikohatara.collectioncatalog.data.SampleDataContainer

class CollectionCatalogApplication: Application() {
    //lateinit var container: SampleAppContainer

    override fun onCreate() {
        super.onCreate()
        //container = SampleDataContainer(this)
    }
}
