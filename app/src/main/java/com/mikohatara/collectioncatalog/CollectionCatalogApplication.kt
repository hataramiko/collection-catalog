package com.mikohatara.collectioncatalog

import android.app.Application

//@HiltAndroidApp
class CollectionCatalogApplication: Application() {
    //private lateinit var container: SampleAppContainer

    override fun onCreate() {
        super.onCreate()
        //container = SampleDataContainer(this)
    }
}
