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

    /*private val _uiState = MutableStateFlow(ItemEntryUiState())
    val uiState: StateFlow<ItemEntryUiState> = _uiState.asStateFlow()*/

    var uiState by mutableStateOf(ItemEntryUiState())
        private set

    init {
        if (plateNumber != null && numberVariant != null) {
            loadItem(plateNumber, numberVariant)
        }
    }

    fun updateUiState(item: Plate, itemDetails: ItemDetails, isNew: Boolean) {
        uiState = ItemEntryUiState(
            item = item,
            itemDetails = itemDetails,
            isNew = isNew
        )
    }

    fun saveEntry() = viewModelScope.launch {
        /*if (!uiState.value.isNew) {
            updateItem()
        } else {
            addNewItem()
        }*/
    }




    suspend fun addNewItem() = viewModelScope.launch {
        plateRepository.addPlate(uiState.item!!)
    }

    suspend fun updateItem() {
        /*if (plateNumber == null || numberVariant == null) {
            throw RuntimeException(
                "updateItem() was called but either plateNumber or numberVariant is null"
            )
        }*/

        viewModelScope.launch {
            plateRepository.updatePlate(uiState.item!!)
        }
    }

    private fun loadItem(plateNumber: String, numberVariant: String) = viewModelScope.launch {
        plateRepository.getPlateStream(plateNumber, numberVariant).let {
            uiState = ItemEntryUiState(
                item = it.firstOrNull(),
                itemDetails = it.firstOrNull()!!.toItemDetails(),
                isNew = false
            )
        }
    }
}

data class ItemDetails(
    val country: String = "",
    val region: String? = null,
    val area: String? = null,
    val type: String = "",
    val period: String? = null,
    val year: Int? = null,
    val number: String = "",
    val variant: String = "",
    val imagePath: String? = null,
    val width: Double? = null,
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false,
)

fun ItemDetails.toPlate(): Plate = Plate(
    CommonDetails(country, region, area, type, period, year),
    UniqueDetails(number, variant, imagePath, null, null, null, null, null, null),
    Grading(isKeeper, isForTrade, null),
    Source(null, null, null, null, null),
    Measurements(width, null, null)
)

fun Plate.toItemDetails(): ItemDetails = ItemDetails(
    country = commonDetails.country,
    region = commonDetails.region,
    area = commonDetails.area,
    type = commonDetails.type,
    period = commonDetails.period,
    year = commonDetails.year,
    number = uniqueDetails.number,
    variant = uniqueDetails.variant,
    imagePath = uniqueDetails.imagePath,
    width = measurements.width,
    isKeeper = grading.isKeeper,
    isForTrade = grading.isForTrade
)