package com.mikohatara.collectioncatalog.ui.navigation

import androidx.navigation.NavHostController
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ARG
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HOME_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_SCREEN


object CollectionCatalogScreens {
    const val HOME_SCREEN = "home"
    const val ITEM_SCREEN = "item"
}

object CollectionCatalogDestinationArgs {
    const val ITEM_ARG = "item"
}

object CollectionCatalogDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val ITEM_ROUTE = "$ITEM_SCREEN/{$ITEM_ARG}"
}

class CollectionCatalogNavigationActions(private val navController: NavHostController) {

    fun navigateToHomeScreen() {

    }

    fun navigateToItemScreen(item: Plate) {
        navController.navigate("$ITEM_SCREEN/$item")
    }
}