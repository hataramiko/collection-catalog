package com.mikohatara.collectioncatalog.ui.navigation

import androidx.navigation.NavHostController
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.ui.help.HelpPage
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.COLLECTION_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.HELP_PAGE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ARCHIVE_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.COLLECTION_LIST_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HELP_DEFAULT_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_DEFAULT_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.SETTINGS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.STATS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.WISHLIST_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ARCHIVE_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.CATALOG_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.COLLECTION_ENTRY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.COLLECTION_LIST_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HELP_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HOME_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_ENTRY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_SUMMARY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.SETTINGS_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.STATS_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.WISHLIST_SCREEN

object CollectionCatalogScreens {
    const val CATALOG_SCREEN = "catalog"
    const val HOME_SCREEN = "home"
    const val WISHLIST_SCREEN = "wishlist"
    const val ARCHIVE_SCREEN = "archive"
    const val STATS_SCREEN = "stats"
    const val SETTINGS_SCREEN = "settings"
    const val HELP_SCREEN = "help"
    const val ITEM_SUMMARY_SCREEN = "itemSummary"
    const val ITEM_ENTRY_SCREEN = "itemEntry"
    const val COLLECTION_LIST_SCREEN = "collectionList"
    const val COLLECTION_ENTRY_SCREEN = "collectionEntry"
}

object CollectionCatalogDestinationArgs {
    const val ITEM_TYPE = "itemType"
    const val ITEM_ID = "itemId"
    const val COLLECTION_ID = "collectionId"
    const val HELP_PAGE = "helpPage"
}

object CollectionCatalogDestinations {
    const val CATALOG_DEFAULT_ROUTE = "$CATALOG_SCREEN/{$ITEM_TYPE}"
    const val CATALOG_COLLECTION_ROUTE = "$CATALOG_SCREEN/{$ITEM_TYPE}/{$COLLECTION_ID}"
    const val CATALOG_WISHLIST_ROUTE = "$CATALOG_SCREEN/{$ITEM_TYPE}"
    const val CATALOG_ARCHIVE_ROUTE = "$CATALOG_SCREEN/{$ITEM_TYPE}"
    const val HOME_DEFAULT_ROUTE = HOME_SCREEN
    const val HOME_COLLECTION_ROUTE = "$HOME_SCREEN/{$COLLECTION_ID}"
    const val WISHLIST_ROUTE = WISHLIST_SCREEN
    const val ARCHIVE_ROUTE = ARCHIVE_SCREEN
    const val STATS_ROUTE = STATS_SCREEN
    const val SETTINGS_ROUTE = SETTINGS_SCREEN
    const val HELP_DEFAULT_ROUTE = HELP_SCREEN
    const val HELP_PAGE_ROUTE = "$HELP_SCREEN/{$HELP_PAGE}"
    const val ITEM_SUMMARY_ROUTE = "$ITEM_SUMMARY_SCREEN/{$ITEM_TYPE}/{$ITEM_ID}"
    const val ITEM_ENTRY_ADD_ROUTE = "$ITEM_ENTRY_SCREEN/{$ITEM_TYPE}"
    const val ITEM_ENTRY_EDIT_ROUTE = "$ITEM_ENTRY_SCREEN/{$ITEM_TYPE}/{$ITEM_ID}"
    const val COLLECTION_LIST_ROUTE = COLLECTION_LIST_SCREEN
    const val COLLECTION_ENTRY_ADD_ROUTE = COLLECTION_ENTRY_SCREEN
    const val COLLECTION_ENTRY_EDIT_ROUTE = "$COLLECTION_ENTRY_SCREEN/{$COLLECTION_ID}"
}

class CollectionCatalogNavigationActions(private val navController: NavHostController) {

    fun navigateToCatalogScreen(itemType: ItemType = ItemType.PLATE, collectionId: Int? = null) {
        navController.navigate(
            if (collectionId != null) {
                "$CATALOG_SCREEN/${itemType.name}/$collectionId"
            } else {
                "$CATALOG_SCREEN/${itemType.name}"
            }
        )
    }

    fun navigateToHomeScreen(collectionId: Int? = null) {
        navController.navigate(
            if (collectionId != null) {
                "$HOME_SCREEN/$collectionId"
            } else {
                HOME_DEFAULT_ROUTE
            }
        )
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

    fun navigateToHelpScreen(helpPage: HelpPage = HelpPage.DEFAULT) {
        navController.navigate(
            if (helpPage != HelpPage.DEFAULT) {
                "$HELP_SCREEN/$helpPage"
            } else HELP_DEFAULT_ROUTE
        )
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

    fun navigateToCollectionListScreen() {
        navController.navigate(COLLECTION_LIST_ROUTE)
    }

    fun navigateToCollectionEntryScreen(collectionId: Int? = null) {
        navController.navigate(
            if (collectionId != null) {
                "$COLLECTION_ENTRY_SCREEN/$collectionId"
            } else {
                COLLECTION_ENTRY_SCREEN
            }
        )
    }
}
