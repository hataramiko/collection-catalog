package com.mikohatara.collectioncatalog.ui.navigation

import android.util.Log
import androidx.navigation.NavHostController
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_KEY
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.HOME_SCREEN
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogScreens.ITEM_SCREEN


object CollectionCatalogScreens {
    const val HOME_SCREEN = "home"
    const val ITEM_SCREEN = "item"
}

object CollectionCatalogDestinationArgs {
    const val ITEM_KEY = "itemKey"
}

object CollectionCatalogDestinations {
    const val HOME_ROUTE = HOME_SCREEN
    const val ITEM_ROUTE = "$ITEM_SCREEN/{$ITEM_KEY}"
}

class CollectionCatalogNavigationActions(private val navController: NavHostController) {

    fun navigateToHomeScreen() {

    }

    /*fun navigateToItemScreen(item: Plate) {
        navController.navigate("$ITEM_SCREEN/$item")
    }*/

    fun navigateToItemScreen(number: String, variant: Char) {
        Log.d("number", number)
        Log.d("variant", variant.toString())
        val item = arrayOf(number, variant.toString())
        Log.d("array", item.toString())
        Log.d("number", number)
        Log.d("variant", variant.toString())
        Log.d("kok", navController.navigate("$ITEM_SCREEN/$item").toString())
    }
}