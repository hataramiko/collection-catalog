package com.mikohatara.collectioncatalog.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.data.WantedPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatsUiState(
    val plates: List<Plate> = emptyList(),
    val wishlist: List<WantedPlate> = emptyList(),
    val archive: List<FormerPlate> = emptyList()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val plateRepository: PlateRepository
) : ViewModel() {
    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferences()
        )

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        getPlates()
        getWishlist()
        getArchive()
    }

    fun getCountries(): Set<String> {
        return uiState.value.plates.map { it.commonDetails.country }
            .sortedWith(compareByDescending<String> { country ->
                uiState.value.plates.count { it.commonDetails.country == country }}
                .thenBy { it }
            )
            .toSet()
    }

    private fun getPlates() {
        plateRepository.getAllPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(plates = items)
        }.launchIn(viewModelScope)
    }

    private fun getWishlist() {
        plateRepository.getAllWantedPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(wishlist = items)
        }.launchIn(viewModelScope)
    }

    private fun getArchive() {
        plateRepository.getAllFormerPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(archive = items)
        }.launchIn(viewModelScope)
    }
}
