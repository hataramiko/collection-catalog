package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs
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
)

@HiltViewModel
class ItemEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    private val plateNumber: String? =
        savedStateHandle.get<String>(CollectionCatalogDestinationArgs.PLATE_NUMBER)
    private val numberVariant: String? =
        savedStateHandle.get<String>(CollectionCatalogDestinationArgs.NUMBER_VARIANT)

    var uiState by mutableStateOf(ItemEntryUiState())
        private set

    init { // TODO this works for now, but should be improved
        if (plateNumber != "{plateNumber}" && numberVariant != "{numberVariant}") {
            loadItem(plateNumber, numberVariant)
        } else {
            uiState = ItemEntryUiState(isNew = true)
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        val item = uiState.item
        val isNew = uiState.isNew

        uiState = ItemEntryUiState(item = item, itemDetails = itemDetails, isNew = isNew)
    }

    fun saveEntry() = viewModelScope.launch {
        if (!uiState.isNew) {
            updateItem()
        } else {
            addNewItem()
        }
    }

    private suspend fun addNewItem() = viewModelScope.launch {
        plateRepository.addPlate(uiState.itemDetails.toPlate())
    }

    private suspend fun updateItem() = viewModelScope.launch {
        plateRepository.updatePlate(uiState.itemDetails.toPlate())
    }

    private fun loadItem(plateNumber: String?, numberVariant: String?) = viewModelScope.launch {
        plateRepository.getPlateStream(plateNumber!!, numberVariant!!).let {
            uiState = ItemEntryUiState(
                item = it.firstOrNull(),
                itemDetails = it.firstOrNull()!!.toItemDetails(),
                isNew = false
            )
        }
    }
}

data class ItemDetails(
    // CommonDetails
    val country: String = "",
    val region: String? = null,
    val area: String? = null,
    val type: String = "",
    val period: String? = null,
    val year: Int? = null,
    // UniqueDetails
    val number: String = "",
    val variant: String = "",
    val imagePath: String? = null,
    val vehicle: String? = null,
    val notes: String? = null,
    val date: String? = null,
    val cost: Double? = null,
    val value: Double? = null,
    val status: String? = null,
    // Grading
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false,
    val condition: String? = null,
    // Source
    val sourceName: String? = null,
    val sourceAlias: String? = null,
    val sourceDetails: String? = null,
    val sourceType: String? = null,
    val sourceCountry: String? = null,
    // Measurements
    val width: Double? = null,
    val height: Double? = null,
    val weight: Double? = null,
)
