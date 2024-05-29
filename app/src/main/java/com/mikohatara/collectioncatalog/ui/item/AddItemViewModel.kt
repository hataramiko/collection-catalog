package com.mikohatara.collectioncatalog.ui.item

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mikohatara.collectioncatalog.data.Grading
import com.mikohatara.collectioncatalog.data.CommonDetails
import com.mikohatara.collectioncatalog.data.Measurements
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.Source
import com.mikohatara.collectioncatalog.data.UniqueDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AddItemUiState(
    val newItemDetails: NewItemDetails = NewItemDetails()
)

@HiltViewModel
class AddItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    var uiState by mutableStateOf(AddItemUiState())
        private set

    fun updateUiState(newItemDetails: NewItemDetails) {
        uiState = AddItemUiState(newItemDetails = newItemDetails)
    }

    suspend fun addItem() {
        plateRepository.addPlate(uiState.newItemDetails.toItem())
    }
}

data class NewItemDetails(
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

fun NewItemDetails.toItem(): Plate = Plate(
    CommonDetails(country, region, area, type, period, year),
    UniqueDetails(number, variant, imagePath, null, null, null, null, null, null),
    Grading(isKeeper, isForTrade, null),
    Source(null, null, null, null, null),
    Measurements(width, null, null)
)