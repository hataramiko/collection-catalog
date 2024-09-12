package com.mikohatara.collectioncatalog.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.ui.home.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SettingsUiState(
    //TODO remove defaultSortBy. No longer applied through Settings
    val defaultSortBy: SortBy = SortBy.COUNTRY_AND_TYPE_ASC
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val userPreferences = userPreferencesRepository.userPreferences

    private val _uiState = MutableStateFlow(SettingsUiState())
    //val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    val uiState: StateFlow<SettingsUiState> =
        userPreferences.map { preferences ->
            SettingsUiState(defaultSortBy = preferences.defaultSortOrder)
        } //as StateFlow<SettingsUiState>
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )
}
