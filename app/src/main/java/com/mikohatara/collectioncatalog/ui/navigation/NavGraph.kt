package com.mikohatara.collectioncatalog.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mikohatara.collectioncatalog.data.Plate
import com.mikohatara.collectioncatalog.data.samplePlates
import com.mikohatara.collectioncatalog.ui.home.HomeScreen
import com.mikohatara.collectioncatalog.ui.item.AddItemScreen
import com.mikohatara.collectioncatalog.ui.item.ItemScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun CollectionCatalogNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = CollectionCatalogDestinations.HOME_ROUTE,
    navActions: CollectionCatalogNavigationActions = remember(navController) {
        CollectionCatalogNavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
    ) {

        composable(CollectionCatalogDestinations.HOME_ROUTE) { //entry ->
            HomeScreen(
                onAddItem = { navActions.navigateToAddItemScreen() },
                onItemClick = { item -> navActions.navigateToItemScreen(
                    item.uniqueDetails.number,
                    item.uniqueDetails.variant
                ) }
            )
        }

        composable(CollectionCatalogDestinations.ITEM_ROUTE) {
            ItemScreen(
                onBack = { navController.popBackStack() },
                onDelete = {  }
            )
        }

        composable(CollectionCatalogDestinations.ADD_ITEM_ROUTE) {
            AddItemScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}