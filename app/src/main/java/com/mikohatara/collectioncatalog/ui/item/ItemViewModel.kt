package com.mikohatara.collectioncatalog.ui.item

import android.content.ClipData.Item
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ItemUiState(
    val item: Plate? = null //?: samplePlates[0]
)

@HiltViewModel
class ItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    /*val plateNumber: String =
        savedStateHandle[CollectionCatalogDestinationArgs.PLATE_NUMBER]!! //?
    val numberVariant: String =
        savedStateHandle[CollectionCatalogDestinationArgs.NUMBER_VARIANT]!!*/

    val plateNumber: String =
        savedStateHandle.get<String>(CollectionCatalogDestinationArgs.PLATE_NUMBER)!!
    val numberVariant: String =
        savedStateHandle.get<String>(CollectionCatalogDestinationArgs.NUMBER_VARIANT)!!

    val uiState: StateFlow<ItemUiState> =
        plateRepository.getPlateStream(plateNumber, numberVariant)
            .map { ItemUiState(item = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemUiState(item = samplePlates[5])
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/*
sealed interface ItemScreenUiState {
    object Loading : ItemScreenUiState
    data class Error(val throwable: Throwable) : ItemScreenUiState
    data class Success(val data: List<Plate>) : ItemScreenUiState
}*/