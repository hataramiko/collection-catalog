package com.mikohatara.collectioncatalog.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.collectioncatalog.data.UserPreferencesRepository
import com.mikohatara.collectioncatalog.ui.home.SortBy
import com.mikohatara.collectioncatalog.util.toCountryCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class SettingsUiState(
    val userCountry: String
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val userPreferences = userPreferencesRepository.userPreferences

    private val _uiState = MutableStateFlow<SettingsUiState?>(null)
    val uiState: StateFlow<SettingsUiState?> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.collect { preferences ->
                _uiState.value = SettingsUiState(
                    userCountry = preferences.userCountry.ifBlank {
                        Locale.getDefault().country ?: "FI"
                    }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun redirectToLanguageSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    fun setUserCountry(userCountry: String) {
        val newCountryCode = userCountry.toCountryCode()
        _uiState.update { it?.copy(userCountry = newCountryCode) }
        updateUserCountry(newCountryCode)
    }

    private fun updateUserCountry(userCountry: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveUserCountry(userCountry)
        }
    }
}
