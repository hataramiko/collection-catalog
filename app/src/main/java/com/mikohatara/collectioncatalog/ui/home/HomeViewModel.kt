package com.mikohatara.collectioncatalog.ui.home

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.CollectionColor
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.COLLECTION_ID
import com.mikohatara.collectioncatalog.util.exportPlatesToCsv
import com.mikohatara.collectioncatalog.util.getCurrentYear
import com.mikohatara.collectioncatalog.util.importPlatesFromCsv
import com.mikohatara.collectioncatalog.util.normalizeString
import com.mikohatara.collectioncatalog.util.toDateString
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
import java.io.OutputStreamWriter
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.roundToLong

data class HomeUiState(
    val items: List<Plate> = emptyList(),
    val sortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val filters: FilterData = FilterData(),
    val activeFilterCount: Int = 0,
    val periodSliderPosition: ClosedRange<Float>? = null,
    val yearSliderPosition: ClosedRange<Float>? = null,
    //val dateSliderPosition: ClosedRange<Float>? = null, TODO
    val costSliderPosition: ClosedRange<Float>? = null,
    val valueSliderPosition: ClosedRange<Float>? = null,
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
class HomeViewModel @Inject constructor(
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
            SortBy.AGE_ASC -> items.sortedWith(
                compareBy<Plate, Int?>(nullsLast()) {
                    it.commonDetails.year ?: it.commonDetails.periodStart
                }
                    .thenBy { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenBy { it.commonDetails.type }
                    .thenBy { it.uniqueDetails.regNo }
            )
            SortBy.AGE_DESC -> items.sortedWith(
                compareByDescending<Plate, Int?>(nullsLast()) {
                    it.commonDetails.year ?: it.commonDetails.periodStart
                }
                    .thenByDescending { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenByDescending { it.commonDetails.type }
                    .thenByDescending { it.uniqueDetails.regNo }
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

    fun openFilterBottomSheet() {
        setFilterSliderStartPositions()
        showFilterBottomSheet.value = true
    }

    fun setFilter() {
        setPeriodFilter()
        setYearFilter()
        //setDateFilter() TODO
        setCostFilter()
        setValueFilter()
        val filters = _uiState.value.filters

        val filteredItems = _allItems.filter { item ->
            val isWithinPeriodRange = filters.periodRange?.let { range ->
                val periodStart = item.commonDetails.periodStart
                val periodEnd = item.commonDetails.periodEnd

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
                    /*periodStart != null && periodEnd == null -> {
                        periodStart >= range.start && periodStart <= range.endInclusive
                    }
                    periodStart == null && periodEnd != null -> {
                        periodEnd >= range.start && periodEnd <= range.endInclusive
                    }*/
                    else -> false
                }
            } != false
            val isWithinYearRange = filters.yearRange?.let { range ->
                //TODO Might also want to include items whose period meets the year?
                /*val periodStart = item.commonDetails.periodStart
                val periodEnd = item.commonDetails.periodEnd*/
                val year = item.commonDetails.year

                when {
                    year != null -> year in range
                    /*periodStart != null && periodEnd != null -> { // For an inclusive approach
                        periodStart <= range.endInclusive && periodEnd >= range.start
                    }*/
                    else -> false
                }
            } != false
            /*val isWithinDateRange = filters.dateRange?.let { range -> //TODO
                val date = item.uniqueDetails.date
                when {
                    date != null -> date in range
                    else -> false
                }
            } != false*/
            val isWithinCostRange = filters.costRange?.let { range ->
                val cost = item.uniqueDetails.cost

                when {
                    cost != null -> cost in range
                    else -> false
                }
            } != false
            val isWithinValueRange = filters.valueRange?.let { range ->
                val value = item.uniqueDetails.value

                when {
                    value != null -> value in range
                    else -> false
                }
            } != false

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
                filters.colorMain.isNotEmpty() && filters.colorMain.none {
                    it == item.color.main
                } -> false
                filters.colorSecondary.isNotEmpty() && filters.colorSecondary.none {
                    it == item.color.secondary
                } -> false
                filters.sourceType.isNotEmpty() && filters.sourceType.none {
                    it == item.source.type
                } -> false
                filters.sourceCountry.isNotEmpty() && filters.sourceCountry.none {
                    it == item.source.country
                } -> false
                !isWithinPeriodRange -> false
                !isWithinYearRange -> false
                //!isWithinDateRange -> false TODO
                !isWithinCostRange -> false
                !isWithinValueRange -> false
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

    fun updatePeriodSliderPosition(periodSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(periodSliderPosition = periodSliderPosition) }
    }

    fun updateYearSliderPosition(yearSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(yearSliderPosition = yearSliderPosition) }
    }

    /*fun updateDateSliderPosition(dateSliderPosition: ClosedRange<Float>) { //TODO
        _uiState.update { it.copy(dateSliderPosition = dateSliderPosition) }
    }*/

    fun updateCostSliderPosition(costSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(costSliderPosition = costSliderPosition) }
    }

    fun updateValueSliderPosition(valueSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(valueSliderPosition = valueSliderPosition) }
    }

    fun resetFilter() {
        _uiState.update { it.copy(
            filters = FilterData(),
            periodSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat(),
            yearSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat(),
            //dateSliderPosition = getMinDate()..getMaxDate(), TODO
            costSliderPosition = getMinCost().toFloat()..getMaxCost().toFloat(),
            valueSliderPosition = getMinValue().toFloat()..getMaxValue().toFloat()
        ) }
        setFilter()
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

    fun getSortByOptions(): List<SortBy> {
        val sortByOptions = SortBy.entries.filter {
            it != SortBy.END_DATE_NEWEST && it != SortBy.END_DATE_OLDEST
        }
        return sortByOptions
    }

    fun getFilterCount(): Int {
        val filters = _uiState.value.filters
        val countrySize = filters.country.size
        val typeSize = filters.type.size
        val locationSize = filters.location.size
        val colorMainSize = filters.colorMain.size
        val colorSecondarySize = filters.colorSecondary.size
        val sourceTypeSize = filters.sourceType.size
        val sourceCountrySize = filters.sourceCountry.size

        val yearSliderRange = getYearSliderRange()
        //val dateSliderRange = getDateSliderRange() TODO
        val costSliderRange = getCostSliderRange()
        val valueSliderRange = getValueSliderRange()
        val periodSize = if (
            isSliderActive(_uiState.value.periodSliderPosition, yearSliderRange)
        ) 1 else 0
        val yearSize = if (
            isSliderActive(_uiState.value.yearSliderPosition, yearSliderRange)
        ) 1 else 0
        /*val dateSize = if ( //TODO
            isSliderActive(_uiState.value.dateSliderPosition, dateSliderRange)
        ) 1 else 0*/
        val costSize = if (
            isSliderActive(_uiState.value.costSliderPosition, costSliderRange)
        ) 1 else 0
        val valueSize = if (
            isSliderActive(_uiState.value.valueSliderPosition, valueSliderRange)
        ) 1 else 0

        return countrySize + typeSize + locationSize + periodSize + yearSize + //dateSize + TODO
                costSize + valueSize + colorMainSize + colorSecondarySize +
                sourceTypeSize + sourceCountrySize
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

    fun getColorsMain(): Set<String> {
        return _allItems.mapNotNull { it.color.main }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getColorsSecondary(): Set<String> {
        return _allItems.mapNotNull { it.color.secondary }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getSourceTypes(): Set<String> {
        return _allItems.mapNotNull { it.source.type }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getSourceCountries(): Set<String> {
        return _allItems.mapNotNull { it.source.country }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getYearSliderRange(): ClosedRange<Float> {
        return getMinYear().toFloat()..getMaxYear().toFloat()
    }

    /*fun getDateSliderRange(): ClosedRange<Float> { //TODO
        return getMinDate()..getMaxDate()
    }*/

    fun getCostSliderRange(): ClosedRange<Float> {
        return getMinCost().toFloat()..getMaxCost().toFloat()
    }

    fun getValueSliderRange(): ClosedRange<Float> {
        return getMinValue().toFloat()..getMaxValue().toFloat()
    }

    fun exportItems(context: Context, uri: Uri) {
        _uiState.update { it.copy(isExporting = true, exportResult = null) }

        viewModelScope.launch {
            try {
                val items = _uiState.value.items
                val contentResolver = context.contentResolver
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        exportPlatesToCsv(writer, items)
                    }
                }
                _uiState.update { it.copy(
                    isExporting = false,
                    exportResult = ExportResult.Success(getExportMessage(true, context))
                ) }
            } catch (e: Exception) {
                Log.e("HomeViewModel, export", "Export failed", e)
                _uiState.update { it.copy(
                    isExporting = false,
                    exportResult = ExportResult.Failure(getExportMessage(false, context))
                ) }
            }
        }
    }

    fun clearExportResult() {
        _uiState.update { it.copy(isExporting = false, exportResult = null) }
    }

    fun importItems(context: Context, uri: Uri) {
        _uiState.update { it.copy(isImporting = true, importResult = null) }

        viewModelScope.launch {
            try {
                val plates = importPlatesFromCsv(context, uri)
                if (plates != null && plates.isNotEmpty()) { // Size check might be unnecessary
                    /*plates.forEach {
                        plateRepository.addPlate(it)
                    }*/
                    plateRepository.addPlates(plates)
                    _uiState.update { it.copy(
                        isImporting = false,
                        importResult = ImportResult.Success(getImportMessage(context, plates.size))
                    ) }
                } else {
                    Log.e("HomeViewModel, import", "Importing failed, plates list empty")
                    _uiState.update { it.copy(
                        isImporting = false,
                        importResult = ImportResult.Failure(getImportMessage(context))
                    ) }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel, import", "Importing failed", e)
                _uiState.update { it.copy(
                    isImporting = false,
                    importResult = ImportResult.Failure(getImportMessage(context))
                ) }
            }
        }
    }

    fun clearImportResult() {
        _uiState.update { it.copy(isImporting = false, importResult = null) }
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
                        setFilter()
                    }
            } else {
                plateRepository.getAllPlatesStream().collect { items ->
                    _allItems.clear()
                    _allItems.addAll(items)
                    _uiState.update { it.copy(items = items, isLoading = false) }
                    setFilter()
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

    private fun setFilterSliderStartPositions() {
        if (uiState.value.periodSliderPosition == null) {
            _uiState.update { it.copy(
                periodSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat()) }
        }
        if (uiState.value.yearSliderPosition == null) {
            _uiState.update { it.copy(
                yearSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat()) }
        }
        /*if (uiState.value.dateSliderPosition == null) { //TODO
            _uiState.update { it.copy(
                dateSliderPosition = getMinDate()..getMaxDate()) }
        }*/
        if (uiState.value.costSliderPosition == null) {
            _uiState.update { it.copy(
                costSliderPosition = getMinCost().toFloat()..getMaxCost().toFloat()) }
        }
        if (uiState.value.valueSliderPosition == null) {
            _uiState.update { it.copy(
                valueSliderPosition = getMinValue().toFloat()..getMaxValue().toFloat()) }
        }
    }

    private fun isSliderActive(
        sliderPosition: ClosedRange<Float>?,
        defaultRange: ClosedRange<Float>
    ): Boolean {
        return sliderPosition != null && sliderPosition != defaultRange
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

    /*private fun setDateFilter() { //TODO
        val dateRange = uiState.value.dateSliderPosition ?: return
        val rangeStart = dateRange.start
        val rangeEnd = dateRange.endInclusive
        val rangeStartString = rangeStart.toLong().toDateString()
        val rangeEndString = rangeEnd.toLong().toDateString()

        if (rangeStart != getMinDate() && rangeEnd != getMaxDate() && rangeStartString != null && rangeEndString != null) {
            _uiState.update { it.copy(filters = it.filters.copy(dateRange = rangeStartString..rangeEndString)) }
        } else {
            _uiState.update { it.copy(filters = it.filters.copy(dateRange = null)) }
        }
    }*/

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

    private fun getMinYear(): Int {
        val maxYear = getMaxYear()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return 1900
        val allYears = listOfNotNull(
            items.mapNotNull { it.commonDetails.periodStart }.takeIf { it.isNotEmpty() },
            items.mapNotNull { it.commonDetails.periodEnd }.takeIf { it.isNotEmpty() },
            items.mapNotNull { it.commonDetails.year }.takeIf { it.isNotEmpty() }
        ).flatten()

        val minYear = if (allYears.isNotEmpty()) {
            allYears.minOf { it }
        } else {
            1900
        }
        return if (minYear < maxYear) minYear else maxYear - 1
    }

    private fun getMaxYear(): Int {
        val currentYear = getCurrentYear()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return currentYear
        val allYears = listOfNotNull(
            items.mapNotNull { it.commonDetails.periodStart }.takeIf { it.isNotEmpty() },
            items.mapNotNull { it.commonDetails.periodEnd }.takeIf { it.isNotEmpty() },
            items.mapNotNull { it.commonDetails.year }.takeIf { it.isNotEmpty() }
        ).flatten()

        val maxYear = if (allYears.isNotEmpty()) {
            allYears.maxOf { it }
        } else {
            currentYear
        }
        return listOf(currentYear, maxYear).maxOf { it }
    }

    /*private fun getMinDate(): Float { //TODO
        val maxDate = getMaxDate()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return maxDate
        val allDates = items.mapNotNull { it.uniqueDetails.date?.toTimestamp() }.sorted()
        val minDate = allDates.firstOrNull()?.toFloat() ?: return maxDate

        return minOf(maxDate, minDate)
    }

    private fun getMaxDate(): Float {
        val today = Calendar.getInstance().timeInMillis.toFloat()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return today
        val allDates = items.mapNotNull { it.uniqueDetails.date?.toTimestamp() }.sorted()
        val maxDate = allDates.lastOrNull()?.toFloat() ?: return today

        return maxOf(today, maxDate)
    }*/

    private fun getMinCost(): Long {
        return 0L
    }

    private fun getMaxCost(): Long {
        val fallback = 0L
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return fallback
        val allValues = items.mapNotNull { it.uniqueDetails.cost }

        return if (allValues.isNotEmpty()) {
            allValues.maxOf { it }
        } else fallback
    }

    private fun getMinValue(): Long {
        return 0L
    }

    private fun getMaxValue(): Long {
        val fallback = 0L
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return fallback
        val allValues = items.mapNotNull { it.uniqueDetails.value }

        return if (allValues.isNotEmpty()) {
            allValues.maxOf { it }
        } else fallback
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
    //val dateRange: ClosedRange<String>? = null, TODO
    val costRange: ClosedRange<Long>? = null,
    val valueRange: ClosedRange<Long>? = null,
    val location: Set<String> = emptySet(),
    val colorMain: Set<String> = emptySet(),
    val colorSecondary: Set<String> = emptySet(),
    val sourceType: Set<String> = emptySet(),
    val sourceCountry: Set<String> = emptySet(),
    //val archivalDate: ClosedRange<String>? = null, TODO
    val archivalReason: Set<String> = emptySet(),
    val recipientCountry: Set<String> = emptySet()
)
