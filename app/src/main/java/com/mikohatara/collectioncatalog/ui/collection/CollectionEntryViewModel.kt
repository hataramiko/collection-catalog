package com.mikohatara.collectioncatalog.ui.collection

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.CollectionColor
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.COLLECTION_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionEntryUiState(
    val collection: Collection? = null,
    val collectionDetails: CollectionDetails = CollectionDetails(),
    val isNew: Boolean = false,
    val isValidEntry: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

@HiltViewModel
class CollectionEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val collectionRepository: CollectionRepository
) : ViewModel() {
    private val collectionId: Int? = savedStateHandle.get<Int>(COLLECTION_ID)

    private val _uiState = MutableStateFlow(CollectionEntryUiState())
    val uiState: StateFlow<CollectionEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (collectionId != null) {
                loadCollection(collectionId)
            } else {
                _uiState.update { it.copy(isNew = true) }
            }
        }
    }

    fun updateUiState(collectionDetails: CollectionDetails) {
        val collection = uiState.value.collection
        val isNew = uiState.value.isNew
        val isValidEntry = !collectionDetails.name.isNullOrBlank()
        val initialDetails = if (isNew) CollectionDetails() else
            collection?.toCollectionDetails() ?: CollectionDetails()

        _uiState.value = if (collectionDetails != initialDetails) {
            CollectionEntryUiState(
                collection = collection,
                collectionDetails = collectionDetails,
                isNew = isNew,
                isValidEntry = isValidEntry,
                hasUnsavedChanges = true
            )
        } else {
            CollectionEntryUiState(
                collection = collection,
                collectionDetails = collectionDetails,
                isNew = isNew,
                isValidEntry = isValidEntry
            )
        }
    }

    fun saveEntry() = viewModelScope.launch {
        if (uiState.value.isNew) addNewCollection() else updateCollection()
    }

    fun deleteCollection() = viewModelScope.launch {
        uiState.value.collection?.let { collectionRepository.deleteCollectionWithPlates(it) }
    }

    fun updateCollectionColor(input: String) {
        val newColor = CollectionColor.entries.find { it.name == input } ?: CollectionColor.DEFAULT
        _uiState.update {
            it.copy(collectionDetails = it.collectionDetails.copy(color = newColor))
        }
    }

    private fun addNewCollection() = viewModelScope.launch {
        collectionRepository.addCollection(uiState.value.collectionDetails.toCollection())
    }

    private fun updateCollection() = viewModelScope.launch {
        collectionRepository.updateCollection(uiState.value.collectionDetails.toCollection())
    }

    private fun loadCollection(collectionId: Int) = viewModelScope.launch {
        collectionRepository.getCollectionStream(collectionId).let {
            val collection = it.firstOrNull()
            _uiState.value = CollectionEntryUiState(
                collection = collection,
                collectionDetails = collection?.toCollectionDetails() ?: CollectionDetails(),
                isNew = false,
                isValidEntry = true
            )
        }
    }
}

data class CollectionDetails(
    val id: Int? = null,
    val emoji: String? = null,
    val name: String? = null,
    val color: CollectionColor = CollectionColor.DEFAULT
)

fun Collection.toCollectionDetails(): CollectionDetails = CollectionDetails(
    id = id,
    emoji = emoji,
    name = name,
    color = color
)

fun CollectionDetails.toCollection(): Collection = Collection(
    id ?: 0,
    emoji?.takeIf { it.isNotBlank() },
    name ?: "",
    color
)

fun String.isCollectionColor(): Boolean {
    return CollectionColor.entries.any { it.name == this }
}

fun String.toColor(): Color {
    return CollectionColor.entries.find { it.name == this }?.color ?: Color.Unspecified
}
