package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val items: List<Plate> = emptyList(),
    val sortBy: SortBy = SortBy.COUNTRY_ASC,
    val countryFilter: Set<String> = emptySet(),
    val typeFilter: Set<String> = emptySet(),
    val isKeeperFilter: Boolean = false,
    val isForTradeFilter: Boolean = false
    //val filters: FilterData = FilterData()
    //val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plateRepository: PlateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val showSortByBottomSheet = mutableStateOf(false)
    val showFilterBottomSheet = mutableStateOf(false)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val sortByOptions = SortBy.entries.toList()

    init {
        getPlates()
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
            )
            SortBy.COUNTRY_AND_TYPE_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy { it.commonDetails.type }
            )
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenByDescending { it.commonDetails.type }
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

        val countryFilter = _uiState.value.countryFilter
        val typeFilter = _uiState.value.typeFilter
        val isKeeperFilter = _uiState.value.isKeeperFilter
        val isForTradeFilter = _uiState.value.isForTradeFilter

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
        val filter = _uiState.value.countryFilter
        val newFilter: Set<String> = if (filter.any { it == country }) {
            filter - country
        } else {
            filter + country
        }
        _uiState.update { it.copy(countryFilter = newFilter) }
    }

    fun toggleTypeFilter(type: String) = viewModelScope.launch {
        val filter = _uiState.value.typeFilter
        val newFilter: Set<String> = if (filter.any { it == type }) {
            filter - type
        } else {
            filter + type
        }
        _uiState.update { it.copy(typeFilter = newFilter) }
    }

    fun toggleIsKeeperFilter() = viewModelScope.launch {
        val filter = if (!_uiState.value.isKeeperFilter) {
            true
        } else {
            false
        }
        _uiState.update { it.copy(isKeeperFilter = filter) }
    }

    fun toggleIsForTradeFilter() = viewModelScope.launch {
        val filter = if (!_uiState.value.isForTradeFilter) {
            true
        } else {
            false
        }
        _uiState.update { it.copy(isForTradeFilter = filter) }
    }

    fun resetFilter() {
        _uiState.update { it.copy(
            countryFilter = emptySet(),
            typeFilter = emptySet(),
            isKeeperFilter = false,
            isForTradeFilter = false
        ) }
        setFilter()
        getPlates()
    }

    fun getCountries(): Set<String>/*List<String>*/ {
        return uiState.value.items.map { it.commonDetails.country }.toSet()

        /*val countries = uiState.value.items.map { it.commonDetails.country }.distinct()
        return countries*/
    }

    fun getTypes(): Set<String> {
        return uiState.value.items.map { it.commonDetails.type }.toSet()
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
