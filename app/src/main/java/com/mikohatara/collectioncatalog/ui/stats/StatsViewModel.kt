package com.mikohatara.collectioncatalog.ui.stats

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.data.WantedPlate
import com.mikohatara.collectioncatalog.util.getCurrentYear
import com.mikohatara.collectioncatalog.util.toCurrencyString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val isLoading: Boolean = true,
    val allPlates: List<Plate> = emptyList(),
    val wishlist: List<WantedPlate> = emptyList(),
    val archive: List<FormerPlate> = emptyList(),
    val activeItemType: ItemType = ItemType.PLATE,
    val activeItems: List<Item> = emptyList(),
    val collection: Collection? = null,
    val collectionPlates: List<Plate> = emptyList(),
    val collectionPercentage: Float = 0f,
    val userCountry: String = "FI",
    // Sets for tables
    val countries: Set<String?> = emptySet(),
    val types: Set<String?> = emptySet(),
    val years: Set<Int> = emptySet(), // used by both periodAmounts and yearAmounts
    val periodAmounts: Map<Int, Int> = emptyMap(),
    val yearAmounts: Map<Int, Int> = emptyMap(),
    val startDateYears: Set<String> = emptySet(),
    val locations: Set<String?> = emptySet(),
    val sourceTypes: Set<String?> = emptySet(),
    val sourceCountries: Set<String?> = emptySet(),
    val endDateYears: Set<String> = emptySet(),
    val archivalReasons: Set<String?> = emptySet(),
    val recipientCountries: Set<String?> = emptySet(),
    // Display values for currency fields
    val combinedCostGross: String = "–",
    val combinedCostGrossPerPlate: String = "–",
    val combinedCostNet: String = "–",
    val combinedCostNetPerPlate: String = "–",
    val selectionCost: String = "–",
    val selectionCostPerPlate: String = "–",
    val selectionValue: String = "–",
    val selectionValuePerPlate: String = "–",
    val archivePriceSum: String = "–"
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val plateRepository: PlateRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _allCollections = mutableStateListOf<Collection>()

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private var currentJob: Job? = null

    init {
        viewModelScope.launch {
            val platesJob = launch {
                val plates = plateRepository.getAllPlatesStream().first()
                _uiState.update { it.copy(allPlates = plates) }
            }
            val wishlistJob = launch {
                val wishlist = plateRepository.getAllWantedPlatesStream().first()
                _uiState.update { it.copy(wishlist = wishlist) }
            }
            val archiveJob = launch {
                val archive = plateRepository.getAllFormerPlatesStream().first()
                _uiState.update { it.copy(archive = archive) }
            }
            val collectionsJob = launch {
                try {
                    val collections = collectionRepository.getAllCollectionsStream().first()
                    _allCollections.clear()
                    _allCollections.addAll(collections)
                } catch (e: Exception) {
                    _allCollections.clear()
                }
            }

            platesJob.join()
            wishlistJob.join()
            archiveJob.join()
            collectionsJob.join()

            if (_uiState.value.isLoading) _uiState.update { it.copy(isLoading = false) }
        }

        userPreferencesRepository.userPreferences.onEach { preferences ->
            _uiState.update {
                it.copy(userCountry = preferences.userCountry)
            }
        }.launchIn(viewModelScope)

        _uiState.onEach { state ->
            currentJob?.cancel()

            if (!state.isLoading) {
                currentJob = viewModelScope.launch(Dispatchers.Default) {
                    val newActiveItems = getActiveItems(state)
                    ensureActive()
                    val newCountries = getCountries(newActiveItems)
                    val newTypes = getTypes(newActiveItems)
                    ensureActive()
                    val newYears = getYears(newActiveItems)
                    val newPeriodAmounts = getPeriodAmounts(newActiveItems)
                    val newYearAmounts = getYearAmounts(newActiveItems)
                    ensureActive()
                    val newStartDateYears = getStartDateYears(newActiveItems)
                    val newLocations = getLocations(newActiveItems)
                    ensureActive()
                    val newSourceTypes = getSourceTypes(newActiveItems)
                    val newSourceCountries = getSourceCountries(newActiveItems)
                    ensureActive()
                    val newEndDateYears = getEndDateYears(newActiveItems)
                    val newArchivalReasons = getArchivalReasons(newActiveItems)
                    val newRecipientCountries = getRecipientCountries(newActiveItems)
                    ensureActive()
                    val newCombinedCostGross = getCombinedCost()
                    val newCombinedCostGrossPerPlate = getCombinedCost(isPerPlate =  true)
                    val newCombinedCostNet = getCombinedCost(isNet = true)
                    val newCombinedCostNetPerPlate = getCombinedCost(isNet = true, isPerPlate = true)
                    ensureActive()
                    val newSelectionCost = getSelectionCost(newActiveItems)
                    val newSelectionCostPerPlate = getSelectionCost(newActiveItems, true)
                    val newSelectionValue = getSelectionValue(newActiveItems)
                    val newSelectionValuePerPlate = getSelectionValue(newActiveItems, true)
                    val newArchivePriceSum = getArchivePriceSum(newActiveItems)
                    ensureActive()

                    if (isActive && _uiState.value.activeItemType == state.activeItemType) {
                        val newState = state.copy(
                            activeItems = newActiveItems,
                            countries = newCountries,
                            types = newTypes,
                            years = newYears,
                            periodAmounts = newPeriodAmounts,
                            yearAmounts = newYearAmounts,
                            startDateYears = newStartDateYears,
                            locations = newLocations,
                            sourceTypes = newSourceTypes,
                            sourceCountries = newSourceCountries,
                            endDateYears = newEndDateYears,
                            archivalReasons = newArchivalReasons,
                            recipientCountries = newRecipientCountries,
                            combinedCostGross = newCombinedCostGross,
                            combinedCostGrossPerPlate = newCombinedCostGrossPerPlate,
                            combinedCostNet = newCombinedCostNet,
                            combinedCostNetPerPlate = newCombinedCostNetPerPlate,
                            selectionCost = newSelectionCost,
                            selectionCostPerPlate = newSelectionCostPerPlate,
                            selectionValue = newSelectionValue,
                            selectionValuePerPlate = newSelectionValuePerPlate,
                            archivePriceSum = newArchivePriceSum
                        )
                        _uiState.update { newState }
                    }
                }
            } else currentJob?.cancel()
        }.launchIn(viewModelScope)
    }

    fun setActiveItemType(itemType: ItemType) {
        viewModelScope.launch {
            clearActiveItems()
            _uiState.update { it.copy(activeItemType = itemType) }
        }
    }

    fun getActiveItems(state: StatsUiState): List<Item> {
        return when (state.activeItemType) {
            ItemType.PLATE -> if (uiState.value.collection != null) {
                uiState.value.collectionPlates.map { Item.PlateItem(it) }
            } else uiState.value.allPlates.map { Item.PlateItem(it) }
            ItemType.WANTED_PLATE -> uiState.value.wishlist.map { Item.WantedPlateItem(it) }
            ItemType.FORMER_PLATE -> uiState.value.archive.map { Item.FormerPlateItem(it) }
        }
    }

    fun getCountries(activeItems: List<Item>): Set<String?> {
        return activeItems.map { item ->
            when (item) {
                is Item.PlateItem -> item.plate.commonDetails.country
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails.country
                is Item.FormerPlateItem -> item.formerPlate.commonDetails.country
            }
        }.sortedWith(compareByDescending<String?> { country ->
                activeItems.count { item ->
                    when (item) {
                        is Item.PlateItem -> item.plate.commonDetails.country == country
                        is Item.WantedPlateItem -> item.wantedPlate.commonDetails.country == country
                        is Item.FormerPlateItem -> item.formerPlate.commonDetails.country == country
                    }
                }
            }.thenBy { it }).toSet()
    }

    fun getTypes(activeItems: List<Item>): Set<String?> {
        return activeItems.map { item ->
            when (item) {
                is Item.PlateItem -> item.plate.commonDetails.type
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails.type
                is Item.FormerPlateItem -> item.formerPlate.commonDetails.type
            }
        }.sortedWith(compareByDescending<String?> { type ->
            activeItems.count { item ->
                when (item) {
                    is Item.PlateItem -> item.plate.commonDetails.type == type
                    is Item.WantedPlateItem -> item.wantedPlate.commonDetails.type == type
                    is Item.FormerPlateItem -> item.formerPlate.commonDetails.type == type
                }
            }
        }.thenBy { it }).toSet()
    }

    fun getYears(activeItems: List<Item>): Set<Int> {
        val minYear = getMinYear()
        val maxYear = getMaxYear()

        val yearsFromMinToMax = (minYear..maxYear).toSet()
        val itemYears = activeItems.mapNotNull { item ->
            when (item) {
                is Item.PlateItem -> item.plate.commonDetails.year
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails.year
                is Item.FormerPlateItem -> item.formerPlate.commonDetails.year
            }

        }.toSet()

        return (yearsFromMinToMax + itemYears).toSortedSet()
    }

    fun getPeriodAmounts(activeItems: List<Item>): Map<Int, Int> {
        if (activeItems.isEmpty()) {
            return emptyMap()
        }

        val yearAmounts = mutableMapOf<Int, Int>()
        activeItems.forEach { item ->
            val details = when (item) {
                is Item.PlateItem -> item.plate.commonDetails
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails
                is Item.FormerPlateItem -> item.formerPlate.commonDetails
            }
            val periodStart = details.periodStart
            val periodEnd = details.periodEnd

            if (periodStart != null && periodEnd != null && periodStart <= periodEnd) {
                for (year in periodStart..periodEnd) {
                    yearAmounts[year] = (yearAmounts[year] ?: 0) + 1
                }
            }
        }

        return yearAmounts.toSortedMap()
    }

    fun getYearAmounts(activeItems: List<Item>): Map<Int, Int> {
        if (activeItems.isEmpty()) {
            return emptyMap()
        }
        return activeItems.mapNotNull { item ->
            when (item) {
                is Item.PlateItem -> item.plate.commonDetails.year
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails.year
                is Item.FormerPlateItem -> item.formerPlate.commonDetails.year
            }
        }.groupBy { it }.mapValues { it.value.size }.toSortedMap()
    }

    fun getStartDateYears(activeItems: List<Item>): Set<String> {
        return getStartDateYearIntList(activeItems).map { it.toString() }.toSet()
    }

    fun getEndDateYears(activeItems: List<Item>): Set<String> {
        return getEndDateYearIntList(activeItems).map { it.toString() }.toSet()
    }

    fun getLocations(activeItems: List<Item>): Set<String?> {
        if (_uiState.value.activeItemType != ItemType.PLATE) return emptySet()

        return activeItems.map { item ->
            when (item) {
                is Item.PlateItem -> item.plate.uniqueDetails.status
                else -> null
            }
        }.sortedWith(compareByDescending<String?> { location ->
            activeItems.count { item ->
                when (item) {
                    is Item.PlateItem -> item.plate.uniqueDetails.status == location
                    else -> false
                }
            }
        }.thenBy { it }).toSet()
    }

    fun getSourceTypes(activeItems: List<Item>): Set<String?> {
        if (_uiState.value.activeItemType == ItemType.WANTED_PLATE) return emptySet()

        return activeItems.map { item ->
            when (item) {
                is Item.PlateItem -> item.plate.source.type
                is Item.FormerPlateItem -> item.formerPlate.source.type
                else -> null
            }
        }.sortedWith(compareByDescending<String?> { sourceType ->
            activeItems.count { item ->
                when (item) {
                    is Item.PlateItem -> item.plate.source.type == sourceType
                    is Item.FormerPlateItem -> item.formerPlate.source.type == sourceType
                    else -> false
                }
            }
        }.thenBy { it }).toSet()
    }

    fun getSourceCountries(activeItems: List<Item>): Set<String?> {
        if (_uiState.value.activeItemType == ItemType.WANTED_PLATE) return emptySet()

        return activeItems.map { item ->
            when (item) {
                is Item.PlateItem -> item.plate.source.country
                is Item.FormerPlateItem -> item.formerPlate.source.country
                else -> null
            }
        }.sortedWith(compareByDescending<String?> { sourceCountry ->
            activeItems.count { item ->
                when (item) {
                    is Item.PlateItem -> item.plate.source.country == sourceCountry
                    is Item.FormerPlateItem -> item.formerPlate.source.country == sourceCountry
                    else -> false
                }
            }
        }.thenBy { it }).toSet()
    }

    fun getArchivalReasons(activeItems: List<Item>): Set<String?> {
        if (_uiState.value.activeItemType != ItemType.FORMER_PLATE) return emptySet()

        return activeItems.map { item ->
            when (item) {
                is Item.FormerPlateItem -> item.formerPlate.archivalDetails.archivalReason
                else -> null
            }
        }.sortedWith(compareByDescending<String?> { archivalReason ->
            activeItems.count { item ->
                when (item) {
                    is Item.FormerPlateItem -> item
                        .formerPlate.archivalDetails.archivalReason == archivalReason
                    else -> false
                }
            }
        }.thenBy { it }).toSet()
    }

    fun getRecipientCountries(activeItems: List<Item>): Set<String?> {
        if (_uiState.value.activeItemType != ItemType.FORMER_PLATE) return emptySet()

        return activeItems.map { item ->
            when (item) {
                is Item.FormerPlateItem -> item.formerPlate.archivalDetails.recipientCountry
                else -> null
            }
        }.sortedWith(compareByDescending<String?> { recipientCountry ->
            activeItems.count { item ->
                when (item) {
                    is Item.FormerPlateItem -> item
                        .formerPlate.archivalDetails.recipientCountry == recipientCountry
                    else -> false
                }
            }
        }.thenBy { it }).toSet()
    }

    fun getPropertyExtractor(property: String): (Item) -> String? {
        val itemType = _uiState.value.activeItemType

        return { item ->
            when (itemType) {
                ItemType.PLATE -> {
                    if (item is Item.PlateItem) {
                        when (property) {
                            "country" -> item.plate.commonDetails.country
                            "type" -> item.plate.commonDetails.type
                            "startDateYear" -> item.plate.uniqueDetails.date?.split("-")?.firstOrNull()
                            "location" -> item.plate.uniqueDetails.status
                            "sourceType" -> item.plate.source.type
                            "sourceCountry" -> item.plate.source.country
                            else -> ""
                        }
                    } else ""
                }
                ItemType.WANTED_PLATE -> {
                    if (item is Item.WantedPlateItem) {
                        when (property) {
                            "country" -> item.wantedPlate.commonDetails.country
                            "type" -> item.wantedPlate.commonDetails.type
                            else -> ""
                        }
                    } else ""
                }
                ItemType.FORMER_PLATE -> {
                    if (item is Item.FormerPlateItem) {
                        when (property) {
                            "country" -> item.formerPlate.commonDetails.country
                            "type" -> item.formerPlate.commonDetails.type
                            "startDateYear" -> item.formerPlate.uniqueDetails.date?.split("-")?.firstOrNull()
                            "sourceType" -> item.formerPlate.source.type
                            "sourceCountry" -> item.formerPlate.source.country
                            "endDateYear" -> item.formerPlate.archivalDetails.archivalDate?.split("-")?.firstOrNull()
                            "archivalReason" -> item.formerPlate.archivalDetails.archivalReason
                            "recipientCountry" -> item.formerPlate.archivalDetails.recipientCountry
                            else -> ""
                        }
                    } else ""
                }
            }
        }
    }

    fun getCombinedCost(isNet: Boolean = false, isPerPlate: Boolean = false): String {
        val countryCode = _uiState.value.userCountry
        val platesCost = _uiState.value.allPlates.sumOf { it.uniqueDetails.cost?.toLong() ?: 0 }
        val archiveCost = _uiState.value.archive.sumOf { it.uniqueDetails.cost?.toLong() ?: 0 }
        val archivePrice = _uiState.value.archive.sumOf { it.archivalDetails.price?.toLong() ?: 0 }
        val combinedCost = if (isNet) {
            platesCost + archiveCost - archivePrice
        } else platesCost + archiveCost

        if (combinedCost == 0L) return "–"

        if (isPerPlate) {
            val platesSize = _uiState.value.allPlates.size
            val archiveSize = _uiState.value.archive.size
            val combinedSize = platesSize + archiveSize

            if (combinedSize == 0) {
                return "–"
            } else {
                val costPerPlate = combinedCost / combinedSize
                return costPerPlate.toCurrencyString(countryCode)
            }
        } else return combinedCost.toCurrencyString(countryCode)
    }

    fun getSelectionCost(activeItems: List<Item>, isPerPlate: Boolean = false): String {
        if (activeItems.isEmpty()) return "–"

        val countryCode = _uiState.value.userCountry
        val itemType = _uiState.value.activeItemType

        if (itemType == ItemType.FORMER_PLATE) {
            val cost = _uiState.value.archive.sumOf { it.uniqueDetails.cost?.toLong() ?: 0 }

            if (isPerPlate) {
                val size = _uiState.value.archive.size

                if (size == 0) {
                    return "–"
                } else {
                    val costPerPlate = cost / size
                    return costPerPlate.toCurrencyString(countryCode)
                }
            } else return cost.toCurrencyString(countryCode)

        } else if (itemType == ItemType.PLATE && _uiState.value.collectionPlates.isNotEmpty()) {
            val cost = _uiState.value.collectionPlates
                .sumOf { it.uniqueDetails.cost?.toLong() ?: 0 }

            if (isPerPlate) {
                val size = _uiState.value.collectionPlates.size

                if (size == 0) {
                    return "–"
                } else {
                    val costPerPlate = cost / size
                    return costPerPlate.toCurrencyString(countryCode)
                }
            } else return cost.toCurrencyString(countryCode)

        } else if (itemType == ItemType.PLATE) {
            val cost = _uiState.value.allPlates.sumOf { it.uniqueDetails.cost?.toLong() ?: 0 }

            if (isPerPlate) {
                val size = _uiState.value.allPlates.size

                if (size == 0) {
                    return "–"
                } else {
                    val costPerPlate = cost / size
                    return costPerPlate.toCurrencyString(countryCode)
                }
            } else return cost.toCurrencyString(countryCode)
        }
        return "–" // Effectively "if (itemType == ItemType.WANTED_PLATE)"
    }

    fun getSelectionValue(activeItems: List<Item>, isPerPlate: Boolean = false): String {
        if (activeItems.isEmpty()) return "–"

        val countryCode = _uiState.value.userCountry
        val itemType = _uiState.value.activeItemType

        if (itemType == ItemType.PLATE && _uiState.value.collectionPlates.isNotEmpty()) {
            val value = _uiState.value.collectionPlates
                .sumOf { it.uniqueDetails.value?.toLong() ?: 0 }

            if (isPerPlate) {
                val size = _uiState.value.collectionPlates.size

                if (size == 0) {
                    return "–"
                } else {
                    val valuePerPlate = value / size
                    return valuePerPlate.toCurrencyString(countryCode)
                }
            } else return value.toCurrencyString(countryCode)

        } else if (itemType == ItemType.PLATE) {
            val value = _uiState.value.allPlates.sumOf { it.uniqueDetails.value?.toLong() ?: 0 }

            if (isPerPlate) {
                val size = _uiState.value.allPlates.size

                if (size == 0) {
                    return "–"
                } else {
                    val valuePerPlate = value / size
                    return valuePerPlate.toCurrencyString(countryCode)
                }
            } else return value.toCurrencyString(countryCode)
        }
        return "–" // Effectively "if (itemType != ItemType.PLATE)"
    }

    fun getArchivePriceSum(activeItems: List<Item>): String {
        if (activeItems.isEmpty()) return "–"

        val countryCode = _uiState.value.userCountry
        val itemType = _uiState.value.activeItemType

        if (itemType == ItemType.FORMER_PLATE) {
            val sum = _uiState.value.archive.sumOf { it.archivalDetails.price?.toLong() ?: 0 }
            return sum.toCurrencyString(countryCode)
        }
        return "–"
    }

    fun getCollections(): List<Collection> {
        val collections = _allCollections
        return collections
    }

    fun setCollection(collection: Collection) {
        setActiveItemType(ItemType.PLATE)
        viewModelScope.launch {
            collectionRepository.getCollectionWithPlatesStream(collection.id).collect {
                val collectionPlates = it?.plates ?: emptyList()
                val collectionSize = collectionPlates.size.toFloat()
                _uiState.update {
                    it.copy(
                        collection = collection,
                        collectionPlates = collectionPlates,
                        collectionPercentage = getPercentageOfAllPlates(collectionSize)
                    )
                }
            }
        }
    }

    fun clearCollection() {
        _uiState.update {
            it.copy(collection = null, collectionPlates = emptyList(), collectionPercentage = 0f)
        }
    }

    private fun getPercentageOfAllPlates(comparisonSize: Float): Float {
        return comparisonSize / uiState.value.allPlates.size.toFloat()
    }

    private fun clearActiveItems() {
        _uiState.update {
            it.copy(
                activeItems = emptyList(),
                countries = emptySet(),
                types = emptySet(),
                years = emptySet(),
                periodAmounts = emptyMap(),
                yearAmounts = emptyMap(),
                startDateYears = emptySet(),
                locations = emptySet(),
                sourceTypes = emptySet(),
                sourceCountries = emptySet(),
                endDateYears = emptySet(),
                archivalReasons = emptySet(),
                recipientCountries = emptySet()
            )
        }
    }

    private fun getMinYear(): Int {
        val maxYear = getMaxYear()
        val items = _uiState.value.activeItems.takeIf { it.isNotEmpty() } ?: return 1900

        val allYears = items.flatMap { item ->
            val details = when (item) {
                is Item.PlateItem -> item.plate.commonDetails
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails
                is Item.FormerPlateItem -> item.formerPlate.commonDetails
            }
            listOfNotNull(details.periodStart, details.periodEnd, details.year)
        }

        val minYear = if (allYears.isNotEmpty()) {
            allYears.minOf { it }
        } else {
            1900
        }
        return if (minYear < maxYear) minYear else maxYear - 1
    }

    private fun getMaxYear(): Int {
        val currentYear = getCurrentYear()
        val items = _uiState.value.activeItems.takeIf { it.isNotEmpty() } ?: return currentYear

        val allYears = items.flatMap { item ->
            val details = when (item) {
                is Item.PlateItem -> item.plate.commonDetails
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails
                is Item.FormerPlateItem -> item.formerPlate.commonDetails
            }
            listOfNotNull(details.periodStart, details.periodEnd, details.year)
        }

        val maxYear = if (allYears.isNotEmpty()) {
            allYears.maxOf { it }
        } else {
            currentYear
        }
        return listOf(currentYear, maxYear).maxOf { it }
    }

    private fun getStartDateYearIntList(activeItems: List<Item>): List<Int> {
        // lastYear should always be the current year, negating the need to pass activeItems?
        val lastYear = getMaxDate(/*activeItems*/)
        val firstYear = getMinStartDate(activeItems)

        if (firstYear >= lastYear) return listOf(lastYear)

        return (firstYear..lastYear).toList()
    }

    private fun getEndDateYearIntList(activeItems: List<Item>): List<Int> {
        val lastYear = getMaxDate()
        val firstYear = getMinEndDate(activeItems)

        if (firstYear >= lastYear) return listOf(lastYear)

        return (firstYear..lastYear).toList()
    }

    private fun getMinStartDate(activeItems: List<Item>): Int {
        val currentYear = getCurrentYear()
        if (activeItems.isEmpty()) return currentYear
        if (activeItems.first() is Item.WantedPlateItem) return currentYear

        val allDates = activeItems.flatMap { item ->
            val details = when (item) {
                is Item.PlateItem -> item.plate.uniqueDetails
                is Item.WantedPlateItem -> return currentYear
                is Item.FormerPlateItem -> item.formerPlate.uniqueDetails
            }
            listOfNotNull(details.date)
        }

        val minDate = if (allDates.isNotEmpty()) allDates.minOf { it } else return currentYear
        val firstYearString = minDate.split("-").firstOrNull() ?: return currentYear
        val firstYear = firstYearString.toIntOrNull()

        return firstYear ?: currentYear
    }

    private fun getMinEndDate(activeItems: List<Item>): Int {
        val currentYear = getCurrentYear()
        if (activeItems.isEmpty()) return currentYear
        if (activeItems.first() is Item.PlateItem ||
            activeItems.first() is Item.WantedPlateItem) return currentYear

        val allDates = activeItems.flatMap { item ->
            val details = when (item) {
                is Item.PlateItem -> return currentYear
                is Item.WantedPlateItem -> return currentYear
                is Item.FormerPlateItem -> item.formerPlate.archivalDetails
            }
            listOfNotNull(details.archivalDate)
        }

        val minDate = if (allDates.isNotEmpty()) allDates.minOf { it } else return currentYear
        val firstYearString = minDate.split("-").firstOrNull() ?: return currentYear
        val firstYear = firstYearString.toIntOrNull()

        return firstYear ?: currentYear
    }

    private fun getMaxDate(): Int {
        return getCurrentYear()
    }
}
