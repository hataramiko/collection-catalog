package com.mikohatara.collectioncatalog.ui.stats

import androidx.lifecycle.ViewModel
import com.mikohatara.collectioncatalog.data.PlateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val plateRepository: PlateRepository
) : ViewModel() {

}
