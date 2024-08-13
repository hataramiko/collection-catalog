package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.util.toItemDetails
import com.mikohatara.collectioncatalog.util.toPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemEntryUiState(
    val item: Plate? = null,
    val itemDetails: ItemDetails = ItemDetails(),
    val isNew: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

@HiltViewModel
class ItemEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    private val itemId: Int? = savedStateHandle.get<Int>(ITEM_ID)

    var uiState by mutableStateOf(ItemEntryUiState())
        private set

    init { // TODO this works for now, but should be improved
        if (itemId != null) {
            loadItem(itemId)
        } else {
            uiState = ItemEntryUiState(isNew = true)
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        val item = uiState.item
        val isNew = uiState.isNew
        val initialDetails = if (isNew) ItemDetails() else item?.toItemDetails()

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
        if (!uiState.isNew) {
            updateItem()
        } else {
            addNewItem()
        }
    }

    private fun setHasUnsavedChanges(hasUnsavedChanges: Boolean) {
        uiState = uiState.copy(hasUnsavedChanges = hasUnsavedChanges)
    }

    private suspend fun addNewItem() = viewModelScope.launch {
        plateRepository.addPlate(uiState.itemDetails.toPlate())
    }

    private suspend fun updateItem() = viewModelScope.launch {
        plateRepository.updatePlate(uiState.itemDetails.toPlate())
    }

    private fun loadItem(itemId: Int?) = viewModelScope.launch {
        plateRepository.getPlateStream(itemId!!).let {
            uiState = ItemEntryUiState(
                item = it.firstOrNull(),
                itemDetails = it.firstOrNull()!!.toItemDetails(),
                isNew = false
            )
        }
    }
}

data class ItemDetails(
    val id: Int = 3,
    // CommonDetails
    val country: String = "",
    val region1st: String? = null,
    val region2nd: String? = null,
    val region3rd: String? = null,
    val type: String = "",
    val periodStart: Int? = null,
    val periodEnd: Int? = null,
    val year: Int? = null,
    // UniqueDetails
    val regNo: String = "",
    val imagePath: String? = null,
    val vehicle: String? = null,
    val notes: String? = null,
    val date: String? = null,
    val cost: Long? = null,
    val value: Long? = null,
    val status: String? = null,
    // Grading
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false,
    // Size
    val width: Int? = null,
    val height: Int? = null,
    val weight: Double? = null,
    // Color
    val main: String? = null,
    val secondary: String? = null,
    // Source
    val sourceName: String? = null,
    val sourceAlias: String? = null,
    val sourceDetails: String? = null,
    val sourceType: String? = null,
    val sourceCountry: String? = null,
)
