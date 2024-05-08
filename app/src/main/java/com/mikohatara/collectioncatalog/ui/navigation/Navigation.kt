package com.mikohatara.collectioncatalog.ui.navigation

import android.util.Log
import androidx.navigation.NavHostController
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_KEY
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.PLATE_NUMBER
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.PLATE_VARIANT
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HOME_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_SCREEN


object CollectionCatalogScreens {
    const val HOME_SCREEN = "home"
    const val ITEM_SCREEN = "item"
}

object CollectionCatalogDestinationArgs {
    const val ITEM_KEY = "itemKey"
    const val PLATE_NUMBER = "plateNumber"
    const val PLATE_VARIANT = 'a'
}

object CollectionCatalogDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val ITEM_ROUTE = "$ITEM_SCREEN/{$PLATE_NUMBER},{$PLATE_VARIANT}"
}

class CollectionCatalogNavigationActions(private val navController: NavHostController) {

    fun navigateToHomeScreen() {

    }

    fun navigateToItemScreen(number: String, variant: Char) {
        Log.d("navNumber", number)
        Log.d("navVariant", variant.toString())
        navController.navigate("$ITEM_SCREEN/$number,$variant")
    }
}