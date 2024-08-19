package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val items: List<Plate> = emptyList(),
    val sortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val filters: FilterData = FilterData(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plateRepository: PlateRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val showSortByBottomSheet = mutableStateOf(false)
    val showFilterBottomSheet = mutableStateOf(false)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val sortByOptions = SortBy.entries.toList()

    // For testing TopRow scroll handling
    private var lastScrollIndex = 0
    private val _isScrollingUp = MutableStateFlow(false)
    val isScrollingUp: StateFlow<Boolean> = _isScrollingUp.asStateFlow()

    fun updateScrollPosition(newScrollIndex: Int) {
        if (newScrollIndex == lastScrollIndex) return

        _isScrollingUp.value = newScrollIndex > lastScrollIndex
        lastScrollIndex = newScrollIndex
    }
    //

    init {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.userPreferences.first()
            val defaultSortBy = userPreferences.defaultSortOrder
            _uiState.update { it.copy(sortBy = defaultSortBy) }
            getPlates()
        }
    }

    fun getPlates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            plateRepository.getAllPlatesStream().collect { items ->
                _uiState.update { it.copy(items = items, isLoading = false) }
                setSortBy(uiState.value.sortBy)
            }
        }
    }

    fun setSortBy(sortBy: SortBy) {
        val items = _uiState.value.items
        val sortedItems = when (sortBy) {
            SortBy.COUNTRY_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy(nullsLast()) { it.commonDetails.region1st }
                    .thenBy(nullsLast()) { it.commonDetails.region2nd }
                    .thenBy(nullsLast()) { it.commonDetails.region3rd }
                    .thenBy { it.uniqueDetails.regNo }
            )
            SortBy.COUNTRY_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenBy(nullsLast()) { it.commonDetails.region1st }
                    .thenBy(nullsLast()) { it.commonDetails.region2nd }
                    .thenBy(nullsLast()) { it.commonDetails.region3rd }
                    .thenByDescending { it.uniqueDetails.regNo }
            )
            SortBy.COUNTRY_AND_TYPE_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy { it.commonDetails.type }
                    //.thenBy { it.uniqueDetails.regNo }
            )
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenByDescending { it.commonDetails.type }
                    //.thenByDescending { it.uniqueDetails.regNo }
            )
            SortBy.DATE_NEWEST -> items.sortedByDescending { it.uniqueDetails.date }
            SortBy.DATE_OLDEST -> items.sortedWith(
                compareBy(nullsLast()) { it.uniqueDetails.date }
            )
        }
        _uiState.update { it.copy(items = sortedItems, sortBy = sortBy) }
    }

    fun setFilter() {
        val (items, _, filters) = _uiState.value

        val filteredItems = items.filter { item ->
            when {
                filters.country.isNotEmpty() && filters.country.none {
                    it == item.commonDetails.country
                } -> false
                filters.type.isNotEmpty() && filters.type.none {
                    it == item.commonDetails.type
                } -> false
                filters.location.isNotEmpty() && filters.location.none {
                    it == item.uniqueDetails.status
                } -> false
                filters.isKeeper && !item.grading.isKeeper -> false
                filters.isForTrade && !item.grading.isForTrade -> false
                else -> true
            }
        }
        _uiState.update { it.copy(items = filteredItems) }
        setSortBy(uiState.value.sortBy)
    }

    fun toggleCountryFilter(country: String) {
        val newFilter = toggleFilter(_uiState.value.filters.country, country)
        _uiState.update { it.copy(filters = it.filters.copy(country = newFilter)) }
    }

    fun toggleTypeFilter(type: String) {
        val newFilter = toggleFilter(_uiState.value.filters.type, type)
        _uiState.update { it.copy(filters = it.filters.copy(type = newFilter)) }
    }

    fun toggleLocationFilter(location: String) {
        val newFilter = toggleFilter(_uiState.value.filters.location, location)
        _uiState.update { it.copy(filters = it.filters.copy(location = newFilter)) }
    }

    fun toggleIsKeeperFilter() {
        val filter = !_uiState.value.filters.isKeeper
        _uiState.update { it.copy(filters = it.filters.copy(isKeeper = filter)) }
    }

    fun toggleIsForTradeFilter() {
        val filter = !_uiState.value.filters.isForTrade
        _uiState.update { it.copy(filters = it.filters.copy(isForTrade = filter)) }
    }

    fun resetFilter() {
        _uiState.update { it.copy(filters = FilterData()) }
        setFilter()
        getPlates()
    }

    fun getCountries(): Set<String> {
        return uiState.value.items.map { it.commonDetails.country }
            .sortedBy { it } // See getTypes for an alternative
            .toSet()
    }

    fun getTypes(): Set<String> {
        return uiState.value.items.map { it.commonDetails.type }
            .sortedWith(compareBy { it }) // See getCountries for an alternative
            .toSet()
    }

    fun getLocations(): Set<String> {
        return uiState.value.items.mapNotNull { it.uniqueDetails.status }
            .sortedWith(compareBy { it })
            .toSet()
    }

    private fun <T> toggleFilter(filters: Set<T>, item: T): Set<T> =
        if (item in filters) filters - item else filters + item
}

enum class SortBy {
    COUNTRY_ASC,
    COUNTRY_DESC,
    COUNTRY_AND_TYPE_ASC,
    COUNTRY_AND_TYPE_DESC,
    DATE_NEWEST,
    DATE_OLDEST
}

data class FilterData(
    val country: Set<String> = emptySet(),
    val type: Set<String> = emptySet(),
    val location: Set<String> = emptySet(),
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false
)
