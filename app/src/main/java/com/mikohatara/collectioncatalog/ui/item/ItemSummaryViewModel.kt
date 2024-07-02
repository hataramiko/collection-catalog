package com.mikohatara.collectioncatalog.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ItemSummaryUiState(
    val item: Plate? = null //?: samplePlates[0]
)

@HiltViewModel
class ItemSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    private val plateNumber: String =
        savedStateHandle.get<String>(CollectionCatalogDestinationArgs.PLATE_NUMBER)!!
        //savedStateHandle[CollectionCatalogDestinationArgs.PLATE_NUMBER]!!
    private val numberVariant: String =
        savedStateHandle.get<String>(CollectionCatalogDestinationArgs.NUMBER_VARIANT)!!
        //savedStateHandle[CollectionCatalogDestinationArgs.NUMBER_VARIANT]!!

    val uiState: StateFlow<ItemSummaryUiState> =
        plateRepository.getPlateStream(plateNumber, numberVariant)
            .map { ItemSummaryUiState(item = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemSummaryUiState(item = samplePlates[5])
            )

    suspend fun deleteItem() /*= viewModelScope.launch*/ {
        plateRepository.deletePlate(uiState.value.item!!)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
