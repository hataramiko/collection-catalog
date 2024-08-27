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
import com.mikohatara.collectioncatalog.util.toPlate
import com.mikohatara.collectioncatalog.util.toWantedPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemEntryUiState(
    val item: Item? = null,
    val itemType: ItemType = ItemType.PLATE,
    val itemDetails: ItemDetails = ItemDetails(),
    val isNew: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

@HiltViewModel
class ItemEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {
    private val itemType: ItemType =
        savedStateHandle.get<String>(ITEM_TYPE)?.let { ItemType.valueOf(it) } ?: ItemType.PLATE
    private val itemId: Int? = savedStateHandle.get<Int>(ITEM_ID)

    private val _uiState = MutableStateFlow(ItemEntryUiState())
    val uiState: StateFlow<ItemEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (itemId != null) {
                loadItem(itemType, itemId)
            } else {
                _uiState.update { it.copy(itemType = itemType, isNew = true) }
            }
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        val item = uiState.value.item
        val isNew = uiState.value.isNew
        val initialDetails = if (isNew) ItemDetails() else when (item) {
            is Item.PlateItem -> item.plate.toItemDetails()
            is Item.WantedPlateItem -> item.wantedPlate.toItemDetails()
            else -> ItemDetails()
        }

        _uiState.value = if (itemDetails != initialDetails) {
            ItemEntryUiState(
                item = item,
                itemType = itemType,
                itemDetails = itemDetails,
                isNew = isNew,
                hasUnsavedChanges = true
            )
        } else {
            ItemEntryUiState(
                item = item,
                itemType = itemType,
                itemDetails = itemDetails,
                isNew = isNew
            )
        }
    }

    fun saveEntry() = viewModelScope.launch {
        if (uiState.value.isNew) addNewItem() else updateItem()
    }

    private suspend fun addNewItem() = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> plateRepository
                .addPlate(uiState.value.itemDetails.toPlate())
            ItemType.WANTED_PLATE -> plateRepository
                .addWantedPlate(uiState.value.itemDetails.toWantedPlate())
        }
    }

    private suspend fun updateItem() = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> plateRepository
                .updatePlate(uiState.value.itemDetails.toPlate())
            ItemType.WANTED_PLATE -> plateRepository
                .updateWantedPlate(uiState.value.itemDetails.toWantedPlate())
        }
    }

    private fun loadItem(itemType: ItemType, itemId: Int) = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> {
                plateRepository.getPlateStream(itemId).let {
                    _uiState.value = ItemEntryUiState(
                        item = it.firstOrNull()?.let { Item.PlateItem(it) },
                        itemType = itemType,
                        itemDetails = it.firstOrNull()?.toItemDetails() ?: ItemDetails(),
                        isNew = false
                    )
                }
            }
            ItemType.WANTED_PLATE -> {
                plateRepository.getWantedPlateStream(itemId).let {
                    _uiState.value = ItemEntryUiState(
                        item = it.firstOrNull()?.let { Item.WantedPlateItem(it) },
                        itemType = itemType,
                        itemDetails = it.firstOrNull()?.toItemDetails() ?: ItemDetails(),
                        isNew = false
                    )
                }
            }
        }
    }
}
