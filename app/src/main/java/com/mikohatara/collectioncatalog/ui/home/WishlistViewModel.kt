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
    val showFilterBottomSheet = mutableStateOf(false)

    private val _isTopRowHidden = MutableStateFlow(false)
    val isTopRowHidden: StateFlow<Boolean> = _isTopRowHidden.asStateFlow()

    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.userPreferences.first()
            val defaultSortBy = userPreferences.defaultSortOrderWishlist
            _uiState.update { it.copy(sortBy = defaultSortBy) }
            getWishlist()
        }
    }

    fun updateTopRowVisibility(itemIndex: Int, topBarCollapsedFraction: Float) {
        _isTopRowHidden.value = (topBarCollapsedFraction > 0.5f) && (itemIndex > 0)
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
            SortBy.COUNTRY_AND_AGE_ASC -> items.sortedBy { it.commonDetails.year }
            SortBy.COUNTRY_AND_AGE_DESC -> items.sortedByDescending { it.commonDetails.year }
            SortBy.START_DATE_NEWEST -> items.sortedByDescending { it.commonDetails.country }
            SortBy.START_DATE_OLDEST -> items.sortedBy { it.commonDetails.country }
            SortBy.END_DATE_NEWEST -> items
            SortBy.END_DATE_OLDEST -> items
        }
        _uiState.update { it.copy(items = sortedItems, sortBy = sortBy) }
        updateDefaultSortBy(sortBy)
    }

    fun getSortByOptions(): List<SortBy> {
        val sortByOptions = SortBy.entries.filter {
            it != SortBy.COUNTRY_AND_AGE_ASC && it != SortBy.COUNTRY_AND_AGE_DESC &&
            it != SortBy.START_DATE_NEWEST && it != SortBy.START_DATE_OLDEST &&
            it != SortBy.END_DATE_NEWEST && it != SortBy.END_DATE_OLDEST
        }
        return sortByOptions
    }

    private fun updateDefaultSortBy(sortBy: SortBy) {
        viewModelScope.launch {
            userPreferencesRepository.saveDefaultSortOrderWishlist(sortBy)
        }
    }
}
