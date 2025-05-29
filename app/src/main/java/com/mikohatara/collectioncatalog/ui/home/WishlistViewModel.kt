package com.mikohatara.collectioncatalog.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.data.WantedPlate
import com.mikohatara.collectioncatalog.util.getCurrentYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class WishlistUiState(
    val items: List<WantedPlate> = emptyList(),
    val sortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val filters: FilterData = FilterData(),
    val periodSliderPosition: ClosedRange<Float>? = null,
    val yearSliderPosition: ClosedRange<Float>? = null,
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
            SortBy.COUNTRY_AND_TYPE_ASC -> items.sortedWith(
                compareBy<WantedPlate, String?>(nullsLast()) { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenBy(nullsLast()) { it.commonDetails.type }
                    .thenBy(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
                    .thenBy { it.id }
            )
            SortBy.COUNTRY_AND_TYPE_DESC -> items.sortedWith(
                compareByDescending<WantedPlate, String?>(nullsLast()) { it.commonDetails.country }
                    .thenBy(nullsFirst()) { it.commonDetails.region1st }
                    .thenByDescending(nullsLast()) { it.commonDetails.type }
                    .thenByDescending(nullsFirst()) {
                        it.commonDetails.year ?: it.commonDetails.periodStart
                    }
                    .thenByDescending { it.id }
            )
            SortBy.AGE_ASC -> items
            SortBy.AGE_DESC -> items
            SortBy.START_DATE_NEWEST -> items
            SortBy.START_DATE_OLDEST -> items
            SortBy.END_DATE_NEWEST -> items
            SortBy.END_DATE_OLDEST -> items
        }
        _uiState.update { it.copy(items = sortedItems, sortBy = sortBy) }
        updateDefaultSortBy(sortBy)
    }

    fun getSortByOptions(): List<SortBy> {
        val sortByOptions = SortBy.entries.filter {
            it != SortBy.AGE_ASC && it != SortBy.AGE_DESC &&
            it != SortBy.START_DATE_NEWEST && it != SortBy.START_DATE_OLDEST &&
            it != SortBy.END_DATE_NEWEST && it != SortBy.END_DATE_OLDEST
        }
        return sortByOptions
    }

    fun openFilterBottomSheet() {
        setFilterSliderStartPositions()
        showFilterBottomSheet.value = true
    }

    fun setFilter() {
        setPeriodFilter()
        setYearFilter()
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

            when {
                filters.country.isNotEmpty() && filters.country.none {
                    it == item.commonDetails.country
                } -> false
                filters.type.isNotEmpty() && filters.type.none {
                    it == item.commonDetails.type
                } -> false
                !isWithinPeriodRange -> false
                !isWithinYearRange -> false
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

    fun updatePeriodSliderPosition(periodSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(periodSliderPosition = periodSliderPosition) }
    }

    fun updateYearSliderPosition(yearSliderPosition: ClosedRange<Float>) {
        _uiState.update { it.copy(yearSliderPosition = yearSliderPosition) }
    }

    fun resetFilter() {
        _uiState.update { it.copy(
            filters = FilterData(),
            periodSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat(),
            yearSliderPosition = getMinYear().toFloat()..getMaxYear().toFloat()
        ) }
        setFilter()
    }

    fun getCountries(): Set<String> {
        return _allItems.map { it.commonDetails.country }
            .filter { it.isNotBlank() }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getTypes(): Set<String> {
        return _allItems.map { it.commonDetails.type }
            .filter { it.isNotBlank() }
            .sortedWith(compareBy { it })
            .toSet()
    }

    fun getYearSliderRange(): ClosedRange<Float> {
        return getMinYear().toFloat()..getMaxYear().toFloat()
    }

    private fun updateDefaultSortBy(sortBy: SortBy) {
        viewModelScope.launch {
            userPreferencesRepository.saveDefaultSortOrderWishlist(sortBy)
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

    private fun <T> toggleFilter(filters: Set<T>, item: T): Set<T> =
        if (item in filters) filters - item else filters + item
}
