package com.mikohatara.collectioncatalog.ui.settings

import androidx.lifecycle.ViewModel
import com.mikohatara.collectioncatalog.ui.home.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val defaultSortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val sortByOptions = SortBy.entries.toList()
}
