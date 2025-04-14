package com.mikohatara.collectioncatalog.ui.help

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.HELP_PAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HelpUiState(
    val helpPage: HelpPage = HelpPage.DEFAULT
)

@HiltViewModel
class HelpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val helpPage: HelpPage =
        savedStateHandle.get<String>(HELP_PAGE)?.let { HelpPage.valueOf(it) } ?: HelpPage.DEFAULT

    private val _uiState = MutableStateFlow< HelpUiState?>(null)
    val uiState: StateFlow<HelpUiState?> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            when (helpPage) {
                HelpPage.DEFAULT -> _uiState.value = HelpUiState(helpPage = HelpPage.DEFAULT)
                HelpPage.IMPORT -> _uiState.value = HelpUiState(helpPage = HelpPage.IMPORT)
            }
        }
    }
}

enum class HelpPage {
    DEFAULT,
    IMPORT
}
