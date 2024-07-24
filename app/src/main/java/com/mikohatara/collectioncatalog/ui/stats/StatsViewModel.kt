package com.mikohatara.collectioncatalog.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class StatsUiState(
    val items: List<Plate> = emptyList(),
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val plateRepository: PlateRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        getItems()
    }

    fun getCountries(): Set<String> {
        return uiState.value.items.map { it.commonDetails.country }
            .sortedWith(compareByDescending<String> { country ->
                uiState.value.items.count { it.commonDetails.country == country }}
                .thenBy { it }
            )
            .toSet()
    }

    private fun getItems() {
        plateRepository.getAllPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(items = items)
        }.launchIn(viewModelScope)
    }
}
