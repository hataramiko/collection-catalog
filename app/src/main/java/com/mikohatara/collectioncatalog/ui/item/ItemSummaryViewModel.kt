package com.mikohatara.collectioncatalog.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
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

    private val itemId: Int = savedStateHandle.get<Int>(ITEM_ID)!!

    val uiState: StateFlow<ItemSummaryUiState> =
        plateRepository.getPlateStream(itemId)
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
