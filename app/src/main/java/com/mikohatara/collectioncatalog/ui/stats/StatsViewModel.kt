package com.mikohatara.collectioncatalog.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.data.WantedPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class StatsUiState(
    val plates: List<Plate> = emptyList(),
    val collection: List<Plate> = emptyList(),
    val wishlist: List<WantedPlate> = emptyList(),
    val archive: List<FormerPlate> = emptyList(),
    val activeItemType: ItemType = ItemType.PLATE
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val plateRepository: PlateRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferences()
        )

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        getPlates()
        getWishlist()
        getArchive()
    }

    fun setActiveItemType(itemType: ItemType) {
        _uiState.update { it.copy(activeItemType = itemType) }
    }

    fun getActiveItems(): List<Item> {
        return when (uiState.value.activeItemType) {
            ItemType.PLATE -> if (uiState.value.collection.isNotEmpty()) {
                uiState.value.collection.map { Item.PlateItem(it) }
            } else uiState.value.plates.map { Item.PlateItem(it) }
            ItemType.WANTED_PLATE -> uiState.value.wishlist.map { Item.WantedPlateItem(it) }
            ItemType.FORMER_PLATE -> uiState.value.archive.map { Item.FormerPlateItem(it) }
        }
    }

    fun getCountries(): Set<String> {
        val activeItems = getActiveItems()

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

    fun getTypes(): Set<String> {
        val activeItems = getActiveItems()

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

    fun getPropertyExtractor(property: String): (Item) -> String {
        return when (uiState.value.activeItemType) {
            ItemType.PLATE -> { item ->
                val plate = (item as Item.PlateItem).plate
                if (property == "country") plate.commonDetails.country
                else if (property == "type") plate.commonDetails.type
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
                else ""
            }
        }
    }

    fun clearCollection() {
        _uiState.update { it.copy(collection = emptyList()) }
    }

    private fun getPlates() {
        plateRepository.getAllPlatesStream().onEach { items ->
            _uiState.value = _uiState.value.copy(plates = items)
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
}
