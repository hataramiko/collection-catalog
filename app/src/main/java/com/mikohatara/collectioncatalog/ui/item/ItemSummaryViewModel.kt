package com.mikohatara.collectioncatalog.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.util.toItemDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ItemSummaryUiState(
    val item: Item? = null,
    val itemDetails: ItemDetails = ItemDetails()
)

@HiltViewModel
class ItemSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {
    private val itemType: ItemType =
        savedStateHandle.get<String>(ITEM_TYPE)?.let { ItemType.valueOf(it) } ?: ItemType.PLATE
    private val itemId: Int = savedStateHandle.get<Int>(ITEM_ID)!!

    val uiState: StateFlow<ItemSummaryUiState> =
        when (itemType) {
            ItemType.PLATE -> plateRepository.getPlateStream(itemId)
                .map {
                    ItemSummaryUiState(
                        item = Item.PlateItem(it!!),
                        itemDetails = it.toItemDetails()
                    )
                }
            ItemType.WANTED_PLATE -> plateRepository.getWantedPlateStream(itemId)
                .map {
                    ItemSummaryUiState(
                        item = Item.WantedPlateItem(it!!),
                        itemDetails = it.toItemDetails()
                    )
                }
            ItemType.FORMER_PLATE -> plateRepository.getFormerPlateStream(itemId)
                .map {
                    ItemSummaryUiState(
                        item = Item.FormerPlateItem(it!!),
                        itemDetails = it.toItemDetails()
                    )
                }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = ItemSummaryUiState()
            )

    suspend fun deleteItem() /*= viewModelScope.launch*/ {
        when (uiState.value.item) {
            is Item.PlateItem -> plateRepository
                .deletePlate((uiState.value.item as Item.PlateItem).plate)
            is Item.WantedPlateItem -> plateRepository
                .deleteWantedPlate((uiState.value.item as Item.WantedPlateItem).wantedPlate)
            else -> {}
        }
    }
}
