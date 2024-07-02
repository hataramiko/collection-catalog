package com.mikohatara.collectioncatalog.ui.home

import android.util.Log
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
    val typeFilter: Set<String> = emptySet()
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
        val filteredItems = ArrayList<Plate>()
        val countryFilter = _uiState.value.countryFilter
        val typeFilter = _uiState.value.typeFilter

        for (item in items) {
            if (
                countryFilter.any { it == item.commonDetails.country } ||
                typeFilter.any { it == item.commonDetails.type }
            ) {
                filteredItems.add(item)
            }
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

    fun resetFilter() {
        _uiState.update { it.copy(countryFilter = emptySet()) }
        setFilter()
        getPlates()
    }

    fun getCountries(): List<String>/*Set<String>*/ {
        val countries = uiState.value.items.map { it.commonDetails.country }.distinct()
        return countries

        //return uiState.value.items.map { it.commonDetails.country }.toSet()
    }

    fun getTypes(): Set<String> {
        return uiState.value.items.map { it.commonDetails.type }.toSet()
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    val uiState: StateFlow<HomeUiState> =
        plateRepository.getAllPlatesByCountryAscStream()
            .map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )*/
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
    val type: Set<String> = emptySet()
)
