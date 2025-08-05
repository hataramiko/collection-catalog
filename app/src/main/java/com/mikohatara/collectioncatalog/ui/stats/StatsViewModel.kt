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
import com.mikohatara.collectioncatalog.util.toCurrencyString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
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
    val countries: Set<String> = emptySet(),
    val types: Set<String> = emptySet(),
    val sourceTypes: Set<String?> = emptySet(),
    val sourceCountries: Set<String?> = emptySet(),
    val archivalReasons: Set<String?> = emptySet(),
    val recipientCountries: Set<String?> = emptySet(),
    // Display values for currency fields
    val combinedCostGross: String = "–",
    val combinedCostGrossPerPlate: String = "–",
    val combinedCostNet: String = "–",
    val combinedCostNetPerPlate: String = "–",
    val selectionCost: String = "–",
    val selectionCostPerPlate: String = "–",
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
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferences()
        )

    private val _allCollections = mutableStateListOf<Collection>()

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getPlates()
            getWishlist()
            getArchive()
            collectionRepository.getAllCollectionsStream().collect {
                _allCollections.clear()
                _allCollections.addAll(it)
            }
        }

        userPreferencesRepository.userPreferences.onEach { preferences ->
            _uiState.update {
                it.copy(userCountry = preferences.userCountry)
            }
        }.launchIn(viewModelScope)

        _uiState.onEach { state ->
            val newActiveItems = getActiveItems()
            val newCountries = getCountries(newActiveItems)
            val newTypes = getTypes(newActiveItems)
            val newSourceTypes = getSourceTypes(newActiveItems)
            val newSourceCountries = getSourceCountries(newActiveItems)
            val newArchivalReasons = getArchivalReasons(newActiveItems)
            val newRecipientCountries = getRecipientCountries(newActiveItems)
            val newCombinedCostGross = getCombinedCost()
            val newCombinedCostGrossPerPlate = getCombinedCost(isPerPlate =  true)
            val newCombinedCostNet = getCombinedCost(isNet = true)
            val newCombinedCostNetPerPlate = getCombinedCost(isNet = true, isPerPlate = true)
            val newSelectionCost = getSelectionCost()
            val newSelectionCostPerPlate = getSelectionCost(isPerPlate = true)
            val newArchivePriceSum = getArchivePriceSum()

            _uiState.update {
                it.copy(
                    activeItems = newActiveItems,
                    countries = newCountries,
                    types = newTypes,
                    sourceTypes = newSourceTypes,
                    sourceCountries = newSourceCountries,
                    archivalReasons = newArchivalReasons,
                    recipientCountries = newRecipientCountries,
                    combinedCostGross = newCombinedCostGross,
                    combinedCostGrossPerPlate = newCombinedCostGrossPerPlate,
                    combinedCostNet = newCombinedCostNet,
                    combinedCostNetPerPlate = newCombinedCostNetPerPlate,
                    selectionCost = newSelectionCost,
                    selectionCostPerPlate = newSelectionCostPerPlate,
                    archivePriceSum = newArchivePriceSum
                )
            }
        }.launchIn(viewModelScope)
    }

    fun setActiveItemType(itemType: ItemType) {
        _uiState.update { it.copy(activeItemType = itemType) }
    }

    fun getActiveItems(): List<Item> {
        return when (uiState.value.activeItemType) {
            ItemType.PLATE -> if (uiState.value.collection != null) {
                uiState.value.collectionPlates.map { Item.PlateItem(it) }
            } else uiState.value.allPlates.map { Item.PlateItem(it) }
            ItemType.WANTED_PLATE -> uiState.value.wishlist.map { Item.WantedPlateItem(it) }
            ItemType.FORMER_PLATE -> uiState.value.archive.map { Item.FormerPlateItem(it) }
        }
    }

    fun getCountries(activeItems: List<Item>): Set<String> {
        return activeItems.map { item ->
            when (item) {
                is Item.PlateItem -> item.plate.commonDetails.country
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails.country
                is Item.FormerPlateItem -> item.formerPlate.commonDetails.country
            }
        }.sortedWith(compareByDescending<String> { country ->
                activeItems.count { item ->
                    when (item) {
                        is Item.PlateItem -> item.plate.commonDetails.country == country
                        is Item.WantedPlateItem -> item.wantedPlate.commonDetails.country == country
                        is Item.FormerPlateItem -> item.formerPlate.commonDetails.country == country
                    }
                }
            }.thenBy { it }).toSet()
    }

    fun getTypes(activeItems: List<Item>): Set<String> {
        return activeItems.map { item ->
            when (item) {
                is Item.PlateItem -> item.plate.commonDetails.type
                is Item.WantedPlateItem -> item.wantedPlate.commonDetails.type
                is Item.FormerPlateItem -> item.formerPlate.commonDetails.type
            }
        }.sortedWith(compareByDescending<String> { type ->
            activeItems.count { item ->
                when (item) {
                    is Item.PlateItem -> item.plate.commonDetails.type == type
                    is Item.WantedPlateItem -> item.wantedPlate.commonDetails.type == type
                    is Item.FormerPlateItem -> item.formerPlate.commonDetails.type == type
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
        return when (uiState.value.activeItemType) {
            ItemType.PLATE -> { item ->
                val plate = (item as Item.PlateItem).plate
                if (property == "country") plate.commonDetails.country
                else if (property == "type") plate.commonDetails.type
                else if (property == "sourceType") plate.source.type
                else if (property == "sourceCountry") plate.source.country
                else ""
            }
            ItemType.WANTED_PLATE -> { item ->
                val wantedPlate = (item as Item.WantedPlateItem).wantedPlate
                if (property == "country") wantedPlate.commonDetails.country
                else if (property == "type") wantedPlate.commonDetails.type
                else ""
            }
            ItemType.FORMER_PLATE -> { item ->
                val formerPlate = (item as Item.FormerPlateItem).formerPlate
                if (property == "country") formerPlate.commonDetails.country
                else if (property == "type") formerPlate.commonDetails.type
                else if (property == "sourceType") formerPlate.source.type
                else if (property == "sourceCountry") formerPlate.source.country
                else if (property == "archivalReason") formerPlate.archivalDetails.archivalReason
                else if (property == "recipientCountry") formerPlate.archivalDetails.recipientCountry
                else ""
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

        if (isPerPlate) {
            val platesSize = _uiState.value.allPlates.size
            val archiveSize = _uiState.value.archive.size
            val combinedSize = platesSize + archiveSize

            if (combinedSize == 0 || combinedCost == 0L) {
                return "–"
            } else {
                val costPerPlate = combinedCost / combinedSize
                return costPerPlate.toCurrencyString(countryCode)
            }
        } else return combinedCost.toCurrencyString(countryCode)
    }

    fun getSelectionCost(isPerPlate: Boolean = false): String {
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

    fun getArchivePriceSum(): String {
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

    private fun getPlates() {
        plateRepository.getAllPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(allPlates = items)
        }.launchIn(viewModelScope)
    }

    private fun getWishlist() {
        plateRepository.getAllWantedPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(wishlist = items)
        }.launchIn(viewModelScope)
    }

    private fun getArchive() {
        plateRepository.getAllFormerPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(archive = items)
        }.launchIn(viewModelScope)
    }

    private fun getPercentageOfAllPlates(comparisonSize: Float): Float {
        return comparisonSize / uiState.value.allPlates.size.toFloat()
    }
}
