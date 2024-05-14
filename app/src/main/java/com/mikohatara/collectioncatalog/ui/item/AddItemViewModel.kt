package com.mikohatara.collectioncatalog.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.PlateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddItemUiState(
    val newItemDetails: NewItemDetails = NewItemDetails()
)

@HiltViewModel
class AddItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    /*private val _uiState = MutableStateFlow(AddItemUiState())
    var uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()*/

    var uiState by mutableStateOf(AddItemUiState())
        private set

    fun updateUiState(newItemDetails: NewItemDetails) {
        uiState = AddItemUiState(newItemDetails = newItemDetails)
    }

    private fun AddItem() = viewModelScope.launch {
        //plateRepository.addPlate()
    }
}

data class NewItemDetails(
    val country: String = "",
    //val region: String? = null,
    //val area: String? = null,
    val type: String = "",
    //val period: String? = null,
    //val year: Int? = null,
    val number: String = "",
    val variant: String = "",
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false,
)