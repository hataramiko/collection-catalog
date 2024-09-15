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
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.ui.components.ModalMenuDrawer
import com.mikohatara.collectioncatalog.ui.home.ArchiveScreen
import com.mikohatara.collectioncatalog.ui.home.HomeScreen
import com.mikohatara.collectioncatalog.ui.home.WishlistScreen
import com.mikohatara.collectioncatalog.ui.item.ItemEntryScreen
import com.mikohatara.collectioncatalog.ui.item.ItemSummaryScreen
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ARCHIVE_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_ENTRY_ADD_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_ENTRY_EDIT_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ITEM_SUMMARY_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.SETTINGS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.STATS_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.WISHLIST_ROUTE
import com.mikohatara.collectioncatalog.ui.settings.SettingsScreen
import com.mikohatara.collectioncatalog.ui.stats.StatsScreen
import com.mikohatara.collectioncatalog.util.getItemId
import com.mikohatara.collectioncatalog.util.getItemType
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
    val onBack = {
        if (navController.currentBackStackEntry?.destination?.route != startDestination) {
            navController.popBackStack()
        }
    }
    
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
                    onAddItem = {
                        navActions.navigateToItemEntryScreen(ItemType.PLATE, null)
                    },
                    onItemClick = {
                        navActions.navigateToItemSummaryScreen(ItemType.PLATE, it.id)
                    },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(
            route = WISHLIST_ROUTE
        ) {
            ModalMenuDrawer(drawerState, currentRoute, navActions) {
                WishlistScreen(
                    onAddItem = {
                        navActions.navigateToItemEntryScreen(ItemType.WANTED_PLATE, null)
                    },
                    onItemClick = {
                        navActions.navigateToItemSummaryScreen(ItemType.WANTED_PLATE, it.id)
                    },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(
            route = ARCHIVE_ROUTE
        ) {
            ModalMenuDrawer(drawerState, currentRoute, navActions) {
                ArchiveScreen(
                    onAddItem = {
                        navActions.navigateToItemEntryScreen(ItemType.FORMER_PLATE, null)
                    },
                    onItemClick = {
                        navActions.navigateToItemSummaryScreen(ItemType.FORMER_PLATE, it.id)
                    },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(
            route = ITEM_SUMMARY_ROUTE,
            arguments = listOf(
                navArgument(ITEM_TYPE) { type = NavType.StringType },
                navArgument(ITEM_ID) { type = NavType.IntType }
            )
        ) {
            ItemSummaryScreen(
                onBack = onBack,
                onEdit = {
                    navActions.navigateToItemEntryScreen(
                        getItemType(it),
                        getItemId(it)
                    )
                }
            )
        }
        composable(
            route = ITEM_ENTRY_ADD_ROUTE,
            arguments = listOf(navArgument(ITEM_TYPE) { type = NavType.StringType })
        ) {
            ItemEntryScreen(
                onBack = onBack
            )
        }
        composable(
            route = ITEM_ENTRY_EDIT_ROUTE,
            arguments = listOf(
                navArgument(ITEM_TYPE) { type = NavType.StringType },
                navArgument(ITEM_ID) { type = NavType.IntType }
            )
        ) {
            ItemEntryScreen(
                onBack = onBack
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
                onBack = onBack
            )
        }
    }
}
