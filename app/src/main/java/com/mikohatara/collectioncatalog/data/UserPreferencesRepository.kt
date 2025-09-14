package com.mikohatara.collectioncatalog.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mikohatara.collectioncatalog.ui.catalog.SortBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val USER_COUNTRY = stringPreferencesKey("user_country")
        val DEFAULT_SORT_ORDER_MAIN = stringPreferencesKey("default_sort_order_main")
        val DEFAULT_SORT_ORDER_WISHLIST = stringPreferencesKey("default_sort_order_wishlist")
        val DEFAULT_SORT_ORDER_ARCHIVE = stringPreferencesKey("default_sort_order_archive")
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
            val userCountry = preferences[USER_COUNTRY] ?: Locale.getDefault().country ?: "FI"
            val defaultSortOrderMain = SortBy.valueOf(
                preferences[DEFAULT_SORT_ORDER_MAIN] ?: SortBy.COUNTRY_AND_TYPE_ASC.toString()
            )
            val defaultSortOrderWishlist = SortBy.valueOf(
                preferences[DEFAULT_SORT_ORDER_WISHLIST] ?: SortBy.COUNTRY_AND_TYPE_ASC.toString()
            )
            val defaultSortOrderArchive = SortBy.valueOf(
                preferences[DEFAULT_SORT_ORDER_ARCHIVE] ?: SortBy.COUNTRY_AND_TYPE_ASC.toString()
            )
            UserPreferences(
                userCountry,
                defaultSortOrderMain,
                defaultSortOrderWishlist,
                defaultSortOrderArchive
            )
        }

    suspend fun saveUserCountry(userCountry: String) {
        dataStore.edit { preferences ->
            preferences[USER_COUNTRY] = userCountry
        }
    }

    suspend fun saveDefaultSortOrderMain(sortOrder: SortBy) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_SORT_ORDER_MAIN] = sortOrder.toString()
        }
    }

    suspend fun saveDefaultSortOrderWishlist(sortOrder: SortBy) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_SORT_ORDER_WISHLIST] = sortOrder.toString()
        }
    }

    suspend fun saveDefaultSortOrderArchive(sortOrder: SortBy) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_SORT_ORDER_ARCHIVE] = sortOrder.toString()
        }
    }
}

data class UserPreferences(
    val userCountry: String = Locale.getDefault().country ?: "FI",
    val defaultSortOrderMain: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val defaultSortOrderWishlist: SortBy = SortBy.COUNTRY_AND_TYPE_ASC,
    val defaultSortOrderArchive: SortBy = SortBy.COUNTRY_AND_TYPE_ASC
)
