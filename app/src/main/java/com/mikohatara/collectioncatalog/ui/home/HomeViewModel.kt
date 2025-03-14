package com.mikohatara.collectioncatalog.ui.home

import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.mikohatara.collectioncatalog.util.exportPlatesToCsv
import com.mikohatara.collectioncatalog.util.importPlatesFromCsv
import com.mikohatara.collectioncatalog.util.normalizeString
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
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val exportResult: ExportResult? = null,
    val importResult: ImportResult? = null
)

sealed class ExportResult {
    data class Success(val message: String) : ExportResult()
    data class Failure(val message: String) : ExportResult()
}

sealed class ImportResult {
    data class Success(val message: String) : ImportResult()
    data class Failure(val message: String) : ImportResult()
}

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
            val defaultSortBy = userPreferences.defaultSortOrderMain
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

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchItems()
    }

    fun toggleSearch() {
        _uiState.update { it.copy(isSearchActive = !it.isSearchActive) }
        updateSearchQuery("")
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
            SortBy.START_DATE_NEWEST -> items.sortedWith(
                compareByDescending<Plate, String?>(nullsLast()) { it.uniqueDetails.date }
                    .thenByDescending { it.id }
            )
            SortBy.START_DATE_OLDEST -> items.sortedWith(
                compareBy<Plate, String?>(nullsLast()) { it.uniqueDetails.date }
                    .thenBy { it.id }
            )
            SortBy.END_DATE_NEWEST -> items
            SortBy.END_DATE_OLDEST -> items
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
        val sortByOptions = SortBy.entries.filter {
            it != SortBy.END_DATE_NEWEST && it != SortBy.END_DATE_OLDEST
        }
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

    fun exportItems(context: Context) {
        _uiState.update { it.copy(isExporting = true, exportResult = null) }

        val timestamp = System.currentTimeMillis()
        val fileName = "CollectionCatalog_Export_$timestamp.csv"

        viewModelScope.launch {
            try {
                val items = _uiState.value.items
                exportPlatesToCsv(context, items, fileName)
                _uiState.update { it.copy(
                    isExporting = false,
                    exportResult = ExportResult.Success(
                        message = context.getExternalFilesDir(
                            android.os.Environment.DIRECTORY_DOCUMENTS
                        )?.absolutePath + "/$fileName"
                    )
                ) }
            } catch (e: Exception) {
                Log.e("HomeViewModel, export", "Export failed", e)
                _uiState.update { it.copy(
                    isExporting = false,
                    exportResult = ExportResult.Failure("Exporting failed: ${e.message}")
                ) }
            }
        }
    }

    fun clearExportResult() {
        _uiState.update { it.copy(isExporting = false, exportResult = null) }
    }

    fun importItems(context: Context, uri: Uri) {
        _uiState.update { it.copy(importResult = null) }
        viewModelScope.launch {
            try {
                val plates = importPlatesFromCsv(context, uri)
                if (plates != null) {
                    plates.forEach {
                        plateRepository.addPlate(it)
                    }
                    //plateRepository.addPlates(plates)
                    Log.d("HomeViewModel, import", "Importing ${plates.size} plates")
                    _uiState.update { it.copy(
                        importResult = ImportResult.Success("Imported ${plates.size} plates")
                    ) }
                } else {
                    Log.e("HomeViewModel, import", "Importing failed, plates list empty")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel, import", "Importing failed", e)
                _uiState.update { it.copy(
                    importResult = ImportResult.Failure("Importing failed: ${e.message}")
                ) }
            }
        }
    }

    fun clearImportResult() {
        _uiState.update { it.copy(importResult = null) }
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

    private fun searchItems() {
        val query = _uiState.value.searchQuery.lowercase()
        setFilter()
        val searchResults = if (query.isBlank()) {
            uiState.value.items.toList()
        } else {
            val queryNormalized = normalizeString(query)
            uiState.value.items.filter {
                val regNoNormalized = normalizeString(it.uniqueDetails.regNo)
                regNoNormalized.contains(queryNormalized)
            }
        }
        _uiState.update { it.copy(items = searchResults) }
        setSortBy(uiState.value.sortBy)
    }

    private fun updateDefaultSortBy(sortBy: SortBy) {
        viewModelScope.launch {
            userPreferencesRepository.saveDefaultSortOrderMain(sortBy)
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
    START_DATE_NEWEST,
    START_DATE_OLDEST,
    END_DATE_NEWEST,
    END_DATE_OLDEST
}

data class FilterData(
    val country: Set<String> = emptySet(),
    val type: Set<String> = emptySet(),
    val location: Set<String> = emptySet()
)
