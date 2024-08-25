package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.data.WantedPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WishlistUiState(
    val items: List<WantedPlate> = emptyList(),
    val sortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val filters: FilterData = FilterData(),
    val isLoading: Boolean = false
)

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val plateRepository: PlateRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _allItems = mutableListOf<WantedPlate>()
    val showSortByBottomSheet = mutableStateOf(false)
    val showFiltersBottomSheet = mutableStateOf(false)

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    val sortByOptions = SortBy.entries.toList()

    init {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.userPreferences.first()
            val defaultSortBy = userPreferences.defaultSortOrder
            _uiState.update { it.copy(sortBy = defaultSortBy) }
            getWishlist()
        }
    }

    fun getWishlist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            plateRepository.getAllWantedPlatesStream().collect { items ->
                _allItems.clear()
                _allItems.addAll(items)
                _uiState.update { it.copy(items = items, isLoading = false) }
                setSortBy(uiState.value.sortBy)
            }
        }
    }

    fun setSortBy(sortBy: SortBy) {
        val items = _uiState.value.items
        val sortedItems = when (sortBy) {
            SortBy.COUNTRY_ASC -> items.sortedBy { it.commonDetails.country }
            SortBy.COUNTRY_DESC -> items.sortedByDescending { it.commonDetails.country }
            SortBy.COUNTRY_AND_TYPE_ASC -> items.sortedBy { it.commonDetails.type }
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedByDescending { it.commonDetails.type }
            SortBy.DATE_NEWEST -> items.sortedByDescending { it.commonDetails.country }
            SortBy.DATE_OLDEST -> items.sortedBy { it.commonDetails.country }
        }
        _uiState.update { it.copy(items = sortedItems, sortBy = sortBy) }
    }
}
