package com.mikohatara.collectioncatalog.ui.navigation

import androidx.navigation.NavHostController
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.NUMBER_VARIANT
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.PLATE_NUMBER
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_ENTRY_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.SETTINGS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.STATS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.WISHLIST_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HOME_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_ENTRY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_SUMMARY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.SETTINGS_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.STATS_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.WISHLIST_SCREEN

object CollectionCatalogScreens {
    const val HOME_SCREEN = "home"
    const val WISHLIST_SCREEN = "wishlist"
    const val STATS_SCREEN = "stats"
    const val SETTINGS_SCREEN = "settings"
    const val ITEM_SUMMARY_SCREEN = "itemSummary"
    const val ITEM_ENTRY_SCREEN = "itemEntry"
}

object CollectionCatalogDestinationArgs {
    const val PLATE_NUMBER = "plateNumber"
    const val NUMBER_VARIANT = "numberVariant"
}

object CollectionCatalogDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val WISHLIST_ROUTE = WISHLIST_SCREEN
    const val STATS_ROUTE = STATS_SCREEN
    const val SETTINGS_ROUTE = SETTINGS_SCREEN
    const val ITEM_SUMMARY_ROUTE = "$ITEM_SUMMARY_SCREEN/{$PLATE_NUMBER}/{$NUMBER_VARIANT}"
    const val ITEM_ENTRY_ROUTE = "$ITEM_ENTRY_SCREEN/{$PLATE_NUMBER}/{$NUMBER_VARIANT}"
}

class CollectionCatalogNavigationActions(private val navController: NavHostController) {

    fun navigateToHomeScreen() {
        navController.navigate(HOME_ROUTE)
    }

    fun navigateToWishlistScreen() {
        navController.navigate(WISHLIST_ROUTE)
    }

    fun navigateToStatsScreen() {
        navController.navigate(STATS_ROUTE)
    }

    fun navigateToSettingsScreen() {
        navController.navigate(SETTINGS_ROUTE)
    }

    fun navigateToItemSummaryScreen(number: String, variant: String) {
        navController.navigate("$ITEM_SUMMARY_SCREEN/$number/$variant")
    }

    fun navigateToItemEntryScreen(number: String?, variant: String?) {
        if (number == null || variant == null) { // TODO probably should improve this whole thing
            navController.navigate(ITEM_ENTRY_ROUTE)
        } else {
            navController.navigate("$ITEM_ENTRY_SCREEN/$number/$variant")
        }
    }
}
