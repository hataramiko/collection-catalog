package com.mikohatara.collectioncatalog.ui.home

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.R
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.ui.catalog.ExportResult
import com.mikohatara.collectioncatalog.ui.catalog.FilterData
import com.mikohatara.collectioncatalog.ui.catalog.ImportResult
import com.mikohatara.collectioncatalog.ui.catalog.SortBy
import com.mikohatara.collectioncatalog.util.exportFormerPlatesToCsv
import com.mikohatara.collectioncatalog.util.getCurrentYear
import com.mikohatara.collectioncatalog.util.importFormerPlatesFromCsv
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

data class ArchiveUiState(
    val items: List<FormerPlate> = emptyList(),
    val sortBy: SortBy = SortBy.START_DATE_NEWEST,
    val filters: FilterData = FilterData(),
    val activeFilterCount: Int = 0,
    val periodSliderPosition: ClosedRange<Float>? = null,
    val yearSliderPosition: ClosedRange<Float>? = null,
    val dateSliderPosition: ClosedRange<Float>? = null,
    val costSliderPosition: ClosedRange<Float>? = null,
    val archivalDateSliderPosition: ClosedRange<Float>? = null,
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportResult: ExportResult? = null,
    val importResult: ImportResult? = null
)

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val plateRepository: PlateRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _allItems = mutableListOf<FormerPlate>()
    val showSortByBottomSheet = mutableStateOf(false)
    val showFilterBottomSheet = mutableStateOf(false)

    private val _isTopRowHidden = MutableStateFlow(false)
    val isTopRowHidden: StateFlow<Boolean> = _isTopRowHidden.asStateFlow()

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userPreferences = userPreferencesRepository.userPreferences.first()
            val defaultSortBy = userPreferences.defaultSortOrderArchive
            _uiState.update { it.copy(sortBy = defaultSortBy) }
            getArchive()
        }
    }

    fun getArchive() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            plateRepository.getAllFormerPlatesStream().collect { items ->
                _allItems.clear()
                _allItems.addAll(items)
                _uiState.update { it.copy(items = items, isLoading = false) }
                setFilter()
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
                compareBy<FormerPlate> { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenBy { it.commonDetails.type }
                    .thenBy(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
                    .thenBy { it.uniqueDetails.regNo }
            )
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedWith(
                compareByDescending<FormerPlate> { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenByDescending { it.commonDetails.type }
                    .thenByDescending(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
                    .thenByDescending { it.uniqueDetails.regNo }
            )
            SortBy.AGE_ASC -> items.sortedWith(
                compareBy<FormerPlate, Int?>(nullsLast()) {
                    it.commonDetails.year ?: it.commonDetails.periodStart
                }
                    .thenBy { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenBy { it.commonDetails.type }
                    .thenBy { it.uniqueDetails.regNo }
            )
            SortBy.AGE_DESC -> items.sortedWith(
                compareByDescending<FormerPlate, Int?>(nullsLast()) {
                    it.commonDetails.year ?: it.commonDetails.periodStart
                }
                    .thenByDescending { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenByDescending { it.commonDetails.type }
                    .thenByDescending { it.uniqueDetails.regNo }
            )
            SortBy.START_DATE_NEWEST -> items
            SortBy.START_DATE_OLDEST -> items
            SortBy.END_DATE_NEWEST -> items.sortedWith(
                compareByDescending<FormerPlate, String?>(nullsLast()) {
                    it.archivalDetails.archivalDate
                }
                    .thenByDescending { it.id }
            )
            SortBy.END_DATE_OLDEST -> items.sortedWith(
                compareBy<FormerPlate, String?>(nullsLast()) { it.archivalDetails.archivalDate }
                    .thenBy { it.id }
            )
        }
        _uiState.update { it.copy(items = sortedItems, sortBy = sortBy) }
        updateDefaultSortBy(sortBy)
    }

    fun getSortByOptions(): List<SortBy> {
        val sortByOptions = SortBy.entries.filter {
            it != SortBy.START_DATE_NEWEST && it != SortBy.START_DATE_OLDEST
        }
        return sortByOptions
    }

    fun getFilterCount(): Int {
        val filters = _uiState.value.filters
        val countrySize = filters.country.size
        val typeSize = filters.type.size
        val vehicleSize = if (filters.hasVehicle) 1 else 0
        val colorMainSize = filters.colorMain.size
        val colorSecondarySize = filters.colorSecondary.size
        val sourceTypeSize = filters.sourceType.size
        val sourceCountrySize = filters.sourceCountry.size
        val archivalReasonSize = filters.archivalReason.size
        val recipientCountrySize = filters.recipientCountry.size

        val yearSliderRange = getYearSliderRange()
        val dateSliderRange = getDateSliderRange()
        val costSliderRange = getCostSliderRange()
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
        val archivalDateSize = if (
            isSliderActive(_uiState.value.archivalDateSliderPosition, archivalDateSliderRange)
        ) 1 else 0

        return countrySize + typeSize + periodSize + yearSize + dateSize + vehicleSize + costSize +
                colorMainSize + colorSecondarySize + sourceTypeSize + sourceCountrySize +
                archivalDateSize + archivalReasonSize + recipientCountrySize
    }

    fun openFilterBottomSheet() {
        setFilterSliderStartPositions()
        showFilterBottomSheet.value = true
    }

    fun setFilter() {
        setPeriodFilter()
        setYearFilter()
        setDateFilter()
        setCostFilter()
        setArchivalDateFilter()
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
                    else -> false
                }
            } != false
            val isWithinYearRange = filters.yearRange?.let { range ->
                val year = item.commonDetails.year

                when {
                    year != null -> year in range
                    else -> false
                }
            } != false
            val isWithinDateRange = filters.dateRange?.let { range ->
                val date = item.uniqueDetails.date
                when {
                    date != null -> date in range
                    else -> false
                }
            } != false
            val isWithinCostRange = filters.costRange?.let { range ->
                val cost = item.uniqueDetails.cost

                when {
                    cost != null -> cost in range
                    else -> false
                }
            } != false
            val isWithinArchivalDateRange = filters.archivalDateRange?.let { range ->
                val archivalDate = item.archivalDetails.archivalDate
                when {
                    archivalDate != null -> archivalDate in range
                    else -> false
                }
            } != false

            val matchesVehicleFilter = if (filters.hasVehicle) {
                !item.uniqueDetails.vehicle.isNullOrEmpty()
            } else true

            when {
                filters.country.isNotEmpty() && filters.country.none {
                    it == item.commonDetails.country
                } -> false
                filters.type.isNotEmpty() && filters.type.none {
                    it == item.commonDetails.type
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
                filters.archivalReason.isNotEmpty() && filters.archivalReason.none {
                    it == item.archivalDetails.archivalReason
                } -> false
                filters.recipientCountry.isNotEmpty() && filters.recipientCountry.none {
                    it == item.archivalDetails.recipientCountry
                } -> false
                !isWithinPeriodRange -> false
                !isWithinYearRange -> false
                !isWithinDateRange -> false
                !isWithinCostRange -> false
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

    fun updateArchivalDateSliderPosition(archivalDateSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(archivalDateSliderPosition = archivalDateSliderPosition) }
    }

    fun toggleVehicleSwitch() {
        _uiState.update { it.copy(filters = it.filters.copy(hasVehicle = !it.filters.hasVehicle)) }
    }

    fun resetFilter() {
        _uiState.update { it.copy(
            filters = FilterData(),
            periodSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat(),
            yearSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat(),
            dateSliderPosition = getMinDate()..getMaxDate(),
            costSliderPosition = getMinCost().toFloat()..getMaxCost().toFloat(),
            archivalDateSliderPosition = getMinArchivalDate()..getMaxArchivalDate()
        ) }
        setFilter()
    }

    fun getCountries(): Set<String> {
        return _allItems.map { it.commonDetails.country }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getTypes(): Set<String> {
        return _allItems.map { it.commonDetails.type }
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

    fun getArchivalReasons(): Set<String> {
        return _allItems.mapNotNull { it.archivalDetails.archivalReason }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getRecipientCountries(): Set<String> {
        return _allItems.mapNotNull { it.archivalDetails.recipientCountry }
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

    fun getArchivalDateSliderRange(): ClosedRange<Float> {
        return getMinArchivalDate()..getMaxArchivalDate()
    }

    fun exportItems(context: Context, uri: Uri) {
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

    fun clearExportResult() {
        _uiState.update { it.copy(isExporting = false, exportResult = null) }
    }

    fun importItems(context: Context, uri: Uri) {
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
            userPreferencesRepository.saveDefaultSortOrderArchive(sortBy)
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
        if (uiState.value.archivalDateSliderPosition == null) {
            _uiState.update { it.copy(
                archivalDateSliderPosition = getMinArchivalDate()..getMaxArchivalDate()) }
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

    private fun getMinDate(): Float {
        val fallback = "1900-01-02".toTimestamp().toFloat()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return fallback
        val allDates = items.mapNotNull { it.uniqueDetails.date }.map { it.toTimestamp() }.sorted()
        val minDate = allDates.firstOrNull()?.toFloat() ?: return fallback
        val maxDate = getMaxDate()

        return if (minDate < maxDate) minDate else fallback
    }

    private fun getMaxDate(): Float {
        val today = Calendar.getInstance().timeInMillis.toFloat()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return today
        val allDates = items.mapNotNull { it.uniqueDetails.date }.map { it.toTimestamp() }.sorted()
        val maxDate = allDates.lastOrNull()?.toFloat() ?: return today

        return maxOf(today, maxDate)
    }

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

    private fun getMinArchivalDate(): Float {
        val fallback = "1900-01-02".toTimestamp().toFloat()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return fallback
        val allDates = items.mapNotNull { it.archivalDetails.archivalDate }
            .map { it.toTimestamp() }.sorted()
        val minDate = allDates.firstOrNull()?.toFloat() ?: return fallback
        val maxDate = getMaxDate()

        return if (minDate < maxDate) minDate else fallback
    }

    private fun getMaxArchivalDate(): Float {
        val today = Calendar.getInstance().timeInMillis.toFloat()
        val items = _allItems.takeIf { it.isNotEmpty() } ?: return today
        val allDates = items.mapNotNull { it.archivalDetails.archivalDate }
            .map { it.toTimestamp() }.sorted()
        val maxDate = allDates.lastOrNull()?.toFloat() ?: return today

        return maxOf(today, maxDate)
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
