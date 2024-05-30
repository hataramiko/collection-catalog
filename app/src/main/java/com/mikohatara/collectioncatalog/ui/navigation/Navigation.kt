package com.mikohatara.collectioncatalog.ui.navigation

import android.util.Log
import androidx.navigation.NavHostController
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.NUMBER_VARIANT
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.PLATE_NUMBER
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ADD_ITEM_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_ENTRY_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ADD_ITEM_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HOME_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_ENTRY_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_SCREEN


object CollectionCatalogScreens {
    const val HOME_SCREEN = "home"
    const val ITEM_SCREEN = "item"
    const val ADD_ITEM_SCREEN = "addItem"
    const val ITEM_ENTRY_SCREEN = "itemEntry"
}

object CollectionCatalogDestinationArgs {
    const val PLATE_NUMBER = "plateNumber"
    const val NUMBER_VARIANT = "numberVariant"
}

object CollectionCatalogDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val ITEM_ROUTE = "$ITEM_SCREEN/{$PLATE_NUMBER}/{$NUMBER_VARIANT}"
    const val ADD_ITEM_ROUTE = ADD_ITEM_SCREEN
    const val ITEM_ENTRY_ROUTE = "$ITEM_ENTRY_SCREEN/{$PLATE_NUMBER}/{$NUMBER_VARIANT}"
}

class CollectionCatalogNavigationActions(private val navController: NavHostController) {

    fun navigateToHomeScreen() {

    }

    fun navigateToItemScreen(number: String, variant: String) {
        navController.navigate("$ITEM_SCREEN/$number/$variant")
    }

    fun navigateToAddItemScreen() {
        navController.navigate(ADD_ITEM_ROUTE)
    }

    fun navigateToItemEntryScreen(number: String?, variant: String?) {
        if (number == null || variant == null) { // TODO probably should improve this whole thing
            navController.navigate(ITEM_ENTRY_ROUTE)
        } else {
            navController.navigate("$ITEM_ENTRY_SCREEN/$number/$variant")
        }
    }
}