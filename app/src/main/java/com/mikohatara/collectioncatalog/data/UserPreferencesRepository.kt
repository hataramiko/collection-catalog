package com.mikohatara.collectioncatalog.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mikohatara.collectioncatalog.ui.home.SortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val DEFAULT_SORT_ORDER = stringPreferencesKey("default_sort_order")
        const val LOG_TAG = "UserPreferencesRepository"
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(LOG_TAG, "Error", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val defaultSortOrder = SortBy.valueOf(
                preferences[DEFAULT_SORT_ORDER] ?: SortBy.COUNTRY_AND_TYPE_ASC.toString()
            )
            UserPreferences(defaultSortOrder)
        }

    suspend fun saveDefaultSortOrder(sortOrder: SortBy) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_SORT_ORDER] = sortOrder.toString()
        }
    }
}

data class UserPreferences(
    val defaultSortOrder: SortBy
)
