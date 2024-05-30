package com.mikohatara.collectioncatalog.ui.item

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikohatara.collectioncatalog.data.CommonDetails
import com.mikohatara.collectioncatalog.data.Grading
import com.mikohatara.collectioncatalog.data.Measurements
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.Source
import com.mikohatara.collectioncatalog.data.UniqueDetails
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.NUMBER_VARIANT
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.PLATE_NUMBER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
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

    suspend fun addNewItem() = viewModelScope.launch {
        plateRepository.addPlate(uiState.itemDetails.toPlate())
    }

    suspend fun updateItem() = viewModelScope.launch {
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

fun ItemDetails.toPlate(): Plate = Plate(
    CommonDetails(country, region, area, type, period, year),
    UniqueDetails(number, variant, imagePath, vehicle, notes, date, cost, value, status),
    Grading(isKeeper, isForTrade, condition),
    Source(sourceName, sourceAlias, sourceDetails, sourceType, sourceCountry),
    Measurements(width, height, weight)
)

fun Plate.toItemDetails(): ItemDetails = ItemDetails(
    // CommonDetails
    country = commonDetails.country,
    region = commonDetails.region,
    area = commonDetails.area,
    type = commonDetails.type,
    period = commonDetails.period,
    year = commonDetails.year,
    // UniqueDetails
    number = uniqueDetails.number,
    variant = uniqueDetails.variant,
    imagePath = uniqueDetails.imagePath,
    vehicle = uniqueDetails.vehicle,
    notes = uniqueDetails.notes,
    date = uniqueDetails.date,
    cost = uniqueDetails.cost,
    value = uniqueDetails.value,
    status = uniqueDetails.status,
    // Grading
    isKeeper = grading.isKeeper,
    isForTrade = grading.isForTrade,
    condition = grading.condition,
    // Source
    sourceName = source.sourceName,
    sourceAlias = source.sourceAlias,
    sourceDetails = source.sourceDetails,
    sourceType = source.sourceType,
    sourceCountry = source.sourceCountry,
    // Measurements
    width = measurements.width,
    height = measurements.height,
    weight = measurements.weight,
)