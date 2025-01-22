package com.mikohatara.collectioncatalog.ui.item

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.data.Item
import com.mikohatara.collectioncatalog.data.ItemDetails
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.util.pasteItemDetails
import com.mikohatara.collectioncatalog.util.toFormerPlate
import com.mikohatara.collectioncatalog.util.toItemDetails
import com.mikohatara.collectioncatalog.util.toPlate
import com.mikohatara.collectioncatalog.util.toWantedPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class ItemEntryUiState(
    val item: Item? = null,
    val itemType: ItemType = ItemType.PLATE,
    val itemDetails: ItemDetails = ItemDetails(),
    val selectedCollections: List<Collection> = emptyList(),
    val isNew: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

@HiltViewModel
class ItemEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val plateRepository: PlateRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {
    private val itemType: ItemType =
        savedStateHandle.get<String>(ITEM_TYPE)?.let { ItemType.valueOf(it) } ?: ItemType.PLATE
    private val itemId: Int? = savedStateHandle.get<Int>(ITEM_ID)

    private val _allCollections = mutableStateListOf<Collection>()

    private val _uiState = MutableStateFlow(ItemEntryUiState())
    val uiState: StateFlow<ItemEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (itemId != null) {
                loadItem(itemType, itemId)
            } else {
                _uiState.update { it.copy(itemType = itemType, isNew = true) }
            }
            collectionRepository.getAllCollectionsStream().collect {
                _allCollections.clear()
                _allCollections.addAll(it)
            }
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        val item = uiState.value.item
        val selectedCollections = uiState.value.selectedCollections
        val isNew = uiState.value.isNew
        val hasUnsavedChanges = uiState.value.hasUnsavedChanges
        val initialDetails = if (isNew) ItemDetails() else when (item) {
            is Item.PlateItem -> item.plate.toItemDetails()
            is Item.WantedPlateItem -> item.wantedPlate.toItemDetails()
            is Item.FormerPlateItem -> item.formerPlate.toItemDetails()
            else -> ItemDetails()
        }

        _uiState.value = if (itemDetails != initialDetails || hasUnsavedChanges) {
            ItemEntryUiState(
                item = item,
                itemType = itemType,
                itemDetails = itemDetails,
                selectedCollections = selectedCollections,
                isNew = isNew,
                hasUnsavedChanges = true
            )
        } else {
            ItemEntryUiState(
                item = item,
                itemType = itemType,
                itemDetails = itemDetails,
                selectedCollections = selectedCollections,
                isNew = isNew
            )
        }
    }

    fun saveEntry() = viewModelScope.launch {
        if (uiState.value.isNew) addNewItem() else updateItem()
    }

    fun getCollections(): List<Collection> {
        val collections = _allCollections
        return collections
    }

    fun toggleCollectionSelection(collection: Collection) {
        val selections = _uiState.value.selectedCollections
        val newSelections: List<Collection> = if (selections.any { it == collection }) {
            selections - collection
        } else {
            selections + collection
        }
        _uiState.update { it.copy(selectedCollections = newSelections, hasUnsavedChanges = true) }
    }

    fun copyItemDetailsToClipboard(context: Context, itemDetails: ItemDetails) {
        val jsonString = Json.encodeToString(itemDetails)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ItemDetails", jsonString)
        clipboard.setPrimaryClip(clip)
    }

    fun pasteItemDetailsFromClipboard(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val clipText = clipData.getItemAt(0).text
            if (clipText != null) {
                try {
                    val copiedItemDetails = Json.decodeFromString<ItemDetails>(clipText.toString())
                    _uiState.update {
                        it.copy(
                            itemDetails = it.itemDetails.pasteItemDetails(copiedItemDetails)
                        )
                    }
                } catch (e: Exception) {
                    // TODO something
                    // Handle invalid JSON
                    Log.e("ItemEntryViewModel", "Error pasting ItemDetails", e)
                }
            }
        }
    }

    /*private fun pasteItemDetails(old: ItemDetails, new: ItemDetails): ItemDetails {
        val endValues = old::class.memberProperties.map { property ->



        /*.javaClass.declaredFields.associate { field ->
            field.isAccessible = true
            val oldValue = field.get(old)
            val newValue = field.get(new)
            val endValue = when {
                newValue == null -> oldValue
                else -> newValue
            }
            field.name to endValue
        }*/

        return old.copy(
            *(endValues.map { (name, value) -> name to value }.toTypedArray())
        ) as ItemDetails
    }*/

    private suspend fun addNewItem() = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> plateRepository
                .addPlateWithCollections(
                    uiState.value.itemDetails.toPlate(),
                    uiState.value.selectedCollections.map { it.id }
                )
            ItemType.WANTED_PLATE -> plateRepository
                .addWantedPlate(uiState.value.itemDetails.toWantedPlate())
            ItemType.FORMER_PLATE -> plateRepository
                .addFormerPlate(uiState.value.itemDetails.toFormerPlate())
        }
    }

    private suspend fun updateItem() = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> plateRepository
                .updatePlateWithCollections(
                    uiState.value.itemDetails.toPlate(),
                    uiState.value.selectedCollections.map { it.id }
                )
            ItemType.WANTED_PLATE -> plateRepository
                .updateWantedPlate(uiState.value.itemDetails.toWantedPlate())
            ItemType.FORMER_PLATE -> plateRepository
                .updateFormerPlate(uiState.value.itemDetails.toFormerPlate())
        }
    }

    private fun loadItem(itemType: ItemType, itemId: Int) = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> {
                plateRepository.getPlateWithCollectionsStream(itemId).let {
                    _uiState.value = ItemEntryUiState(
                        item = it.firstOrNull()?.let { Item.PlateItem(it.plate) },
                        itemType = itemType,
                        itemDetails = it.firstOrNull()?.plate?.toItemDetails() ?: ItemDetails(),
                        selectedCollections = it.firstOrNull()?.collections ?: emptyList(),
                        isNew = false
                    )
                }
            }
            ItemType.WANTED_PLATE -> {
                plateRepository.getWantedPlateStream(itemId).let {
                    _uiState.value = ItemEntryUiState(
                        item = it.firstOrNull()?.let { Item.WantedPlateItem(it) },
                        itemType = itemType,
                        itemDetails = it.firstOrNull()?.toItemDetails() ?: ItemDetails(),
                        isNew = false
                    )
                }
            }
            ItemType.FORMER_PLATE -> {
                plateRepository.getFormerPlateStream(itemId).let {
                    _uiState.value = ItemEntryUiState(
                        item = it.firstOrNull()?.let { Item.FormerPlateItem(it) },
                        itemType = itemType,
                        itemDetails = it.firstOrNull()?.toItemDetails() ?: ItemDetails(),
                        isNew = false
                    )
                }
            }
        }
    }
}
