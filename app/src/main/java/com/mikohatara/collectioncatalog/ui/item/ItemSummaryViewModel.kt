package com.mikohatara.collectioncatalog.ui.item

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.util.toItemDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class ItemSummaryUiState(
    val item: Item? = null,
    val itemDetails: ItemDetails = ItemDetails(),
    val collections: List<Collection> = emptyList()
)

@HiltViewModel
class ItemSummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {
    private val itemType: ItemType =
        savedStateHandle.get<String>(ITEM_TYPE)?.let { ItemType.valueOf(it) } ?: ItemType.PLATE
    private val itemId: Int = savedStateHandle.get<Int>(ITEM_ID)!!

    private val _uiState = MutableStateFlow(ItemSummaryUiState())
    val uiState: StateFlow<ItemSummaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            when (itemType) {
                ItemType.PLATE -> plateRepository.getPlateWithCollectionsStream(itemId).collect {
                    it?.let {
                        _uiState.value = ItemSummaryUiState(
                            item = Item.PlateItem(it.plate),
                            itemDetails = it.plate.toItemDetails(),
                            collections = it.collections
                        )
                    }
                }
                ItemType.WANTED_PLATE -> plateRepository.getWantedPlateStream(itemId).collect {
                    it?.let {
                        _uiState.value = ItemSummaryUiState(
                            item = Item.WantedPlateItem(it),
                            itemDetails = it.toItemDetails()
                        )
                    }
                }
                ItemType.FORMER_PLATE -> plateRepository.getFormerPlateStream(itemId).collect {
                    it?.let {
                        _uiState.value = ItemSummaryUiState(
                            item = Item.FormerPlateItem(it),
                            itemDetails = it.toItemDetails()
                        )
                    }
                }
            }
        }
    }

    suspend fun deleteItem() {
        when (uiState.value.item) {
            is Item.PlateItem -> plateRepository
                .deletePlateWithCollections((uiState.value.item as Item.PlateItem).plate)
            is Item.WantedPlateItem -> plateRepository
                .deleteWantedPlate((uiState.value.item as Item.WantedPlateItem).wantedPlate)
            is Item.FormerPlateItem -> plateRepository
                .deleteFormerPlate((uiState.value.item as Item.FormerPlateItem).formerPlate)
            else -> {}
        }
    }

    fun copyItemDetailsToClipboard(context: Context, itemDetails: ItemDetails) {
        val jsonString = Json.encodeToString(itemDetails)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ItemDetails", jsonString)
        clipboard.setPrimaryClip(clip)
    }
}
