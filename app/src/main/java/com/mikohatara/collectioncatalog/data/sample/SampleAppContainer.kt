package com.mikohatara.collectioncatalog.data.sample

import android.content.Context

interface SampleAppContainer {
    val samplesRepository: SamplesRepository
}

class SampleDataContainer(private val context: Context) : SampleAppContainer {
    override val samplesRepository: SamplesRepository by lazy {
        SamplesOfflineRepository(SampleDatabase.getDatabase(context).sampleDao())
    }
}