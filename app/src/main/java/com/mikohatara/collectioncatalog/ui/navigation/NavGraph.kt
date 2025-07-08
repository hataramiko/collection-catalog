package com.mikohatara.collectioncatalog.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.mikohatara.collectioncatalog.data.CollectionRepository
import com.mikohatara.collectioncatalog.data.ItemType
import com.mikohatara.collectioncatalog.ui.collection.CollectionEntryScreen
import com.mikohatara.collectioncatalog.ui.collection.CollectionListScreen
import com.mikohatara.collectioncatalog.ui.components.ModalMenuDrawer
import com.mikohatara.collectioncatalog.ui.help.HelpPage
import com.mikohatara.collectioncatalog.ui.help.HelpScreen
import com.mikohatara.collectioncatalog.ui.home.ArchiveScreen
import com.mikohatara.collectioncatalog.ui.home.HomeScreen
import com.mikohatara.collectioncatalog.ui.home.WishlistScreen
import com.mikohatara.collectioncatalog.ui.item.ItemEntryScreen
import com.mikohatara.collectioncatalog.ui.item.ItemSummaryScreen
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.COLLECTION_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.HELP_PAGE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_ID
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinationArgs.ITEM_TYPE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.ARCHIVE_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.COLLECTION_ENTRY_ADD_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.COLLECTION_ENTRY_EDIT_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.COLLECTION_LIST_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HELP_DEFAULT_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HELP_PAGE_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_COLLECTION_ROUTE
import com.mikohatara.collectioncatalog.ui.navigation.CollectionCatalogDestinations.HOME_DEFAULT_ROUTE
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
    collectionRepository: CollectionRepository,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = HOME_DEFAULT_ROUTE,
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
    val onEditCollections = { navActions.navigateToCollectionListScreen() }
    val onAddCollection = { navActions.navigateToCollectionEntryScreen() }
    val collectionList by collectionRepository.getAllCollectionsStream().collectAsState(
        initial = emptyList()
    )
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = HOME_DEFAULT_ROUTE
        ) {
            ModalMenuDrawer(
                drawerState,
                currentRoute,
                navActions,
                collectionList,
                onEditCollections,
                onAddCollection,
                currentNavBackStackEntry
            ) {
                HomeScreen(
                    onAddItem = {
                        navActions.navigateToItemEntryScreen(ItemType.PLATE, null)
                    },
                    onItemClick = {
                        navActions.navigateToItemSummaryScreen(ItemType.PLATE, it.id)
                    },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onImportHelp = { navActions.navigateToHelpScreen(HelpPage.IMPORT) }
                )
            }
        }
        composable(
            route = HOME_COLLECTION_ROUTE,
            arguments = listOf(navArgument(COLLECTION_ID) { type = NavType.IntType })
        ) {
            ModalMenuDrawer(
                drawerState,
                currentRoute,
                navActions,
                collectionList,
                onEditCollections,
                onAddCollection,
                currentNavBackStackEntry
            ) {
                HomeScreen(
                    onAddItem = {
                        /*navActions.navigateToItemEntryScreen(ItemType.PLATE, null)*/
                    },
                    onItemClick = {
                        navActions.navigateToItemSummaryScreen(ItemType.PLATE, it.id)
                    },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onImportHelp = { navActions.navigateToHelpScreen(HelpPage.IMPORT) }
                )
            }
        }
        composable(
            route = WISHLIST_ROUTE
        ) {
            ModalMenuDrawer(
                drawerState,
                currentRoute,
                navActions,
                collectionList,
                onEditCollections,
                onAddCollection,
                currentNavBackStackEntry
            ) {
                WishlistScreen(
                    onAddItem = {
                        navActions.navigateToItemEntryScreen(ItemType.WANTED_PLATE, null)
                    },
                    onItemClick = {
                        navActions.navigateToItemSummaryScreen(ItemType.WANTED_PLATE, it.id)
                    },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onImportHelp = { navActions.navigateToHelpScreen(HelpPage.IMPORT) }
                )
            }
        }
        composable(
            route = ARCHIVE_ROUTE
        ) {
            ModalMenuDrawer(
                drawerState,
                currentRoute,
                navActions,
                collectionList,
                onEditCollections,
                onAddCollection,
                currentNavBackStackEntry
            ) {
                ArchiveScreen(
                    onAddItem = {
                        navActions.navigateToItemEntryScreen(ItemType.FORMER_PLATE, null)
                    },
                    onItemClick = {
                        navActions.navigateToItemSummaryScreen(ItemType.FORMER_PLATE, it.id)
                    },
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onImportHelp = { navActions.navigateToHelpScreen(HelpPage.IMPORT) }
                )
            }
        }
        composable(
            route = STATS_ROUTE
        ) {
            ModalMenuDrawer(
                drawerState,
                currentRoute,
                navActions,
                collectionList,
                onEditCollections,
                onAddCollection,
                currentNavBackStackEntry
            ) {
                StatsScreen(
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }
        composable(
            route = SETTINGS_ROUTE
        ) {
            SettingsScreen(
                onBack = { onBack() }
            )
        }
        composable(
            route = HELP_DEFAULT_ROUTE
        ) {
            HelpScreen(
                navActions = navActions,
                onBack = { onBack() }
            )
        }
        composable(
            route = HELP_PAGE_ROUTE,
            arguments = listOf(navArgument(HELP_PAGE) { type = NavType.StringType })
        ) {
            HelpScreen(
                navActions = navActions,
                onBack = { onBack() }
            )
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
            route = COLLECTION_LIST_ROUTE
        ) {
            CollectionListScreen(
                onAddNew = onAddCollection,
                onCollectionClick = {
                    navActions.navigateToCollectionEntryScreen(it.id)
                },
                onBack = onBack
            )
        }
        composable(
            route = COLLECTION_ENTRY_ADD_ROUTE
        ) {
            CollectionEntryScreen(
                onBack = onBack
            )
        }
        composable(
            route = COLLECTION_ENTRY_EDIT_ROUTE,
            arguments = listOf(navArgument(COLLECTION_ID) { type = NavType.IntType })
        ) {
            CollectionEntryScreen(
                onBack = onBack
            )
        }
    }
}
