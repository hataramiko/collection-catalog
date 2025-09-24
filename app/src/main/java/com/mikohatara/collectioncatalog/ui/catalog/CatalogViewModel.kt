package com.mikohatara.collectioncatalog.ui.catalog

import android.content.Context
import android.icu.util.Calendar
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.CollectionColor
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.COLLECTION_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.util.getCurrentYear
import com.mikohatara.collectioncatalog.util.normalizeString
import com.mikohatara.collectioncatalog.util.toDateString
import com.mikohatara.collectioncatalog.util.toItemDetails
import com.mikohatara.collectioncatalog.util.toTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.roundToLong

data class CatalogUiState(
    val items: List<Item> = emptyList(),
    val itemType: ItemType = ItemType.PLATE,
    val isCollection: Boolean = false,
    val sortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val filters: FilterData = FilterData(),
    val activeFilterCount: Int = 0,
    //
    val periodSliderPosition: ClosedRange<Float>? = null,
    val yearSliderPosition: ClosedRange<Float>? = null,
    val dateSliderPosition: ClosedRange<Float>? = null,
    val costSliderPosition: ClosedRange<Float>? = null,
    val valueSliderPosition: ClosedRange<Float>? = null,
    val archivalDateSliderPosition: ClosedRange<Float>? = null,
    //
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
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
class CatalogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository,
    private val collectionRepository: CollectionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _itemType: ItemType = savedStateHandle.get<String>(ITEM_TYPE)
        ?.let { ItemType.valueOf(it) } ?: ItemType.PLATE
    private val _collectionId: Int? = savedStateHandle.get<Int>(COLLECTION_ID)
    private val _collection = mutableStateOf<Collection?>(null)

    private val _allItems = mutableStateListOf<Item>()
    val showSortByBottomSheet = mutableStateOf(false)
    val showFilterBottomSheet = mutableStateOf(false)

    private val _isTopRowHidden = MutableStateFlow(false)
    val isTopRowHidden: StateFlow<Boolean> = _isTopRowHidden.asStateFlow()

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(itemType = _itemType) }

        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.userPreferences.first()
            val defaultSortBy = getDefaultSortBy(_itemType, userPreferences)
            _uiState.update { it.copy(sortBy = defaultSortBy) }
            getItems(_itemType)

            _collectionId?.let {
                _uiState.update { it.copy(isCollection = true) }
                collectionRepository.getCollectionStream(_collectionId).collect {
                    _collection.value = it
                }
            }
        }
    }

    fun getTopBarTitle(context: Context): String {
        val collectionName = getCollectionName() ?: ""
        return collectionName.ifEmpty {
            when (_itemType) {
                ItemType.PLATE -> context.getString(R.string.all_plates)
                ItemType.WANTED_PLATE -> context.getString(R.string.wishlist)
                ItemType.FORMER_PLATE -> context.getString(R.string.archive)
            }
        }
    }

    fun getMaxItemWidth(): Int {
        val allWidths = _allItems.mapNotNull { item ->
            val details = item.toItemDetails()
            details.width
        }
        return allWidths.maxOrNull() ?: 1
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
        val comparator: Comparator<Item>? = when (sortBy) {
            SortBy.COUNTRY_AND_TYPE_ASC ->
                compareBy<Item, String?>(nullsLast()) { it.toItemDetails().country }
                    .thenBy(nullsFirst()) { it.toItemDetails().region1st }
                    .thenBy(nullsLast()) { it.toItemDetails().type }
                    .thenBy(nullsFirst()) {
                        val details = it.toItemDetails()
                        details.year ?: details.periodStart
                    }
                    .thenBy(nullsLast()) { it.toItemDetails().regNo }
            SortBy.COUNTRY_AND_TYPE_DESC ->
                compareByDescending<Item, String?>(nullsFirst()) { it.toItemDetails().country }
                    .thenByDescending(nullsLast()) { it.toItemDetails().region1st }
                    .thenByDescending(nullsFirst()) { it.toItemDetails().type }
                    .thenByDescending(nullsLast()) {
                        val details = it.toItemDetails()
                        details.year ?: details.periodStart
                    }
                    .thenByDescending(nullsFirst()) { it.toItemDetails().regNo }
            SortBy.AGE_ASC ->
                if (_itemType != ItemType.WANTED_PLATE) {
                    compareBy<Item, Int?>(nullsLast()) {
                        val details = it.toItemDetails()
                        details.year ?: details.periodStart
                    }
                        .thenBy(nullsLast()) { it.toItemDetails().country }
                        .thenBy(nullsFirst()) { it.toItemDetails().region1st }
                        .thenBy { it.toItemDetails().type }
                        .thenBy { it.toItemDetails().regNo }
                } else null
            SortBy.AGE_DESC ->
                if (_itemType != ItemType.WANTED_PLATE) {
                    compareByDescending<Item, Int?>(nullsFirst()) {
                        val details = it.toItemDetails()
                        details.year ?: details.periodStart
                    }
                        .thenByDescending(nullsFirst()) { it.toItemDetails().country }
                        .thenByDescending(nullsLast()) { it.toItemDetails().region1st }
                        .thenByDescending(nullsFirst()) { it.toItemDetails().type }
                        .thenByDescending(nullsFirst()) { it.toItemDetails().regNo }
                } else null
            SortBy.START_DATE_NEWEST ->
                if (_itemType == ItemType.PLATE) {
                    compareByDescending<Item, String?>(nullsLast()) {
                        it.toItemDetails().date
                    }.thenByDescending { it.toItemDetails().id }
                } else null
            SortBy.START_DATE_OLDEST ->
                if (_itemType == ItemType.PLATE) {
                    compareBy<Item, String?>(nullsFirst()) {
                        it.toItemDetails().date
                    }.thenBy { it.toItemDetails().id }
                } else null
            SortBy.END_DATE_NEWEST ->
                if (_itemType == ItemType.FORMER_PLATE) {
                    compareByDescending<Item, String?>(nullsLast()) {
                        it.toItemDetails().archivalDate
                    }.thenByDescending { it.toItemDetails().id }
                } else null
            SortBy.END_DATE_OLDEST ->
                if (_itemType == ItemType.FORMER_PLATE) {
                    compareBy<Item, String?>(nullsLast()) {
                        it.toItemDetails().archivalDate
                    }.thenBy { it.toItemDetails().id }
                } else null
        }
        val sortedItems = if (comparator != null) items.sortedWith(comparator) else items

        _uiState.update { it.copy(items = sortedItems, sortBy = sortBy) }
        updateDefaultSortBy(sortBy)
    }

    fun getSortByOptions(): List<SortBy> {
        return when (_itemType) {
            ItemType.PLATE -> SortBy.entries.filter {
                it != SortBy.END_DATE_NEWEST && it != SortBy.END_DATE_OLDEST
            }
            ItemType.WANTED_PLATE -> SortBy.entries.filter {
                it != SortBy.AGE_ASC && it != SortBy.AGE_DESC &&
                it != SortBy.START_DATE_NEWEST && it != SortBy.START_DATE_OLDEST &&
                it != SortBy.END_DATE_NEWEST && it != SortBy.END_DATE_OLDEST
            }
            ItemType.FORMER_PLATE -> SortBy.entries.filter {
                it != SortBy.START_DATE_NEWEST && it != SortBy.START_DATE_OLDEST
            }
        }
    }

    fun getFilterCount(): Int {
        val filters = _uiState.value.filters
        val countrySize = filters.country.size
        val typeSize = filters.type.size
        val vehicleSize = if (filters.hasVehicle) 1 else 0
        val locationSize = filters.location.size
        val colorMainSize = filters.colorMain.size
        val colorSecondarySize = filters.colorSecondary.size
        val sourceTypeSize = filters.sourceType.size
        val sourceCountrySize = filters.sourceCountry.size
        val archivalReasonSize = filters.archivalReason.size
        val recipientCountrySize = filters.recipientCountry.size

        val yearSliderRange = getYearSliderRange()
        val dateSliderRange = getDateSliderRange()
        val costSliderRange = getCostSliderRange()
        val valueSliderRange = getValueSliderRange()
        val archivalDateSliderRange = getArchivalDateSliderRange()

        val periodSize = if (
            isSliderActive(_uiState.value.periodSliderPosition, yearSliderRange)
        ) 1 else 0
        val yearSize = if (
            isSliderActive(_uiState.value.yearSliderPosition, yearSliderRange)
        ) 1 else 0
        val dateSize = if (
            isSliderActive(_uiState.value.dateSliderPosition, dateSliderRange)
        ) 1 else 0
        val costSize = if (
            isSliderActive(_uiState.value.costSliderPosition, costSliderRange)
        ) 1 else 0
        val valueSize = if (
            isSliderActive(_uiState.value.valueSliderPosition, valueSliderRange)
        ) 1 else 0
        val archivalDateSize = if (
            isSliderActive(_uiState.value.archivalDateSliderPosition, archivalDateSliderRange)
        ) 1 else 0

        return countrySize + typeSize + vehicleSize + locationSize + colorMainSize +
                colorSecondarySize + sourceTypeSize + sourceCountrySize + archivalReasonSize +
                recipientCountrySize + periodSize + yearSize + dateSize + costSize +
                valueSize + archivalDateSize
    }

    fun openSortByBottomSheet() {
        showSortByBottomSheet.value = true
    }

    fun openFilterBottomSheet() {
        setFilterSliderStartPositions()
        showFilterBottomSheet.value = true
    }

    fun closeSortByBottomSheet() {
        showSortByBottomSheet.value = false
    }

    fun closeFilterBottomSheet() {
        showFilterBottomSheet.value = false
    }

    fun setFilter() {
        setPeriodFilter()
        setYearFilter()
        setDateFilter()
        setCostFilter()
        setValueFilter()
        setArchivalDateFilter()
        val filters = _uiState.value.filters

        val filteredItems = _allItems.filter { item ->
            val details = item.toItemDetails()
            val isWithinPeriodRange = filters.periodRange?.let { range ->
                val periodStart = details.periodStart
                val periodEnd = details.periodEnd

                when {
                    periodStart != null && periodEnd != null -> {
                        periodStart >= range.start && periodEnd <= range.endInclusive
                    }
                    periodStart != null && range.endInclusive == getMaxYear() -> {
                        periodStart >= range.start && periodStart <= range.endInclusive
                    }
                    range.start == getMinYear() && periodEnd != null -> {
                        periodEnd >= range.start && periodEnd <= range.endInclusive
                    }
                    else -> false
                }
            } != false
            val isWithinYearRange = filters.yearRange?.let { range ->
                val year = details.year

                when {
                    year != null -> year in range
                    else -> false
                }
            } != false
            val isWithinDateRange = filters.dateRange?.let { range ->
                val date = details.date
                when {
                    date != null -> date in range
                    else -> false
                }
            } != false
            val isWithinCostRange = filters.costRange?.let { range ->
                val cost = details.cost

                when {
                    cost != null -> cost in range
                    else -> false
                }
            } != false
            val isWithinValueRange = filters.valueRange?.let { range ->
                val value = details.value

                when {
                    value != null -> value in range
                    else -> false
                }
            } != false
            val isWithinArchivalDateRange = filters.archivalDateRange?.let { range ->
                val archivalDate = details.archivalDate

                when {
                    archivalDate != null -> archivalDate in range
                    else -> false
                }
            } != false

            val matchesVehicleFilter = if (filters.hasVehicle) {
                !details.vehicle.isNullOrEmpty()
            } else true

            when {
                filters.country.isNotEmpty() && filters.country.none {
                    it == details.country
                } -> false
                filters.type.isNotEmpty() && filters.type.none {
                    it == details.type
                } -> false
                filters.location.isNotEmpty() && filters.location.none {
                    it == details.status
                } -> false
                filters.colorMain.isNotEmpty() && filters.colorMain.none {
                    it == details.colorMain
                } -> false
                filters.colorSecondary.isNotEmpty() && filters.colorSecondary.none {
                    it == details.colorSecondary
                } -> false
                filters.sourceType.isNotEmpty() && filters.sourceType.none {
                    it == details.sourceType
                } -> false
                filters.sourceCountry.isNotEmpty() && filters.sourceCountry.none {
                    it == details.sourceCountry
                } -> false
                filters.archivalReason.isNotEmpty() && filters.archivalReason.none {
                    it == details.archivalType
                } -> false
                filters.recipientCountry.isNotEmpty() && filters.recipientCountry.none {
                    it == details.recipientCountry
                } -> false
                !isWithinPeriodRange -> false
                !isWithinYearRange -> false
                !isWithinDateRange -> false
                !isWithinCostRange -> false
                !isWithinValueRange -> false
                !isWithinArchivalDateRange -> false
                !matchesVehicleFilter -> false
                else -> true
            }
        }
        _uiState.update { it.copy(items = filteredItems, activeFilterCount = getFilterCount()) }
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

    fun toggleColorMainFilter(colorMain: String) {
        val newFilter = toggleFilter(_uiState.value.filters.colorMain, colorMain)
        _uiState.update { it.copy(filters = it.filters.copy(colorMain = newFilter)) }
    }

    fun toggleColorSecondaryFilter(colorSecondary: String) {
        val newFilter = toggleFilter(_uiState.value.filters.colorSecondary, colorSecondary)
        _uiState.update { it.copy(filters = it.filters.copy(colorSecondary = newFilter)) }
    }

    fun toggleSourceTypeFilter(sourceType: String) {
        val newFilter = toggleFilter(_uiState.value.filters.sourceType, sourceType)
        _uiState.update { it.copy(filters = it.filters.copy(sourceType = newFilter)) }
    }

    fun toggleSourceCountryFilter(sourceCountry: String) {
        val newFilter = toggleFilter(_uiState.value.filters.sourceCountry, sourceCountry)
        _uiState.update { it.copy(filters = it.filters.copy(sourceCountry = newFilter)) }
    }

    fun toggleArchivalReasonFilter(archivalReason: String) {
        val newFilter = toggleFilter(_uiState.value.filters.archivalReason, archivalReason)
        _uiState.update { it.copy(filters = it.filters.copy(archivalReason = newFilter)) }
    }

    fun toggleRecipientCountryFilter(recipientCountry: String) {
        val newFilter = toggleFilter(_uiState.value.filters.recipientCountry, recipientCountry)
        _uiState.update { it.copy(filters = it.filters.copy(recipientCountry = newFilter)) }
    }

    fun updatePeriodSliderPosition(periodSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(periodSliderPosition = periodSliderPosition) }
    }

    fun updateYearSliderPosition(yearSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(yearSliderPosition = yearSliderPosition) }
    }

    fun updateDateSliderPosition(dateSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(dateSliderPosition = dateSliderPosition) }
    }

    fun updateCostSliderPosition(costSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(costSliderPosition = costSliderPosition) }
    }

    fun updateValueSliderPosition(valueSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(valueSliderPosition = valueSliderPosition) }
    }

    fun updateArchivalDateSliderPosition(archivalDateSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(archivalDateSliderPosition = archivalDateSliderPosition) }
    }

    fun toggleVehicleSwitch() {
        _uiState.update { it.copy(filters = it.filters.copy(hasVehicle = !it.filters.hasVehicle)) }
    }

    fun resetFilter() {
        _uiState.update { it.copy(
            filters = FilterData(),
            periodSliderPosition = getYearSliderRange(),
            yearSliderPosition = getYearSliderRange(),
            dateSliderPosition = getDateSliderRange(),
            costSliderPosition = getCostSliderRange(),
            archivalDateSliderPosition = getArchivalDateSliderRange()
        ) }
        setFilter()
    }

    fun getCountries(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.country }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getTypes(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.type }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getLocations(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.status }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getColorsMain(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.colorMain }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getColorsSecondary(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.colorSecondary }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getSourceTypes(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.sourceType }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getSourceCountries(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.sourceCountry }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getArchivalReasons(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.archivalType }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getRecipientCountries(): Set<String> {
        val details = _allItems.map { it.toItemDetails() }
        return details.mapNotNull { it.recipientCountry }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getYearSliderRange(): ClosedRange<Float> {
        return getMinYear().toFloat()..getMaxYear().toFloat()
    }

    fun getDateSliderRange(): ClosedRange<Float> {
        return getMinDate()..getMaxDate()
    }

    fun getCostSliderRange(): ClosedRange<Float> {
        return getMinCost().toFloat()..getMaxCost().toFloat()
    }

    fun getValueSliderRange(): ClosedRange<Float> {
        return getMinValue().toFloat()..getMaxValue().toFloat()
    }

    fun getArchivalDateSliderRange(): ClosedRange<Float> {
        return getMinArchivalDate()..getMaxArchivalDate()
    }

    fun getCollectionName(): String? {
        return _collection.value?.name
    }

    fun getCollectionEmoji(): String? {
        return _collection.value?.emoji
    }

    fun getCollectionColor(): CollectionColor {
        return _collection.value?.color ?: CollectionColor.DEFAULT
    }

    /*fun exportItems(context: Context, uri: Uri) {//TODO
        _uiState.update { it.copy(isExporting = true, exportResult = null) }

        viewModelScope.launch {
            try {
                val items = uiState.value.items
                val contentResolver = context.contentResolver
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        exportFormerPlatesToCsv(writer, items)
                    }
                }
                _uiState.update { it.copy(
                    isExporting = false,
                    exportResult = ExportResult.Success(getExportMessage(true, context))
                ) }
            } catch (e: Exception) {
                Log.e("ArchiveViewModel, export", "Export failed", e)
                _uiState.update { it.copy(
                    isExporting = false,
                    exportResult = ExportResult.Failure(getExportMessage(false, context))
                ) }
            }
        }
    }

    fun importItems(context: Context, uri: Uri) {//TODO
        _uiState.update { it.copy(isImporting = true, importResult = null) }

        viewModelScope.launch {
            try {
                val formerPlates = importFormerPlatesFromCsv(context, uri)
                if (formerPlates != null && formerPlates.isNotEmpty()) {
                    plateRepository.addFormerPlates(formerPlates)
                    _uiState.update { it.copy(
                        isImporting = false,
                        importResult = ImportResult.Success(getImportMessage(context, formerPlates.size))
                    ) }
                } else {
                    Log.e("ArchiveViewModel, import", "Import failed, list empty")
                    _uiState.update { it.copy(
                        isImporting = false,
                        importResult = ImportResult.Failure(getImportMessage(context))
                    ) }
                }
            } catch (e: Exception) {
                Log.e("ArchiveViewModel, import", "Import failed", e)
                _uiState.update { it.copy(
                    isImporting = false,
                    importResult = ImportResult.Failure(getImportMessage(context))
                ) }
            }
        }
    }*/

    fun clearExportResult() {
        _uiState.update { it.copy(isExporting = false, exportResult = null) }
    }

    fun clearImportResult() {
        _uiState.update { it.copy(isImporting = false, importResult = null) }
    }

    private fun searchItems() {
        val query = _uiState.value.searchQuery.lowercase()
        setFilter()
        val searchResults = if (query.isBlank()) {
            uiState.value.items.toList()
        } else {
            val queryNormalized = normalizeString(query)
            uiState.value.items.filter { item ->
                val details = item.toItemDetails()
                val regNo = details.regNo

                if (regNo != null) {
                    val regNoNormalized = normalizeString(regNo)//.lowercase())
                    regNoNormalized.contains(queryNormalized)
                } else false
            }
        }
        _uiState.update { it.copy(items = searchResults) }
        setSortBy(uiState.value.sortBy)
    }

    private fun updateDefaultSortBy(sortBy: SortBy) {
        viewModelScope.launch {
            when (_itemType) {
                ItemType.PLATE -> userPreferencesRepository
                    .saveDefaultSortOrderMain(sortBy)
                ItemType.WANTED_PLATE -> userPreferencesRepository
                    .saveDefaultSortOrderWishlist(sortBy)
                ItemType.FORMER_PLATE -> userPreferencesRepository
                    .saveDefaultSortOrderArchive(sortBy)
            }
        }
    }

    private fun setFilterSliderStartPositions() {
        if (uiState.value.periodSliderPosition == null) {
            _uiState.update { it.copy(
                periodSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat()) }
        }
        if (uiState.value.yearSliderPosition == null) {
            _uiState.update { it.copy(
                yearSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat()) }
        }
        if (uiState.value.dateSliderPosition == null) {
            _uiState.update { it.copy(
                dateSliderPosition = getMinDate()..getMaxDate()) }
        }
        if (uiState.value.costSliderPosition == null) {
            _uiState.update { it.copy(
                costSliderPosition = getMinCost().toFloat()..getMaxCost().toFloat()) }
        }
        if (uiState.value.valueSliderPosition == null) {
            _uiState.update { it.copy(
                valueSliderPosition = getMinValue().toFloat()..getMaxValue().toFloat()) }
        }
        if (uiState.value.archivalDateSliderPosition == null) {
            _uiState.update { it.copy(
                archivalDateSliderPosition = getMinArchivalDate()..getMaxArchivalDate()) }
        }
    }

    private fun setPeriodFilter() {
        val periodRange = uiState.value.periodSliderPosition ?: return
        val rangeStart = periodRange.start.roundToInt()
        val rangeEnd = periodRange.endInclusive.roundToInt()
        if (rangeStart == getMinYear() && rangeEnd == getMaxYear()) {
            _uiState.update { it.copy(filters = it.filters.copy(periodRange = null)) }
        } else {
            _uiState.update { it.copy(filters = it.filters.copy(periodRange = rangeStart..rangeEnd)) }
        }
    }

    private fun setYearFilter() {
        val yearRange = uiState.value.yearSliderPosition ?: return
        val rangeStart = yearRange.start.roundToInt()
        val rangeEnd = yearRange.endInclusive.roundToInt()
        if (rangeStart == getMinYear() && rangeEnd == getMaxYear()) {
            _uiState.update { it.copy(filters = it.filters.copy(yearRange = null)) }
        } else {
            _uiState.update { it.copy(filters = it.filters.copy(yearRange = rangeStart..rangeEnd)) }
        }
    }

    private fun setDateFilter() {
        val dateRange = uiState.value.dateSliderPosition ?: return
        val rangeStart = dateRange.start
        val rangeEnd = dateRange.endInclusive
        val startString = rangeStart.toLong().toDateString()
        val endString = rangeEnd.toLong().toDateString()

        if ((rangeStart != getMinDate() || rangeEnd != getMaxDate()) &&
            startString.isNotBlank() && endString.isNotBlank()) {
            _uiState.update { it.copy(filters = it.filters.copy(dateRange = startString..endString)) }
        } else {
            _uiState.update { it.copy(filters = it.filters.copy(dateRange = null)) }
        }
    }

    private fun setCostFilter() {
        val costRange = uiState.value.costSliderPosition ?: return
        val rangeStart = costRange.start.roundToLong()
        val rangeEnd = costRange.endInclusive.roundToLong()
        if (rangeStart == getMinCost() && rangeEnd == getMaxCost()) {
            _uiState.update { it.copy(filters = it.filters.copy(costRange = null)) }
        } else {
            _uiState.update { it.copy(filters = it.filters.copy(costRange = rangeStart..rangeEnd)) }
        }
    }

    private fun setValueFilter() {
        val valueRange = uiState.value.valueSliderPosition ?: return
        val rangeStart = valueRange.start.roundToLong()
        val rangeEnd = valueRange.endInclusive.roundToLong()
        if (rangeStart == getMinValue() && rangeEnd == getMaxValue()) {
            _uiState.update { it.copy(filters = it.filters.copy(valueRange = null)) }
        } else {
            _uiState.update { it.copy(filters = it.filters.copy(valueRange = rangeStart..rangeEnd)) }
        }
    }

    private fun setArchivalDateFilter() {
        val dateRange = uiState.value.archivalDateSliderPosition ?: return
        val rangeStart = dateRange.start
        val rangeEnd = dateRange.endInclusive
        val startString = rangeStart.toLong().toDateString()
        val endString = rangeEnd.toLong().toDateString()

        if ((rangeStart != getMinDate() || rangeEnd != getMaxDate()) &&
            startString.isNotBlank() && endString.isNotBlank()) {
            _uiState.update { it
                .copy(filters = it.filters.copy(archivalDateRange = startString..endString)) }
        } else {
            _uiState.update { it.copy(filters = it.filters.copy(archivalDateRange = null)) }
        }
    }

    private fun getItems(itemType: ItemType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (itemType) {
                ItemType.PLATE -> {
                    if (_collectionId != null) {
                        collectionRepository.getCollectionWithPlatesStream(_collectionId)
                            .collect { collectionPlates ->
                                val items = collectionPlates?.plates
                                    ?.map { Item.PlateItem(it) } ?: emptyList()
                                _allItems.clear()
                                if (collectionPlates != null) {
                                    _allItems.addAll(items)
                                    _uiState.update {
                                        it.copy(items = items, isLoading = false)
                                    }
                                }
                                setFilter()
                            }
                    } else {
                        plateRepository.getAllPlatesStream().collect { plates ->
                            val items = plates.map { Item.PlateItem(it) }
                            _allItems.clear()
                            _allItems.addAll(items)
                            _uiState.update { it.copy(items = items, isLoading = false) }
                            setFilter()
                        }
                    }
                }
                ItemType.WANTED_PLATE -> {
                    plateRepository.getAllWantedPlatesStream().collect { wantedPlates ->
                        val items = wantedPlates.map { Item.WantedPlateItem(it) }
                        _allItems.clear()
                        _allItems.addAll(items)
                        _uiState.update { it.copy(items = items, isLoading = false) }
                        setFilter()
                    }
                }
                ItemType.FORMER_PLATE -> {
                    plateRepository.getAllFormerPlatesStream().collect { formerPlates ->
                        val items = formerPlates.map { Item.FormerPlateItem(it) }
                        _allItems.clear()
                        _allItems.addAll(items)
                        _uiState.update { it.copy(items = items, isLoading = false) }
                        setFilter()
                    }
                }
            }
        }
    }

    private fun getDefaultSortBy(itemType: ItemType, preferences: UserPreferences): SortBy {
        return when (itemType) {
            ItemType.PLATE -> preferences.defaultSortOrderMain
            ItemType.WANTED_PLATE -> preferences.defaultSortOrderWishlist
            ItemType.FORMER_PLATE -> preferences.defaultSortOrderArchive
        }
    }

    private fun isSliderActive(
        sliderPosition: ClosedRange<Float>?,
        defaultRange: ClosedRange<Float>
    ): Boolean {
        return sliderPosition != null && sliderPosition != defaultRange
    }

    private fun getMinYear(): Int {
        val maxYear = getMaxYear()
        if (_allItems.isEmpty()) return 1900

        val allYears = _allItems.flatMap { item ->
            val details = item.toItemDetails()
            listOfNotNull(details.periodStart, details.periodEnd, details.year)
        }

        val minYear = allYears.minOrNull() ?: 1900
        return if (minYear < maxYear) minYear else maxYear - 1
    }

    private fun getMaxYear(): Int {
        val currentYear = getCurrentYear()
        if (_allItems.isEmpty()) return currentYear

        val allYears = _allItems.flatMap { item ->
            val details = item.toItemDetails()
            listOfNotNull(details.periodStart, details.periodEnd, details.year)
        }

        val maxYear = allYears.maxOrNull() ?: currentYear
        return listOf(currentYear, maxYear).maxOf { it }
    }

    private fun getMinDate(): Float {
        val fallback = "1900-01-02".toTimestamp().toFloat()
        if (_allItems.isEmpty()) return fallback

        val allDates = _allItems.mapNotNull { item ->
            val details = item.toItemDetails()
            val date = details.date

            if (date != null) {
                try {
                    date.toTimestamp()
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        val minDate = allDates.minOrNull()?.toFloat() ?: return fallback
        val maxDate = getMaxDate()
        return if (minDate < maxDate) minDate else fallback
    }

    private fun getMaxDate(): Float {
        val today = Calendar.getInstance().timeInMillis.toFloat()
        if (_allItems.isEmpty()) return today

        val allDates = _allItems.mapNotNull { item ->
            val details = item.toItemDetails()
            val date = details.date

            if (date != null) {
                try {
                    date.toTimestamp()
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        val maxDate = allDates.maxOrNull()?.toFloat() ?: return today
        return listOf(today, maxDate).maxOf { it }
    }

    private fun getMinCost(): Long {
        return 0L
    }

    private fun getMaxCost(): Long {
        val fallback = 0L
        if (_allItems.isEmpty()) return fallback

        val allValues = _allItems.mapNotNull { item ->
            val details = item.toItemDetails()
            details.cost
        }

        return if (allValues.isNotEmpty()) {
            allValues.maxOf { it }
        } else fallback
    }

    private fun getMinValue(): Long {
        return 0L
    }

    private fun getMaxValue(): Long {
        val fallback = 0L
        if (_allItems.isEmpty()) return fallback

        val allValues = _allItems.mapNotNull { item ->
            val details = item.toItemDetails()
            details.value
        }

        return if (allValues.isNotEmpty()) {
            allValues.maxOf { it }
        } else fallback
    }

    private fun getMinArchivalDate(): Float {
        val fallback = "1900-01-02".toTimestamp().toFloat()
        if (_allItems.isEmpty()) return fallback

        val allDates = _allItems.mapNotNull { item ->
            val details = item.toItemDetails()
            val date = details.archivalDate

            if (date != null) {
                try {
                    date.toTimestamp()
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        val minDate = allDates.minOrNull()?.toFloat() ?: return fallback
        val maxDate = getMaxDate()
        return if (minDate < maxDate) minDate else fallback
    }

    private fun getMaxArchivalDate(): Float {
        val today = Calendar.getInstance().timeInMillis.toFloat()
        if (_allItems.isEmpty()) return today

        val allDates = _allItems.mapNotNull { item ->
            val details = item.toItemDetails()
            val date = details.archivalDate

            if (date != null) {
                try {
                    date.toTimestamp()
                } catch (e: Exception) {
                    null
                }
            } else null
        }

        val maxDate = allDates.maxOrNull()?.toFloat() ?: return today
        return listOf(today, maxDate).maxOf { it }
    }

    private fun getExportMessage(isSuccess: Boolean, context: Context): String {
        val stringResId = if (isSuccess) {
            R.string.export_msg_success
        } else R.string.export_msg_failure
        return context.getString(stringResId)
    }

    private fun getImportMessage(context: Context, size: Int = 0): String {
        return if (size > 0) {
            context.resources.getQuantityString(R.plurals.import_msg_success_size, size, size)
        } else context.getString(R.string.import_msg_failure)
    }

    private fun <T> toggleFilter(filters: Set<T>, item: T): Set<T> =
        if (item in filters) filters - item else filters + item
}

enum class SortBy {
    COUNTRY_AND_TYPE_ASC,
    COUNTRY_AND_TYPE_DESC,
    AGE_ASC,
    AGE_DESC,
    START_DATE_NEWEST,
    START_DATE_OLDEST,
    END_DATE_NEWEST,
    END_DATE_OLDEST
}

data class FilterData(
    val country: Set<String> = emptySet(),
    val type: Set<String> = emptySet(),
    val periodRange: ClosedRange<Int>? = null,
    val yearRange: ClosedRange<Int>? = null,
    val hasVehicle: Boolean = false,
    val dateRange: ClosedRange<String>? = null,
    val costRange: ClosedRange<Long>? = null,
    val valueRange: ClosedRange<Long>? = null,
    val location: Set<String> = emptySet(),
    val colorMain: Set<String> = emptySet(),
    val colorSecondary: Set<String> = emptySet(),
    val sourceType: Set<String> = emptySet(),
    val sourceCountry: Set<String> = emptySet(),
    val archivalDateRange: ClosedRange<String>? = null,
    val archivalReason: Set<String> = emptySet(),
    val recipientCountry: Set<String> = emptySet()
)
