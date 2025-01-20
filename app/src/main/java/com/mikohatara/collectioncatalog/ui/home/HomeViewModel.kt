package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.COLLECTION_ID
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
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository,
    private val collectionRepository: CollectionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    // Make this private and add an isCollection Boolean to the UiState?
    val collectionId: Int? = savedStateHandle.get<Int>(COLLECTION_ID)
    private val _collection = mutableStateOf<Collection?>(null)

    private val _allItems = mutableStateListOf<Plate>()
    val showSortByBottomSheet = mutableStateOf(false)
    val showFilterBottomSheet = mutableStateOf(false)

    private val _isTopRowHidden = MutableStateFlow(false)
    val isTopRowHidden: StateFlow<Boolean> = _isTopRowHidden.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.userPreferences.first()
            val defaultSortBy = userPreferences.defaultSortOrder
            _uiState.update { it.copy(sortBy = defaultSortBy) }
            getPlates()
            collectionId?.let {
                collectionRepository.getCollectionStream(collectionId).collect {
                    _collection.value = it
                }
            }
        }
    }

    fun updateTopRowVisibility(itemIndex: Int, topBarCollapsedFraction: Float) {
        _isTopRowHidden.value = (topBarCollapsedFraction > 0.5f) && (itemIndex > 0)
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
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenBy { it.commonDetails.type }
                    .thenBy(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
                    .thenBy { it.uniqueDetails.regNo }
            )
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenByDescending { it.commonDetails.type }
                    .thenByDescending(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
                    .thenByDescending { it.uniqueDetails.regNo }
            )
            SortBy.COUNTRY_AND_AGE_ASC -> items.sortedWith(
                compareBy<Plate> { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenBy(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
            )
            SortBy.COUNTRY_AND_AGE_DESC -> items.sortedWith(
                compareByDescending<Plate> { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenByDescending(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
            )
            SortBy.DATE_NEWEST -> items.sortedByDescending { it.uniqueDetails.date }
            SortBy.DATE_OLDEST -> items.sortedWith(
                compareBy(nullsLast()) { it.uniqueDetails.date }
            )
        }
        _uiState.update { it.copy(items = sortedItems, sortBy = sortBy) }
        updateDefaultSortBy(sortBy)
    }

    fun setFilter() {
        val filters = _uiState.value.filters

        val filteredItems = _allItems.filter { item ->
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

    fun resetFilter() {
        _uiState.update { it.copy(filters = FilterData()) }
        setFilter()
    }

    fun getCollectionName(): String? {
        return _collection.value?.name
    }

    fun getSortByOptions(): List<SortBy> {
        val sortByOptions = SortBy.entries.toList()
        return sortByOptions
    }

    fun getCountries(): Set<String> {
        return _allItems.map { it.commonDetails.country }
            .sortedBy { it } // See getTypes for an alternative
            .toSet()
    }

    fun getTypes(): Set<String> {
        return _allItems.map { it.commonDetails.type }
            .sortedWith(compareBy { it }) // See getCountries for an alternative
            .toSet()
    }

    fun getLocations(): Set<String> {
        return _allItems.mapNotNull { it.uniqueDetails.status }
            .sortedWith(compareBy { it })
            .toSet()
    }

    private fun getPlates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (collectionId != null) {
                collectionRepository
                    .getCollectionWithPlatesStream(collectionId).collect { collection ->
                        _allItems.clear()
                        if (collection != null) {
                            _allItems.addAll(collection.plates)
                            _uiState.update {
                                it.copy(items = collection.plates, isLoading = false)
                            }
                        }
                        setSortBy(uiState.value.sortBy)
                    }
            } else {
                plateRepository.getAllPlatesStream().collect { items ->
                    _allItems.clear()
                    _allItems.addAll(items)
                    _uiState.update { it.copy(items = items, isLoading = false) }
                    setSortBy(uiState.value.sortBy)
                }
            }
        }
    }

    private fun updateDefaultSortBy(sortBy: SortBy) {
        viewModelScope.launch {
            userPreferencesRepository.saveDefaultSortOrder(sortBy)
        }
    }

    private fun <T> toggleFilter(filters: Set<T>, item: T): Set<T> =
        if (item in filters) filters - item else filters + item
}

enum class SortBy {
    COUNTRY_ASC,
    COUNTRY_DESC,
    COUNTRY_AND_TYPE_ASC,
    COUNTRY_AND_TYPE_DESC,
    COUNTRY_AND_AGE_ASC,
    COUNTRY_AND_AGE_DESC,
    DATE_NEWEST,
    DATE_OLDEST
}

data class FilterData(
    val country: Set<String> = emptySet(),
    val type: Set<String> = emptySet(),
    val location: Set<String> = emptySet()
)
