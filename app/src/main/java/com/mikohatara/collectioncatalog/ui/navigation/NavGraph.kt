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
import com.mikohatara.collectioncatalog.ui.components.ModalMenuDrawer
import com.mikohatara.collectioncatalog.ui.home.HomeScreen
import com.mikohatara.collectioncatalog.ui.home.WishlistScreen
import com.mikohatara.collectioncatalog.ui.item.ItemEntryScreen
import com.mikohatara.collectioncatalog.ui.item.ItemSummaryScreen
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_ENTRY_ADD_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_ENTRY_EDIT_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_SUMMARY_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.SETTINGS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.STATS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.WISHLIST_ROUTE
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
    startDestination: String = HOME_ROUTE,
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
        composable(
            route = HOME_ROUTE
        ) {
            ModalMenuDrawer(drawerState, currentRoute, navActions) {
                HomeScreen(
                    onAddItem = { navActions.navigateToItemEntryScreen(null) },
                    onItemClick = { navActions.navigateToItemSummaryScreen(it.id) },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(
            route = WISHLIST_ROUTE
        ) {
            ModalMenuDrawer(drawerState, currentRoute, navActions) {
                WishlistScreen(
                    onAddItem = { /*TODO*/ },
                    onItemClick = { /*TODO*/ },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(
            route = ITEM_SUMMARY_ROUTE,
            arguments = listOf(navArgument(ITEM_ID) { type = NavType.IntType })
        ) {
            ItemSummaryScreen(
                onBack = { navController.popBackStack() },
                onEdit = { navActions.navigateToItemEntryScreen(it.id) },
                onDelete = { /*TODO remove this from here?*/ }
            )
        }
        composable(
            route = ITEM_ENTRY_ADD_ROUTE
        ) {
            ItemEntryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = ITEM_ENTRY_EDIT_ROUTE,
            arguments = listOf(
                navArgument(ITEM_ID) { type = NavType.IntType }
            )
        ) {
            ItemEntryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = STATS_ROUTE
        ) {
            ModalMenuDrawer(drawerState, currentRoute, navActions) {
                StatsScreen(
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(
            route = SETTINGS_ROUTE
        ) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
