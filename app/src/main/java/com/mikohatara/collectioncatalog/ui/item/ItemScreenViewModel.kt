package com.mikohatara.collectioncatalog.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ItemScreenUiState(
    val item: Plate? = null
)

@HiltViewModel
class ItemScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    val item: String = savedStateHandle[CollectionCatalogDestinationArgs.ITEM_KEY]!! //?

    //private val itemId: String = checkNotNull(savedStateHandle["itemId"])

    //private val itemData: Flow<Plate> = plateRepository.getPlateStream()

    //private val item: Flow<Plate> = plateRepository.getPlateStream()

/*
    val uiState: StateFlow<ItemScreenUiState> = plateRepository
        .plates.map<List<Plate>, ItemScreenUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addItemScreen() {
        viewModelScope.launch {
            plateRepository.add(plate)
        }
    }*/
}

/*
sealed interface ItemScreenUiState {
    object Loading : ItemScreenUiState
    data class Error(val throwable: Throwable) : ItemScreenUiState
    data class Success(val data: List<Plate>) : ItemScreenUiState
}*/