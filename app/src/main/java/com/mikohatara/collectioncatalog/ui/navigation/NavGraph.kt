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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mikohatara.collectioncatalog.ui.components.ModalMenuDrawer
import com.mikohatara.collectioncatalog.ui.home.HomeScreen
import com.mikohatara.collectioncatalog.ui.item.ItemEntryScreen
import com.mikohatara.collectioncatalog.ui.item.ItemSummaryScreen
import com.mikohatara.collectioncatalog.ui.settings.SettingsScreen
import com.mikohatara.collectioncatalog.ui.stats.StatsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
        modifier = modifier
    ) {
        composable(CollectionCatalogDestinations.HOME_ROUTE) { //entry ->
            ModalMenuDrawer(drawerState, currentRoute, navActions) {
                HomeScreen(
                    onAddItem = { navActions.navigateToItemEntryScreen(null, null) },
                    onItemClick = { item -> navActions.navigateToItemSummaryScreen(
                        item.uniqueDetails.number,
                        item.uniqueDetails.variant
                    ) },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(CollectionCatalogDestinations.ITEM_SUMMARY_ROUTE) {
            ItemSummaryScreen(
                onBack = { navController.popBackStack() },
                onEdit = { item -> navActions.navigateToItemEntryScreen(
                    item.uniqueDetails.number,
                    item.uniqueDetails.variant
                ) },
                onDelete = {  }
            )
        }
        composable(CollectionCatalogDestinations.ITEM_ENTRY_ROUTE) {
            ItemEntryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(CollectionCatalogDestinations.STATS_ROUTE) {
            ModalMenuDrawer(drawerState, currentRoute, navActions) {
                StatsScreen(
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(CollectionCatalogDestinations.SETTINGS_ROUTE) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
