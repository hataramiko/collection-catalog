package com.mikohatara.collectioncatalog.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.Collection
import com.mikohatara.collectioncatalog.data.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionListUiState(
    val collectionList: List<Collection> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CollectionListViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CollectionListUiState())
    val uiState: StateFlow<CollectionListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCollections()
        }
    }

    private fun getCollections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            collectionRepository.getAllCollectionsStream().collect { collections ->
                _uiState.update { it.copy(collectionList = collections, isLoading = false) }
            }
        }
    }
}
