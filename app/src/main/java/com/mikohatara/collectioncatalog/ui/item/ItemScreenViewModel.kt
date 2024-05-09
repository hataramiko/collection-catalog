package com.mikohatara.collectioncatalog.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ItemScreenUiState(
    val item: Plate? = null
)

@HiltViewModel
class ItemScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    private val plateNumber: String =
        savedStateHandle[CollectionCatalogDestinationArgs.PLATE_NUMBER]!! //?
    private val numberVariant: String =
        savedStateHandle[CollectionCatalogDestinationArgs.NUMBER_VARIANT]!!

    //private val plateNumber: String =
        //checkNotNull(savedStateHandle[CollectionCatalogDestinationArgs.PLATE_NUMBER])
    /*private val plateVariant: Char =
        savedStateHandle[CollectionCatalogDestinationArgs.PLATE_VARIANT.toString()]!!*/

    //private val plateVariant: Char =
        //checkNotNull(savedStateHandle[CollectionCatalogDestinationArgs.PLATE_VARIANT.toString()])

/*
    val item = plateRepository.getPlateStream(plateNumber, plateVariant).asLiveData()*/
/*
    val uiState: StateFlow<ItemScreenUiState> =
        plateRepository.getPlateStream(plateNumber, plateVariant)*/

    //private val itemId: String = checkNotNull(savedStateHandle["itemId"])

    //private val itemData: Flow<Plate> = plateRepository.getPlateStream()

    //private val item: Flow<Plate> = plateRepository.getPlateStream()

    val uiState: StateFlow<ItemScreenUiState> =
        plateRepository.getPlateStream(plateNumber, numberVariant).map { ItemScreenUiState(it) }
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ItemScreenUiState()
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