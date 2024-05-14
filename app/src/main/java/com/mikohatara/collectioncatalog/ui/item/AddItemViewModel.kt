package com.mikohatara.collectioncatalog.ui.item

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

@HiltViewModel
class AddItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()

    private fun AddItem() = viewModelScope.launch {
        //plateRepository.addPlate()
    }
}
