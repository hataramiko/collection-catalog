package com.mikohatara.collectioncatalog.ui.item

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.Immutable
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
import com.mikohatara.collectioncatalog.data.UserPreferences
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.util.InternalClipboardManager
import com.mikohatara.collectioncatalog.util.filePathFromUri
import com.mikohatara.collectioncatalog.util.pasteItemDetails
import com.mikohatara.collectioncatalog.util.toFormerPlate
import com.mikohatara.collectioncatalog.util.toItemDetails
import com.mikohatara.collectioncatalog.util.toPlate
import com.mikohatara.collectioncatalog.util.toWantedPlate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Immutable
data class ItemEntryUiState(
    val isLoading: Boolean = false,
    val item: Item? = null,
    val itemType: ItemType = ItemType.PLATE,
    val itemDetails: ItemDetails = ItemDetails(),
    val temporaryImageUri: Uri? = null,
    val selectedCollections: List<Collection> = emptyList(),
    val isValidEntry: Boolean = true,
    val isNew: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

@HiltViewModel
class ItemEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userPreferencesRepository: UserPreferencesRepository,
    private val plateRepository: PlateRepository,
    private val collectionRepository: CollectionRepository,
    private val internalClipboardManager: InternalClipboardManager
) : ViewModel() {
    private val itemType: ItemType =
        savedStateHandle.get<String>(ITEM_TYPE)?.let { ItemType.valueOf(it) } ?: ItemType.PLATE
    private val itemId: Int? = savedStateHandle.get<Int>(ITEM_ID)

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferences
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferences()
        )
    val canPasteFromInternalClipboard: StateFlow<Boolean> = internalClipboardManager.copiedItemDetails
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = internalClipboardManager.canPasteItemDetails()
        )

    private val _allCollections = mutableStateListOf<Collection>()

    private val _uiState = MutableStateFlow(ItemEntryUiState())
    val uiState: StateFlow<ItemEntryUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {

            launch {
                collectionRepository.getAllCollectionsStream().collect {
                    _allCollections.clear()
                    _allCollections.addAll(it)
                }
            }

            try {
                if (itemId != null) {
                    withContext(Dispatchers.IO) {
                        loadItem(itemType, itemId)
                    }
                } else {
                    _uiState.update {
                        it.copy(itemType = itemType, isValidEntry = false, isNew = true)
                    }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateUiState(newItemDetails: ItemDetails) {
        _uiState.update { state ->
            val initialDetails = if (state.isNew) {
                ItemDetails()
            } else {
                when (val item = state.item) {
                    is Item.PlateItem -> item.plate.toItemDetails()
                    is Item.WantedPlateItem -> item.wantedPlate.toItemDetails()
                    is Item.FormerPlateItem -> item.formerPlate.toItemDetails()
                    else -> ItemDetails()
                }
            }
            val hasUnsavedChanges = newItemDetails != initialDetails ||
                    state.temporaryImageUri != null || state.hasUnsavedChanges
            val isValidEntry = itemType == ItemType.WANTED_PLATE ||
                    !(newItemDetails.regNo.isNullOrBlank() ||
                    newItemDetails.country.isNullOrBlank() ||
                    newItemDetails.type.isNullOrBlank())

            state.copy(
                itemDetails = newItemDetails,
                isValidEntry = isValidEntry,
                hasUnsavedChanges = hasUnsavedChanges
            )
        }
    }

    fun saveEntry(context: Context) = viewModelScope.launch {
        if (uiState.value.temporaryImageUri != null) saveImageToInternalStorage(context)
        if (uiState.value.itemDetails.imagePath == null && !uiState.value.isNew) clearImagePath()
        if (uiState.value.isNew) addNewItem() else updateItem()
        _uiState.update { it.copy(hasUnsavedChanges = false) }
    }

    fun handlePickedImage(uri: Uri?) {
        _uiState.update { it.copy(temporaryImageUri = uri, hasUnsavedChanges = true) }
    }

    fun saveImageToInternalStorage(context: Context) {
        uiState.value.temporaryImageUri?.let { uri ->
            val newImagePath = filePathFromUri(uri, context)
            val newItemDetails = uiState.value.itemDetails.copy(imagePath = newImagePath)
            updateUiState(newItemDetails)
        }
    }

    fun clearImagePath() {
        _uiState.update { state ->
            val newItemDetails = state.itemDetails.copy(imagePath = null)
            state.copy(
                itemDetails = newItemDetails,
                temporaryImageUri = null,
                hasUnsavedChanges = true
            )
        }
    }

    fun deleteUnusedImages(context: Context) = viewModelScope.launch {
        val directory = context.filesDir
        val files = directory.listFiles() ?: return@launch
        val imagesInUse = getImagesInUse()
        files.forEach { file ->
            if (!imagesInUse.contains(file.absolutePath)) file.delete()
        }
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

    fun copyItemDetails() {
        val itemDetails = _uiState.value.itemDetails
        internalClipboardManager.copyItemDetails(itemDetails)
    }

    fun pasteItemDetails() {
        internalClipboardManager.pasteItemDetails()?.let {
            val currentItemDetails = _uiState.value.itemDetails
            val newItemDetails = currentItemDetails.pasteItemDetails(it)
            updateUiState(newItemDetails)
        } ?: run {
            //TODO nothing to paste, internal clipboard was empty
        }
    }

    fun showToast(context: Context, message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNewItem() = viewModelScope.launch {
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

    private fun updateItem() = viewModelScope.launch {
        when (itemType) {
            ItemType.PLATE -> plateRepository
                .updatePlateWithCollections(
                    uiState.value.itemDetails.toPlate(),
                    uiState.value.selectedCollections.map { it.id }
                )
            ItemType.WANTED_PLATE -> plateRepository
                .updateWantedPlate(uiState.value.itemDetails.toWantedPlate())
            ItemType.FORMER_PLATE -> plateRepository
                .updateFormerPlate(uiState.value.itemDetails // Use .copy to nullify some values
                    .copy(value = null, status = null).toFormerPlate())
        }
    }

    private suspend fun loadItem(itemType: ItemType, itemId: Int) {
        when (itemType) {
            ItemType.PLATE -> {
                plateRepository.getPlateWithCollectionsStream(itemId).firstOrNull()?.let { item ->
                    _uiState.update {
                        it.copy(
                            item = Item.PlateItem(item.plate),
                            itemType = itemType,
                            itemDetails = item.plate.toItemDetails(),
                            selectedCollections = item.collections,
                            isNew = false
                        )
                    }
                }
            }
            ItemType.WANTED_PLATE -> {
                plateRepository.getWantedPlateStream(itemId).firstOrNull()?.let { item ->
                    _uiState.update {
                        it.copy(
                            item = Item.WantedPlateItem(item),
                            itemType = itemType,
                            itemDetails = item.toItemDetails(),
                            isNew = false
                        )
                    }
                }
            }
            ItemType.FORMER_PLATE -> {
                plateRepository.getFormerPlateStream(itemId).firstOrNull()?.let { item ->
                    _uiState.update {
                        it.copy(
                            item = Item.FormerPlateItem(item),
                            itemType = itemType,
                            itemDetails = item.toItemDetails(),
                            isNew = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun getImagesInUse(): List<String> {
        val platesAsync = viewModelScope
            .async { plateRepository.getAllPlatesStream().firstOrNull() ?: emptyList() }
        val wantedPlatesAsync = viewModelScope
            .async { plateRepository.getAllWantedPlatesStream().firstOrNull() ?: emptyList() }
        val formerPlatesAsync = viewModelScope
            .async { plateRepository.getAllFormerPlatesStream().firstOrNull() ?: emptyList() }

        val plates = platesAsync.await()
        val wantedPlates = wantedPlatesAsync.await()
        val formerPlates = formerPlatesAsync.await()

        return plates.mapNotNull { it.uniqueDetails.imagePath } +
                wantedPlates.mapNotNull { it.imagePath } +
                formerPlates.mapNotNull { it.uniqueDetails.imagePath }
    }
}
