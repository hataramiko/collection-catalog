package com.mikohatara.collectioncatalog.ui.item

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemEntryUiState(
    val item: Item? = null,
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

    var uiState by mutableStateOf(ItemEntryUiState())
        private set

    init {
        if (itemId != null) {
            loadItem(itemType, itemId)
        } else {
            uiState = ItemEntryUiState(isNew = true)
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        val item = uiState.item
        val isNew = uiState.isNew
        val initialDetails = if (isNew) ItemDetails() else when (item) {
            is Item.PlateItem -> item.plate.toItemDetails()
            is Item.WantedPlateItem -> item.wantedPlate.toItemDetails()
            else -> ItemDetails()
        }

        uiState = if (itemDetails != initialDetails) {
            ItemEntryUiState(
                item = item,
                itemDetails = itemDetails,
                isNew = isNew,
                hasUnsavedChanges = true
            )
        } else {
            ItemEntryUiState(item = item, itemDetails = itemDetails, isNew = isNew)
        }
    }

    fun saveEntry() = viewModelScope.launch {
        if (uiState.isNew) addNewItem() else updateItem()
    }

    private suspend fun addNewItem() = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> plateRepository.addPlate(uiState.itemDetails.toPlate())
            ItemType.WANTED_PLATE -> plateRepository
                .addWantedPlate(uiState.itemDetails.toWantedPlate())
        }
    }

    private suspend fun updateItem() = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> plateRepository.updatePlate(uiState.itemDetails.toPlate())
            ItemType.WANTED_PLATE -> plateRepository
                .updateWantedPlate(uiState.itemDetails.toWantedPlate())
        }
    }

    private fun loadItem(itemType: ItemType, itemId: Int) = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> {
                plateRepository.getPlateStream(itemId).let {
                    uiState = ItemEntryUiState(
                        item = it.firstOrNull()?.let { Item.PlateItem(it) },
                        itemDetails = it.firstOrNull()?.toItemDetails() ?: ItemDetails(),
                        isNew = false
                    )
                }
            }
            ItemType.WANTED_PLATE -> {
                plateRepository.getWantedPlateStream(itemId).let {
                    uiState = ItemEntryUiState(
                        item = it.firstOrNull()?.let { Item.WantedPlateItem(it) },
                        itemDetails = it.firstOrNull()?.toItemDetails() ?: ItemDetails(),
                        isNew = false
                    )
                }
            }
        }
    }
}
