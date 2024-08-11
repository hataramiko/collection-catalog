package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.data.WantedPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Item {
    data class PlateData(val plate: Plate) : Item()
    data class WantedPlateData(val wantedPlate: WantedPlate) : Item()
    data class FormerPlateData(val formerPlate: FormerPlate) : Item()
}

data class HomeUiState(
    val items: List<Plate> = emptyList(),
    val sortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val filters: FilterData = FilterData()
    //val isLoading: Boolean = false
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

    init {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.userPreferences.first()
            val defaultSortBy = userPreferences.defaultSortOrder
            _uiState.value = _uiState.value.copy(sortBy = defaultSortBy)
            getPlates()
        }
    }

    fun getPlates() {
        plateRepository.getAllPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(items = items)
            setSortBy(uiState.value.sortBy)
        }.launchIn(viewModelScope)
    }

    fun setSortBy(sortBy: SortBy) {
        val items = _uiState.value.items
        val sortedItems = when (sortBy) {
            SortBy.COUNTRY_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy(nullsLast()) { it.commonDetails.region }
                    .thenBy(nullsLast()) { it.commonDetails.area }
                    .thenBy { it.uniqueDetails.number }
            )
            SortBy.COUNTRY_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenByDescending { it.commonDetails.region }
                    .thenByDescending { it.commonDetails.area }
                    .thenByDescending { it.uniqueDetails.number }
            )
            SortBy.COUNTRY_AND_TYPE_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy { it.commonDetails.type }
                    //.thenBy { it.uniqueDetails.number }
            )
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenByDescending { it.commonDetails.type }
                    //.thenByDescending { it.uniqueDetails.number }
            )
            SortBy.DATE_NEWEST -> items.sortedByDescending { it.uniqueDetails.date }
            SortBy.DATE_OLDEST -> items.sortedWith(
                compareBy(nullsLast()) { it.uniqueDetails.date }
            )
        }
        _uiState.value = _uiState.value.copy(items = sortedItems, sortBy = sortBy)
    }

    fun setFilter() {
        val items = _uiState.value.items
        val filters = _uiState.value.filters

        val countryFilter = filters.country
        val typeFilter = filters.type
        val isKeeperFilter = filters.isKeeper
        val isForTradeFilter = filters.isForTrade

        val filteredItems = items.filter { item ->
            val passCountryFilter = countryFilter.isEmpty() ||
                    countryFilter.any { it == item.commonDetails.country }
            val passTypeFilter = typeFilter.isEmpty() ||
                    typeFilter.any { it == item.commonDetails.type }
            val passIsKeeperFilter = !isKeeperFilter || item.grading.isKeeper
            val passIsForTradeFilter = !isForTradeFilter || item.grading.isForTrade

            passCountryFilter && passTypeFilter && passIsKeeperFilter && passIsForTradeFilter
        }

        _uiState.value = _uiState.value.copy(items = filteredItems)
        setSortBy(uiState.value.sortBy)
    }

    fun toggleCountryFilter(country: String) = viewModelScope.launch {
        val filter = _uiState.value.filters.country
        val newFilter: Set<String> = if (filter.any { it == country }) {
            filter - country
        } else {
            filter + country
        }
        _uiState.update { it.copy(filters = it.filters.copy(country = newFilter)) }
    }

    fun toggleTypeFilter(type: String) = viewModelScope.launch {
        val filter = _uiState.value.filters.type
        val newFilter: Set<String> = if (filter.any { it == type }) {
            filter - type
        } else {
            filter + type
        }
        _uiState.update { it.copy(filters = it.filters.copy(type = newFilter)) }
    }

    fun toggleIsKeeperFilter() = viewModelScope.launch {
        val filter = !_uiState.value.filters.isKeeper
        _uiState.update { it.copy(filters = it.filters.copy(isKeeper = filter)) }
    }

    fun toggleIsForTradeFilter() = viewModelScope.launch {
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
    val isKeeper: Boolean = false,
    val isForTrade: Boolean = false
)
