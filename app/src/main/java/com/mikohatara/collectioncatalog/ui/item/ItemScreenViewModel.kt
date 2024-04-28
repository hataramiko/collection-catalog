package com.mikohatara.collectioncatalog.ui.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemScreenViewModel @Inject constructor(
    private val plateRepository: PlateRepository
) : ViewModel() {
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

sealed interface ItemScreenUiState {
    object Loading : ItemScreenUiState
    data class Error(val throwable: Throwable) : ItemScreenUiState
    data class Success(val data: List<Plate>) : ItemScreenUiState
}