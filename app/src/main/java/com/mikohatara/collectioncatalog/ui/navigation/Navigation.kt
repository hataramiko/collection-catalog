package com.mikohatara.collectioncatalog.ui.navigation

import androidx.navigation.NavHostController
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ARCHIVE_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.SETTINGS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.STATS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.WISHLIST_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ARCHIVE_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HOME_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_ENTRY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_SUMMARY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.SETTINGS_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.STATS_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.WISHLIST_SCREEN

object CollectionCatalogScreens {
    const val HOME_SCREEN = "home"
    const val WISHLIST_SCREEN = "wishlist"
    const val ARCHIVE_SCREEN = "archive"
    const val STATS_SCREEN = "stats"
    const val SETTINGS_SCREEN = "settings"
    const val ITEM_SUMMARY_SCREEN = "itemSummary"
    const val ITEM_ENTRY_SCREEN = "itemEntry"
}

object CollectionCatalogDestinationArgs {
    const val ITEM_TYPE = "itemType"
    const val ITEM_ID = "itemId"
}

object CollectionCatalogDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val WISHLIST_ROUTE = WISHLIST_SCREEN
    const val ARCHIVE_ROUTE = ARCHIVE_SCREEN
    const val STATS_ROUTE = STATS_SCREEN
    const val SETTINGS_ROUTE = SETTINGS_SCREEN
    const val ITEM_SUMMARY_ROUTE = "$ITEM_SUMMARY_SCREEN/{$ITEM_TYPE}/{$ITEM_ID}"
    const val ITEM_ENTRY_ADD_ROUTE = "$ITEM_ENTRY_SCREEN/{$ITEM_TYPE}"
    const val ITEM_ENTRY_EDIT_ROUTE = "$ITEM_ENTRY_SCREEN/{$ITEM_TYPE}/{$ITEM_ID}"
}

class CollectionCatalogNavigationActions(private val navController: NavHostController) {

    fun navigateToHomeScreen() {
        navController.navigate(HOME_ROUTE)
    }

    fun navigateToWishlistScreen() {
        navController.navigate(WISHLIST_ROUTE)
    }

    fun navigateToArchiveScreen() {
        navController.navigate(ARCHIVE_ROUTE)
    }

    fun navigateToStatsScreen() {
        navController.navigate(STATS_ROUTE)
    }

    fun navigateToSettingsScreen() {
        navController.navigate(SETTINGS_ROUTE)
    }

    fun navigateToItemSummaryScreen(itemType: ItemType = ItemType.PLATE, itemId: Int) {
        navController.navigate("$ITEM_SUMMARY_SCREEN/${itemType.name}/$itemId")
    }

    fun navigateToItemEntryScreen(itemType: ItemType = ItemType.PLATE, itemId: Int? = null) {
        navController.navigate(
            if (itemId != null) {
                "$ITEM_ENTRY_SCREEN/${itemType.name}/$itemId"
            } else {
                "$ITEM_ENTRY_SCREEN/${itemType.name}"
            }
        )
    }
}
