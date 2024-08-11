package com.mikohatara.collectioncatalog.ui.home

import androidx.lifecycle.ViewModel
import com.mikohatara.collectioncatalog.data.FormerPlate
import com.mikohatara.collectioncatalog.data.PlateRepository
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ArchiveUiState(
    val items: List<FormerPlate> = emptyList()
)

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val plateRepository: PlateRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()
}
